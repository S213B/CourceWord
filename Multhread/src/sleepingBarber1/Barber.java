package sleepingBarber1;

public class Barber extends Thread {
	private BarberShop barberShop;
	private int num;
	
	public Barber(BarberShop barberShop, int num) {
		this.barberShop = barberShop;
		this.num = num + 1;
	}
	
	public void run() {
		int j;
		while(true) {
			barberShop.checkCustomer("Barber_#" + num);
			j = (int) (Math.random() * 50 + 50);
			try {
				sleep(j);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
}
