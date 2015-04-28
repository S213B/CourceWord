package jurassicPark;

public class SafariArea {
	private int car, people;
	private Object lock;
	
	public SafariArea() {
		this.car = 0;
		this.people = 0;
		this.lock = new Object();
	}
	
	public void ride(String name) {
		synchronized(lock) {
			people++;
			System.out.println(name + " lines up to take a safari ride.");
			while(car <= 0) {
				try {
					lock.wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			car--;
			System.out.println(name + " takes a safari ride.");
			lock.notifyAll();
		}
	}
	
	public void back(String name) {
		synchronized(lock) {
			car++;
			System.out.println(name + " is availabel.");
			while(people <= 0) {
				try {
					lock.wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			people--;
			System.out.println(name + " is used.");
			lock.notifyAll();
		}
	}
}
