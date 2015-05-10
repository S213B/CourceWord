package priv;

import java.net.InetAddress;

public class AddProxy extends Command implements Runnable {
	
	private InetAddress proxyIP;
	private Integer proxyPort;
	
	public AddProxy(String[] cmd) {
		if(cmd.length == 5) {
			this.proxyIP = Command.toIP(cmd[1]);
			this.proxyPort = Command.toPort(cmd[2]);
			this.ip = Command.toIP(cmd[3]);
			this.port = Command.toPort(cmd[4]);
			if(this.proxyIP != null && this.proxyPort != null && this.ip != null && this.port != null) {
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
				Node node = Host.neighborList.get(this.name);					//check whether is neighbor
				if(node != null && Host.routeTable.containsKey(this.name) && !node.isDown()) {
					node.setProxy(true);
					node.setProxyIP(proxyIP);
					node.setProxyPort(proxyPort);
				} else {
					System.out.println("Not a valid neighbor. Add proxy failed.");
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
