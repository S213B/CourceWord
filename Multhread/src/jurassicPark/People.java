package jurassicPark;

public class People extends Thread {
	private String name;
	private SafariArea sa;
	
	public People(String name, SafariArea sa) {
		this.name = name;
		this.sa = sa;
	}
	
	public void run() {
		System.out.println(name + " visits museum.");
		try {
			sleep((int) (Math.random()*200 +300));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(name + " finishes visiting.");
		sa.ride(name);
	}
}
