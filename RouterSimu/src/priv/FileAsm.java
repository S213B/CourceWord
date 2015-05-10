package priv;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;

public class FileAsm implements Runnable {
	
	private int conv, seq, senderPort;
	private InetAddress senderIP;
	private String cntKey;
	public FileAsm(int conv, InetAddress senderIP, int senderPort) {
		this.conv = conv;
		this.senderIP = senderIP;
		this.senderPort = senderPort;
		this.cntKey = senderIP.getHostAddress()+':'+senderPort+'_'+conv;
		this.seq = Host.fileReceChunkNeed.get(cntKey);
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
//			try {
//				Thread.sleep(500);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			File file = new File(this.senderIP.getHostAddress() + ':' + this.senderPort + '_' + String.valueOf(conv));
			BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file));
			int len = 0;
			for(int i = 1; i <= seq; i++) {
				byte[] buf = Host.fileReceChunk.get(this.cntKey+i);
				out.write(buf);
				Host.fileReceChunk.remove(this.cntKey+i);
				len += buf.length;
			}
			System.out.println("Packet received");
			System.out.println("Source = " + this.senderIP.getHostAddress() + ':' + this.senderPort);
			System.out.println("Destination = " + Host.getLocalIP().getHostAddress() + ':' + Host.getLocalPort());
			System.out.println("File received successfully");
			System.out.println("File length: " + len + 'B');
			out.flush();
			out.close();
			System.out.println("File length: " + file.length());
			try {
				Thread.sleep(Host.getTimeout()*1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				Host.fileReceChunkCnt.remove(cntKey);
				Host.fileReceChunkNeed.remove(cntKey);
			}
		} catch (FileNotFoundException e) {
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
