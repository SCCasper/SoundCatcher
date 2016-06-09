package debug;

import java.util.Vector;

public class SCDebug {
	private static SCDebug instance = new SCDebug();
	private static boolean debug = false;
	private static Vector<String> trace = new Vector<String>();
	
	private SCDebug(){
		
	}
	
	public synchronized static SCDebug getInstance(){
		return instance;
	}
	
	public static void DebugMsg(String msg){
		if(debug)
			System.out.println(msg);
	}
	public static void setDebug(boolean toggle){
		debug = toggle;
	}
	
	public static void putLog(String string) {
		trace.add(string);
	}
	
	public static void printLog() {
		int traceCount = trace.size();
		for(int i = 0; i < traceCount; i++) {
			System.out.println(trace.remove(0));
		}
	}
	
}

