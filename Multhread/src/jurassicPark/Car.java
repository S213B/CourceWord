package jurassicPark;

public class Car extends Thread {
	private String name;
	SafariArea sa;
	
	public Car(String name, SafariArea sa) {
		this.name = name;
		this.sa = sa;
	}
	
	public void run() {
		while(true) {
			sa.back(name);
			try {
				sleep(3000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
