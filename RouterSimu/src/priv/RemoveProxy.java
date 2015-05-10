package priv;

public class RemoveProxy extends Command implements Runnable {
	
	public RemoveProxy(String[] cmd) {
		if(cmd.length == 3) {
			this.ip = Command.toIP(cmd[1]);
			this.port = Command.toPort(cmd[2]);
			if(this.ip != null && this.port != null) {
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
			synchronized(Host.rtLock) {
				Node node = Host.neighborList.get(this.name);					//check whether is neighbor
				if(node != null && Host.routeTable.containsKey(this.name)) {
					if(node.isProxy()) {
						node.setProxy(false);
						node.setProxyIP(null);
						node.setProxyPort(-1);
					} else {
						System.out.println("No proxy exists.");
					}
				} else {
					System.out.println("Not a neighbor. Add proxy failed.");
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
