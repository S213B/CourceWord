package priv;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Enumeration;

public class FileSender implements Runnable {
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			while(true) {
				Thread.sleep(500);
				Enumeration<Long> keys = Host.fileChunk.keys();
				synchronized(Host.rtLock) {
					while(keys.hasMoreElements()) {
						System.out.println("Timeout, Resend!!!!!!");
						Long key = keys.nextElement();
						Host.fileResendCnt.put(key, 0);
						String name = Host.fileIdx.get(key);
						Node node = Host.routeTable.get(name);
						if(node == null) {
							Host.fileResendCnt.remove(key);
							Host.fileChunk.remove(key);
							continue;
						}
						byte[] buf = Host.fileChunk.get(key);
						DatagramPacket packet = new DatagramPacket(buf, ((int) (buf[1] & 0xff))*Host.FILELENUNIT+Host.HEADERLEN,
							node.getNextIP(), node.getNextPort());
						DatagramSocket socket = new DatagramSocket();
						socket.send(packet);
						socket.close();
					}
				}
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
