package main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


public class KloaCov2 {

	public static void main(String[] args) {
		try {
			List<SewagePlant> plants = KloaCov2.loadPlants();
			RKITerminal rki = new RKITerminal();
			for (SewagePlant sp : plants) {
				new SewageTerminal(sp, rki);
			}
		} catch (Exception e) {
			System.err.println("An error occured: " + e.getMessage());
		}
	}


	public static List<SewagePlant> loadPlants() {
		List<SewagePlant> plants = new LinkedList<>();

		//
		try (BufferedReader r = new BufferedReader(new FileReader("plants.txt"))) {
			while (r.ready()) {
				plants.add(KloaCov2.parsePlant(r.readLine()));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		Collections.shuffle(plants);
		plants = plants.subList(0, 3);
		// REPLACE THE FOLLOWING CODE
//		plants.add(KloaCov2.parsePlant("Karlsruhe-Neureut;350000;8.359;49.046"));
//		plants.add(KloaCov2.parsePlant("Bruchsal;50000;8.594;49.142"));
		// END OF CODE TO REPLACE

		return plants;
	}


	private static SewagePlant parsePlant(String line) {
		try {
			String[] parts = line.split("[;]");
			if (parts.length == 4) {
				return new SewagePlant(parts[0], Integer.parseInt(parts[1]), Double.parseDouble(parts[2]),
                        Double.parseDouble(parts[3]));
			}
		} catch (Exception e) {
			System.err.println("Error parsing line: " + line);
			e.printStackTrace();
		}
		return null;
	}

}
