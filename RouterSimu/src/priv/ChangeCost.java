package priv;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class ChangeCost extends Command implements Runnable {
	
	private Float cost;
	
	public ChangeCost(String[] cmd) {
		if(cmd.length == 4) {
			this.ip = Command.toIP(cmd[1]);
			this.port = Command.toPort(cmd[2]);
			this.cost = Command.toFlt(cmd[3]);
			if(this.ip != null && this.port != null && this.cost != null) {
				this.setCmdValid(true);
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
			String name = ip.getHostAddress() + ':' + port;
			if(Host.neighborList.containsKey(name) && Host.routeTable.containsKey(name)) {
				synchronized(Host.rtLock) {
					Node node = Host.neighborList.get(name);
					if(node.isDown()){
						System.out.println("Sorry, the link has been down.");
						return;
					}
					node.setCost(cost);											//set new cost
					node.setChCost(cost);
					node.setChanged(true);
				}
				new Thread(Host.bellman).start();								//update routing table
				
				//inform neighbor the new cost
				byte[] header = Command.initHeader(Host.CHANGE, 
						Host.getLocalPort(), this.port, Host.getLocalIP(), this.ip);		//create header
				byte[] content = Command.flt2ByteArr(cost);									//create content
				byte[] buf = new byte[Host.HEADERLEN+content.length];						//create buf sent by socket
				Command.a2a(buf, header, 0);
				Command.a2a(buf, content, Host.HEADERLEN);
				DatagramPacket packet = new DatagramPacket(buf, buf.length, this.ip, this.port);			//no content need
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
		}else {
			System.out.println("Command arguments error.");
		}
	}
}
