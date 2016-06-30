package server;

import debug.SCDebug;

public class AudioBuffer {
	private byte buffer[];	// Location of saved real Audio data
	private boolean [] deliverFlags = new boolean[100];
	public static final int AUDIO_BUFFER_SIZE = AudioSource.FRAME_SIZE;	// Audio buffer size
	
	public AudioBuffer() {
		buffer = new byte[AUDIO_BUFFER_SIZE];
	}
	
	synchronized public void getBuffer(int number, byte[] tempBuffer) {
		if(!deliverFlags[number]) {
			try {
				SCDebug.DebugMsg("Deliver " + number + ": WAIT ");
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.arraycopy(buffer, 0, tempBuffer, 0, AudioBuffer.AUDIO_BUFFER_SIZE);
		deliverFlags[number] = false;
		notifyAll();
	}
	
	synchronized public void setBuffer(byte [] buffer) {
		System.arraycopy(buffer, 0, this.buffer, 0, AudioBuffer.AUDIO_BUFFER_SIZE);
		setFlags();
                notifyAll();
                
                try {
	                Thread.sleep(1);
                } catch (Exception e) {
                	e.printStackTrace();
                }
	}
	
	public void setFlags() {
		for(int i=0; i<Stub.endIdx; i++) {
			deliverFlags[i] = true;
		}
	}
	
	public void resetDeliverFlag(int number) {
		deliverFlags[number] = false;
	}
	public boolean isReadable(int number) {
		return deliverFlags[number];
	}

}
