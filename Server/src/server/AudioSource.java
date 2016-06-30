package server;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

import debug.SCDebug;

public class AudioSource extends Thread {
   private TargetDataLine targetDataLine;
   private AudioInputStream audioInputStream;
   public static AudioFormat AUDIO_FORMAT;
   private byte tempBuffer[] = new byte[AudioBuffer.AUDIO_BUFFER_SIZE];

   // AudioFormat attribute
   public static final float AUDIO_SAMPLERATE = 44100.0F; // Sample Rate
   public static final int INT_AUDIO_SAMPLERATE = 44100; // Sample Rate
   public static final int NUMBER_OF_BITS_IN_CHANNEL = 16; // Number of bits
                                             // in each
   // channel
   public static final int NUMBER_OF_CHANNELS = 2; // Number of channels
                                       // (2=stereo)
   public static final int NUMBER_OF_BYTES_IN_FRAME = 4; // Number of bytes in
                                             // each frame
   public static final float NUMBER_OF_FRAME_PER_SECOND = 44100.0F; // Number
                                                      // of
   public static final int FRAME_SIZE = (int) AUDIO_SAMPLERATE * NUMBER_OF_CHANNELS * NUMBER_OF_BITS_IN_CHANNEL
         / Byte.SIZE / 50; // frame size
                        // frames
                        // per
   // second
   private static final boolean ENDIAN = false; // Big-endian (true) or
                                       // little-endian
   // (false)

   private AudioBuffer[] audioBuffers;

   static int writeIndex; // write buffer index

   public AudioSource(AudioBuffer[] audioBuffers) {
      try {
         this.audioBuffers = audioBuffers;

         writeIndex = 0;

         AUDIO_FORMAT = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, AUDIO_SAMPLERATE, NUMBER_OF_BITS_IN_CHANNEL,
               NUMBER_OF_CHANNELS, NUMBER_OF_BYTES_IN_FRAME, NUMBER_OF_FRAME_PER_SECOND, ENDIAN);

         // Create our TargetDataLine that will be used to read audio data by
         // first
         // creating a DataLine instance for our audio format type
         DataLine.Info info = new DataLine.Info(TargetDataLine.class, AUDIO_FORMAT);

         // Next we ask the AudioSystem to retrieve a line that matches the
         // DataLine Info
         this.targetDataLine = (TargetDataLine) AudioSystem.getLine(info);
         audioInputStream = new AudioInputStream(this.targetDataLine);

         // Open the TargetDataLine with the specified format
         this.targetDataLine.open(AUDIO_FORMAT);

      } catch (LineUnavailableException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }

   }

   public void run() {
      try {
         int readSize;
         long beforeTime = 0, currentTime = 0;
         targetDataLine.start();
         while (true) {
            beforeTime = System.currentTimeMillis();
            readSize = audioInputStream.read(tempBuffer, 0, FRAME_SIZE);
            SCDebug.DebugMsg("Read time : " + (beforeTime - currentTime) + ", Read size : " + readSize
                  + ", Remain data : " + targetDataLine.available() + "  writeIndex" + writeIndex);

               audioBuffers[writeIndex].setBuffer(tempBuffer);
               currentTime = System.currentTimeMillis();
               writeIndex = (writeIndex + 1) % Server.NUM_OF_BUFFERS;
               SCDebug.DebugMsg("Source Write");
         }
      } catch (Exception e) {
         e.printStackTrace();
      } finally {
         SCDebug.printLog();
         // stopReadData();
      }
   }

   private void stopReadData() {
      this.targetDataLine.stop();
      this.targetDataLine.flush();
      this.targetDataLine.close();
   }

}
