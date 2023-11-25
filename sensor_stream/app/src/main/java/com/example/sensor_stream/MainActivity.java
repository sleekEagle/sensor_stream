package com.example.sensor_stream;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    protected static final String TAG = "sensor_stream";
    protected static final String TAG1 = "sensor_stream_creat";

    private SensorManager sensorManager;
    private Sensor mAcc;
    private int[] sensorTypes={Sensor.TYPE_GYROSCOPE,Sensor.TYPE_ACCELEROMETER,Sensor.TYPE_GRAVITY};
    ArrayList<Sensor> sensor_list=new ArrayList<Sensor>();
    private TextView myTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myTextView = (TextView)findViewById(R.id.myTextView);
        getWindow(). addFlags (WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Server server=new Server();
        server.startServer();

        Log.i(TAG,"in main");
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> deviceSensors = sensorManager.getSensorList(Sensor.TYPE_ALL);
        Log.i(TAG,"length of list = " + String.valueOf(deviceSensors.size()));

        for (int i=0;i<deviceSensors.size();i++){
            Log.i(TAG,"sensor type = " + deviceSensors.get(i).getStringType());
            Log.i(TAG,"sensor name = " + deviceSensors.get(i).getName());
        }
        //get WiFi address
        String localIpAddress = getLocalIpAddress();
        updateText(localIpAddress);


    }

    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do something here if sensor accuracy changes.
    }

    @Override
    public final void onSensorChanged(SensorEvent event) {
        Sensor sensor=event.sensor;
        SensorData sd = new SensorData(sensor.getType(),event.accuracy,event.timestamp,event.values);
        Log.i(TAG,"event! " + sensor.getName());
        Server.sensorDataList.add(sd);
    }

    @Override
    protected void onResume() {
        super.onResume();
        for(int i=0;i<sensorTypes.length;i++){
            Sensor sensor = sensorManager.getDefaultSensor(sensorTypes[i]);
            if (sensor != null){
                // Success! this sensor is available
                sensor_list.add(sensor);
                Log.i(TAG,"sensor available : " + sensor.getName());
                sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME);
            } else {
                Log.i(TAG,"This sensor is not available");
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    private void updateText(String newText) {
        if (myTextView != null) {
            myTextView.setText(newText);
        }
        else{
            Log.i(TAG,"null pointer");
        }

    }

    public static String getLocalIpAddress() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface iface = interfaces.nextElement();
                // filters out 127.0.0.1 and inactive interfaces
                if (iface.isLoopback() || !iface.isUp())
                    continue;

                Enumeration<InetAddress> addresses = iface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();
                    String ip = addr.getHostAddress();
                    // Check if the IP address is in the IPv4 format
                    if (ip.matches("\\d+(\\.\\d+){3}")) {
                        return ip;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}