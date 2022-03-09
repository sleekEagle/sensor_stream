# sensor_stream
This streams the Android sensor values to a computer.
The streaming happens through TCP over ADB connection. 
The streaming can happen either over USB or WiFi connection. 
The python3 script client.py receives the sensor data stream and store it in a file.
Edit the list sensorTypes in the class MainActivity.java to limit the types of 
sensors that are streamed. 

## format of the sensor data file
sensor_type , accuracy_level , timestamp , values


