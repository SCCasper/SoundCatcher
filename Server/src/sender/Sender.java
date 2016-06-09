package sender;

public abstract class Sender{
	protected static final int UDP_SPORT = 6000;
	protected static final int TCP_SPORT = 12000;
	
	public abstract void sendData(byte[] buffer);	
}
