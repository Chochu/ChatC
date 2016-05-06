package Client;
//import Client.ClientNet.AuthPoll;
public class ClientGuiListener extends Thread {
	ClientNet cn;
	//AuthPoll poll;
	public ClientGuiListener(ClientNet cnn){
		cn = cnn;
	}
	@Override
	public void run() {
		while(true){ 
			String s1 = cn.Receive();
			
			if(s1 != null){
				System.out.println(s1);
				int getsocketstring = Integer.parseInt(""+s1.charAt(0));
				for(int x = 0; x < ClientGUI.CTPArr.size();x++){
					if( ClientGUI.CTPArr.get(x).chatRoomid == getsocketstring){
						ClientGUI.CTPArr.get(x).addStringtoChat(s1.substring(1));
					}
				}
			}
		}
	}
}
