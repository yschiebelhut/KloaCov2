package main;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Matrikel-Nr. 3354235
 */
public class VariantMeasurement {
	private SewagePlant plant;
	private Date date;
	private Map<Variant, Integer> measurements;

	public VariantMeasurement(SewagePlant plant) {
		this.plant = plant;
		this.date = new Date();
		this.measurements = new HashMap<>();
	}

	public VariantMeasurement(SewagePlant plant, Map<Variant, Integer> measurements) {
		this.plant = plant;
		this.date = new Date();
		this.measurements = measurements;
	}

	public VariantMeasurement(SewagePlant plant, Date date, Map<Variant, Integer> measurements) {
		this.plant = plant;
		this.date = date;
		this.measurements = measurements;
	}

	public int getTotalValue() {
		int sum = 0;
		for (Variant v :
				this.measurements.keySet()) {
			sum += this.measurements.get(v);
		}
		return sum;
	}

	public SewagePlant getPlant() {
		return plant;
	}

	public Date getDate() {
		return date;
	}

	public Map<Variant, Integer> getMeasurements() {
		return measurements;
	}
}
