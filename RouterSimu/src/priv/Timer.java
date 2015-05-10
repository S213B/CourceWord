package priv;

import java.util.Enumeration;
import java.util.HashMap;

public class Timer implements Runnable {

	private double to;			//timeout
	private int toCnt;			//timeout count
	
	public Timer() {
		this.to = Host.getTimeout();
		this.toCnt = 0;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(true) {
			try {
				Thread.sleep((long) (to*1000));				//every timeout send routing table
				toCnt++;									//every 3*timeout remove dead routing table record
				if(toCnt == 3) {
					synchronized(Host.rtLock) {
						Enumeration<Node> nodes = Host.neighborList.elements();	//all records in rt
						HashMap<String, Boolean> names = new HashMap<String, Boolean>();
						while(nodes.hasMoreElements()) {
							Node node = nodes.nextElement();
							if(node.isHeartBeat()) {				//whether node is reached or neighbor sent rt
								node.heartBeat(false);
							} else {						//it is disconnected remove it from rt
								if(!(node.isDown() && node.getCost() != Float.MAX_VALUE)) {
									node.setCost(Float.MAX_VALUE);				//set cost to infinite to show neighbor dead
									names.put(node.getName(), true);
									Host.holdList.put(node.getName(), true);
									Host.routeTable.remove(node.getName());		//remove from rt
								}
							}
						}
						if(names.size() != 0) {
							new Thread(new HoldDown(names)).start();;
						}
					}
					new Thread(Host.bellman).start();
					toCnt = 0;
				}
				new Thread(new RTSender()).start();			//broadcast routing table every timeout seconds
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
