package milk;

public class Roommate implements Runnable{
	
	private Object milkLock;
	private Object buyLock;
	private boolean milk;
	private boolean buy;

	public Roommate() {
		this.milkLock = new Object();
		this.buyLock = new Object();
		this.milk = false;
		this.buy = false;
	}
	
	public void run() {
		try {
			Thread.sleep((int) (Math.random() * 400 + 100));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(Thread.currentThread().getName() + " arrives home.");
		try {
			Thread.sleep((int) (Math.random() * 400 + 100));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		synchronized(milkLock) {
			if(milk) {
				System.out.println(Thread.currentThread().getName() + " finds milk in frig and stays at home.");
				return;
			} else {
				System.out.println(Thread.currentThread().getName() + " finds No milk");
			}
		}
		try {
			Thread.sleep((int) (Math.random() * 400 + 100));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		synchronized(buyLock) {
			if(buy) {
				if(milk) {
					System.out.println(Thread.currentThread().getName() + " notices someone just put milk in frig, so stays at home.");
				} else {
					System.out.println(Thread.currentThread().getName() + " notices someone has left for grocery, so stays at home.");
				}
				return;
			} else {
				System.out.println(Thread.currentThread().getName() + " leaves for grocery.");
				buy = true;
			}
		}
		try {
			Thread.sleep((int) (Math.random() * 400 + 100));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(Thread.currentThread().getName() + " buys milk.");
		try {
			Thread.sleep((int) (Math.random() * 400 + 100));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		synchronized(milkLock) {
			synchronized(buyLock) {
				System.out.println(Thread.currentThread().getName() + " arrives home and puts milk in frig");
				milk = true;	
			}
		}

	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Roommate rm = new Roommate();
		Thread thread_1 = new Thread(rm, "Roommate_1");
		Thread thread_2 = new Thread(rm, "Roommate_2");
		thread_1.start();
		thread_2.start();
	}

}
