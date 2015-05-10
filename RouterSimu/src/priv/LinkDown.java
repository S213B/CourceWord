package priv;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

//Destroy the link with node A

public class LinkDown extends Command implements Runnable {
	
	public LinkDown(String[] cmd) {
		if(cmd.length == 3) {
			this.ip = Command.toIP(cmd[1]);
			this.port = Command.toPort(cmd[2]);
			if(this.ip != null && this.port != null) {
				this.setCmdValid(true);
				this.name = this.ip.getHostAddress() + ':' + this.port;
			} else {
				this.setCmdValid(false);
			}
		} else {
			this.setCmdValid(false);
		}
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		if(this.isCmdValid()) {
			synchronized(Host.rtLock) {
				Node node = Host.neighborList.get(this.name);
				if(node != null && Host.routeTable.containsKey(this.name)) {	//check whether A is neighbor and live
//					HashMap<String, Boolean> hold = new HashMap<String, Boolean>();
//					hold.put(this.name, true);
					node.setCost(Float.MAX_VALUE);
					node.setDown(true);
					node.setChanged(false);
					new Thread(Host.bellman).start();							//update routing table
//					new Thread(new HoldDown(hold)).start();
//					Enumeration<Node> elements = Host.routeTable.elements();
//					while(elements.hasMoreElements()) {							//set cost of the nodes which is connected through A
//						Node ele = elements.nextElement();
//						if(ele.getCost() == Float.MAX_VALUE) {
//							continue;
//						}
//						String nextName = ele.getNextIP().getHostAddress() + ':' + ele.getNextPort();
//						if(nextName.equals(this.name) && !ele.equals(node)) {
//							if(ele.getOriCost() != Float.MAX_VALUE) {			//if they are neighbors, set cost to original cost
//								ele.setCost(ele.getOriCost());
//							} else {
//								ele.setCost(Float.MAX_VALUE);					//if not neighbors, which means cannot connect temporarily
//							}
//						}
//					}
//					node.setCost(Float.MAX_VALUE);
					
					//inform A with DOWN signal
					byte[] header = Command.initHeader((byte) (Host.DOWN),
							Host.getLocalPort(), this.port, Host.getLocalIP(), this.ip);		//initiate header
					
					DatagramPacket packet = new DatagramPacket(header, header.length, this.ip, this.port);			//no content need
					try {
						DatagramSocket socket = new DatagramSocket();
						socket.send(packet);
						socket.close();
					} catch (SocketException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				} else {
					System.out.println("Not a neighbor.");
				}
			}
		} else {
			System.out.println("Command arguments error.");
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		LinkDown l = new LinkDown("linkdown 127.0.0.1 12341".split(" "));
//		System.out.println(l.Host.HEADERLEN);
//		l.initHeader();
//		System.out.println(l.Host.HEADERLEN);
//		System.out.println("OK");
//		Node node;
//		try {
//			node = new Node(InetAddress.getLocalHost(), 12345, 1);
//			System.out.println(node.getCost());
//			node.setCost(Float.MAX_VALUE);
//			System.out.println(node.getCost());
//		} catch (UnknownHostException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}

}
