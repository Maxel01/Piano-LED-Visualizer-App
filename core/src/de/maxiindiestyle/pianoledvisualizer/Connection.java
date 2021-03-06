package de.maxiindiestyle.pianoledvisualizer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.Socket;
import com.badlogic.gdx.net.SocketHints;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.GdxRuntimeException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Connection implements Disposable {

    private static final int PORT = 9999;

    private Socket socket;
    private ArrayList<InetAddress> addresses;
    private Thread receiveThread;
    private ArrayList<String> messages;

    public Connection() {
        new Thread(() -> {
            if(Addresses.instance == null) {
                this.addresses = new Addresses().getNetworkIPs(3000);
            } else {
                this.addresses = Addresses.instance.getNetworkIPs(3000);
            }
        }).start();
    }

    public boolean connect(String host) {
        if(socket != null) return false;
        SocketHints socketHints = new SocketHints();
        try {
            socket = Gdx.net.newClientSocket(Net.Protocol.TCP, host, PORT, socketHints);
            receiveThread = new Thread(read());
            receiveThread.start();
            return true;
            /*socket.getOutputStream().write(new String("Hello World\n\r").getBytes());
            System.out.println("Send message");
            InputStream in;
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            System.out.println("received: " + br.readLine());*/
        } catch (GdxRuntimeException e) {
            System.err.println("Failed to connect: " + host);
            socket = null;
            return false;
        }
    }

    public void send(String data) {
        if(!isConnected()) return;
        data += ";";
        try {
            socket.getOutputStream().write(data.getBytes());
            socket.getOutputStream().flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Runnable read() {
        return () -> {
            messages = new ArrayList<>();
            try {
                System.out.println("Reading");
                BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String line = null;
                while (!receiveThread.isInterrupted() && (line = br.readLine()) != null) {
                    messages.add(line);
                    System.out.println("read something " + line);
                }
                disconnect();
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
    }

    public ArrayList<InetAddress> getAddresses() {
        return addresses;
    }

    public void disconnect() {
        socket.dispose();
        socket = null;
    }

    public boolean isReady() {
        return addresses != null;
    }

    public boolean isConnected() {
        return socket != null && socket.isConnected();
    }

    @Override
    public void dispose() {
        if(socket == null) return;
        socket.dispose();
        socket = null;
    }

    public static class Addresses {

        public static Addresses instance; // Used for Android implementation

        public ArrayList<InetAddress> getNetworkIPs(int timeout) {
            final byte[] ip = getIP();
            System.out.println(Arrays.toString(ip));
            ExecutorService threads = Executors.newCachedThreadPool();
            ArrayList<InetAddress> addresses = new ArrayList<>();
            for (int i = 1; i <= 254; i++) {
                final int j = i;  // i as non-final variable cannot be referenced from inner class
                threads.execute(new Runnable() {   // new thread for parallel execution
                    public void run() {
                        try {
                            ip[3] = (byte) j;
                            InetAddress address = InetAddress.getByAddress(ip);
                            String output = address.toString().substring(1);
                            if (address.isReachable(timeout)) {
                                //System.out.println(output + " is on the network " + address.getHostName());
                                synchronized (addresses) {
                                    addresses.add(address);
                                }
                            } else {
                                //System.out.println("Not Reachable: "+output);
                            }
                        } catch (Exception e) {
                            // Not Reachable
                        }
                    }
                });     // dont forget to start the thread
            }
            try {
                threads.awaitTermination(timeout, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return addresses;
        }

        public byte[] getIP() {
            try {
                return InetAddress.getLocalHost().getAddress();
            } catch (Exception e) {
                return null;
            }
        }
    }
}
