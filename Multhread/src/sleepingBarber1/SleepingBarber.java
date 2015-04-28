package sleepingBarber1;

public class SleepingBarber {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		/*
		 *Assume there are 3 barbers and 3 chairs in barbershop.
		 *Initially all babers are cutting and no customer is waiting.
		 */
		
		BarberShop barberShop = new BarberShop(3);
		Barber[] barber = new Barber[3];
		Customer[] customer = new Customer[7];
		
		System.out.println("Start:");
		for(int i = 0; i < 7; i++) {
			if(i < 3){
				barber[i] = new Barber(barberShop, i);
			}
			customer[i] = new Customer(barberShop, i*100);
		}
		for(int i = 0; i < 7; i++) {
			if(i < 3) {
				barber[i].start();
			}
			customer[i].start();
		}
	}

}
