package receiver;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.StringTokenizer;

import debug.SCDebug;
import sender.WebSender;
import server.Stub;
import webCode.HttpHeader;
import webCode.HttpMethod;
import webCode.HttpRequestHandler;

public class WebReceiver extends Receiver {
	protected static final int WEB_RPORT = 80;

	private static final String EOL = "\r\n";
	private static final String HTTP_WEBSOCKET_SEC_KEY = "SEC-WEBSOCKET-KEY:";

	private static final int HTTP_TIMEOUT = 10000;

	private static final String WEB_FILE_LOCATION = "web/";

	private ServerSocket server = null;
	private HttpRequestHandler request = null;

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
		//routingClient(header, client);
	}

//	private String getHTTPRequest(Socket client) {
//		String header = null;
//		try {
//			byte[] byteHeader = new byte[HTTP_HEADER_SIZE];
//			client.getInputStream().read(byteHeader);
//			header = new String(byteHeader);
//			header = header.trim();
//		} catch (IOException e) {
//			SCDebug.DebugMsg("WEBSERVER = CANNOT GET HEADER");
//		}
//		return header;
//	}

	private void routingClient(String header, Socket client) throws IOException {
		String[] tokensHeader = header.split(EOL);
		String fileName = null;
		boolean webSocketFlag = false;
		for (String token : tokensHeader) {
			StringTokenizer tokenizer;
			if (token.length() != 0) {
				tokenizer = new StringTokenizer(token);
				String attr = tokenizer.nextToken();
				attr = attr.toUpperCase();
				if (attr.equals(HttpMethod.GET)) {// Good Request
					fileName = tokenizer.nextToken();
					if (fileName.startsWith("/")) {
						if (fileName.equals("/")) {
							fileName = "SoundCatcher.html";
						} else {
							fileName = fileName.substring(1);
						}
					}
				} else if (attr.equals(HTTP_WEBSOCKET_SEC_KEY)) {
					String key = tokenizer.nextToken();
					webSocketHandShake(key, client);
					clientConnect(client);
					webSocketFlag = true;
				} else {// Bad Request
						// clientWriteData(client, "HTTP/1.1 400 Bad Request");
				}
			} else {
				System.out.println("read Not" + token);
			}
		}
		if (fileName.length() != 0 && !webSocketFlag) {
			httpHandShake(fileName, client);
			client.close();
		}

	}

	private void clientWriteData(Socket client, String data) {
		clientWriteData(client, data.getBytes());
	}

	private void clientWriteData(Socket client, byte[] data) {
		try {
			client.getOutputStream().write(data);
		} catch (IOException e) {
			SCDebug.DebugMsg("WEBSERVER : WRITE ERROR");
		}
	}

	private void httpHandShake(String fileName, Socket client) {
		byte[] data = getSourceData(WEB_FILE_LOCATION + fileName);

		clientWriteData(client, "HTTP/1.1 200 OK" + EOL);
		clientWriteData(client, "Server: SoundCatcher" + EOL);
		if (fileName.endsWith(".jpg")) {
			clientWriteData(client, "Content-type: image/jpeg" + EOL);
		} else if (fileName.endsWith(".png")) {
			clientWriteData(client, "Content-type: image/png" + EOL);
		}

		clientWriteData(client, "Content-length: " + data.length + EOL + EOL);
		clientWriteData(client, data);
	}

	private void webSocketHandShake(String secKey, Socket client) throws IOException {
		try {
			String substr = secKey + HttpHeader.SEC_WEBSOCKET_MAGICKEY;
			byte[] keyEncoded;

			keyEncoded = Base64.getEncoder()
					.encode(MessageDigest.getInstance("SHA-1").digest(substr.getBytes("UTF-8")));

			clientWriteData(client, "HTTP/1.1 101 Switching Protocols" + EOL);
			clientWriteData(client, "Server: SoundCatcher" + EOL);
			clientWriteData(client, "Upgrade: websocket" + EOL);
			clientWriteData(client, "Connection: Upgrade" + EOL);
			clientWriteData(client, "Sec-WebSocket-Accept: ");
			clientWriteData(client, keyEncoded);

			clientWriteData(client, EOL + EOL);

		} catch (Exception e) {
			// TODO: Reject this handshake
		}
	}

	private byte[] getSourceData(String FilePath) {
		File file = new File(FilePath);
		byte[] fileData = new byte[(int) file.length()];
		try {
			FileInputStream in;
			in = new FileInputStream(FilePath);
			in.read(fileData);
			in.close();
		} catch (IOException e) {
			SCDebug.DebugMsg("WEBSERVER : CANNOT FIND FILE");
		}

		return fileData;
	}

	private void clientConnect(Socket client) {
		if (!stub.existClient(client.getInetAddress())) {
			WebSender temp = new WebSender(client, stub);
			stub.createDeliver(temp, client.getInetAddress());
		}
	}

}
