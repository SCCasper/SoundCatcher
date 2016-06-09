package com.example.administrator.casperv01;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

/**
 * Created by Administrator on 2016-05-22.
 */
public class AudioHandler {
    private static final int BLOCK_SIZE = 3528;
    private static final int AudioSampleRate = 44100;
    private static final int AudioChannel = AudioFormat.CHANNEL_OUT_STEREO;
    private static final int AudioBit = AudioFormat.ENCODING_PCM_16BIT;
    private static final int AudioMode = AudioTrack.MODE_STREAM;

    private AudioTrack audioTrack;
    private byte [] tempBuffer;

    public AudioHandler()
    {
        super();
        tempBuffer = new byte[BLOCK_SIZE];
        createAudioTrack();
        audioTrack.play();
        audioTrack.pause();

    }
    private void createAudioTrack()
    {
        audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, AudioSampleRate, AudioChannel, AudioBit, audioTrack.getMinBufferSize(AudioSampleRate, AudioChannel, AudioBit), AudioMode);
    }

    public void writePCM(byte[] receiveBuffer)
    {
        System.arraycopy(receiveBuffer, 0, tempBuffer, 0, BLOCK_SIZE);
        audioTrack.write(tempBuffer, 0, BLOCK_SIZE);
    }

    public void play() {
        audioTrack.play();
    }

    public void pause() {
        audioTrack.flush();
        audioTrack.pause();
    }

    public int getAudioState(){
        return audioTrack.getPlayState();
    }
}