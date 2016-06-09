package com.example.administrator.casperv01;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.util.Calendar;

/**
 * Created by Administrator on 2016-05-29.
 */
public class AudioFile {

    private final static String AUDIO_RAW_FILENAME = "RawAudio.raw";
    public static String AUDIO_WAV_FILENAME = "FinalAudio.wav";




    public static boolean isSdcardExit(){
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
            return true;
        else
            return false;
    }

    public static String getRawFilePath(){
        String mAudioRawPaht = "";

        if(isSdcardExit()){
            String filePath = Environment.getExternalStorageDirectory().getAbsolutePath();
            mAudioRawPaht = filePath  + "/" + AUDIO_RAW_FILENAME;
        }
        return mAudioRawPaht;
    }

    public static String getWavFIlePath(){
        String mAudioWavPath = "";
        if(isSdcardExit()) {
            String filePath = Environment.getExternalStorageDirectory().getAbsolutePath();
            // 일치하는 폴더가 없으면 생성

            File SCFolder = new File(filePath + "/SoundCatcher");

            if( !SCFolder.exists() ) {
                SCFolder.mkdirs();
            }


            mAudioWavPath = filePath + "/SoundCatcher/" + AUDIO_WAV_FILENAME;
        }
        return mAudioWavPath;
    }

    public static long getFileSize(String path){
        File mFile = new File(path);
        if(!mFile.exists())
            return -1;
        return mFile.length();
    }
}
