package sender;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import debug.SCDebug;
import server.AudioBuffer;

public class UDPSender extends Sender {
	private DatagramSocket sendSocket;
	private DatagramPacket sendPacket;
	
	public void setAddress(InetAddress dest) {
		try {
			sendSocket = new DatagramSocket();
			sendPacket = new DatagramPacket(new byte[AudioBuffer.AUDIO_BUFFER_SIZE], AudioBuffer.AUDIO_BUFFER_SIZE,
					dest, UDP_SPORT);
		} catch (IOException e) {
		}
	}

	@Override
	public void sendData(byte[] buffer) {		
		try {
			sendPacket.setData(buffer, 0, buffer.length);
			sendSocket.send(sendPacket);
			SCDebug.DebugMsg("UDPSENDER : SEND");			
		} catch (IOException e) {
			SCDebug.DebugMsg("UDPSENDER : SEND ERROR");
		}
	}

}
