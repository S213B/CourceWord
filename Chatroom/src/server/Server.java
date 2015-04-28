package server;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class Server {
	public static HashMap<String,User> userList = new HashMap<String, User>();	//all user info
	private ServerSocket ss;
	private Socket s;
	public static final int serverPort = 31213;		//fixed listening port of server
	
	public Server() {
		try {
			ss = new ServerSocket(serverPort);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		initUserInfo();								//read user name and password from file and initiate
		Runtime.getRuntime().addShutdownHook(new CtrC(ss));		//hook for control+c
	}
	
	private void initUserInfo() {
		try {
			BufferedReader bf = new BufferedReader(new FileReader("resource/credentials.txt"));
			String tempString;
			while((tempString = bf.readLine()) != null) {
				String[] info = tempString.split(" ");
				userList.put(info[0], new User(info[0], info[1].hashCode()));	//store user name and password hash in userList
			}
			bf.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void start() {
		new Thread(new TimeOut()).start();		//run thread to check heartbeat continually
		
		while(true) {
			try {
				s = ss.accept();				//listening on port
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Connection c = new Connection(s);	//once connection established, run a thread to handle it and back to listen on port
			new Thread(c).start();
		}
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		if(args.length == 1) {
			Long loginBlockInterval = Long.valueOf(args[0]);		//first argument - login blocked time, optional
			User.setLoginBlockTime(loginBlockInterval);
			if(args.length == 2) {
				Long heartbeatInterval = Long.valueOf(args[1]);		//second argument - heartbeat check interval, optional
				TimeOut.setTimeOut(heartbeatInterval);
			}
		}
		
		Server s = new Server();
		s.start();
	}

}
