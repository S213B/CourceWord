package priv;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Hashtable;

public class DatagramReceiver implements Runnable {
	
	private byte type;
	private short len;					//used buf length, unit is two words(8 bytes)
	private int senderPort;				//sender port
	private int recePort;				//receiver port
	private InetAddress senderIP;		//sender ip
	private InetAddress receIP;		//receiver ip
	private int conversation;			//use current time to identify conversation between two nodes
	private int sequence;				//identify segment in same conversation
//	private short checksum;
	private byte[] buf, header;			//buf = header + content
	private byte[][] content;			//routing table, content = ip + port + cost
	private DatagramPacket packet;
	private DatagramSocket socket;
	
	public DatagramReceiver() {
		this.buf = new byte[Host.SEGMENTLEN];
		this.header = new byte[Byte.SIZE*2+Integer.SIZE*2+Short.SIZE*2];		//type_1+len_1+port_2+con_4+seq_4+check_2 = 14
		this.content = new byte[(buf.length-Host.HEADERLEN)/10][10];				//ip_4+cost_4+port_2 = 10
		this.packet = new DatagramPacket(buf, buf.length);
		try {
			this.socket = new DatagramSocket(Host.getLocalPort());
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(true) {
			try {
				this.socket.receive(this.packet);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
//			System.out.println("Listening on port: " + this.socket.getLocalPort());
			parsePacket();
			if((this.type & Host.ROUTETABLE) != 0) {		//routing table packet
				String name = this.senderIP.getHostAddress() + ':' + this.senderPort;		//name: sender name
				synchronized(Host.rtLock) {
					Node node = Host.neighborList.get(name);					//check sender whether in neighbor list
//					System.out.println('\n'+System.currentTimeMillis());
//					System.out.println("Package from "+this.senderIP.getHostAddress()+':'+this.senderPort+" to "+this.receIP.getHostAddress()+':'+this.recePort);
//					System.out.println("Pakage contains "+this.len+" records.");
					if(node == null) {											//sender is a new neighbor
						node = new Node(this.senderIP, this.senderPort, -1);	//create a new node
						Host.neighborList.put(name, node);						//add it into neighbor list
						Host.routeTable.put(name, node);						//and local routing table
						for(int i = 0; i < this.len; i++) {						//add its routing table
							byte[] bIP = {this.content[i][0], this.content[i][1], this.content[i][2], this.content[i][3]};
							InetAddress _ip = getIPfrCont(bIP);					//read des ip
							
							byte[] bPort = {this.content[i][4], this.content[i][5]};
							int _port = getPortfrCont(bPort);					//des port
							
							byte[] bCost = {this.content[i][6], this.content[i][7], this.content[i][8], this.content[i][9]};
							float _cost = getCostfrCont(bCost);					//cost to des
							
							if(_ip.equals(Host.getLocalIP()) && _port == Host.getLocalPort()) {
								node.setCost(_cost);							//if the des is local host
								node.setOriCost(_cost);							//set cost and original cost of new neighbor
								continue;
							} else if(_cost > Integer.MAX_VALUE) {
								continue;										//if its a dead way, not add
							}
							
							String to = _ip.getHostAddress() + ':' + _port;
							node.getRT().put(to, _cost);
//							System.out.println("New Neighbor Receive:\n"+"from: "+name+"\nto: "+_ip.getHostAddress()+':'+_port+"\nCost: "+_cost);
						}
						node.heartBeat(true);									//set this neighbor live
						new Thread(Host.bellman).start();					//recalculate new routing table
					} else {									//not a new neighbor
						Hashtable<String, Float> rt = new Hashtable<String, Float>();
//						node.getRT().clear();					//clear old routing table of this neighbor
						for(int i = 0; i < this.len; i++) {		//add this neighbor's routing table
							byte[] bIP = {this.content[i][0], this.content[i][1], this.content[i][2], this.content[i][3]};
							InetAddress _ip = getIPfrCont(bIP);		//des ip
							
							byte[] bPort = {this.content[i][4], this.content[i][5]};
							int _port = getPortfrCont(bPort);		//des port
							
							byte[] bCost = {this.content[i][6], this.content[i][7], this.content[i][8], this.content[i][9]};
							float _cost = getCostfrCont(bCost);		//cost to des
							if(_ip.equals(Host.getLocalIP()) && _port == Host.getLocalPort()) {
								continue;							//no need to know the cost to self
							}
							if(_cost > Integer.MAX_VALUE) {			//no need to store a dead way for routing table calculation
								continue;
							}
							String to = _ip.getHostAddress() + ':' + _port;
							rt.put(to, _cost);
//							System.out.println("Receive:\n"+"from: "+name+"\nto: "+_ip.getHostAddress()+':'+_port+"\nCost: "+_cost+'\n');
						}
						if(!Host.routeTable.containsKey(name)) {		//if this is a reviving neighbor
							Host.routeTable.put(name, node);			//put it into local routing table
							node.setCost(node.getOriCost());			//set cost to original
						} else if(node.isDown()) {
							node.setCost(node.getOriCost());			//reachable down-neighbor shutdown and restart before dead
							node.setDown(false);
						}
//						System.out.println("Diff with old rt?");
//						Enumeration<String> keys = node.getRT().keys();
//						while(keys.hasMoreElements()) {
//							String key = keys.nextElement();
//							System.out.print(key+':'+node.getRT().get(key)+" | ");
//						}
//						System.out.println();
//						keys = rt.keys();
//						while(keys.hasMoreElements()) {
//							String key = keys.nextElement();
//							System.out.print(key+':'+rt.get(key)+" | ");
//						}
//						System.out.println();
						if(!rt.equals(node.getRT())) {					//whether new routing table different with old one
							node.setRt(rt);								//set new routing table
							new Thread(Host.bellman).start();			//calculate new routing table
//							System.out.println("Yes");
						}
						node.heartBeat(true);							//set this neighbor live
					}
				}
			} else if((this.type & Host.DOWN) != 0) {
				String name = this.senderIP.getHostAddress() + ':' + this.senderPort;
				synchronized(Host.rtLock) {
					Node node = Host.neighborList.get(name);
					node.setCost(Float.MAX_VALUE);
					node.setDown(true);
					node.setChanged(false);
					new Thread(Host.bellman).start();							//update routing table
//					Enumeration<Node> elements = Host.routeTable.elements();
//					while(elements.hasMoreElements()) {							//set cost of the nodes which is connected through A
//						Node ele = elements.nextElement();
//						if(ele.getCost() == Float.MAX_VALUE) {
//							continue;
//						}
//						String nextName = ele.getNextIP().getHostAddress() + ':' + ele.getNextPort();
//						if(nextName.equals(name)) {
//							if(ele.getOriCost() != Float.MAX_VALUE) {			//if they are neighbors, set cost to original cost
//								ele.setCost(ele.getOriCost());
//							} else {
//								ele.setCost(Float.MAX_VALUE);					//if not neighbors, which means cannot connect temporarily
//							}
//						}
//					}
//					node.setCost(Float.MAX_VALUE);								//set link down
//					node.setDown(true);
				}
			} else if((this.type & Host.UP) != 0) {
				String name = this.senderIP.getHostAddress() + ':' + this.senderPort;
				synchronized(Host.rtLock) {
					Node node = Host.neighborList.get(name);				//whether a neighbor
					if(Host.routeTable.containsKey(name)) {				//whether is dead due to down over 3*timeout seconds
						node.setDown(false);
						if(node.getOriCost() <= node.getCost()) {		//if original is better, update routing table, otherwise ignore
							node.setCost(node.getOriCost());
							node.setNextIP(node.getIP());
							node.setNextPort(node.getPort());
						}
					} else {											//revive from dead and update
						Host.routeTable.put(node.getName(), node);
						node.setCost(node.getOriCost());
						node.setNextIP(node.getIP());
						node.setNextPort(node.getPort());
						node.getRT().clear();
					}
				}
				new Thread(Host.bellman).start();						//update routing table
			} else if((this.type & Host.CHANGE) != 0) {	
				String name = this.senderIP.getHostAddress() + ':' + this.senderPort;
				byte[] _cost = {buf[Host.HEADERLEN], buf[Host.HEADERLEN+1], buf[Host.HEADERLEN+2], buf[Host.HEADERLEN+3]};
				float cost = byteArr2Flt(_cost);
				synchronized(Host.rtLock) {
					Node node = Host.neighborList.get(name);
					if(node.isDown()) {
						continue;										//no need to set if down
					}
					node.setCost(cost);
					node.setChCost(cost);								//set new cost
					node.setChanged(true);
				}
				new Thread(Host.bellman).start();						//update routing table
//					float preCost = node.getCost();
//					Enumeration<Node> elements = Host.routeTable.elements();
//					while(elements.hasMoreElements()) {							//set cost of the nodes which is connected through A
//						Node ele = elements.nextElement();
//						String nextName = ele.getNextIP().getHostAddress() + ':' + ele.getNextPort();
//						if(nextName.equals(name)) {
//							if(ele.getOriCost() != Float.MAX_VALUE) {			//if they are neighbors
//								float t = ele.getCost() + cost - preCost;		//new cost through A
//								if(ele.getOriCost() <= t) {						//original cost is better, connect directly
//									ele.setCost(ele.getOriCost());
//									ele.setNextIP(ele.getIP());
//									ele.setNextPort(ele.getPort());
//								} else {										//new cost is better, connect through A
//									ele.setCost(t);
//								}
//							} else {
//								ele.setCost(ele.getCost() + cost - preCost);	//if not neighbors, can only connect through A now
//							}
//						}
//					}
			} else {										//file packet
				try {
					if(!this.receIP.equals(Host.getLocalIP()) || this.recePort != Host.getLocalPort()) {	//whether host is des
						if((this.type & Host.FILE) != 0) {
							System.out.println("File Packet #" + this.sequence + "received and forward.");	//true: forward file packet
							System.out.println("Source: " + this.senderIP + ':' + this.senderPort);
							System.out.println("Destination: " + this.receIP + ':' + this.recePort);
						}
						String name = this.receIP.getHostAddress()+':'+this.recePort;
						synchronized(Host.rtLock) {
							Node receNode = Host.routeTable.get(name);
							if(receNode != null) {
								DatagramPacket tPacket = new DatagramPacket(buf, this.len*Host.FILELENUNIT+Host.HEADERLEN, receNode.getNextIP(), receNode.getNextPort());
								socket.send(tPacket);
							}
						}
					} else {														//false: receive file packet
						if((this.type & Host.CORRUPT) != 0) {			//sent file packet corrupted, need to resend
//							System.out.println("Corrupted Signal, Resend!!!");
							resend(this.conversation, this.sequence);
						} else if((this.type & Host.ACK) != 0) {		//sent file packet received succeed
							long key = ((long)this.conversation << 32) | (this.sequence & 0xffffffffL);
							Host.fileChunk.remove(key);
							Host.fileIdx.remove(key);
							Host.fileResendCnt.remove(key);
							System.out.println("ACK #" + this.sequence + " received.");
//							System.out.println(Host.fileChunk.size() + " chunks has no ACK.");
							if(Host.fileChunk.size() == 0) {
								System.out.println("File send successfully.");
							} else {
								long halfKey = (long)this.conversation << 32;
								for(int i = this.sequence-1; i > 0; i--) {
									long k = halfKey | (i & 0xffffffffL);
									Integer t = Host.fileResendCnt.get(k);
									if(t != null) {
										if(t == 2) {
											resend(this.conversation, i);
											Host.fileResendCnt.put(k, 0);
										} else {
											Host.fileResendCnt.put(k, t+1);
										}
									}
								}
							}
						} else {										//receive file packet
							if(checksumValid()) {						//check if corrupt
								String cntKey = this.senderIP.getHostAddress()+':'+this.senderPort+'_'+this.conversation;
								if(!Host.fileReceChunk.containsKey(cntKey+this.sequence)) {		//check if duplicate
									if(Host.fileReceChunkCnt.contains(cntKey)) {
										if(!(Host.fileReceChunkCnt.get(cntKey) < Host.fileReceChunkNeed.get(cntKey))) {
											System.out.println("\ndelayed packet, ignore.\n");
											continue;					//if its a delayed packet, ignore it
										}
									}
//									System.out.println("New Packet Rece!!! #"+this.sequence);
									this.header[0] = Host.ACK;			//receive successfully, send ACK
									swapPort();
									swapIP();
									String name = this.senderIP.getHostAddress()+':'+this.senderPort;
									synchronized(Host.rtLock) {
										Node receNode = Host.routeTable.get(name);
										if(receNode != null) {
											DatagramPacket tPacket = new DatagramPacket(header, Host.HEADERLEN, receNode.getNextIP(), receNode.getNextPort());
											socket.send(tPacket);
										}
									}
									swapPort();
									swapIP();
//									System.out.println("rece, send ACK #"+this.sequence);
									byte[] content = new byte[this.len*Host.FILELENUNIT];
									for(int i = 0; i < content.length; i++) {
										content[i] = buf[Host.HEADERLEN+i];
									}
									Host.fileReceChunk.put(cntKey+this.sequence, content);
									if(Host.fileReceChunkCnt.containsKey(cntKey)) {
										int receCnt = Host.fileReceChunkCnt.get(cntKey);
										Host.fileReceChunkCnt.put(cntKey, receCnt+1);
									} else {
										Host.fileReceChunkCnt.put(cntKey, 1);
									}
									if((this.type & Host.END) != 0) {
										Host.fileReceChunkNeed.put(cntKey, this.sequence);
									}
									if(Host.fileReceChunkCnt.get(cntKey) == Host.fileReceChunkNeed.get(cntKey)) {
										new Thread(new FileAsm(this.conversation, this.senderIP, this.senderPort)).start();
									}
								}
							} else {				
								//packet is corrupted, send feedback
								System.out.println("Packet Corrupted, Request Resend!!!");
								this.header[0] = Host.CORRUPT;
								swapPort();
								swapIP();
								String name = this.senderIP.getHostAddress()+':'+this.senderPort;
								synchronized(Host.rtLock) {
									Node receNode = Host.routeTable.get(name);
									if(receNode != null) {
										DatagramPacket tPacket = new DatagramPacket(header, Host.HEADERLEN, receNode.getNextIP(), receNode.getPort());
										socket.send(tPacket);
									}
								}
							}
						}
					}
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	private void parsePacket() {
		// TODO Auto-generated method stub
		for(int i = 0; i < Host.HEADERLEN; i++) {		//read header
			header[i] = buf[i];
		}
		int offset = 0;						//set up offset to read header
		
		this.type = header[offset++];		//read type
		
		this.len = (short) (header[offset++] & 0xff);		//read length
		
		byte[] bSPort = {header[offset], header[offset+1]};		//read sender port
		offset += bSPort.length;
		this.senderPort = byteArr2Short(bSPort);
		
		byte[] bRPort = {header[offset], header[offset+1]};			//read receiver port
		offset += bRPort.length;
		this.recePort = byteArr2Short(bRPort);
		
		byte[] bSIP = {header[offset], header[offset+1], header[offset+2], header[offset+3]};		//read sender ip
		offset += bSIP.length;
		try {
			this.senderIP = InetAddress.getByAddress(bSIP);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		byte[] bRIP = {header[offset], header[offset+1], header[offset+2], header[offset+3]};		//read receiver ip
		offset += bRIP.length;
		try {
			this.receIP = InetAddress.getByAddress(bRIP);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		byte[] bCon = {header[offset], header[offset+1], header[offset+2], header[offset+3]};		//read conversation #
		offset += bCon.length;
		this.conversation = byteArr2Int(bCon);
		
		byte[] bSeq = {header[offset], header[offset+1], header[offset+2], header[offset+3]};		//read sequence #
		offset += bSeq.length;
		this.sequence = byteArr2Int(bSeq);
		
		if((this.type & Host.ROUTETABLE) != 0) {
			for(int i = 0, k = Host.HEADERLEN; i < this.len ; i++) {		//read if content is routing table
				for(int j = 0; j < content[0].length; j++) {
					content[i][j] = buf[k++];
				}
			}
		}
	}
	
	private void resend(int conv, int seq) throws IOException {
		System.out.println("Corrupted, Resend!!!");
		long key = ((long)conv << 32) | (seq & 0xffffffffL);
		byte[] buf = Host.fileChunk.get(key);
		if(buf != null) {
			String name = Host.fileIdx.get(key);
			String[] names = name.split(":");
			InetAddress _ip = InetAddress.getByName(names[0]);
			int _port = Integer.parseInt(names[1]);
			DatagramPacket packet = new DatagramPacket(
					buf, ((int) (buf[1] & 0xff))*Host.FILELENUNIT+Host.HEADERLEN, _ip, _port);
			socket.send(packet);
		}
	}

	public static InetAddress getIPfrCont(byte[] bs) {
		// TODO Auto-generated method stub
		byte[] bIP = {bs[0], bs[1], bs[2], bs[3]};
		InetAddress ip = null;
		try {
			ip = InetAddress.getByAddress(bIP);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ip;
	}

	public static int getPortfrCont(byte[] bs) {
		// TODO Auto-generated method stub
		byte[] x = {bs[0], bs[1]};
		return ByteBuffer.wrap(x).getShort();
	}
	
	public static float getCostfrCont(byte[] bs) {
		byte[] x = {bs[0], bs[1], bs[2], bs[3]};
		return ByteBuffer.wrap(x).getFloat();
	}

	private void swapPort() {
		this.header[2] ^= this.header[4];
		this.header[4] ^= this.header[2];
		this.header[2] ^= this.header[4];
		
		this.header[3] ^= this.header[5];
		this.header[5] ^= this.header[3];
		this.header[3] ^= this.header[5];
	}

	private void swapIP() {
		for(int i = 6; i < 10; i++) {
			this.header[i] ^= this.header[i+4];
			this.header[i+4] ^= this.header[i];
			this.header[i] ^= this.header[i+4];
		}
	}

	private boolean checksumValid() {			//validate checksum value
		byte[] r = {0, 0};
		for(int i = 0; i < Host.HEADERLEN + this.len*Host.FILELENUNIT - 1; i+=2) {
			r[0] ^= buf[i];
			r[1] ^= buf[i+1];
		}
		
		return r[0] == 0 && r[1] == 0;
	}
	
	public static int byteArr2Int(byte[] x) {				//convert byte array to integer
		return ByteBuffer.wrap(x).getInt();
	}
	
	public static int byteArr2Short(byte[] port) {			//convert byte array to short(port)
		return ByteBuffer.wrap(port).getShort();
//		return ((port[0] & 0xff) << 8) | (port[1] & 0xff);
	}
	
	public static float byteArr2Flt(byte[] cost) {
		return ByteBuffer.wrap(cost).getFloat();
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		float a = -10000;
//		while(a <= 10000) {
//			float b = byteArr2Flt(Command.flt2ByteArr(a));
//			if(a != b) {
//				System.out.println("error: "+a);
//			} else {
//				System.out.println("ok: "+a);
//			}
//			a+=0.1;
//		}
//		byte a = 1, b = 2;
//		a ^= b;
//		b ^= a;
//		a ^= b;
//		System.out.println("a = " + a + ", b = " + b);
	}

}
