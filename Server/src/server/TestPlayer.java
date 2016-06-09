package server;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import debug.SCDebug;

public class TestPlayer extends Thread{
	private int number;
	private AudioBuffer[] audioBuffers;
	private byte tempBuffer[] = new byte[AudioBuffer.AUDIO_BUFFER_SIZE]; 
	
	private int readIndex;	// audio data
	
	SourceDataLine speaker;
	
	public TestPlayer(AudioBuffer[] audioBuffers, int number) {
		try {
			speaker = AudioSystem.getSourceDataLine(AudioSource.AUDIO_FORMAT);
			speaker.open();
			speaker.start();
		} catch (LineUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			speaker.drain();
			speaker.stop();
			speaker.close();
		}
		
		SCDebug.DebugMsg("TestPlayer : CREATE TestPlayer");
		this.audioBuffers = audioBuffers;
		this.number = number;
		readIndex = 0;
	}
	
	public void setReadIndex(int readIndex) {
		this.readIndex = readIndex;
	}
	
	public void run() {
		long currentTime = System.currentTimeMillis();
		try {
			while(true) {
				//SCDebug.DebugMsg("CONSUMER " + this.number + ": READ DATA" + "readIndex : " + readIndex);
				audioBuffers[readIndex].getBuffer(this.number, tempBuffer);
				readIndex = (readIndex + 1) % Server.NUM_OF_BUFFERS;
				SCDebug.DebugMsg("TestPlayer Read");
				speaker.write(tempBuffer, 0, tempBuffer.length);					
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
}
