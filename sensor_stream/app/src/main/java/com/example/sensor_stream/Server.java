package com.example.sensor_stream;


import android.util.Log;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class Server {
    protected static final String TAG = "cam_stream";
    public static volatile  Socket client = null;
    public static volatile  DataOutputStream out;
    public static volatile DataInputStream in;

    public static volatile int byte_len=0;
    public static volatile ArrayList<SensorData> sensorDataList=new ArrayList<SensorData>();

    public void startServer(){
        Log.i(TAG,"Starting server...");
        new Thread(new ServerStart()).start();
    }

    static byte[] floatToByte(float[] input) {
        byte[] bytes = new byte[input.length*4];
        for (int x = 0; x < input.length; x++) {
            ByteBuffer.wrap(bytes, x*4, 4).putFloat(input[x]);
        }
        return bytes;
    }

    static class ServerStart implements Runnable {
        public ServerSocket mSocketServer = null;
        public static final int SERVER_PORT = 9500;
        @Override
        public void run() {
            try{
                mSocketServer = new ServerSocket(SERVER_PORT);
            } catch (IOException e) {
                Log.i(TAG, e.getMessage());
            }

            try {
                Log.i(TAG, "connecting...");
                client = mSocketServer.accept();
                Log.i(TAG, "Connected! local port = " + client.getLocalPort());
                Log.i(TAG,String.valueOf(client.isBound()));
                out = new DataOutputStream(client.getOutputStream());
                in = new DataInputStream(new BufferedInputStream(client.getInputStream()));
                Log.i(TAG, "Created data stream");
                out.write(100);
            } catch (IOException e) {
                Log.i(TAG, e.getMessage());
            }
            //wait till the client is ready to receive
            Log.i(TAG, "Waiting till client is ready....");
            try{
                int begin = in.read();
                Log.i(TAG,String.valueOf(begin));
                while(begin!=23){
                    begin=in.read();
                    Log.i(TAG,String.valueOf(begin));
                }
            }catch (IOException e) {
                Log.i(TAG, e.getMessage());
            }
            while(true) {
                try {
                    if(sensorDataList.size()>1) {
                        Log.i(TAG, "list size = " + String.valueOf(sensorDataList.size()));
                        SensorData sd = sensorDataList.remove(0);
                        byte[] bar=floatToByte(sd.data);
                        //indicate the beginning of a data packet
                        out.write(22);
                        out.writeInt(sd.type);
                        out.writeInt(sd.accuracy);
                        out.writeLong(sd.timestamp);
                        Log.i(TAG,String.valueOf(sd.timestamp));
                        out.writeInt(bar.length);
                        out.write(bar);
                        out.flush();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }catch(NullPointerException e){
                    e.printStackTrace();
                }
                /*if (img_list.size()>0) {
                    try {

                        Log.i(TAG,"done writing");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }catch (NullPointerException e){
                        e.printStackTrace();
                    }*/
            }

        }
    }
}
