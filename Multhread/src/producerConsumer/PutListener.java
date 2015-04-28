package producerConsumer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;

public class PutListener implements ActionListener {
	JButton putButton;
	JLabel numLabel;
	Shared data;

	public PutListener(JButton putButton, JLabel numLabel, Shared data) {
		this.putButton = putButton;
		this.numLabel = numLabel;
		this.data = data;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		putButton.setEnabled(false);
		Producer p = new Producer(data, numLabel);
		p.start();
	}
}
