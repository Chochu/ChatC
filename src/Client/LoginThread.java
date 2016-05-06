package Client;
//import Client.ClientNet.AuthPoll;
public class LoginThread extends Thread {
	ClientGUI thisClient;
	ClientNet cn;
	//AuthPoll poll;
	public LoginThread(ClientGUI client,ClientNet cnn){
		thisClient = client;
		cn = cnn;
	}
	@Override
	public void run() {
		while(true){ 
			//System.out.println("Running");
			try {
				Thread.sleep(1);
			} catch (InterruptedException e1) {e1.printStackTrace();}
			if(thisClient.loginPane.LoginisClick){
				System.out.println("Button is Clicked");
				//thisClient.loginPane.LoginisClick = false;
//				poll = cn.Connect_Polling(thisClient.loginPane.Username,thisClient.loginPane.HashPassword);
//				System.out.println("Poll created");
//				while(!poll.Check()){
//					try {
//						Thread.sleep(1);
//					} catch (InterruptedException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//				}
				int con_val = cn.Connect(thisClient.loginPane.Username,thisClient.loginPane.HashPassword);
				if (2 == 2){
					System.out.println("Successfully Auth");
					System.out.println("Calling initRoomPane Function");
					return;
					//thisClient.initRoomPane();
				}
			}
		}
	}
}
