package main;

import debug.SCDebug;
import server.Server;

public class Main {

	public static void main(String[] args) {
		SCDebug.setDebug(true);
		Server server = new Server();
		server.run();
	}

}
