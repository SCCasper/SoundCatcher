package server;

import debug.SCDebug;
import sender.Sender;

public class Deliver extends Thread{
	private Sender sender;
	private int number;
	private AudioBuffer[] audioBuffers;
	private byte tempBuffer[] = new byte[AudioBuffer.AUDIO_BUFFER_SIZE]; 
	
	private int readIndex;	// audio data
	
	private boolean stopFlag = false;
	
	public Deliver(AudioBuffer[] audioBuffers, int number) {
		SCDebug.DebugMsg("Deliver: CREATE Deliver " + number);
		this.audioBuffers = audioBuffers;
		this.number = number;
		readIndex = 0;
	}
	
	public void setReadIndex(int readIndex) {
		this.readIndex = readIndex;
	}
	
	public void setSender(Sender sender) {
		this.sender = sender;
	}
	
	public int getNumber() {
		return this.number;
	}
	
	public void run() {
		try {
			while(!stopFlag) {
				audioBuffers[readIndex].getBuffer(this.number, tempBuffer);
				readIndex = (readIndex + 1) % Server.NUM_OF_BUFFERS;
				SCDebug.DebugMsg("Deliver " + this.number + " Read");
				SCDebug.DebugMsg("Deliver " + this.number + ": READ DATA" + "readIndex : " + readIndex);
				sender.sendData(tempBuffer);					
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	public void stopDeliver() {
		this.stopFlag = true;
	}
}
