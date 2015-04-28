package sleepingBarber1;

public class Customer extends Thread {
	private BarberShop barberShop;
	private int index;
	
	public Customer(BarberShop barberShop, int index) {
		this.barberShop = barberShop;
		this.index = index + 100;
	}
	
	public void run() {
		int i = 1, j;
		while(true) {
			barberShop.checkBarber("Customer_#" + (i + index));
			i++;
			j = (int) (Math.random() * 100 + 50);
			try {
				sleep(j);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
