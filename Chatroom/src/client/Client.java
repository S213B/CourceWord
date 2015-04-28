package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;

public class Client {	//the client to accept command
	public static final int serverPort = 31213;	//fixed listening port of server
	private InetAddress serverIP;
	private HashMap<String, InetAddress> ip;	//user name and IP address of other clients
	private HashMap<String, Integer> port;		//user name and port of other clients
	private static boolean online = false;		//whether this client is logged in
	private static String userName = null;		//the user name logged on this client
	private Receiver receThread;				//the thread listening on port
	private static final int EQUAL = 0, GREATERTHAN = 1, LESSTHAN = -1;	//for checking parameter number of command

	public Client(InetAddress serverIP) {
		this.serverIP = serverIP;
		this.ip = new HashMap<String, InetAddress>();
		this.port = new HashMap<String, Integer>();
		this.receThread = new Receiver();
	}

	public static String getUserName() {
		return userName;
	}

	public static boolean getStatus() {
		return online;
	}

	public static void setStatus(boolean status) {
		online = status;
	}

	public void start() throws IOException {
		new Thread(receThread).start();		//run a thread to accept respond or message
		new Thread(new Heartbeat(serverIP, serverPort)).start();	//run a thread to send heartbeat signal to server

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		Socket s = null;
		NewOutputStreamWriter writer = null;
		String[] cmd = null;

		while (true) {
			while (true) {
				cmd = br.readLine().split(" ");		//user input command continually
				String pref = cmd[0].toLowerCase();	//command sent as (command, sender, message if need)
				if (pref.equals("login")) {
					if (cmd.length == 1) {
						if (getStatus()) {
							System.out.println("Please logout first.");
						} else {
							System.out.print("Username: ");
							userName = br.readLine();
							System.out.print("Password: ");
							int password = br.readLine().hashCode();	//send hash of password rather than plain text
							s = new Socket(serverIP, serverPort);
							writer = new NewOutputStreamWriter(s.getOutputStream());
							while (receThread.getClientPort() == -1);	//wait for receive thread blind a listening port
							writer.write("0" + "`" + userName + "`" + password
									+ "`" + receThread.getClientPort());	//send (0-login, sender, password, listening port) to server 
							login(s);
						}
						break;
					} else {
						System.out.println("Incorrect Parameter.");
						break;
					}
				} else if (pref.equals("message")) {
					if (checkCmdParaNum(cmd, 2, Client.GREATERTHAN, true)) {
						s = new Socket(serverIP, serverPort);
						writer = new NewOutputStreamWriter(s.getOutputStream());
						String msg = cmd[2];
						for (int i = 3; i < cmd.length; i++) {	//concatenate the message
							msg = msg + " " + cmd[i];
						}
						writer.write(1 + "`" + userName + "`" + cmd[1] + "`" + msg);	//send (1-message, sender, recipient, message) to server
						message(s);
						break;
					}
				} else if (pref.equals("broadcast")) {
					if (checkCmdParaNum(cmd, 1, Client.GREATERTHAN, false)) {
						s = new Socket(serverIP, serverPort);
						writer = new NewOutputStreamWriter(s.getOutputStream());
						String msg = cmd[1];
						for (int i = 2; i < cmd.length; i++) {	//concatenate the message
							msg = msg + " " + cmd[i];
						}
						writer.write(2 + "`" + userName + "`" + msg);	//send (2-broadcast, sender, message) to server
						broadcast(s);
						break;
					}
				} else if (pref.equals("online")) {
					if (checkCmdParaNum(cmd, 1, Client.EQUAL, false)) {
						s = new Socket(serverIP, serverPort);
						writer = new NewOutputStreamWriter(s.getOutputStream());
						writer.write("3" + "`" + userName);		//send (3-online, sender) to server
						online(s);
						break;
					}
				} else if (pref.equals("block")) {
					if (checkCmdParaNum(cmd, 2, Client.EQUAL, true)) {
						s = new Socket(serverIP, serverPort);
						writer = new NewOutputStreamWriter(s.getOutputStream());
						writer.write(4 + "`" + userName + "`" + cmd[1]);	//send (4-block, sender, blocked user name) to server
						block(s);
						break;
					}
				} else if (pref.equals("unblock")) {
					if (checkCmdParaNum(cmd, 2, Client.EQUAL, true)) {
						s = new Socket(serverIP, serverPort);
						writer = new NewOutputStreamWriter(s.getOutputStream());
						writer.write(5 + "`" + userName + "`" + cmd[1]);	//send (5-unblock, sender, unblocked user name) to server
						unblock(s);
						break;
					}
				} else if (pref.equals("logout")) {
					if (checkCmdParaNum(cmd, 1, Client.EQUAL, false)) {
						s = new Socket(serverIP, serverPort);
						writer = new NewOutputStreamWriter(s.getOutputStream());
						writer.write(6 + "`" + userName);	//send (6-logout, sender) to server
						writer.close();
						s.close();
						logout();
						break;
					}
				} else if (pref.equals("getaddress")) {
					if (checkCmdParaNum(cmd, 2, Client.EQUAL, true)) {
						s = new Socket(serverIP, serverPort);
						writer = new NewOutputStreamWriter(s.getOutputStream());
						writer.write(7 + "`" + userName + "`" + cmd[1]);	//send (7-getAddress, sender, user name) to server
						getAddress(s, cmd[1]);
						break;
					}
				} else if (pref.equals("private")) {
					if (checkCmdParaNum(cmd, 2, Client.GREATERTHAN, true)) {
						if (!ip.containsKey(cmd[1])) {
							System.out.println("You should get address of " + cmd[1] + " first."); //check if got address of recipient
						} else {
							s = new Socket(serverIP, serverPort);
							writer = new NewOutputStreamWriter(
									s.getOutputStream());
							String msg = cmd[2];
							for (int i = 3; i < cmd.length; i++) { //concatenate message
								msg = msg + " " + cmd[i];
							}
							priv(cmd[1], msg, writer);
						}
						break;
					}
				} else {
					System.out.println("Unknown Command.");
					break;
				}
				if (!getStatus()) {
					System.out.println("Please login first.");
				} else {
					System.out.println("Incorrect Parameter.");
				}
			}
			if(writer != null) {
				writer.close();
			}
			if(s != null) {
				s.close();
			}
		}
	}

	private void priv(String receName, String msg, NewOutputStreamWriter serverWriter) {
		// TODO Auto-generated method stub
		Socket privS;
		try {
			privS = new Socket(ip.get(receName), port.get(receName));
			NewOutputStreamWriter writer = new NewOutputStreamWriter(
					privS.getOutputStream());
			writer.write(8 + "`" + userName + "`" + msg);	//send message to other client
			writer.close();
		} catch (ConnectException e) {	//if cannot connect to recipient, send the off line message to server
			System.out.println("The recipient is offline now. Leave as an offline message.");
			try {
				serverWriter.write("8" + "`" + userName + "`" + receName + "`"
						+ msg);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void getAddress(Socket s, String reqUser) throws IOException {
		// TODO Auto-generated method stub
		BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
		String resp = br.readLine();
		if (resp.equals("99")) {
			System.out.println("You have been logout automatically. Please login again.");
			setStatus(false);
		} else if (resp.equals("71")) {
			System.out.println(reqUser + " is not online now.");
		} else if (resp.equals("72")) {
			System.out.println("You have been blocked by the recipient.");
		} else if (resp.split("`")[0].equals("74")) {
			String _ip = resp.split("`")[1].split("/")[1];
			System.out.println(reqUser + "\'s IP address: " + _ip);
			ip.put(reqUser, InetAddress.getByName(_ip));
			String _port = resp.split("`")[2];
			System.out.println(reqUser + "\'s port: " + _port);
			port.put(reqUser, new Integer(_port));
		} else {
			System.out.println(reqUser + " denied your private conversation.");
		}
	}

	private void logout() {
		// TODO Auto-generated method stub
		System.out.println("Bye-Bye");
		System.exit(0);
		return;
	}

	private void unblock(Socket s) throws IOException {
		// TODO Auto-generated method stub
		BufferedReader br = new BufferedReader(new InputStreamReader(
				s.getInputStream()));
		String resp = br.readLine();
		if (resp.equals("99")) {
			System.out.println("You have been logout automatically. Please login again.");
			setStatus(false);
		} else if (resp.split("`")[0].equals("51")) {
			System.out.println(resp.split("`")[1] + " is unblocked.");
		} else if (resp.split("`")[0].equals("52")) {
			System.out.println(resp.split("`")[1] + " has not been blocked.");
		} else if (resp.split("`")[0].equals("53")) {
			System.out.println("Cannot find " + resp.split("`")[1] + ".");	//user does not exist
		}
	}

	private void block(Socket s) throws IOException {
		// TODO Auto-generated method stub
		BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
		String resp = br.readLine();
		if (resp.equals("99")) {
			System.out.println("You have been logout automatically. Please login again.");
			setStatus(false);
		} else if (resp.split("`")[0].equals("41")) {
			System.out.println(resp.split("`")[1] + " have been blocked.");
		} else if (resp.split("`")[0].equals("42")) {
			System.out.println("Cannot find " + resp.split("`")[1] + ".");	//user does not exist
		}
	}

	private void online(Socket s) throws IOException {
		// TODO Auto-generated method stub
		BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
		String resp = br.readLine();
		while (resp.split("`")[0].equals("31")) {	//print all online user
			System.out.println(resp.split("`")[1]);
			resp = br.readLine();
		}
		if (resp.equals("99")) {
			System.out.println("You have been logout automatically. Please login again.");
			setStatus(false);
		}
	}

	private void broadcast(Socket s) throws IOException {
		// TODO Auto-generated method stub
		BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
		String resp = br.readLine();
		if (resp.equals("99")) {
			System.out.println("You have been logout automatically. Please login again.");
			setStatus(false);
		} else if (resp.equals("21")) {
			System.out.println("You have been blocked by some recipients.");
		}
	}

	private void message(Socket s) throws IOException {
		// TODO Auto-generated method stub
		BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
		String resp = br.readLine();
		if (resp.equals("99")) {
			System.out.println("You have been logout automatically. Please login again.");
			setStatus(false);
		} else if (resp.equals("11")) {
			System.out.println("You have been blocked by the recipient.");
		} else if (resp.equals("12")) {
			System.out.println("Cannot find the recipient you entered.");
		} else if (resp.equals("13")) {
			System.out.println("The recipient is offline now. Leave as an offline message.");
		}
	}

	private void login(Socket s) throws IOException {
		// TODO Auto-generated method stub
		NewOutputStreamWriter writer = new NewOutputStreamWriter(s.getOutputStream());
		BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
		BufferedReader si = new BufferedReader(new InputStreamReader(System.in));
		String resp = br.readLine();
		while (resp.equals("4")) {
			System.out.println("Incorrect username or password.");
			System.out.print("Username: ");
			userName = si.readLine();
			System.out.print("Password: ");
			int password = si.readLine().hashCode();
			writer.write("0" + "`" + userName + "`" + password + "`" + receThread.getClientPort());
			resp = br.readLine();
		}
		if (resp.equals("3")) {
			System.out.println("Welcome...");
			setStatus(true);
		} else if (resp.equals("7")) {	//login same account before
			System.out.println("Welcome back...");
			setStatus(true);
		} else if (resp.equals("5")) {
			System.out.println("Your account has been blocked. Please try again later.");
		}
		writer.close();
		br.close();
	}
	
	private boolean checkCmdParaNum(String[] cmd, int cnt, int check, boolean notSelf) {	//check parameter number of command
		// TODO Auto-generated method stub
		if (!getStatus()) {
			return false;
		}
		
		if(notSelf) {
			return !cmd[1].equals(userName);
		}

		if (check == 0) {
			return cmd.length == cnt;
		} else if (check > 0) {
			return cmd.length > cnt;
		} else {
			return cmd.length < cnt;
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		if (args.length == 1 || args.length == 2) {		//first argument - server IP address
			try {										//second argument - heartbeat interval, optional
				InetAddress serverIP = InetAddress.getByName(args[0]);
				System.out.println("Server IP address: "
						+ serverIP.getHostAddress());
				Client c = new Client(serverIP);
				c.start();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (args.length == 2) {
				Heartbeat.setHbInterval(Long.valueOf(args[1]));
			}
		} else {
			System.out.println("Please enter server IP address [heartbeat interval in second].");
		}
	}

}
