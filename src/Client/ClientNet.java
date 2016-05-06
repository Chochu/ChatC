package Client;

import java.io.*;
import java.net.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.Callable;

public class ClientNet {
	// debug
	private boolean DEBUG;
	
	// Socket
	private int ServerPort;
	private InetAddress ServerAddress;
	private Socket ClientSocket;
	
	// Thread
	private ServerHandler ServerHandler;
	
	// Data
	protected String Data;
	
	// Debug
	private void print(String dbg) {
		if (DEBUG) {
			System.out.println("[ClientNet]: "+dbg);
		}
	}
	
	// constructor
	public ClientNet(InetAddress addr) {
		DEBUG = true;
		ServerPort = 36801;
		ServerAddress = addr;
	}
	
	public void ChangeAddress(InetAddress addr) {
		if (!ClientSocket.isConnected() || ClientSocket.isClosed()) {
			ServerAddress = addr;
		}
	}
	
	public class DataPoll {
		private Callable<Integer> function;
		private int tval;
		private int fval;
		public DataPoll(Callable<Integer> func, int t, int f) {
			function = func;
			tval = t;
			fval = f;
		}
		public boolean Check() {
			int retval = 0;
			try {
				retval = function.call();
			} catch (Exception e) {e.printStackTrace();}
			return (retval == tval)? true:false;
		}
		public boolean Failed() {
			int retval = 0;
			try {
				retval = function.call();
			} catch (Exception e) {e.printStackTrace();}
			return (retval == fval)? true:false;
		}
	}
	
	public DataPoll Connect_Polling(String uname, String passwd) {
		// connect to server if not yet connected
		if ( ((ClientSocket == null) || (ClientSocket.isClosed())) && 
				((ServerHandler == null) || (!ServerHandler.isAlive())) ) {
			try {
				ClientSocket = new Socket(ServerAddress,ServerPort);
				ClientSocket.setKeepAlive(true);
				print("client connecting to server");
			} catch (UnknownHostException e) {
				print("Unknown host");
			} catch (IOException e) {
				e.printStackTrace();
			}
			print("Client connected");
			ServerHandler = new ServerHandler(ClientSocket,DEBUG);
			ServerHandler.start();
		}
		
		// authenticate
		ServerHandler.Authenticate(uname, passwd);
		return new DataPoll(new Callable<Integer>() {
			public Integer call() {return ServerHandler.getAuthStatus();}
		}, 2, -1);
	}
	
	public int Connect(String uname, String passwd) {
		
		// connect to server if not yet connected
		if ( ((ClientSocket == null) || (ClientSocket.isClosed())) && 
				((ServerHandler == null) || (!ServerHandler.isAlive())) ) {
			try {
				ClientSocket = new Socket(ServerAddress,ServerPort);
				ClientSocket.setKeepAlive(true);
				print("client connecting to server");
			} catch (UnknownHostException e) {
				print("Unknown host");
			} catch (IOException e) {
				e.printStackTrace();
			}
			print("Client connected");
			ServerHandler = new ServerHandler(ClientSocket,DEBUG);
			ServerHandler.start();
		}
		
		// authenticate
		ServerHandler.Authenticate(uname, passwd);
		while (ServerHandler.getAuthStatus() == 1) {
			try {Thread.sleep(1);} catch (InterruptedException e) {}}
		return ServerHandler.getAuthStatus();
	}

	
	// communication
	public void Send(String data, int rindex) {
		// remove trailing spaces and newlines
		data = data.trim().replaceAll("\r?\n", "");
		ServerHandler.Send(data, rindex);
	}
	
	public boolean Ready() {
		try {
			Thread.sleep(1);
		} catch (InterruptedException e) {e.printStackTrace();}
		return ServerHandler.readReady();
	}
	
	public String Receive() {
		if (Ready())
			return ServerHandler.getData();
		else
			return null;
	}
	
	// Server commands
	public HashMap<Integer,String> GetChatRooms() {
		ServerHandler.SendCommand(1,"GetServerRooms");
		while(!ServerHandler.Info.containsKey("RET_STAT")) {
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {e.printStackTrace();}
		}
		System.out.println("Done Wating --------------");
		// check if correct data
//		if (!ServerHandler.Info.containsKey("RET_STAT"))
//			return null;
		String retstat = ServerHandler.Info.get("RET_STAT");
		if (!retstat.split(":",2)[0].equals("GetServerRooms"))
			return null;
		// unserialize
		System.out.println("Done Check*******************");
		HashMap<Integer,String> rooms = new HashMap<Integer,String>();
		retstat = retstat.split(":",2)[1];
		for (String b : retstat.split(";")) {
			rooms.put(Integer.parseInt(b.split(",",2)[0]), b.split(",",2)[1]);
		}
		System.out.println("Done unserialize ---------@@@@");
		return rooms;
	}
	
	public Vector<String> GetUsersOnline(int index) {
		ServerHandler.SendCommand(18, "GETUSERS:"+index);
		while(!Ready()) {
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {e.printStackTrace();}
		}
		// check if correct data
		if (!ServerHandler.Info.containsKey("RET_RSP"))
			return null;
		String retstat = ServerHandler.Info.get("RET_RSP");
		if (!retstat.split(":",2)[0].equals("GetServerRooms"))
			return null;
		// unserialize
		return null;
	}
	
	public void JoinChatRoom(int index) {
		ServerHandler.SendCommand(18, "JOIN:"+index);
	}
	
	public void LeaveChatRoom(int index) {
		ServerHandler.SendCommand(18, "LEAVE:"+index);
	}
	
	// Close connection
	public void CloseConnection() {
		ServerHandler.SendTerminate();
		ServerHandler.Terminate();
		if (ServerHandler.isKilled())
			ServerHandler = null;
	}
}
