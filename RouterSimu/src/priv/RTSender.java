package priv;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Enumeration;

//header 24 bytes:
//1 byte type : ROUTETABLE, FILE, END, ACK, DOWN, UP, CHANGE
//1 byte length: length of content, unit is 10 bytes(1 routing table tuple)
//2 byte local port: sender port #
//2 byte receiver port: receiver port #
//4 byte local ip: sender ip
//4 byte receiver ip: receiver ip
//4 byte conversation: to identify in multiple connections between two nodes
//4 byte sequence: to identify order and arrival in multiple segments
//2 byte checksum: XOR all data in header+content
//content 10 bytes per tuple (routing table):
//4 byte ip
//2 byte port
//4 byte cost with poison reverse

public class RTSender implements Runnable {
	
	private byte type;
	private short len;					//used content length, count of rows in content
//	private int localPort;				//sender port
//	private int recePort;				//receiver port
//	private InetAddress localIP;		//sender ip
//	private InetAddress receIP;			//receiver ip
//	private int conversation;			//use current time to identify conversation between two nodes
	private int sequence;				//identify segment in same conversation
//	private short checksum;
	private byte[] buf, header;			//buf = header + content
	private byte[][] content;			//routing table, content = ip + port + cost
	private DatagramPacket packet;
	private DatagramSocket socket;
	
	public RTSender() {
		this.type = Host.ROUTETABLE;
		this.len = 0;							//# of routing table tuples
//		this.localPort = Host.getLocalPort();
//		this.conversation = -1;					//initiate when send
		this.sequence = 0;
//		this.checksum = 0;
		this.header = null;
		this.buf = new byte[Host.SEGMENTLEN];
		this.content = new byte[(buf.length-Host.HEADERLEN)/10][10];		//ip_4+cost_4+port_2 = 10
//		try {
//			this.socket = new DatagramSocket();
//		} catch (SocketException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		synchronized(Host.rtLock) {
			Enumeration<Node> neighbors = Host.neighborList.elements();			//all neighbors and send routing table
			while(neighbors.hasMoreElements()) {
				Node rece = neighbors.nextElement();							//the receiver
				if(rece.getCost() > Integer.MAX_VALUE || rece.isDown()) {							//if receiver unreachable
					continue;
				}
				Enumeration<Node> rtElements = Host.routeTable.elements();		//routing table elements
				len = 0;
				for(int offset = 0; len < content.length && rtElements.hasMoreElements(); offset = 0) {	// protocol content
					Node des = rtElements.nextElement();						//one routing table record
					
					byte[] ip = des.getIP().getAddress();						//des ip
					Command.a2a(this.content[len], ip, offset);
					offset += ip.length;
					
					byte[] port = Command.short2ByteArr(des.getPort());			//des port
					Command.a2a(this.content[len], port, offset);
					offset += port.length;
					
					float fltCost;												//cost to des
					if(des.getNextIP().equals(rece.getIP()) && des.getNextPort() == rece.getPort()) {
						continue;												//receiver is next hop 2 des, poison reverse
					} else if(des.getCost() > Integer.MAX_VALUE) {
						continue;												//cannot get to des
					} else if(des.getIP().equals(rece.getIP()) && des.getPort() == rece.getPort()) {
						continue;												//way to des
					} else {
						fltCost = des.getCost();
					}
					
					byte[] cost = Command.flt2ByteArr(fltCost);
					Command.a2a(this.content[len], cost, offset);				//copy cost to content
					len++;
//					System.out.println('\n'+System.currentTimeMillis());
//					System.out.println("Send:\nthrough: "+des.getNextIP()+':'+des.getNextPort()+"\nto: "+des.getIP()+':'+des.getPort()+"\nRTto:"+rece.getIP()+':'+rece.getPort()+"\nPoison Reverse "+fltCost+'\n');

				}
				
				//create protocol header
				if(rtElements.hasMoreElements()) {
					System.out.println("Too large routing table... work well when nodes < 145 in network");
				}
				this.header = Command.initHeader(type, len, 
						Host.getLocalPort(), rece.getPort(), Host.getLocalIP(), rece.getIP(), sequence+len);
				
				Command.a2a(buf, header, 0);								//copy header into buf
				
				for(int i = 0; i < this.len; i++) {
					Command.a2a(buf, content[i], header.length + i*content[0].length);	//copy content into buf
				}
				
//				byte[] _checksum = {0, 0};								//at last, calculate checksum
//				for(int j = 0; j < this.header.length + this.len*this.content[0].length; j+=2) {
//					_checksum[0] ^= buf[j];
//					_checksum[1] ^= buf[j+1];
//				}
//				Command.a2a(this.buf, _checksum, this.header.length-2);							//add checksum
				
				this.packet = new DatagramPacket(buf, header.length + len*content[0].length,
						rece.getIP(), rece.getPort());						//send routing table to receiver
				try {
					this.socket = new DatagramSocket();
					this.socket.send(packet);
					this.socket.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
//				System.out.println("Send successfully, from: "+Host.getLocalIP().getHostAddress()+':'+Host.getLocalPort()+" to: "+rece.getIP().getHostAddress()+':'+rece.getPort());
//				System.out.println("Send " + len + " records.");
			}
		}
	}



}
