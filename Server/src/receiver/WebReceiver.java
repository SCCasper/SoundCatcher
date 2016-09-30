package receiver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import debug.SCDebug;
import http.HttpRequestHandler;
import http.HttpResponseHandler;
import sender.WebSender;
import server.Stub;

public class WebReceiver extends Receiver {
	protected static final int WEB_RPORT = 80;

	private static final int HTTP_TIMEOUT = 500;

	private ServerSocket server = null;
	private HttpRequestHandler request = null;
	private HttpResponseHandler response = null;

	public WebReceiver(Stub stub) {
		super(stub);
	}

	@Override
	public void run() {
		initSocket();
		initHandler();
		try {
			SCDebug.DebugMsg("WEBReceiver : RUN");
			while (true) {
				Socket client = this.server.accept();
				client.setSoTimeout(HTTP_TIMEOUT);
				handling(client);
			}
		} catch (IOException e) {
			SCDebug.DebugMsg("WEBReceiver : ERROR");
		}
	}

	private void initSocket() {
		try {
			this.server = new ServerSocket(WEB_RPORT);
			SCDebug.DebugMsg("WEBReceiver : SOCKET OPEN Success");
		} catch (IOException e) {
			SCDebug.DebugMsg("WEBReceiver : SOCKET OPEN ERROR");
		}
	}

	private void initHandler() {
		this.request = new HttpRequestHandler();	// Change to Factory Pattern
		SCDebug.DebugMsg("WEBReceiver : Request Handler OPEN Success");
	}

	private void handling(Socket client) {
		request.handle(client);
		response = new HttpResponseHandler(client);
		response.setURL(request.getURL());
		response.setRequestHeaders(request.getHeaders());
		response.handle();
		if(response.isWebsocket()){
			clientConnect(client);
		}
	}
	
	private void clientConnect(Socket client) {
		if (!stub.existClient(client.getInetAddress())) {
			WebSender temp = new WebSender(client, stub);
			stub.createDeliver(temp, client.getInetAddress());
		}
	}

}
