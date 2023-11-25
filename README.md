# snsr_strm
This streams the Android sensor values to a computer.<br>
The streaming happens through TCP over ADB connection. <br>
The streaming can happen either over USB or WiFi connection. <br>
The python3 script client.py receives the sensor data stream and store it in a file. <br>
Edit the list sensorTypes in the class MainActivity.java to change the types of 
sensors that are streamed. 

# Format of the sensor data file
sensor_type , accuracy_level , timestamp of device , values , timestamp of local mahcine <br>

# Usage
1. Connect both the device and the computer to the same WiFi network (if using WiFi) <br>
2. Turn on the snsr_strm app <br>
3. Note the IP address shown in the app <br>
   (We have to use this IP address in the computer python client code) <br>
4. In the computer tun the client.py code <br>
`python client.py --output \path\to\data\file\ --ip 192.168.0.105`



