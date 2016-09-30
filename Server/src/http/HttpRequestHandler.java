package http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.Socket;
import java.util.HashMap;

import debug.SCDebug;

public class HttpRequestHandler {
	private String method;
	private String URL;
	private String HttpVersion;
	private HashMap<String, String> headers;
	private String body;

	public HttpRequestHandler() {
		headers = new HashMap<String, String>();
		SCDebug.DebugMsg("HttpRequestHandler : Start");
	}

	public void handle(Socket client) {
		String HttpRequestLine = "";
		try {
			/* Get Client InputStream */
			InputStream is = client.getInputStream();
			Reader reader = new InputStreamReader(is);
			BufferedReader bufferReader = new BufferedReader(reader);

			/* Get Request Line ex) GET / Http/1.1 */
			HttpRequestLine = bufferReader.readLine();
			SCDebug.DebugMsg(HttpRequestLine);
			if (HttpRequestLine == null) {
				SCDebug.DebugMsg("HttpRequestHandler : NO DATA Receive");
				return;
			}

			try {
				String[] HttpRequestTokens = HttpRequestLine.split(" ");
				this.method = HttpRequestTokens[0];
				this.URL = HttpRequestTokens[1];
				this.HttpVersion = HttpRequestTokens[2];
			} catch (Exception e) {
				SCDebug.DebugMsg("HttpRequestHandler : RequestLine Error");
				return;
			}
			/* Get Headers */
			while (bufferReader.ready()) {
				String line = bufferReader.readLine();
				if (line.length() == 0) {
					break;
				}
				SCDebug.DebugMsg(line);
				int separatorPos = line.indexOf(": ");
				String key = line.substring(0, separatorPos);
				String value = line.substring(separatorPos + 2, line.length());
				value.trim();
				headers.put(key, value);
			}
		} catch (IOException e) {
			SCDebug.DebugMsg("HttpRequestHandler : InputStream Error");
		}
	}

	public String getMethod() {
		return method;
	}

	public String getURL() {
		return URL;
	}

	public String getHttpVersion() {
		return HttpVersion;
	}

	public HashMap<String, String> getHeaders() {
		return headers;
	}

	public String getBody() {
		return body;
	}
}
