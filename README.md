# Piano-LED-Visualizer-App

# Setup your Raspi
Follow the steps under this repository: (https://github.com/onlaj/Piano-LED-Visualizer)
Be sure that your Raspi has an internet connection

# Update your files
sudo chown -R pi /home/Piano-LED-Visualizer/
sudo chown -R pi /home/Piano-LED-Visualizer/Songs/
-> now you can edit this directory via FTP access

copy and upload visualizer_server.py
add "import visualizer_server" in visualizer.py at the beginning of the file
add "server = visualizer_server.VisualizerServer(menu, usersettings, ledsettings, ledstrip)" before "while True" (around line 2000 - 2200)

# Install the App on your Android phone
make sure you are connected to the same wifi as the raspi
select your raspi and enjoy
