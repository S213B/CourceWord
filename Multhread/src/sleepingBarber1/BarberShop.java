package sleepingBarber1;

public class BarberShop {
	private int barber, chair, customer;
	private final int  chairAmout;
	
	public BarberShop(int chairAmout) {
		this.chair = chairAmout;
		this.chairAmout = chairAmout;
		this.barber = 0;
		this.customer = 0;
	}
	
	public synchronized void checkBarber(String cName) {
		System.out.print(cName + " comes to barbershop. ");
		if(customer <= 0 && barber > 0) {
			customer++;
			barber--;
			System.out.println(cName + " wakes sleeping barber and gets haircut.");
			notifyAll();
		} else if (chair > 0) {
			System.out.println(cName + " waits for barber.");
			chair--;
			customer++;
			while(barber <= 0) {
				try {
					wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			chair++;
			barber--;
			customer--;
			System.out.println(cName + " is brought to barber chair and gets haircut.");
		} else {
			System.out.println(cName + " leaves because of no more chair.");
		}
	}
	
	public synchronized void checkCustomer(String bName) {
		int waitingCustomer = chairAmout - chair;
		barber++;
		System.out.println(bName + " finishes haircut and checks waiting room.");
		if(waitingCustomer > 0) {
			if(waitingCustomer == 1) {
				System.out.println("There is 1 customer in waiting room.");
			} else {
				System.out.println("There are " + waitingCustomer + " customers in waiting room.");
			}
			notifyAll();
		} else {
			System.out.println("No waiting customer now. So " + bName + " sleeps.");
			while(customer <= 0) {
				try {
					wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			customer--;
		}
	}
	
}
