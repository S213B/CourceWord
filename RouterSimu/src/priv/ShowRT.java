package priv;

import java.util.Enumeration;

public class ShowRT extends Command implements Runnable {
	
	public ShowRT(String[] cmd) {
		if(cmd.length == 1) {
			this.setCmdValid(true);
		} else {
			this.setCmdValid(false);
		}
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		if(this.isCmdValid()) {
			System.out.println("<Current Time>Distance vector list is:");
			synchronized(Host.rtLock) {
				Enumeration<Node> ele = Host.routeTable.elements();
				while(ele.hasMoreElements()) {
					Node node = ele.nextElement();
					if(node.getCost() != Float.MAX_VALUE) {
						System.out.println("Destination = " + node.getName() + ", Cost = " + node.getCost()
								+ ", Link = " + node.getNextIP() + ':' + node.getNextPort() + ".");
					}
				}
			}
		} else {
			System.out.println("Command arguments error.");
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
