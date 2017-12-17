import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;

import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class Main extends JFrame {

	private Tune tune;
	
	public Main() {
		setTitle("3310");
		setSize(new Dimension(640,480));
		setLocationRelativeTo(null);
		setLayout(new BorderLayout());
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		JTextArea songField = new JTextArea();
		songField.setText("8e2 8d2 4#f1 4#g1 8#c2 8b1 4d1 4e1 8b1 8a1 4#c1 4e1 2.a1");
		songField.setLineWrap(true);
		songField.setWrapStyleWord(true);
		add(songField, BorderLayout.CENTER);
		
		JPanel left = new JPanel();
		left.setLayout(new FlowLayout(FlowLayout.CENTER));
		left.setPreferredSize(new Dimension(100,left.getPreferredSize().height));
		left.add(new JLabel("Tempo:"));
		
		NumberFormat tempoFormat = NumberFormat.getIntegerInstance();
		JFormattedTextField tempoField = new JFormattedTextField(tempoFormat);
		tempoField.setValue((long)Tune.DEFAULT_TEMPO);
		tempoField.setPreferredSize(new Dimension(40,tempoField.getPreferredSize().height));
		left.add(tempoField);
		
		JButton playButton = new JButton("Play");
		playButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (tune == null) {
					tune = new Tune(Math.toIntExact((long)tempoField.getValue()), songField.getText());
					tune.play();
					tune.addActionListener(new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent e) {
							tune = null;
							playButton.setText("Play");
							songField.setEnabled(true);
						}
						
					});
					playButton.setText("Stop");
					songField.setEnabled(false);
				}
				else tune.stop();
			}
		});
		left.add(playButton);
		
		add(left, BorderLayout.WEST);
		
		setVisible(true);
	}
	
	public static void main(String[] args) {
		
		new Main();
	}
}