package cigaretteSmokers;

public class Smoker extends Thread {
	private Ingredient needing;
	private String name;
	
	public Smoker(String name, Ingredient needing) {
		this.needing = needing;
		this.name = name;
	}
	
	public void smoke() {
		needing.take(name);
		System.out.println(name + " is making cigarette.");
		try {
			sleep(300);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		needing.putback(name);
		System.out.println(name + " starts smoking.");
		try {
			sleep(300);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(name + " finishes smoking.");
	}
	
	public void run() {
		for(;;) {
			smoke();
		}
		//System.out.println(name + " smokes too much to die.");
	}
}
