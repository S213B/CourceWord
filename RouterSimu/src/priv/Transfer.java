package priv;

//header 24 bytes:
//1 byte type : ROUTETABLE, FILE, END, ACK, DOWN, UP, CHANGE
//1 byte length: length of content, unit is 8 bytes
//2 byte local port: sender port #
//2 byte receiver port: receiver port #
//4 byte local ip: sender ip
//4 byte receiver ip: receiver ip
//4 byte conversation: to identify in multiple connections between two nodes
//4 byte sequence: to identify order and arrival in multiple segments
//2 byte checksum: XOR all data in header+content
//content 1500-20-8-24=1448=181*8
//file data

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Transfer extends Command implements Runnable {
	
	private String fileName;
	private byte type;
	
	public Transfer(String[] cmd) {
		if(cmd.length == 4) {
			this.fileName = cmd[1];
			this.ip = Command.toIP(cmd[2]);
			this.port = Command.toPort(cmd[3]);
			if(this.ip != null && this.port != null) {
				this.setCmdValid(true);
				this.type = Host.FILE;
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
			if(Host.routeTable.containsKey(ip.getHostAddress()+':'+port)) {
				File file = new File(this.fileName);
				long flen = file.length();
				if(file.exists()) {
					try {
						BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
						int sequence = 0;
						Integer conv = null;
						Node nextNode = null;
						while(flen > 0) {
							byte[] content = new byte[Host.SEGMENTLEN - Host.HEADERLEN];
							int len = in.read(content);
							flen -= len;
							if(flen == 0) {								//complete header
								type |= Host.END;						//if last file chunk
							}
							
							int padding = len;
							len =  ((len >>> 3) + ((len & 0x07) == 0 ? 0 : 1));
							sequence ++;
							
							byte[] header = Command.initHeader(type, (byte)len, Host.getLocalPort(), port, Host.getLocalIP(), ip, sequence);
							if(conv == null) {
								byte[] bCon = {header[14], header[15], header[16], header[17]};		//read conversation #
								conv = DatagramReceiver.byteArr2Int(bCon);
							} else {
								Command.a2a(header, Command.int2ByteArr(conv), 14);
							}
							
							while((padding & 0x07) != 0) {
								content[padding++] = 0;
							}
							
							byte[] buf = new byte[Host.SEGMENTLEN];
							Command.a2a(buf, header, 0);
							Command.a2a(buf, content, Host.HEADERLEN);
							
							byte[] _checksum = {0, 0};						//at last, calculate checksum
							for(int i = 0; i < Host.HEADERLEN + len*Host.FILELENUNIT - 1; i+=2) {
								_checksum[0] ^= buf[i];
								_checksum[1] ^= buf[i+1];
							}
							Command.a2a(buf, _checksum, Host.HEADERLEN-2);	//add checksum
							
							DatagramPacket packet;
							synchronized(Host.rtLock) {
								nextNode = Host.routeTable.get(ip.getHostAddress()+':'+port);
								InetAddress _ip;
								int _port;
								if(nextNode.isProxy()) {
									_ip = nextNode.getProxyIP();
									_port = nextNode.getNextPort();
								} else {
									_ip = nextNode.getNextIP();
									_port = nextNode.getNextPort();
								}
								packet = new DatagramPacket(buf, Host.HEADERLEN + len*Host.FILELENUNIT, _ip, _port);
							}
							DatagramSocket socket = new DatagramSocket();
							socket.send(packet);
							socket.close();
							
//							System.out.println("Send from transfer...");
							
							long key = ((long) conv << 32) | (sequence & 0xffffffffL);
							Host.fileChunk.put(key, buf);										//save for resend
							Host.fileIdx.put(key, ip.getHostAddress()+':'+port);
							Host.fileResendCnt.put(key, 0);
						}
						System.out.println("Next hop = " + nextNode.getNextIP() + ':' + nextNode.getNextPort());
						in.close();
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					System.out.println("No such file exists.");	
				}
			} else {
				System.out.println("Cannot reach " + ip.getHostAddress()+':'+port);
			}
		} else {
			System.out.println("Command arguments error.");
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
