package server;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class CtrC extends Thread {		//thread handling control+c
	private ServerSocket ss;
	
	public CtrC(ServerSocket ss) {
		this.ss = ss;
	}
	
	public void run() {
		try {
//			BufferedWriter bw = new BufferedWriter(new FileWriter("/resource/userlist.txt"));
//			bw.write(Server.userList.toString());		//writer current user info before exit
//			bw.close();
			Server.userList.toString();
			if(!ss.isClosed()) {		//connect to the listening port to close it
				Socket s = new Socket("127.0.0.1", Server.serverPort);
				ss.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("\nBye-Bye");
	}
}
