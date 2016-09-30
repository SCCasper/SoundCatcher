package http;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.HashMap;

import debug.SCDebug;
import http.HttpStatus.Code;

public class HttpResponseHandler {
	private static final String STATUS_LINE = "HTTP/1.1 %d %s\r\n";
	private static final String HEADER_LINE = "%s: %s\r\n";
	private static final String EOL = "\r\n";

	private static final String DEFAULT_LOCATION = "web/";
	private static final String DEFAULT_HTML = "SoundCatcher.html";

	private String HTTPVersion;
	private String URL;
	private int HTTPStatusNum;
	private Code HTTPStatusCode;
	private HashMap<String, String> headers;
	private HashMap<String, String> requestHeaders;
	private OutputStream outStream;
	private PrintWriter printWriter;
	private boolean bodyCheck = true;
	private boolean isWebSocket = false;

	public HttpResponseHandler(Socket client) {
		try {
			headers = new HashMap<String, String>();
			outStream = new BufferedOutputStream(client.getOutputStream());
		} catch (IOException e) {
			SCDebug.DebugMsg("HttpResponseHandler : Cannot Get OutputStream");
		}
		SCDebug.DebugMsg("HttpResponseHandler : Start");

		/* Default File */
		URL = DEFAULT_HTML;

		/* Default Response Set */
		HTTPVersion = HttpVersion.Http11;
		HTTPStatusNum = HttpStatus.OK_200;
		HTTPStatusCode = HttpStatus.getStatusByCode(HttpStatus.OK_200);
		setHeader(HttpHeader.SERVER, "SoundCatcher");
	}

	public void handle() {
		handleWebsocket();
		handleGet();
		handleHeaders();
		handleFile();
		close();
		clear();
	}

	public void setHTTPVersion(String HttpVersion) {
		HTTPVersion = HttpVersion;
	}

	public String getHttpVersion() {
		return HTTPVersion;
	}

	public void setHttpStatusNum(int HTTPStatusNum) {
		this.HTTPStatusNum = HTTPStatusNum;
	}

	public int getHttpStatusNum() {
		return HTTPStatusNum;
	}

	public void setURL(String URL) {
		if (!URL.equals("/")) {
			this.URL = URL;
		}
	}

	public void setHeader(String key, String value) {
		headers.put(key, value);
	}

	public void setRequestHeaders(HashMap<String, String> headers) {
		this.requestHeaders = headers;
	}

	private void handleHeaders() {
		printWriter = new PrintWriter(outStream);

		/* Send StatusLine */
		String StatusLine = String.format(STATUS_LINE, HTTPStatusNum, HTTPStatusCode.getMessage());
		printWriter.print(StatusLine);
		SCDebug.DebugMsg(StatusLine);

		/* Send Headers */
		for (String headerKey : headers.keySet()) {
			String headerLine = String.format(HEADER_LINE, headerKey, headers.get(headerKey));
			printWriter.print(headerLine);
			SCDebug.DebugMsg(headerLine);
		}
		printWriter.print(EOL);
		printWriter.flush();
	}

	private void close() {
		if (!isWebsocket()) {
			printWriter.close();
		}
	}

	private void handleGet() {
		File file = new File(DEFAULT_LOCATION + URL);
		if (!file.exists()) {
			setHttpStatusNum(HttpStatus.NOT_FOUND_404);
			HTTPStatusCode = HttpStatus.getStatusByCode(HttpStatus.NOT_FOUND_404);
			return;
		}
		if (!file.canRead()) {
			setHttpStatusNum(HttpStatus.FORBIDDEN_403);
			HTTPStatusCode = HttpStatus.getStatusByCode(HttpStatus.FORBIDDEN_403);
			return;
		}
		if (file.isFile() && bodyCheck) {
			setHeader(HttpHeader.CONTENT_LENGTH, String.valueOf(file.length()));
			handleContentType(file.getName());
		}
	}

	private void handleContentType(String fileName) {
		if (fileName.endsWith(".js")) {
			setHeader(HttpHeader.CONTENT_SCRIPT_TYPE, "application/javascript");
		} else if (fileName.endsWith(".css")) {
			setHeader(HttpHeader.CONTENT_SCRIPT_TYPE, "text/css");
		} else if (fileName.endsWith(".jpg")) {
			setHeader(HttpHeader.CONTENT_TYPE, "jpg");
		} else if (fileName.endsWith(".png")) {
			setHeader(HttpHeader.CONTENT_TYPE, "png");
		}
	}

	private void handleFile() {
		if (bodyCheck) {
			File file = new File(DEFAULT_LOCATION + URL);
			try {
				FileInputStream fin = null;
				int c;
				fin = new FileInputStream(file);
				while ((c = fin.read()) != -1) {
					outStream.write(c);
				}
				fin.close();
			} catch (IOException e) {
				SCDebug.DebugMsg("HttpResponseHandler : Fail Send to File");
				return;
			}
		}
	}

	private void handleWebsocket() {
		if (requestHeaders.containsKey(HttpHeader.UPGRADE)
				&& requestHeaders.get(HttpHeader.UPGRADE).toLowerCase().equals(HttpHeader.WEBSOCKET)) {

			bodyCheck = false;
			isWebSocket = true;

			/* Websocket Header Add */
			setHttpStatusNum(HttpStatus.SWITCHING_PROTOCOLS_101);
			HTTPStatusCode = HttpStatus.getStatusByCode(HttpStatus.SWITCHING_PROTOCOLS_101);
			headers.put(HttpHeader.UPGRADE, HttpHeader.WEBSOCKET);
			headers.put(HttpHeader.CONNECTION, HttpHeader.UPGRADE);

			/* Get WebSeckey */
			try {
				String secKey = requestHeaders.get(HttpHeader.SEC_WEBSOCKET_KEY) + HttpHeader.SEC_WEBSOCKET_MAGICKEY;
				byte[] keyEncoded = Base64.getEncoder()
						.encode(MessageDigest.getInstance("SHA-1").digest(secKey.getBytes("UTF-8")));
				headers.put(HttpHeader.SEC_WEBSOCKET_ACCEPT, new String(keyEncoded));
			} catch (Exception e) {
				SCDebug.DebugMsg("HttpResponseHandler : Fail to Websocket");
				return;
			}
		}
	}

	public boolean isWebsocket() {
		return isWebSocket;
	}

	private void clear() {
		this.requestHeaders.clear();
		this.headers.clear();
	}

}
