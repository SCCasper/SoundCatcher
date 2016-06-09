package server;

public class Server {
	private AudioSource audioSource;
	private AudioBuffer[] audioBuffers;
	private Stub stub;
	
	public static final int NUM_OF_BUFFERS = 2;
	
	public Server() {
		// Create all object that will use
		audioBuffers = new AudioBuffer[NUM_OF_BUFFERS];
		for(int i = 0; i < NUM_OF_BUFFERS; i++) {
			audioBuffers[i] = new AudioBuffer();
		}
		audioSource = new AudioSource(audioBuffers);
		stub = new Stub(audioBuffers);
	}
	
	public void run() {
		// Function that real operate
		audioSource.start();
		stub.run();
	}
}
