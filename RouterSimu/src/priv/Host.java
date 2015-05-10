package priv;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.util.Enumeration;
import java.util.Hashtable;

public class Host {
	public static final byte ROUTETABLE = 0x01;		//routing table signal
	public static final byte FILE = 0x02;			//file signal
	public static final byte END = 0x04;			//file send
	public static final byte ACK = 0x08;			//file reply
	public static final byte DOWN = 0x10;			//routing table
	public static final byte UP = 0x20;				//routing table
	public static final byte CHANGE = 0x40;			//routing table
	public static final byte CORRUPT = (byte) 0x80;		//file reply
	public static final short SEGMENTLEN = 1500-8-20;	//IP header 20, UDP header 8
	public static final byte FILELENUNIT = 8;			//file length unit
	public static final byte HEADERLEN = 24;			//length of header
	public static final Object rtLock = new Object();		//prevent multithread modify local routing table
	public static final BellmanFord bellman = new BellmanFord();
	private static boolean ISCLOSED = false;
	private static int localPort;
	private static InetAddress localIP;
	private static int timeout;
	public static Hashtable<String, Node> routeTable, neighborList;
	public static Hashtable<Long, byte[]> fileChunk;
	public static Hashtable<Long, Integer> fileResendCnt;
	public static Hashtable<Long, String> fileIdx;
	public static Hashtable<String, byte[]> fileReceChunk;
	public static Hashtable<String, Integer> fileReceChunkCnt, fileReceChunkNeed;
	public static Hashtable<String, Boolean> holdList;
	
	public Host(int localPort, int timeout, BufferedReader br) throws IOException {	//initiate routing table
		Host.setLocalPort(localPort);
//		Host.setLocalIP(InetAddress.getLocalHost());
		Host.setLocalIP(InetAddress.getLoopbackAddress());
		Host.setTimeout(timeout);
		Host.routeTable = new Hashtable<String, Node>();
		Host.neighborList = new Hashtable<String, Node>();
		Host.holdList = new Hashtable<String, Boolean>();
		Host.fileChunk = new Hashtable<Long, byte[]>();
		Host.fileIdx =  new Hashtable<Long, String>();
		Host.fileResendCnt = new Hashtable<Long, Integer>();
		Host.fileReceChunk = new Hashtable<String, byte[]>();
		Host.fileReceChunkCnt = new Hashtable<String, Integer>();
		Host.fileReceChunkNeed = new Hashtable<String, Integer>();
		String t;
		for(int i = 0; (t = br.readLine()) != null; i++) {
			String[] ts = t.split(" ");
			String name = ts[0];
			Float weight = Command.toFlt(ts[1]);
			ts = ts[0].split(":");
			InetAddress ip = Command.toIP(ts[0]);
			Integer port = Command.toPort(ts[1]);
			if(ip != null && port != null && weight != null) {
				Node node = new Node(ip, port, weight);
				node.setOriCost(weight);
				Host.neighborList.put(name, node);
				Host.routeTable.put(name, node);
				if(Host.getLocalIP().equals(InetAddress.getLoopbackAddress()) && !ip.equals(Host.getLocalIP())) {
					Host.setLocalIP(InetAddress.getLocalHost());
				}
			} else {
				System.out.println("Line " + (i+1) + " is not valid.");
			}
		}
		
	}
	
	public void start() throws IOException {
		new Thread(new DatagramReceiver()).start();				//start to receive UDP packets
		new Thread(new RTSender()).start();						//send routing table firstly
		new Thread(new Timer()).start();						//start timer to check timeout
		new Thread(new FileSender()).start();   				///start timer to auto send file chunks without ack
		
		System.out.println("Welcom...");
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		while(true) {
			String str = br.readLine();			//read command
			str.toLowerCase();
			String[] cmd = str.split(" ");
			
			switch(cmd[0]) {					//start corresponding thread
				case "linkdown":
					new Thread(new LinkDown(cmd)).start();
					break;
				case "linkup":
					new Thread(new LinkUp(cmd)).start();
					break;
				case "changecost":
					new Thread(new ChangeCost(cmd)).start();
					break;
				case "showrt":
					new Thread(new ShowRT(cmd)).start();
					break;
				case "close":
					new Thread(new Close(cmd)).start();
					break;
				case "transfer":
					new Thread(new Transfer(cmd)).start();
					break;
				case "addproxy":
					new Thread(new AddProxy(cmd)).start();
					break;
				case "removeproxy":
					new Thread(new RemoveProxy(cmd)).start();
					break;

				case "info":
					Enumeration<Node> nodes = Host.routeTable.elements();
					System.out.println("local DV:  (\'*\' means neighbor)");
					while(nodes.hasMoreElements()) {
						Node node = nodes.nextElement();
						if(Host.neighborList.containsValue(node))
							System.out.print("*"+node.getName()+" : ");
						else
							System.out.print(node.getName()+" : ");
						Enumeration<String> keys = node.getRT().keys();
						while(keys.hasMoreElements()) {
							String key = keys.nextElement();
							System.out.print(key + ": " + node.getRT().get(key) + " | ");
						}
						System.out.println("\n------------------------------------------------------------------");
					}
					System.out.println();
					new Thread(new ShowRT(cmd)).start();
					break;
					
				default:
					System.out.println("Unknown Command.");			
					break;
			}
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		if(args.length != 1) {
			System.out.println("Argument erroe. Please specify configuration file.");
			return;
		}
		try {
			File file = new File(args[0]);
			if(!file.exists()) {
				System.out.println("Cannot find config file.");
				return;
			}
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			String firstLine = br.readLine();
			if(firstLine == null) {			// at least one statement in configuration file
				System.out.println("Configuration file error.");
				br.close();
				return;
			}
			String[] firstLines = firstLine.split(" ");
			Integer _localPort = Command.toPort(firstLines[0]);
			Integer _timeout = Command.toNum(firstLines[1]);
			if(_localPort != null && _timeout != null) {
				Host host = new Host(_localPort, _timeout, br);
				host.start();
			} else {
				System.out.println("Line 1: Invalid port number(0-65535) or too large timeout number(< 1 billion)");
				br.close();
				return;
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	}

//	private static void setRouteTable(Hashtable<String, Node> routeTable) {
//		Host.routeTable = routeTable;
//	}

	private static void setLocalPort(int localPort) {
		Host.localPort = localPort;
	}

	private static void setTimeout(int timeout) {
		Host.timeout = timeout;
	}

//	private static void setBroadcastList(Hashtable<String, Node> broadcastList) {
//		Host.neighborList = broadcastList;
//	}

	public static int getLocalPort() {
		return localPort;
	}

	public static InetAddress getLocalIP() {
		return localIP;
	}

	private static void setLocalIP(InetAddress localIP) {
		Host.localIP = localIP;
	}

	public static int getTimeout() {
		return timeout;
	}

	public static boolean getStatus() {
		return Host.ISCLOSED;
	}
	
	public static void setStatus(boolean status) {
		Host.ISCLOSED = status;
	}
}
