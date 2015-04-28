package client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;

public class Heartbeat implements Runnable {	//the thread sending heartbeat signal to server
	private InetAddress serverIP;
	private int serverPort;
	private Socket s;
	public static long hbInterval = 30;		//default interval
	
	public Heartbeat (InetAddress serverIP, int serverPort) {
		this.serverIP = serverIP;
		this.serverPort = serverPort;
	}
	
	public static void setHbInterval(long t) {
		hbInterval = t;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(true) {
			if(Client.getStatus()) {
				try {
					s = new Socket(serverIP, serverPort);
					NewOutputStreamWriter writer = new NewOutputStreamWriter(s.getOutputStream());
					BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
					writer.write(9 + "`" + Client.getUserName());
					String resp = br.readLine();
					if(resp.equals("99")) {
						System.out.println("You have been logout automatically. Please login again.");
						Client.setStatus(false);
					}
					writer.close();
					br.close();
					s.close();
					Thread.sleep(hbInterval*1000);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
	}

}
