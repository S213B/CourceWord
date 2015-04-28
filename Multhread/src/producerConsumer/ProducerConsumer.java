package producerConsumer;

import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.*;

public class ProducerConsumer {
	private static JFrame frame;
	private static JButton putButton, getButton;
	private static JLabel numLabel;
	Shared data;
	
	public ProducerConsumer() {
		frame = new JFrame("Producer - Consumer");
		putButton = new JButton("Produce");
		getButton = new JButton("Consume");
		numLabel = new JLabel("Amount: 0  (0-3)");
		data = new Shared();
	}
	
	public void buildGUI() {
		
		//numLabel.setText("213");
		numLabel.setHorizontalAlignment(SwingConstants.CENTER);
		numLabel.setFont(new Font("Helvetica", 1, 25));
		
		putButton.addActionListener(new PutListener(putButton, numLabel, data));
		getButton.addActionListener(new GetListener(getButton, numLabel, data));
		
		Box box = new Box(BoxLayout.X_AXIS);
		box.add(Box.createHorizontalStrut(40));
		box.add(putButton);
		box.add(Box.createHorizontalStrut(25));
		box.add(getButton);
		
		frame.getContentPane().add(BorderLayout.CENTER, numLabel);
		frame.getContentPane().add(BorderLayout.SOUTH, box);
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(300, 200);
		frame.setResizable(false);
		frame.setVisible(true);
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ProducerConsumer pc = new ProducerConsumer();
		pc.buildGUI();
	}

}
