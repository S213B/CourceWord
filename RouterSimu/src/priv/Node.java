package priv;

import java.net.InetAddress;
import java.util.Hashtable;

public class Node {
	private InetAddress ip, nextIP, proxyIP;
	private	int port, nextPort, proxyPort;
	private float oriCost, cost, chCost, downCost;
	private boolean isDown;
	private boolean isProxy;
	private boolean isChanged;
	private boolean heartBeat;
//	private boolean isLive;
	private Hashtable<String, Float> rt;
	private String name;
	
	public Node(InetAddress ip, int port, float weight) {
		this.ip = this.nextIP = ip;
		this.port = this.nextPort = port;
		this.oriCost = this.chCost = this.downCost = Float.MAX_VALUE;
		this.cost = weight;
		this.isDown = false;
		this.isProxy = false;
		this.isChanged = false;
		this.proxyIP = null;
		this.proxyPort = -1;
		this.heartBeat = false;
		this.rt = new Hashtable<String, Float>();
		this.name = this.ip.getHostAddress()+':'+this.port;
	}
	
	public String getName() {
		return name;
	}

	public Hashtable<String, Float> getRT() {
		return this.rt;
	}
	
	public void setRt(Hashtable<String, Float> rt) {
		this.rt = rt;
	}

	public InetAddress getIP() {
		return ip;
	}

	public void setIP(InetAddress ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public float getCost() {
		return cost;
	}

	public void setCost(float cost) {
		this.cost = cost;
	}

	public boolean isDown() {
		return isDown;
	}

	public void setDown(boolean isDown) {
		this.isDown = isDown;
	}
	

	public boolean isChanged() {
		return isChanged;
	}

	public void setChanged(boolean isChanged) {
		this.isChanged = isChanged;
	}

	public InetAddress getNextIP() {
		return nextIP;
	}
	

	public void setNextIP(InetAddress nextIP) {
		this.nextIP = nextIP;
	}
	

	public int getNextPort() {
		return nextPort;
	}
	

	public void setNextPort(int nextPort) {
		this.nextPort = nextPort;
	}

	public InetAddress getProxyIP() {
		return proxyIP;
	}

	public void setProxyIP(InetAddress proxyIP) {
		this.proxyIP = proxyIP;
	}

	public int getProxyPort() {
		return proxyPort;
	}

	public void setProxyPort(int proxyPort) {
		this.proxyPort = proxyPort;
	}

	public boolean isProxy() {
		return isProxy;
	}

	public void setProxy(boolean isProxy) {
		this.isProxy = isProxy;
	}

	public float getOriCost() {
		return oriCost;
	}

	public void setOriCost(float oriCost) {
		this.oriCost = oriCost;
	}
	
	public float getChCost() {
		return chCost;
	}

	public void setChCost(float chCost) {
		this.chCost = chCost;
	}

	public float getDownCost() {
		return downCost;
	}

	public void setDownCost(float downCost) {
		this.downCost = downCost;
	}

	public boolean isHeartBeat() {
		return heartBeat;
	}
	
	public void heartBeat(boolean beat) {
		this.heartBeat = beat;
	}
	
//	public boolean isLive() {
//		return isLive;
//	}
//
//	public void setLive(boolean isLive) {
//		this.isLive = isLive;
//	}

//	public void downOp() {
//		this.setCost(Float.MAX_VALUE);
//		this.setDown(true);
//	}
}
