package server;

import java.util.Iterator;

public class TimeOut implements Runnable {			//the thread to check heartbeat
	private static long heartbeatInterval = 90;		//default interval
	
	public TimeOut() {
	}
	
	public static void setTimeOut(long t) {
		heartbeatInterval = t;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		Iterator<String> onlineUser = User.onlineList.keySet().iterator();
		while(onlineUser.hasNext()) {
			Server.userList.get(onlineUser.next()).checkHeartbeat();		//check heartbeat of online userss
		}
		try {
			Thread.sleep(heartbeatInterval*1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
