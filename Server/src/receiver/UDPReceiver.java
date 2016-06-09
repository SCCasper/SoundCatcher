package receiver;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.InetAddress;
import java.net.UnknownHostException;


import debug.SCDebug;
import sender.UDPSender;
import server.Stub;

public class UDPReceiver extends Receiver {
	DatagramSocket receiveSocket = null;
	DatagramPacket receivePacket = null;
	
	public UDPReceiver(Stub stub) {
		super(stub);
	}

	@Override
	public void run() {
		try {
			receiveSocket = new DatagramSocket(UDP_RPORT);
			receiveSocket.setReceiveBufferSize(message.length);
			receivePacket = new DatagramPacket(message, message.length);
		} catch (SocketException e) {
			SCDebug.DebugMsg("UDPRECEIVER : SOCKET OPEN ERROR");
		}	
		while (true) {
			try {
				SCDebug.DebugMsg("UDPRECEIVER : RUN");
				while (true) {
					SCDebug.DebugMsg("UDPRECEIVER : LISTENING");
					receiveSocket.receive(receivePacket);
					message = receivePacket.getData();
					switch(new String(message)) {
						case "CONN" :
							SCDebug.DebugMsg("Client Connect");
							clientConnect(receivePacket.getAddress());
							break;
						case "EXIT" :
							SCDebug.DebugMsg("Client Exit");
							clientExit(receivePacket.getAddress());
							break;
						default:
							break;
					}
					
				}
			} catch (IOException e) {
				SCDebug.DebugMsg("UDPRECEIVER : SOCKET ERROR");
			} finally {
				receiveSocket.close();
			}
		}
	}
	private void clientConnect(InetAddress address) {
		if(!stub.existClient(address)) {
			SCDebug.DebugMsg("UDPRECEIVER : NEW CLIENT");
			UDPSender temp = new UDPSender();
			temp.setAddress(address);
			stub.createDeliver(temp, address);
		}
	}
	private void clientExit(InetAddress address) {
		if(stub.existClient(address)) {
			stub.removeDeliver(address);
		}
	}
}
