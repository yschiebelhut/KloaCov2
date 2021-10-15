package main;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Matrikel-Nr. 3354235
 */
public class SewageTerminal extends JFrame {

	private SewagePlant plant;
	private RKITerminal rki;

	private HashMap<Variant, JTextField> mapTextFields = new HashMap<>();

	private JPanel panelNorth = new JPanel();

	private JButton btnSend = new JButton("Send");

	public SewageTerminal(SewagePlant plant, RKITerminal rki) {
		this.plant = plant;
		this.rki = rki;

		this.setTitle(this.plant.getTitle());

		this.panelNorth.setLayout(new GridLayout(Variant.values().length, 2, 5, 5));
		for (Variant v :
				Variant.values()) {
			JLabel labelName = new JLabel(v.toString());
			this.panelNorth.add(labelName);
			JTextField textField = new JTextField();
			this.mapTextFields.put(v, textField);
			this.panelNorth.add(textField);
		}
		this.add(panelNorth, BorderLayout.NORTH);

		this.btnSend.addActionListener(e -> {
			Map<Variant, Integer> measurements = new HashMap<>();
			for (Variant v :
					this.mapTextFields.keySet()) {
				try {
					measurements.put(v, Integer.parseInt(this.mapTextFields.get(v).getText()));
				} catch (NumberFormatException exception) {
					String message = "Invalid input for " + v.toString() + ": " + this.mapTextFields.get(v).getText();
					JOptionPane.showMessageDialog(this, message);
					return;
				}
			}

			rki.receiveMeasurements(new VariantMeasurement(this.plant, measurements));

			for (Variant v :
					this.mapTextFields.keySet()) {
				this.mapTextFields.get(v).setEnabled(false);
			}
			this.btnSend.setEnabled(false);

			Thread t = new Thread(() -> {
				try {
					Thread.sleep(10000);
				} catch (InterruptedException ex) {
					ex.printStackTrace();
				}
				for (Variant v :
						this.mapTextFields.keySet()) {
					this.mapTextFields.get(v).setText("");
					this.mapTextFields.get(v).setEnabled(true);
				}
				this.btnSend.setEnabled(true);
			});
			t.start();
		});
		this.add(btnSend);

		this.pack();
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
