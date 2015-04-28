package bird;

public class Bird implements Runnable {
	
	private int room = 3;
	private Object lock;

	public Bird() {
		this.lock = new Object();
		System.out.println("Initially, feeding shed is empty.");
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			Thread.sleep((int)(Math.random()*1000 + 1000));
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		while(true) {
			System.out.println(Thread.currentThread().getName() + " comes.");
			synchronized(lock) {
				if(room <= 0) {
					System.out.println("The feeding shed is full. " + Thread.currentThread().getName() + " waits.");
					while(room <= 0) {
						try {
							lock.wait();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				System.out.println(Thread.currentThread().getName() + " enters feeding shed.");
				room--;	
			}
			try {
				Thread.sleep((int)(Math.random()*1000 + 1000));
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			System.out.println(Thread.currentThread().getName() + " start eating.");
			try {
				Thread.sleep((int)(Math.random()*1000 + 1000));
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			synchronized(lock) {
				System.out.println(Thread.currentThread().getName() + " finishes, and then gets out of feeding shed.");
				room++;
				lock.notifyAll();	
			}
			try {
				Thread.sleep((int)(Math.random()*1000 + 1000));
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		final int birdNum = 5;
		Bird bird = new Bird();
		for(int i = 0; i < birdNum; i++) {
			new Thread(bird, "Bird_" + i).start();;
		}
	}

}
