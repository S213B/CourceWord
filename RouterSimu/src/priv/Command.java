package priv;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

//header 24 bytes:
//1 byte type : ROUTETABLE, FILE, END, ACK, DOWN, UP, CHANGE
//1 byte length: length of content, unit is 10 bytes for routing table, 8 bytes for file
//2 byte local port: sender port #
//2 byte receiver port: receiver port #
//4 byte local ip: sender ip
//4 byte receiver ip: receiver ip
//4 byte conversation: to identify in multiple connections between two nodes
//4 byte sequence: to identify order and arrival in multiple segments
//2 byte checksum: XOR all data in header+content
//content(routing table) 10 bytes per tuple:
//4 byte ip
//2 byte port
//4 byte cost with poison reverse
//content(file) 1500-20-8-24=1448=181*8
//file data

public class Command {
	
	private boolean cmdValid;
	InetAddress ip;
	Integer port;
	String name;
	
	public Command() {
		this.cmdValid = true;
		this.ip = null;
		this.port = null;
		this.name = null;
	}
	
	//preprocess header for command
	//complete for DOWN, CHANGE, UP
	//FILE need length + sequence + checksum
	public static byte[] initHeader(byte type, int sendPort, int recePort, InetAddress sendIP, InetAddress receIP) {
		//type_1 + len_1 + port_2*2 + ip_4*2 + con_4 + seq_4 + check_2 = 24
		byte[] header = new byte[Host.HEADERLEN];
		
		int offset = 0;						//FILE | ROUTETABLE | UP | DOWN | CHANGE
		header[offset++] = type;			//type
		
		header[offset++] = 0;				//length = 0				
		
		byte[] b2 = short2ByteArr(sendPort);
		a2a(header, b2, offset);			//sender port
		offset += b2.length;
		
		b2 = short2ByteArr(recePort);
		a2a(header, b2, offset);			//receiver port
		offset += b2.length;
		
		byte[] b4 = sendIP.getAddress();
		a2a(header, b4, offset);			//sender ip
		offset += b4.length;
		
		b4 = receIP.getAddress();
		a2a(header, b4, offset);			//receiver ip
		offset += b4.length;
		
		b4 = int2ByteArr((int) System.currentTimeMillis());		//use current time as conversation identification
		a2a(header, b4, offset);			//conversation #
		offset += b4.length;
		
		if(type == Host.DOWN) {				//ROUTE TABLE DOWN
			header[1] = 0;
		} else if(type == Host.UP) {		//ROUTE TABLE UP
			header[1] = 0;
		} else if(type == Host.CHANGE) {	//ROUTE TABLE CHANGE
			header[1] = 1;
		}
		
		header[Host.HEADERLEN-1] = header[Host.HEADERLEN-2] = 0;		//make sure checksum is 0 before calculate
		
		return header;
	}
	
	//header with length and sequence
	public static byte[] initHeader(byte type, short len, int sendPort, int recePort,
			InetAddress sendIP, InetAddress receIP, int seq) {
		
		byte[] tHeader = initHeader(type, sendPort, recePort, sendIP, receIP);
		
		tHeader[1] = (byte) (len & 0xff);						//add length
		
		byte[] b4 = int2ByteArr(seq);		//add sequence
		a2a(tHeader, b4, 1*2 + 2*2 + 4*3);
		
		tHeader[tHeader.length-1] = tHeader[tHeader.length-2] = 0;		//make sure checksum is 0 before calculate
		
		return tHeader;
	}
	
	public static boolean isIP(String ip) {
		if(ip.matches("^((\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])\\.){3}(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])$")) {
//			System.out.println("OK");
			return true;
		}
		System.out.println("Not Validate IP address.");
		return false;
	}
	
	public static boolean isNum(String num) {
		if(num.matches("^([+-]?\\d{1,9})$")) {
//			System.out.println("OK");
			return true;
		}
		System.out.println("Not Validate number or too large number(< 1 billion).");
		return false;
	}
	
	public static boolean isPort(String port) {
		
		if(port.matches("^(\\d{1,5})$")) {
			return true;
		}
		System.out.println("Not Validate port number.");
		return false;
	}
	
	public static boolean isFlt(String num) {
		if(num.matches("^([+-]?\\d+\\.?\\d*)$")) {
			return true;
		}
		System.out.println("Not Validate timeout number.");
		return false;
	}
	
	public static InetAddress toIP(String ip) {
		if(isIP(ip)) {
			try {
				return InetAddress.getByName(ip);
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return null;
	}
	
	public static Integer toPort(String port) {
		if(isPort(port)) {
			Integer r = Integer.valueOf(port);
			if(r < 65536) {
				return r;
			}
		}
		
		return null;
	}
	
	public static Integer toNum(String num) {
		if(isNum(num)) {
			return Integer.valueOf(num);
		}
		
		return null;
	}
	
	public static Float toFlt(String num) {
		if(isFlt(num)) {
			return Float.valueOf(num);
		}
		
		return null;
	}
	
	public static void a2a(byte[] a, byte[] b, int offset) {			//array copy to array
		for(int i = 0; i < b.length; i++) {
			a[i+offset] = b[i];
		}
	}
	
	public static byte[] flt2ByteArr(float cost) {						//float(cost) to 4 byte array
		return ByteBuffer.allocate(4).putFloat(cost).array();
	}
	
	public static byte[] int2ByteArr(int seq) {							//int to 4 byte array
		return ByteBuffer.allocate(4).putInt(seq).array();
	}
	
	public static byte[] short2ByteArr(int port) {						//short(port) to 2 byte array
		port &= 0xffff;
		byte[] r = {(byte) (port >>> 8), (byte) (port)}; 
		return r;
	}

	public boolean isCmdValid() {
		return cmdValid;
	}

	public void setCmdValid(boolean cmdValid) {
		this.cmdValid = cmdValid;
	}

	public static void main(String[] args) {
//		int port = -2;
//		byte[] a = short2ByteArr(port);
//		System.out.println(Integer.parseInt("-+9999999"));
	}
}

