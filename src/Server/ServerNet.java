package Server;

import java.io.*;
import java.net.*;

public class ServerNet implements Runnable {
	private int ID;
	
	private Thread thread;
	private ServerSocket sSocket;
	
	//private String ServerAddress;
	//private InetAddress ServerAddress;
	private int ServerPort;
	
	ServerNet(int threadid, int port) {
		ID = threadid;
		ServerPort = port;
		try {
			sSocket = new ServerSocket(ServerPort);
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public String receive() {
		return "";
	}
	
	public void send(String Data) {
		
	}
	
	public void run() {
		
	}
	
	public void start() {
		
	}
	
	public void stop() {
		
	}
}
