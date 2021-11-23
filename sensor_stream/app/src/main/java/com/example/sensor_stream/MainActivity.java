package com.example.sensor_stream;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    protected static final String TAG = "sensor_stream";
    private SensorManager sensorManager;
    private Sensor mAcc;
    private int[] sensorTypes={Sensor.TYPE_GYROSCOPE,Sensor.TYPE_ACCELEROMETER,Sensor.TYPE_GRAVITY};
    ArrayList<Sensor> sensor_list=new ArrayList<Sensor>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
    }

    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do something here if sensor accuracy changes.
    }

    @Override
    public final void onSensorChanged(SensorEvent event) {
        Sensor sensor=event.sensor;
        SensorData sd = new SensorData(sensor.getType(),event.accuracy,event.timestamp,event.values);
        //Log.i(TAG,"event! " + sensor.getName());
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
                sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_FASTEST);
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


}