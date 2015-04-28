package cigaretteSmokers;

public class Ingredient {
	private String ingredient;
	private boolean available;
	
	public Ingredient(String ingredient) {
		this.ingredient = ingredient;
		available = true;
	}
	
	public synchronized void take(String name) {
		while(available == false) {
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		available = false;
		System.out.println(name + " takes " + ingredient + " off.");
	}
	
	public synchronized void putback(String name) {
		available = true;
		System.out.println(name + " puts " + ingredient + " back.");
		notifyAll();
	}
}
