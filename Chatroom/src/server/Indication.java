package server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

public class Indication implements Runnable {		//the thread send message to clients
	private InetAddress ip;
	private ArrayList<String> offlineMsg;
	private int port, idx;
	private String sender, msg;
	
	public Indication(InetAddress ip, int port, int idx) {
		this.ip = ip;
		this.port = port;
		this.idx = idx;
		this.sender = "";
	}
	
	public Indication(InetAddress ip, int port, int idx, String sender) {
		this.ip = ip;
		this.port = port;
		this.idx = idx;
		this.sender = sender;
	}
	
	public Indication(InetAddress ip, int port, String sender, String msg) {
		this.ip = ip;
		this.port = port;
		this.sender = sender;
		this.msg = msg;
		this.idx = 0;
	}
	
	public Indication(InetAddress ip, int port, ArrayList<String> offlineMsg) {
		this.ip = ip;
		this.port = port;
		this.offlineMsg = offlineMsg;
		this.idx = -1;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			Socket socket = new Socket(ip, port);
			NewOutputStreamWriter writer = new NewOutputStreamWriter(socket.getOutputStream());
			if(idx > 0) {			
				writer.write(idx + "`" + sender);					//send (command, sender) to recipient
			} else if(idx == 0){
				writer.write(idx + "`" + sender + "`" + msg);		//send (command, sender, message) to recipient
			} else {
				for(int i = 0; i < offlineMsg.size(); i++) {
					writer.write(idx + "`" + offlineMsg.get(i));	//send (command, sender, off line message) to recipient
				}
				offlineMsg.clear();				//clear stored off line message
				writer.write("213");
			}
			writer.close();
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			if(!e.getClass().getName().equals("java.net.ConnectException")) {
				e.printStackTrace();
			}
		}
	}

}
