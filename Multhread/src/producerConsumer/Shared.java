package producerConsumer;

public class Shared {
	private int count;
	
	public Shared() {
		count = 0;
	}
	
	public int show() {
		return count;
	}
	
	public synchronized void get() {
		while(count <= 0) {
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		count--;
		notifyAll();
	}
	
	public synchronized void put() {
		while(count >= 3) {
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		count++;
		notifyAll();
	}

}
