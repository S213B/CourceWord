package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import javax.swing.JOptionPane;

public class Connection implements Runnable {	//the thread to deal with connections established from others
	private Socket s;
	
	public Connection(Socket s) {
		this.s = s;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
			NewOutputStreamWriter writer;
			String[] req = br.readLine().split("`");
			if(req[0].equals("73")) {	//getAddress request
				writer = new NewOutputStreamWriter(s.getOutputStream());
				int conf = JOptionPane.showConfirmDialog(null, req[1] + " requests a private conversation. Do you agree?", 
						"Private Conversation Request", JOptionPane.YES_NO_OPTION);
				if(conf == JOptionPane.YES_OPTION) {
					writer.write("y");
				} else {
					writer.write("n");
				}
				writer.close();
			} else if(req[0].equals("6")) {
				System.out.println("Your account login on other device. You are logout now");
				System.exit(0);
			} else if(req[0].equals("0")) {		//message from other client
				System.out.println(req[1] + ": " + req[2]);
			} else if(req[0].equals("-1")) {	//off line message
				do {
					System.out.println("Offline Msg > " + req[1] + "`" + req[2]);
					req = br.readLine().split("`");
				} while(!req[0].equals("213"));
			} else if(req[0].equals("8")) {		//private conversation
				System.out.println("Private " + req[1] + ": " + req[2]);
			} else if(req[0].equals("21")) {
				System.out.println(req[1] + " logs in.");	//login notification
			} else if(req[0].equals("22")) {
				System.out.println(req[1] + " logs out.");	//logout notification
			}
			br.close();
			s.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
