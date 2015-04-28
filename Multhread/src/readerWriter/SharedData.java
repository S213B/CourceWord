package readerWriter;

public class SharedData {
	private boolean available;
	private Object readWriteLock;
	private int readerNum;
	
	public SharedData() {
		available = true;
		readWriteLock = new Object();
		readerNum = 0;
	}
	
	public void read(String name) {
		synchronized(readWriteLock) {
			while(available == false) {
				try {
					readWriteLock.wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			System.out.println(name + " starts reading shared data.");
			readerNum++;
		}
		try {
			Thread.sleep(300);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		synchronized(readWriteLock) {
			System.out.println(name + " finishes reading shared data.");
			readerNum--;
			readWriteLock.notifyAll();
		}
	}
	
	public void write(String name) {
		synchronized(readWriteLock) {
			while(available == false || readerNum > 0) {
				try {
					readWriteLock.wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			System.out.println(name + " starts writing shared data. No other reader or writer may access the shared data");
			available = false;
		}
		try {
			Thread.sleep(300);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		synchronized(readWriteLock) {
			available = true;
			System.out.println(name + " finishes writing shared data.");
			readWriteLock.notifyAll();
		}
	}
}
