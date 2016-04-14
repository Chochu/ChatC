package Server;

import Global.Packet;
import Global.Packet.PacketData;

public enum Command {
	DEFAULT(-1) {
		public void run(Object[] args) {
			// Unknown command
		}
	},
	PING(0) {
		public void run(Object[] args) {
			// Ping packet. send travel time back.
		}
	},
	REQ_STAT(1) {
		public void run(Object[] args) {// handle client request for server stat
			String data = ((PacketData) args[1]).Data();
			String token = ((ClientHandler) args[0]).getToken();
			PacketData pdata = (PacketData) args[1];
			ClientHandler client = (ClientHandler) args[0];
			// check packet headers for security
			if ((pdata.DataType() != 0) && (pdata.Command() != 1)) {
				// error
				return;
			}
			// extract AuthToken
			int aulen = (int)data.charAt(0);
			String autoken = data.substring(1, aulen+1);

			// check authtoken
			if (!autoken.equals(token)) {
				((ClientHandler) args[0]).Error_InvalidAuthToken(autoken+" != "+token);
				return;
			}
			// check request
			switch(data.substring(aulen+1)) {
			case "GetServerRooms": { // client asks for list of chatrooms
				// serialize ChatRooms
				String serval = "GetServerRooms;";
				for (int i = 0; i < Main.ChatRooms.size(); i++) {
					String element = "" + i + "," + (String) Main.ChatRooms.get(i).get("Room Name") + ";";
					serval += element;
				}
				// send return status packet containing the data
				client.SendCommand(15,serval);
				break;
			}
			default: {
				break;
			}
			}
		}
	},
	REQ_CON(2) {
		public void run(Object[] args) {
			// handle client request for connection
			// TODO authenticate user && send DEC_CON if declined
			// TODO send ACC_CON w/ AuthToken
			ClientHandler client = ((ClientHandler) args[0]);
			String data = ((PacketData) args[1]).Data();
			String token = client.getToken();
			
			// extract username and password
			//String uname = data.split(".")[0];
			//String passwd = data.split(".")[1];
			
			// search database
			
			// if auth then send ACC_CON
			// else send DEC_CON & terminate thread
			client.SendCommand(4, token); // debug
			Main.AddMember(0, token); // debug
		}
	},
	// 3,4 sent by server never received
	ERR_CON(5) {
		public void run(Object[] args) {
			
		}
	},
	ACK(6) {
		public void run(Object[] args) {
			
		}
	},
	TERM_CON(9) {
		public void run(Object[] args) {
			// handle client request to terminate connection
		}
	},
	DATA(14) {
		public void run(Object[] args) { // handle data received from client
			String data = ((PacketData) args[1]).Data();
			String token = ((ClientHandler) args[0]).getToken();
			
			// extract AuthToken
			int aulen = (int)data.charAt(0);
			String autoken = data.substring(1, aulen+1);
			
			// check authtoken
			if (!autoken.equals(token)) {
				((ClientHandler) args[0]).Error_InvalidAuthToken(autoken+" != "+token);
				return;
			}
			
			// extract room number from data
			int rindex = (int)data.charAt(aulen+1); // range 0-255 encoded in ascii
			
			// broadcast
			Main.Broadcast(rindex, data.substring(aulen+2), token);
		}
	},
	RET_STAT(15) {
		public void run(Object[] args) {
			// handle received status
		}
	}
	;
	
	public abstract void run(Object[] args);
	
	private int num;
	Command(int n) {this.num = n;}
	
	public static Command get(int n) {
		for (Command c : values()) {
			if (n == c.num) {
				return c;
			}
		}
		return DEFAULT;
	}
}
