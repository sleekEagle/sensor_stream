#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Thu Nov 18 14:15:00 2021

@author: sleekeagle
"""

# -*- coding: utf-8 -*-
"""
Spyder Editor

This is a temporary script file.
"""

import socket
import struct
from datetime import datetime
from pathlib import Path
import argparse
import os

TCP_PORT = 9500

def get_ts():
    t=datetime.now()
    now_str = t.strftime('%Y-%m-%d %H:%M:%S.%f')[:-3]
    now_str=now_str.split(' ')[-1]
    return now_str.replace(':','_') 
    
def connect(TCP_IP):
    read_int=-1
    print("waiting for connection....")
    while(read_int!=100):
        try:
            s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
            s.connect((TCP_IP, TCP_PORT))
            read_int=int.from_bytes(s.recv(1),"big")
        except:
            print("Could not connect to server")
    print("connected")
    return s


def read_array(size):
    img=bytearray()
    while(size>0):
        read_len=min(size,1024)
        data = s.recv(read_len)
        size -= len(data)
        img+=data
    
    i=0
    data=[]
    while(i<len(img)):
        data.append(struct.unpack('>f', img[i:i+4])[0])
        i+=4
    return data

def read_next_data_array(s):
    ind=int.from_bytes(s.recv(1),"big")
    if(ind!=22):
        return -1
    sensor_type=int.from_bytes(s.recv(4),"big")
    accuracy=int.from_bytes(s.recv(4),"big")
    ts=int.from_bytes(s.recv(8),"big")
    size=int.from_bytes(s.recv(4),"big")
    if(size>1000):
            return -1
    data=read_array(size)
    return sensor_type,accuracy,ts,data

if __name__ == '__main__':
    parser = argparse.ArgumentParser(description='Azure kinect mkv recorder.')
    parser.add_argument('--output', type=str,
                        default='C:\\Users\\lahir\\data\\CPR_experiment\\test\\smartwatch\\', 
                        help='output directory')
    parser.add_argument('--ip', type=str,
                    default='192.168.0.105', 
                    help='IP address of the Android device')
    args = parser.parse_args()

    s=connect(args.ip) 
    #send ready signal to the Android device
    p = struct.pack('!i', 23)
    s.send(p)

    now = datetime.now()
    dt_string = now.strftime("%d-%m-%Y-%H-%M-%S")
    Path(os.path.join(args.output)).mkdir(parents=True, exist_ok=True)
    myfile = Path(args.output,dt_string+".txt")
    myfile.touch(exist_ok=True)

    with open(myfile,"a") as f:
        while(True):
            data=read_next_data_array(s)
            if(data==-1):
                continue
            data_str=','.join([str(item) for item in data])     
            #calculate the ts
            ts=get_ts()
            data_str+=f',{ts}'
            print(data_str)
            f.write(data_str+'\n')




