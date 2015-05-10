package priv;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;

//to calculate routing table with Bellman-Ford algorithm

public class BellmanFord implements Runnable {

	@Override
	public void run() {
		// TODO Auto-generated method stub
		synchronized(Host.rtLock) {

			Enumeration<String> names = Host.routeTable.keys();			//clear old routing table and recalculate
			HashMap<String, Boolean> hold = new HashMap<String, Boolean>();
			while(names.hasMoreElements()) {
				String name = names.nextElement();
				Node node = Host.routeTable.get(name);
				if(node.getOriCost() > Integer.MAX_VALUE) {
					Host.routeTable.remove(name);						//if not neighbor, recalculate
					hold.put(node.getName(), true);						//if cannot reach this time, hold it
				} else if(node.getCost() > Integer.MAX_VALUE) {
					Host.routeTable.remove(name);						//if neighbor is unreachable, remove
					if(!Host.holdList.contains(node.getName())) {
						hold.put(node.getName(), true);					//if just down, recalculate
					}
				} else {
					if(node.isChanged()) {
						node.setCost(node.getChCost());
						node.setNextIP(node.getIP());
						node.setNextPort(node.getPort());
					} else if(!node.isDown()) {
						node.setCost(node.getOriCost());				//if neighbor, set to original cost
						node.setNextIP(node.getIP());
						node.setNextPort(node.getPort());
					} else {
						Host.routeTable.remove(name);					//if reachable-down neighbor, recalculate
						hold.put(node.getName(), true);
					}
//					node.setLive(true);									//only receive from neighbor can set neighbor live
				}
			}
			
			Enumeration<Node> neighbors = Host.neighborList.elements();
			while(neighbors.hasMoreElements()) {
				Node neighbor = neighbors.nextElement();				//look up all neighbors and their routing table
				float cost2Neighbor = neighbor.getCost();
				if(cost2Neighbor > Integer.MAX_VALUE || neighbor.isDown()) {
					continue;											//skip if neighbor unreachable
				}
				Enumeration<String> keys = neighbor.getRT().keys();		//look up in this neighbor's routing table
				while(keys.hasMoreElements()) {
					String key = keys.nextElement();					//traversal neighbor's routing table
					float cost2Des = neighbor.getRT().get(key);			//cost to des
					if(cost2Des > Integer.MAX_VALUE) {
						continue;										//skip if unconnected
					} else if(key.equals(Host.getLocalIP().getHostAddress()+':'+Host.getLocalIP())) {
						continue;										//skip if connect to myself
					} else if(Host.holdList.containsKey(key)) {
						continue;										//skip if hold down
					}
					Node des = Host.routeTable.get(key);				//whether des in local rt already
					if(des == null) {									//whether in local routing table
						if(Host.neighborList.containsKey(key)) {		//whether a down neighbor
							des = Host.neighborList.get(key);
							des.setCost(cost2Neighbor+cost2Des);
						} else {
							String[] name = key.split(":");				//not in local routing table
							InetAddress ip = null;						//ip of des
							try {
								ip = InetAddress.getByName(name[0]);
							} catch (UnknownHostException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							int port = Integer.parseInt(name[1]);				//port of des
							des = new Node(ip, port, cost2Neighbor+cost2Des);	//create a new record to des
						}
						des.setNextIP(neighbor.getIP());					//set next hop info
						des.setNextPort(neighbor.getPort());
						Host.routeTable.put(key, des);						//add it in routing table
					} else if(des.getCost() > (cost2Neighbor+cost2Des)){	//in local routing table
						des.setCost(cost2Neighbor+cost2Des);				// but higher cost through old way
						des.setNextIP(neighbor.getIP());
						des.setNextPort(neighbor.getPort());
					}
					hold.remove(des.getName());
				}
			}
			if(hold.size() != 0) {
				Iterator<String> ts = hold.keySet().iterator();
				while(ts.hasNext()) {
					String t = ts.next();
					Host.holdList.put(t, true);
				}
				new Thread(new HoldDown(hold)).start();
			}
			new Thread(new RTSender()).start();
			
//			synchronized(Host.rtLock) {
//				System.out.println((short)System.currentTimeMillis());
//				Enumeration<Node> nodes = Host.routeTable.elements();
//				System.out.println("After Bellman-Ford local DV: ");
//				while(nodes.hasMoreElements()) {
//					Node node = nodes.nextElement();
//					if(Host.neighborList.containsValue(node))
//						System.out.print("*"+node.getName()+" : ");
//					else
//						System.out.print(node.getName()+" : ");
//					Enumeration<String> keys = node.getRT().keys();
//					while(keys.hasMoreElements()) {
//						String key = keys.nextElement();
//						System.out.print(key + ": " + node.getRT().get(key) + " | ");
//					}
//					System.out.println("\n------------------------------------------");
//				}
//				System.out.println("<Current Time>Distance vector list is:");
//	
//				Enumeration<Node> ele = Host.routeTable.elements();
//				while(ele.hasMoreElements()) {
//					Node node = ele.nextElement();
//					if(node.getCost() != Float.MAX_VALUE) {
//						System.out.println("Destination = " + node.getName() + ", Cost = " + node.getCost()
//								+ ", Link = " + node.getNextIP() + ':' + node.getNextPort() + ".");
//					}
//				}
//			}
		}
	}

	public static void main(String[] args) {
		//TODO Auto-generated method stub
//		try {
//			InetAddress a = InetAddress.getLoopbackAddress();
//			System.out.println(a.equals(InetAddress.getByName("127.0.0.1")));
//		} catch (UnknownHostException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//		byte a = (byte) 0xff;
//		System.out.println((int)(a&0xff));
	}

}
