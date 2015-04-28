package producerConsumer;

import javax.swing.JLabel;

public class Consumer extends Thread {
	private Shared data;
	private JLabel numLabel;
	
	public Consumer(Shared data, JLabel numLabel) {
		this.data = data;
		this.numLabel = numLabel;
	}
	
	public void run() {
		while(true) {
			data.get();
			numLabel.setText("Amount: " + data.show() + "  (0-3)");
			try {
				sleep((int) (Math.random()*500 + 500));
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
