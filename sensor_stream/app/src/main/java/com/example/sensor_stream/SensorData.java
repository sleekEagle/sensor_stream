package com.example.sensor_stream;

public class SensorData {
    int type,accuracy;
    long timestamp;
    float[] data;
    public SensorData(int type,int accuracy,long timestamp,float[] data){
        this.type=type;
        this.accuracy=accuracy;
        this.timestamp=timestamp;
        this.data=data;
    }
}
