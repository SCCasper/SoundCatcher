package receiver;

import server.Stub;

public abstract class Receiver extends Thread{
	protected Stub stub;
	protected byte [] message = new byte[4];
	protected static final int UDP_RPORT = 5000;
	protected static final int WEB_RPORT = 80;
	
	public Receiver(Stub stub){
		this.stub = stub;
	}
}
