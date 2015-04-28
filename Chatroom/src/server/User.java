package server;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class User {				//store user info
	private String name;
	private HashMap<String, Boolean> blacklist, blockedBy;
	private boolean online, isHeartbeat, loginBlocked;
	private InetAddress ip;			//IP+port to identify a client in case of multiple clients on one PC
	private int port, password;		//password - hash of password
	private ArrayList<String> offlineMsg;
	private static long loginBlockTime = 60;	
	public static HashMap<String, InetAddress> onlineList = new HashMap<String, InetAddress>();		//online user list
	public static Object onlineListLock = new Object(), offlineMsgLock = new Object();		//lock for multiple thread operating it
	
	public User(String name, int password) {
		this.name = name;
		this.password = password;
		this.online = false;
		this.isHeartbeat = false;
		this.loginBlocked = false;
		this.ip = null;
		this.port = -1;
		this.blacklist = new HashMap<String, Boolean>();
		this.blockedBy = new HashMap<String, Boolean>();
		this.offlineMsg = new ArrayList<String>();
	}
	
	public String getName() {
		return name;
	}
	
	public InetAddress getIP() {
		return ip;
	}
	
	public int getPort() {
		return port;
	}
	
	public void addOfflineMsg(String msg) {
		synchronized(offlineMsgLock) {
			offlineMsg.add(msg);
		}
	}
	
	public synchronized int login(int password, InetAddress ip, int port) {		//synchronized for multiple thread change online status
		if(this.loginBlocked) {				//login has been blocked for some time
			return 3;
		}
		if(this.password == password) {
			if(online){
				loginSucceed(ip, port);		//kick current user off line
				return 2;
			} else {
				loginSucceed(ip, port);		//login success
				return 0;
			}
		} else {							//wrong password
			return 1;
		}
	}

	public synchronized void logout() {
		online = false;
		isHeartbeat = false;
		synchronized(onlineListLock) {
			onlineList.remove(name);	//in case of multiple login and logout thread operation
		}
		notifyPresence(false);			//notify off line presence to all online users 
	}

	private void notifyPresence(boolean b) {
		// TODO Auto-generated method stub
		Iterator<String> onlineUser =  onlineList.keySet().iterator();				//get all online user
		while(onlineUser.hasNext()) {
			String userName = onlineUser.next();
			if(this.blacklist.containsKey(userName) || userName.equals(name)) {		//not notify blocked user and self
				continue;
			}
			User u = Server.userList.get(userName);
			int idx;
			if(b) {
				idx = 21;		//notify online
			} else {
				idx = 22;		//notify off line
			}
			new Thread(new Indication(u.getIP(), u.getPort(), idx, name)).start();
		}
	}

	private void loginSucceed(InetAddress ip, int port) {		//set up initial status
		online = true;
		isHeartbeat = true;
		this.ip = ip;
		this.port = port;
		notifyPresence(true);				//notify online presence to all online users 
		synchronized(onlineListLock) {
			onlineList.put(name, ip);		//synchronized in case of multiple login and logout thread operation
		}
		if(offlineMsg.size() != 0){			//any off line message need to be sent to user
			pushOfflineMsg();
		}
	}
	
	private void pushOfflineMsg() {
		// TODO Auto-generated method stub
		synchronized(offlineMsgLock) {		//synchronized in case of login while adding off line message 
			new Thread(new Indication(ip, port, offlineMsg)).start();	//new thread to send
		}
	}

	public void blockLogin() {		//block login for some time
		loginBlocked = true;
		try {
			Thread.sleep(loginBlockTime*1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		loginBlocked = false;
	}
	
	public HashMap<String, Boolean> getBlacklist() {
		return blacklist;
	}
	
	public void addBlacklist(String name) {
		blacklist.put(name, true);			//no duplicate key in HashMap, no need to check duplicate
	}
	
	public boolean removeBlacklist(String name) {
		if(blacklist.containsKey(name)) {	//check if blocked before
			blacklist.remove(name);
			return true;
		} else {
			return false;
		}
	}
	
	public HashMap<String, Boolean> getBlockedBy() {
		return blockedBy;
	}
	
	public void addBlockedBy(String name) {
		blockedBy.put(name, true);			//no duplicate key in HashMap, no need to check duplicate
	}
	
	public boolean removeBlockedBy(String name) {
		if(blockedBy.containsKey(name)) {	//check if blocked by before
			blockedBy.remove(name);
			return true;
		} else {
			return false;
		}
	}
	
	public void heartbeat() {
		isHeartbeat = true;
	}
	
	public synchronized boolean checkHeartbeat() {
		if(isHeartbeat) {
			isHeartbeat = false;
			return true;
		} else {
			disconnect();		//no heartbeat in past, disconnect
			return false;
		}
	}

	private void disconnect() {
		online = false;
		isHeartbeat = false;
		synchronized(onlineListLock) { 	//synchronized in case of multiple login and logout thread operation
			onlineList.remove(name);
		}
	}
	
	public static void setLoginBlockTime(long t) {
		loginBlockTime = t;
	}

}