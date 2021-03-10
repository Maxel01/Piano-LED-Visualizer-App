# Piano-LED-Visualizer-App

# Setup your Raspi
Follow the steps under this repository: (https://github.com/onlaj/Piano-LED-Visualizer)<br>
Be sure that your Raspi has an internet connection

# Update your files
`sudo chown -R pi /home/Piano-LED-Visualizer/`<br>
`sudo chown -R pi /home/Piano-LED-Visualizer/Songs/`<br>
-> now you can edit this directory via FTP access<br>
<br>
copy and upload visualizer_server.py<br>
add "import visualizer_server" in visualizer.py at the beginning of the file<br>
add "server = visualizer_server.VisualizerServer(menu, usersettings, ledsettings, ledstrip)" before "while True" (around line 2000 - 2200)<br>

# Install the App on your Android phone
make sure you are connected to the same wifi as the raspi<br>
select your raspi and enjoy

# Issues
At the moment, the app is in test mode.<br>
So please report every issue or bug to help improve the app.<br>
I am also happy to get new feature ideas!
