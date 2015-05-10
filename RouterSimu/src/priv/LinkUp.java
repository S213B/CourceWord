package priv;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class LinkUp extends Command implements Runnable {
	
	public LinkUp(String[] cmd) {
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
				Node node = Host.neighborList.get(this.name);			//whether a neighbor
				if(node != null) {
					if(Host.routeTable.containsKey(this.name)) {		//whether is dead due to down over 3*timeout seconds
						if(node.isDown()) {								//whether is down
							node.setDown(false);
							if(node.getOriCost() <= node.getCost()) {	//if original is better, update routing table, otherwise ignore
								node.setCost(node.getOriCost());
								node.setNextIP(node.getIP());
								node.setNextPort(node.getPort());
							}
						} else {
							System.out.println("This neighbor has not been down.");
							return;
						}
					} else {											//revive from dead before and update
						Host.routeTable.put(node.getName(), node);
						node.setCost(node.getOriCost());
						node.setNextIP(node.getIP());
						node.setNextPort(node.getPort());
						node.getRT().clear();
					}
					
					//send UP signal
					byte[] header = Command.initHeader(Host.UP,
							Host.getLocalPort(), this.port, Host.getLocalIP(), this.ip);		//initiate header
					DatagramPacket packet = new DatagramPacket(header, header.length, this.ip, this.port);
					try {
						DatagramSocket socket = new DatagramSocket();							//send UP signal
						socket.send(packet);;
						socket.close();
					} catch (SocketException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					new Thread(Host.bellman).start();
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
//		try {
//			InetAddress add = InetAddress.getLocalHost();
//			System.out.println(add);
//			System.out.println(add.equals(InetAddress.getByName("10.241.173.169")));
//		} catch (UnknownHostException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}

}
