package com.example.administrator.casperv01;

import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Created by Administrator on 2016-05-22.
 */
public class UdpNetwork {
    //private static final String SERVER_IP = "192.168.1.6";   //Raspberry SoundCatcher
    //private static final String SERVER_IP = "192.168.1.111";
//    private static final String SERVER_IP = "113.198.84.73";
    //192.168.0.2 , 0.18
//    private static final String SERVER_IP = "172.24.1.1";
    private static final String SERVER_IP="192.168.0.2";     //Sound Catcher
//    private static final String SERVER_IP="192.168.0.2"; //SoundCatcher
    //private static final String SERVER_IP = "192.168.1.2";      //Lab 114

    private static final int SERVER_SPORT = 5000;
    private static final int SERVER_RPORT = 6000;

    private static final int TIME_OUT = 5000;
    public static final int BUFFER_SIZE = 3528;//Network Internal Buffer Size
    // private static final int ALIVE_CHECK_MAX_NUM = 5;


    private DatagramSocket sendSocket;
    private DatagramSocket receiveSocket;
    private DatagramPacket sendPacket;
    private DatagramPacket receivePacket;
    private InetAddress ServerAddress;

    private byte[] audioSample;
    private String protocalMsg;
    private byte[] protocalBytes;

    UdpNetwork() {
        try {
            Log.d("MYLOG", SERVER_IP);
            ServerAddress = InetAddress.getByName(SERVER_IP);

            //send
            sendSocket = new DatagramSocket();
            protocalBytes = new byte[4];
            sendPacket = new DatagramPacket(protocalBytes, protocalBytes.length,  ServerAddress, SERVER_SPORT);

            //receive
            receiveSocket = new DatagramSocket(SERVER_RPORT);
            receiveSocket.setReceiveBufferSize(BUFFER_SIZE*2);
            receiveSocket.setSoTimeout(TIME_OUT);
            audioSample = new byte[BUFFER_SIZE];
            receivePacket = new DatagramPacket(audioSample, BUFFER_SIZE);

        }catch(IOException e) {

        }
    }

    public void setSendMsg(String str) {
        protocalMsg = str;
        protocalBytes = protocalMsg.getBytes();
        sendPacket = new DatagramPacket(protocalBytes, protocalBytes.length,  ServerAddress, SERVER_SPORT);
    }

    public void stopNetwork() {
        sendSocket.close();
        receiveSocket.close();
    }

    public void connectServer()
    {
        try {
            setSendMsg("CONN");
            sendSocket.send(sendPacket);
        }catch(IOException e){
            //Host Cannot Find
            Log.d("Log", "CONNECT ERROR");
        }
    }

    public void exitNetwork(){
        try {
            setSendMsg("EXIT");
            sendSocket.send(sendPacket);
            stopNetwork();

        }catch(IOException e){
            Log.d("Log", "EXIT ERROR");
        }
    }

//    public void aliveCheck(){
//        try{
//            setSendMsg("LIVE");
//            sendSocket.send(sendPacket);
//
//        }catch(IOException e){
//            Log.d("Log", "LIVE ERROR");
//        }
//    }

    public byte [] readAudioSample() {
        try {
            receivePacket = new DatagramPacket(audioSample, BUFFER_SIZE);
            receiveSocket.receive(receivePacket);
            return receivePacket.getData();

        }catch(IOException e) {
            Log.d("Log", "Time Out Error");
            exitNetwork();
            return null;
        }
    }
}