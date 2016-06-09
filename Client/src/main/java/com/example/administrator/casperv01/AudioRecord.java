package com.example.administrator.casperv01;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Administrator on 2016-05-23.
 */
public class AudioRecord{
    private static final int BLOCK_SIZE = 3528;
    private static final int AudioSampleRate = 44100;
    private static final int channels = 2;
    private static final int encodingBit = 16;
    private static long byteRate = channels * AudioSampleRate * encodingBit / 8;
    private String PcmFilename = "";
    private String WavFilename= "";
    private Main main;




    private static String filePath = Environment.getExternalStorageDirectory().getPath();




    private byte [] cpyData = null;

    private static File oldFile;
    private static File newFile;

    private FileInputStream in = null;
    private FileOutputStream dOut = null;

    public boolean isRecording = false;


    public AudioRecord()
    {
        super();
        cpyData = new byte[BLOCK_SIZE];


    }

    private void createRecordFile(){
        PcmFilename = AudioFile.getRawFilePath();
        WavFilename = AudioFile.getWavFIlePath();

        Log.d("MYLOG", PcmFilename);
        Log.d("MYLOG", WavFilename);

        oldFile = new File(PcmFilename);
        newFile = new File(WavFilename);
    }


    public void recordStart(){
        createRecordFile();
        try {
            dOut = new FileOutputStream(oldFile);
        }catch(FileNotFoundException e){

        }
    }

    public void recordStop(){
        try{
            if(dOut != null) {
                Log.d("MYLOG", "dOUT !NULL");
                dOut.flush();
                dOut.close();
            }
        }catch(IOException e){
        }
        copyWavFile();
        //oldFile.delete();
    }


    public void writeAudioData(byte [] receiveBuffer)
    {

        Log.d("MYLOG", "RECEIVE DATA : " + new String(receiveBuffer));
        System.arraycopy(receiveBuffer, 0, cpyData, 0, BLOCK_SIZE);
        Log.d("MYLOG", "COPY DATA : " + new String(cpyData));


        try {
            dOut.write(cpyData);
        } catch (IOException e) {

        }

        Log.d("MYLOG", "PCM DATA");
    }


    public void copyWavFile()
    {
        Log.d("Log", "Enter COPY WAV FILE");
        long totalAudioLen = 0;
        long totalDataLen = totalAudioLen + 36;
        long longSampleRate = 44100;
        int channels = 2;
        long byteRate = 16*longSampleRate*channels /8;
        byte[] data = new byte[352900];


        try{
            Log.d("Log", "IN AND OUT BEFORE");
            in = new FileInputStream(oldFile);
            dOut = new FileOutputStream(newFile);

            totalAudioLen = in.getChannel().size();

            totalDataLen = totalAudioLen +36;

            WriteWaveFileHeader(dOut, totalAudioLen, totalDataLen, longSampleRate, channels, byteRate);

            while(in.read(data) != -1){
                Log.d("MYLOG" , "WRITE DATA");
                dOut.write(data);
            }

            Log.d("MYLOG", "END");

            dOut.flush();

            in.close();
            dOut.close();



        }catch(FileNotFoundException e){

        }catch(IOException ex){

        }
    }


    private void WriteWaveFileHeader(FileOutputStream out, long totalAudioLen,
                                     long totalDataLen, long longSampleRate, int channels, long byteRate) throws IOException
    {

        byte[] header = new byte[44];
        header[0] = 'R'; // RIFF/WAVE header
        header[1] = 'I';
        header[2] = 'F';
        header[3] = 'F';
        header[4] = (byte) (totalDataLen & 0xff);
        header[5] = (byte) ((totalDataLen >> 8) & 0xff);
        header[6] = (byte) ((totalDataLen >> 16) & 0xff);
        header[7] = (byte) ((totalDataLen >> 24) & 0xff);
        header[8] = 'W';
        header[9] = 'A';
        header[10] = 'V';
        header[11] = 'E';
        header[12] = 'f'; // 'fmt ' chunk
        header[13] = 'm';
        header[14] = 't';
        header[15] = ' ';
        header[16] = 16; // 4 bytes: size of 'fmt ' chunk
        header[17] = 0;
        header[18] = 0;
        header[19] = 0;
        header[20] = 1; // format = 1
        header[21] = 0;
        header[22] = (byte) channels;
        header[23] = 0;
        header[24] = (byte) (longSampleRate & 0xff);
        header[25] = (byte) ((longSampleRate >> 8) & 0xff);
        header[26] = (byte) ((longSampleRate >> 16) & 0xff);
        header[27] = (byte) ((longSampleRate >> 24) & 0xff);
        header[28] = (byte) (byteRate & 0xff);
        header[29] = (byte) ((byteRate >> 8) & 0xff);
        header[30] = (byte) ((byteRate >> 16) & 0xff);
        header[31] = (byte) ((byteRate >> 24) & 0xff);
        header[32] = (byte) (2 * 16 / 8); // block align
        header[33] = 0;
        header[34] = 16; // bits per sample
        header[35] = 0;
        header[36] = 'd';
        header[37] = 'a';
        header[38] = 't';
        header[39] = 'a';
        header[40] = (byte) (totalAudioLen & 0xff);
        header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
        header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
        header[43] = (byte) ((totalAudioLen >> 24) & 0xff);
        out.write(header, 0, 44);
    }

}
