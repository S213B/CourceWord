package producerConsumer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;

public class GetListener implements ActionListener {
	JButton getButton;
	JLabel numLabel;
	Shared data;

	public GetListener(JButton getButton, JLabel numLabel, Shared data) {
		this.getButton = getButton;
		this.numLabel = numLabel;
		this.data = data;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		getButton.setEnabled(false);
		Consumer c = new Consumer(data, numLabel);
		c.start();
	}

}
