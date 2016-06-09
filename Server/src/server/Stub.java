package server;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Vector;

import debug.SCDebug;
import receiver.Receiver;
import receiver.UDPReceiver;
import receiver.WebReceiver;
import sender.Sender;
import sender.UDPSender;

public class Stub {
	static boolean clientFlag [] = new boolean[100];
	static int endIdx = -1;
	static int clientCount = 0;
	private int newIdx = 0;
	
	private AudioBuffer[] audioBuffers;	// Buffer that give to Consumer
	private Vector<Receiver> receivers;
	private HashMap<InetAddress, Deliver> delivers;

	
	public Stub(AudioBuffer[] audioBuffers) {
		this.audioBuffers = audioBuffers;
		this.receivers = new Vector<Receiver>();
		this.delivers = new HashMap<InetAddress, Deliver>();
		
		createReceivers();
	}
	
	public void createDeliver(Sender sender, InetAddress address){
		if(delivers.containsKey(address))
			return;
		
		if(clientCount-1 == endIdx) {
			newIdx = clientCount;
			endIdx++;
		}
		else {
			newIdx = 0;
			while(clientFlag[newIdx] != false) {
				newIdx++;
			}
		}
		clientFlag[newIdx] = true;
		
		Deliver deliver = new Deliver(audioBuffers, newIdx);
		deliver.setSender(sender);
		deliver.setReadIndex(AudioSource.writeIndex);
	
		delivers.put(address, deliver);
		
		Stub.clientCount++;
		deliver.start();		
	}
	
	public void removeDeliver(InetAddress address){		
		Deliver deliver = delivers.remove(address);
		int deliverNumber = deliver.getNumber(); 
		clientFlag[deliverNumber] = false;
		deliver.stopDeliver();
		if(clientCount-1 == deliverNumber)
			endIdx--;
		clientCount--;	
	}
	
	public boolean existClient(InetAddress address) {
		return delivers.containsKey(address);
	}
	
	private void createReceivers() {
		/* Create Protocol Listener  */
		receivers.add(new UDPReceiver(this));
		receivers.add(new WebReceiver(this));
	}
	
	public void run() {
		SCDebug.DebugMsg("STUB : RUN");
		
		// test deliver
		/*
		new TestPlayer(audioBuffers, 0).run();
		clientCount++;
		endIdx++;
		clientFlag[0] = true;
		*/
		
		for(Receiver receiver : receivers){
			receiver.start();
		}		
	}
}
