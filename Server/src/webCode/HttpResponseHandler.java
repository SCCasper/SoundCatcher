package webCode;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;

import debug.SCDebug;
import webCode.HttpStatus.Code;

public class HttpResponseHandler {
	private static final String STATUS_LINE = "HTTP/1.1 %d %s\r\n";
	private static final String HEADER_LINE = "%s: %s\r\n";
	private static final String EOL = "\r\n";
	
	private String HTTPVersion;
	private String URI;
	private int HTTPStatusNum;
	private Code HTTPStatusCode;
	private HashMap<String, String> headers;
	private OutputStream outStream;
	
	public HttpResponseHandler(Socket client) {
		try {
			headers = new HashMap<String, String>();
			outStream = new BufferedOutputStream(client.getOutputStream());
		} catch (IOException e) {
			SCDebug.DebugMsg("HttpResponseHandler : Cannot Get OutputStream");
		}
		SCDebug.DebugMsg("HttpResponseHandler : Start");
		
		//Default Response
		HTTPVersion = HttpVersion.Http11;
		HTTPStatusNum = HttpStatus.OK_200;
		HTTPStatusCode = HttpStatus.getStatusByCode(HttpStatus.OK_200);
	}
	
	public void setHTTPVersion(String HttpVersion){
		HTTPVersion = HttpVersion;
	}
	public String getHttpVersion(){
		return HTTPVersion;
	}
	public int getHttpStatusNum(){
		return HTTPStatusNum;
	}
	
	private void sendResponse(){
		
	}
}
