package com.example.administrator.casperv01;

import android.util.Log;

/**
 * Created by Administrator on 2016-05-29.
 */
public class Main extends Thread{
    private UdpNetwork udpNetwork;
    private AudioHandler audioHandler;
    private AudioRecord audioRecorder;
    private boolean aliveFlag = true;
    private boolean recordFlag = false;
    private byte [] audioSampleBuffer;

    // private Byte pre = 0;

    Main(UdpNetwork udpNetwork, AudioHandler audioHandler, AudioRecord audioRecorder) {
        this.udpNetwork = udpNetwork;
        this.audioHandler = audioHandler;
        this.audioRecorder = audioRecorder;
    }

    public void setAliveFlag(boolean flag) {
        this.aliveFlag = flag;
    }

    public void setRecordFlag(boolean flag){  this.recordFlag = flag; }

    public boolean getRecordFlag(){
        return this.recordFlag;
    }

    @Override
    public void run() {
        udpNetwork.connectServer();
        while (aliveFlag) {
            try {
                audioSampleBuffer = udpNetwork.readAudioSample();

                audioHandler.writePCM(audioSampleBuffer);
                if(recordFlag){
                    Log.d("MYLOG", new String(audioSampleBuffer));
                    audioRecorder.writeAudioData(audioSampleBuffer);
                }


            } catch(NullPointerException e) {
                Log.d("Log", "NullPointException");
                break;
            }

            // data leak test
            /*
            Byte test = audioSampleBuffer[audioSampleBuffer.length-1];
            int diff = test.byteValue() - pre.byteValue();

            if(diff > 1)
                Log.d("Log", Integer.toString(diff));

            byte [] testBuffer = new byte [3528];
            System.arraycopy(audioSampleBuffer, 0, testBuffer, 0, testBuffer.length);
            audioHandler.writePCM(testBuffer);

            pre = test;
            */

        }
        udpNetwork.exitNetwork();
        Log.d("Log", "Thread End");
    }
}
