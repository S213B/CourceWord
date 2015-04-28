package sleepingBarber2;

import java.util.ArrayDeque;
import java.util.Scanner;

public class Customer implements Runnable {
	private int barber;
	private int free;
	private int take;
	private Object lock;
	private Object payLock;
	private ArrayDeque<String> queue;
	
	public Customer(int cap) {
		this.barber = 3;
		this.free = cap;
		this.take = 0;
		this.lock = new Object();
		this.payLock = new Object();
		queue = new ArrayDeque<String>(cap);
		System.out.println("Initially, all barber are sleeping.");
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(true) {
			synchronized(lock) {
				System.out.println(Thread.currentThread().getName() + " comes.");
					if(barber > 0) {
						System.out.println(Thread.currentThread().getName() + " wakes a barber and takes haircut.");
						barber--;
					} else if(free > 0) {
						if(take <4) {
							System.out.println(Thread.currentThread().getName() + " sits to wait.");
						} else {
							System.out.println(Thread.currentThread().getName() + " stands to wait.");
						}
						free--;
						take++;
						queue.offer(Thread.currentThread().getName());
						while(barber <= 0 || !queue.peek().equals(Thread.currentThread().getName())) {
							try {
								lock.wait();
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						System.out.println("It's " + Thread.currentThread().getName() + "'s turn to take haircut.");
						free++;
						take--;
						queue.poll();
						barber--;
					} else {
						System.out.println("The shop is full. " + Thread.currentThread().getName() + " leaves.");
						break;
					}
			}
			try {
				Thread.sleep((int)(Math.random()*500 + 500));
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println(Thread.currentThread().getName() + " is done.");
			synchronized(payLock) {
				System.out.println(Thread.currentThread().getName() + " goes to register to pay.");
				try {
					Thread.sleep((int)(Math.random()*400 + 100));
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println(Thread.currentThread().getName() + " is paid and leaves.");
			}
			synchronized(lock) {
				barber++;
				if(take <= 0) {
					System.out.println("Barber sleeps.");
				} else {
					lock.notifyAll();
				}
			}
			try {
				Thread.sleep((int)(Math.random()*2000 + 1000));
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int cap;
		System.out.println("Input waiting area capacity");
		Scanner in = new Scanner(System.in);
		while(true) {
			cap = in.nextInt();
			if(cap > 3) {
				break;
			}
			System.out.println("Incorrect Input. It should be >= 4.");
		}
		Customer c = new Customer(cap);
		cap += 4;
		for(int i = 0; i < cap; i++) {
			new Thread(c, "Customer_" + i).start();
		}
	}

}
