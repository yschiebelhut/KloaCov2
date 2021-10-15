package main;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.*;

/**
 * @author Matrikel-Nr. 3354235
 */
public class RKITerminal extends JFrame {

	private Map<SewagePlant, VariantMeasurement> measurements = new HashMap<>();

	private JLabel lblTotalPopulation = new JLabel();
	private HashMap<Variant, JLabel> mapLabels = new HashMap<>();

	private JPanel panelNorth = new JPanel();
	private BWMap map = new BWMap();

	public RKITerminal() {
		this.setTitle("RKI (Overview BW)");


		// create labels for total count
		for (Variant v :
				Variant.values()) {
			this.mapLabels.put(v, new JLabel());
		}


		this.panelNorth.setLayout(new GridLayout(Variant.values().length + 1, 2, 5, 5));
		this.lblTotalPopulation.setText("Population measured: " + this.getTotalPopulation());
		this.panelNorth.add(lblTotalPopulation);
		this.panelNorth.add(new JLabel(""));
		HashMap<Variant, Double> averages = new HashMap<>();
		for (Variant v :
				Variant.values()) {
			int sum = 0;
			int count = 0;
			for (SewagePlant p :
					this.measurements.keySet()) {
				count++;
				sum += this.measurements.get(p).getMeasurements().get(v);
			}
			double average = 0;
			if (sum != 0) {
				average = (double) sum / count;
			}
			averages.put(v, average);
		}

		ArrayList<Variant> sortedKeys = new ArrayList<>(Arrays.asList(Variant.values()));
		Collections.sort(sortedKeys);
		for (Variant v :
				sortedKeys) {
			JLabel labelName = new JLabel(v.toString());
			this.panelNorth.add(labelName);
			System.out.println(averages.get(v));
			this.mapLabels.get(v).setText(String.valueOf(averages.get(v)));
			this.panelNorth.add(this.mapLabels.get(v));
		}
		this.add(panelNorth, BorderLayout.NORTH);

		this.add(map);


		this.pack();
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	private int getTotalPopulation() {
		int sum = 0;
		for (SewagePlant p :
				this.measurements.keySet()) {
			sum += p.getPopulation();
		}
		return sum;
	}

	public void receiveMeasurements(VariantMeasurement meas) {
		this.measurements.put(meas.getPlant(), meas);
		this.lblTotalPopulation.setText("Population measured: " + this.getTotalPopulation());

		HashMap<Variant, Double> averages = new HashMap<>();
		for (Variant v :
				Variant.values()) {
			int sum = 0;
			int count = 0;
			for (SewagePlant p :
					this.measurements.keySet()) {
				count++;
				sum += this.measurements.get(p).getMeasurements().get(v);
			}
			double average = (double) sum / count;
			averages.put(v, average);
		}

		for (Variant v :
				averages.keySet()) {
			this.mapLabels.get(v).setText(String.format("%.2f", averages.get(v)));
		}

		this.map.setMapItem(meas.getPlant(), meas.getTotalValue());


		// save to file
		try (PrintWriter p = new PrintWriter(new FileWriter("ppp-logs.txt", true))) {
			String line = meas.getPlant().getName() +
					" (" + meas.getDate().toString() + "): " +
					meas.getMeasurements();
			p.println(line);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
