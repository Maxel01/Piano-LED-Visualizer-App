import socket
import selectors
import types
from threading import Thread
import time
import mido
import os

class VisualizerServer:

    def __init__(self, menu, usersettings, ledsettings, ledstrip):
        self.menu = menu
        self.usersettings = usersettings
        self.ledsettings = ledsettings
        self.ledstrip = ledstrip
        self.selector = None
        self.server_socket = None
        self.start_server()

    def execute(self, cmd, socket):
        if len(str(cmd)) == 0:
            return
        if cmd.find(";") != -1:
            firstCmd = cmd[:cmd.find(";")]
            nextCmd = cmd[cmd.find(";") + 1:]
            self.execute(firstCmd, socket)
            self.execute(nextCmd, socket)
            return
        print(cmd)
        (action, location, choice) = str(cmd).split(".", 2)
        if action == "change_settings":
            self.menu.change_settings(location=location, choice=choice)
        elif action == "change_value":
            (key, value) = str(choice).split("=")
            #value = value.replace(",", ".")
            value = eval(value)
            if location == "RGB":
                print("key:", key, ", value:", value)
                value = self.manipulate_color(key, value, self.ledsettings) # TODO other approach get_color()
                print("key:", key, ", value manipulated:", value)
                print("before", self.ledsettings.get_colors())
                self.ledsettings.change_color(key, value)
                self.ledsettings.color_mode = "Single"
                self.usersettings.change_setting_value("color_mode", self.ledsettings.color_mode)
                print("after", self.ledsettings.get_colors())
            elif location == "Color_for_slow_speed":
                self.ledsettings.speed_slowest[key.lower()] = value
                self.usersettings.change_setting_value("speed_slowest_" + key.lower(), value)
            elif location == "Color_for_fast_speed":
                self.ledsettings.speed_fastest[key.lower()] = value
                self.usersettings.change_setting_value("speed_fastest_" + key.lower(), value)
            elif location == "Backlight_Color":
                backlight_color = self.ledsettings.get_backlight_color(key)
                value = value - backlight_color
                self.ledsettings.change_backlight_color(key, value)
            elif location == "Custom_RGB":
                value = value - self.ledsettings.get_adjacent_color(key)
                self.ledsettings.change_adjacent_color(key, value)
            elif location == "Backlight_Brightness":
                backlight_brightness = self.ledsettings.backlight_brightness_percent
                value = value - backlight_brightness
                self.ledsettings.change_backlight_brightness(value)
            elif location == "Brightness":
                brightness = self.ledstrip.brightness_percent
                value = value - brightness
                self.ledstrip.change_brightness(value)
            elif location == "Period":
                self.ledsettings.speed_period_in_seconds = value
                self.usersettings.change_setting_value("speed_period_in_seconds", value)
            elif location == "Max_notes_in_period":
                self.ledsettings.speed_max_notes = value
                self.usersettings.change_setting_value("speed_max_notes", value)
        elif action == "request":
            if location == "Ports_Settings":
                if choice == "Input" or choice == "Playback":
                    ports = mido.get_input_names()
                    message = location + "." + choice + "." + str(ports)
                    print(message)
                    socket.send(message.encode() + b"\n\r")
            elif location == "Play_MIDI":
                if choice == "Choose song":
                    songs_list = os.listdir("Songs")
                    print(songs_list)
                    message = location + "." + choice + "." + str(songs_list)
                    print(message)
                    socket.send(message.encode() + b"\n\r")


    def manipulate_color(self, key, value, attribute_holder):
        if key == "Red":
            value = value - attribute_holder.red
        elif key == "Green":
            value = value - attribute_holder.green
        elif key == "Blue":
            value = value - attribute_holder.blue
        return value

    def accept_wrapper(self, sock):
        conn, addr = sock.accept()  # Should be ready to read
        print('accepted connection from', addr)
        conn.setblocking(False)
        data = types.SimpleNamespace(addr=addr, inb=b'', outb=b'')
        events = selectors.EVENT_READ | selectors.EVENT_WRITE
        self.selector.register(conn, events, data=data)

    def service_connection(self, key, mask):
        sock = key.fileobj
        data = key.data
        if mask & selectors.EVENT_READ:
            recv_data = sock.recv(1024)  # Should be ready to read
            if recv_data:
                data.outb += recv_data
                self.execute(recv_data.decode(encoding='UTF-8'), sock)
            else:
                print('closing connection to', data.addr)
                self.selector.unregister(sock)
                sock.close()
        if mask & selectors.EVENT_WRITE:
            if data.outb:
                #print('echoing', repr(data.outb), 'to', data.addr)
                #sent = sock.send(data.outb + b"\n\r")  # Should be ready to write
                #data.outb = data.outb[sent:]
                pass

    def start_server_thread(self):
        host = '0.0.0.0'  # Standard loopback interface address (localhost)
        port = 9999        # Port to listen on (non-privileged ports are > 1023)

        self.selector = selectors.DefaultSelector()
        self.server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.server_socket.bind((host, port))
        self.server_socket.listen()
        print('listening on', (host, port))
        self.server_socket.setblocking(False)
        self.selector.register(self.server_socket, selectors.EVENT_READ, data=None)

        while True:
            try:
                events = self.selector.select(timeout=None)
                for key, mask in events:
                    if key.data is None:
                        self.accept_wrapper(key.fileobj)
                    else:
                        self.service_connection(key, mask)
                time.sleep(5)
            except KeyboardInterrupt:
                self.server_socket.close()
                print("Socket closed!")

    def start_server(self):
        try:
            thread = Thread(target=self.start_server_thread)
            thread.start()
        except:
            print("Error: unable to start thread")
