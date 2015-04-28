package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Iterator;

public class Connection implements Runnable {	//thread dealing with established connection from client
	private Socket s;
	private BufferedReader br;
	private String[] cmd;

	public Connection(Socket s) {
		this.s = s;
		this.br = null;
		this.cmd = null;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			br = new BufferedReader(new InputStreamReader(s.getInputStream()));
			String str = br.readLine();
			if(str != null) {
				cmd = str.split("`");
				int idx = new Integer(cmd[0]);
				switch(idx) {
					case 0:
						login(cmd);
						break;
					case 1:
						message(cmd);
						break;
					case 2:
						broadcast(cmd);
						break;
					case 3:
						online(cmd);
						break;
					case 4:
						block(cmd);
						break;
					case 5:
						unblock(cmd);
						break;
					case 6:
						logout(cmd);
						break;
					case 7:
						getAddress(cmd);
						break;
					case 8:
						leaveOffline(cmd);
						break;
					case 9:
						heartbeat(cmd);
						break;
				}
			}
			br.close();
			s.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void leaveOffline(String[] cmd) throws IOException {
		// TODO Auto-generated method stub
		NewOutputStreamWriter writer = new NewOutputStreamWriter(s.getOutputStream());
		String senderName = cmd[1];
		String receName = cmd[2];
		String msg = cmd[3];
		if(senderName == null) {
			writer.write("99");			//sender is off line.
		} else {
			Server.userList.get(receName).logout();
			Server.userList.get(receName).addOfflineMsg(senderName + ": " + msg);
			writer.write("213");
		}
		writer.close();
	}

	private void heartbeat(String[] cmd) throws IOException {
		// TODO Auto-generated method stub
		NewOutputStreamWriter writer = new NewOutputStreamWriter(s.getOutputStream());
		String senderName = cmd[1];
		if(senderName == null) {
			writer.write("99");
		} else {
			writer.write("213");
			User sender = Server.userList.get(senderName);
			sender.heartbeat();
		}
		writer.close();
	}

	private void getAddress(String[] cmd) throws IOException {
		// TODO Auto-generated method stub
		NewOutputStreamWriter writer = new NewOutputStreamWriter(s.getOutputStream());
		String senderName = cmd[1];
		String receName = cmd[2];
		if(senderName == null) {
			writer.write("99");
		} else {
			if(!User.onlineList.containsKey(receName)) {
				writer.write("71");		//recipient is not online
			} else {
				User sender = Server.userList.get(senderName);
				if(sender.getBlockedBy().containsKey(receName)) {
					writer.write("72");		//blocked by recipient
				} else {
					User rece = Server.userList.get(receName);		//need agreement of recipient first
					Socket socket = new Socket(rece.getIP(), rece.getPort());
					NewOutputStreamWriter confReq = new NewOutputStreamWriter(socket.getOutputStream());
					BufferedReader confBack = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					confReq.write(73 + "`" + senderName);
					String resp = confBack.readLine();
					if(resp.equals("y")) {
						writer.write(74 + "`" + rece.getIP().toString() + "`" + rece.getPort());
					} else {
						writer.write("75");
					}
					confReq.close();
					confBack.close();
				}
			}
		}
		writer.close();
	}

	private void logout(String[] cmd) {
		// TODO Auto-generated method stub
		String senderName = cmd[1];
		User sender = Server.userList.get(senderName);
		sender.logout();
	}

	private void unblock(String[] cmd) throws IOException {
		// TODO Auto-generated method stub
		NewOutputStreamWriter writer = new NewOutputStreamWriter(s.getOutputStream());
		String senderName = cmd[1];
		String unblockedName = cmd[2];
		if(senderName == null) {
			writer.write("99");
		} else {
			if(Server.userList.containsKey(unblockedName)) {	//check unblock user exist
				if(Server.userList.get(senderName).removeBlacklist(unblockedName)) {	//check unblock user has been blocked before
					Server.userList.get(unblockedName).removeBlockedBy(senderName);
					writer.write(51 + "`" + unblockedName);
				} else {
					writer.write(52 + "`" + unblockedName);
				}
			} else {
				writer.write(53 + "`" + unblockedName);
			}
		}
		writer.close();
	}

	private void block(String[] cmd) throws IOException {
		// TODO Auto-generated method stub
		NewOutputStreamWriter writer = new NewOutputStreamWriter(s.getOutputStream());
		String senderName = cmd[1];
		String blockedName = cmd[2];
		if(senderName == null) {
			writer.write("99");
		} else {
			if(Server.userList.containsKey(blockedName)) {		//check unblock user exist
				Server.userList.get(senderName).addBlacklist(blockedName);
				Server.userList.get(blockedName).addBlockedBy(senderName);
				writer.write(41 + "`" + blockedName);
			} else {
				writer.write(42 + "`" + blockedName);
			}
		}
		writer.close();
	}

	private void online(String[] cmd) throws IOException {
		// TODO Auto-generated method stub
		NewOutputStreamWriter writer = new NewOutputStreamWriter(s.getOutputStream());
		String senderName = cmd[1];
		if(senderName == null) {
			writer.write("99");
		} else {
			User sender = Server.userList.get(senderName);
			Iterator<String> onlineUser = User.onlineList.keySet().iterator();	//send all online users back
			while(onlineUser.hasNext()) {
				String name = onlineUser.next();
				if(!name.equals(senderName) && !sender.getBlockedBy().containsKey(name)) {
					writer.write(31 + "`" + name);
				}
			}
			writer.write("213");		//send "213" as end mark
		}
		writer.close();
	}

	private void broadcast(String[] cmd) throws IOException {
		// TODO Auto-generated method stub
		NewOutputStreamWriter writer = new NewOutputStreamWriter(s.getOutputStream());
		String senderName = cmd[1];
		String msg = cmd[2];
		if(senderName == null) {
			writer.write("99");
		} else {
			User sender = Server.userList.get(senderName);
			Iterator<String> onlineUser = User.onlineList.keySet().iterator();	//send message to all online users
			boolean isBlocked = false;											//if blocked by any online user
			while(onlineUser.hasNext()) {
				String receName = onlineUser.next();
				if(sender.getBlockedBy().containsKey(receName)) {				//check if blocked by anyone
					isBlocked = true;
				} else if(!receName.equals(senderName)){
					User rece = Server.userList.get(receName);					//not send to self
					new Thread(new Indication(rece.getIP(), rece.getPort(), senderName, msg)).start();	//a thread to send message
				}
			}
			if(isBlocked) {
				writer.write("21");		//blocked by some online user
			} else {
				writer.write("213");
			}
		}
		writer.close();
	}

	private void message(String[] cmd) throws IOException {
		// TODO Auto-generated method stub
		NewOutputStreamWriter writer = new NewOutputStreamWriter(s.getOutputStream());
		String senderName = cmd[1];
		String receName = cmd[2];
		String msg = cmd[3];
		if(senderName == null) {
			writer.write("99");
		} else {
			User sender = Server.userList.get(senderName);
			if(sender.getBlockedBy().containsKey(receName)) {	//check if blocked by recipient
				writer.write("11");
			} else if(!Server.userList.containsKey(receName)) {	//check if recipient exists
				writer.write("12");
			} else {
				if(!User.onlineList.containsKey(receName)) {	//check if recipient online, if not leave an off line message
					Server.userList.get(receName).addOfflineMsg(senderName + ": " + msg);
					writer.write("13");
				} else {
					User rece = Server.userList.get(receName);
					InetAddress receIP = rece.getIP();
					int recePort = rece.getPort();
					new Thread(new Indication(receIP, recePort, senderName, msg)).start();	//a thread to send message
				}
				writer.write("213");
			}
		}
		writer.close();
	}

	private void login(String[] cmd) throws IOException {
		// TODO Auto-generated method stub
		NewOutputStreamWriter writer = new NewOutputStreamWriter(s.getOutputStream());
		User u = null;
		
		for(int i = 0; i < 3; i++) {	//three times to login
			String name = cmd[1];
			int password = new Integer(cmd[2]);
			int port = new Integer(cmd[3]);
			
			if(!Server.userList.containsKey(name)) {	//wrong user name
				if(i != 2) {							//not accept login request after third time
					writer.write("4");
					cmd = br.readLine().split("`");
					continue;
				}
			} else {
				u = Server.userList.get(name);
				InetAddress preIP = u.getIP();
				int prePort = u.getPort();
				switch(u.login(password, s.getInetAddress(), port)) {
					case 0:
						writer.write("3");					//login success 
						writer.close();
						return;
					case 1:
						if(i != 2) {
							writer.write("4");				//wrong password
							cmd = br.readLine().split("`");
						}
						break;
					case 2:
						if(s.getInetAddress().equals(preIP) && prePort == port) {	//IP+port to identify client
							writer.write("7");				//login same account before
						} else {
							writer.write("3"); 				//login success
							new Thread(new Indication(preIP, prePort, 6)).start(); //notify current user logout
						}
						writer.close();
						return;
					case 3:
						writer.write("5");					//login has been blocked
						writer.close();
						return;
				}
			}
		}
		writer.write("5");		//block login due to three times login failure
		writer.close();
		if(u != null) {
			u.blockLogin();		//block login
		}
	}
}
