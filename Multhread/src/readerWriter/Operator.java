package readerWriter;

public class Operator extends Thread {
	private String name;
	private SharedData data;
	
	public Operator(String name, SharedData data) {
		this.name = name;
		this.data = data;
	}
	
	public void read() {
		data.read(name);
	}
	
	public void write() {
		data.write(name);
	}
	
	public void run() {
		if(name.startsWith("Reader")) {
			try {
				sleep((int) (Math.random() * 1500));
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			while(true) {
				read();
				try {
					sleep((int) (Math.random() * 1000) + 500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} else if(name.startsWith("Writer")) {
			while(true) {
				write();
				try {
					sleep((int) (Math.random() * 500) + 500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} else {
			return;
		}
	}
}
