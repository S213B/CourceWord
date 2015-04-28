package client;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Receiver implements Runnable {		//the thread listening on a port
	private int clientPort;
	
	public Receiver() {
		this.clientPort = -1;
	}
	
	public int getClientPort() {
		return clientPort;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			ServerSocket ss = new ServerSocket(0);
			clientPort = ss.getLocalPort();
			Socket s;
			while(true) {
				s = ss.accept();
				new Thread(new Connection(s)).start();	 	//dispense the established connection to a thread to handle it and back to listen
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
