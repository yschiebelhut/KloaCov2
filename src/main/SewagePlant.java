package main;

/**
 * @author Matrikel-Nr. 3354235
 */
public class SewagePlant implements MapItem {

	private String name;
	private int population;
	private double longitude;
	private double latitude;

	public SewagePlant(String name, int population, double longitude, double latitude) throws KloaCov2Exception {
		this.name = name;
		this.population = population;
		if (longitude < 7 || longitude > 10 || latitude < 47 || latitude > 50) {
			throw new KloaCov2Exception("Out of bonds: " + longitude + " " + latitude);
		}
		this.longitude = longitude;
		this.latitude = latitude;
	}

	@Override
	public String getTitle() {
		return this.name + " (" + this.population + " people)";
	}

	@Override
	public double getLongitude() {
		return this.longitude;
	}

	@Override
	public double getLatitude() {
		return this.latitude;
	}

	public int getPopulation() {
		return population;
	}

	public String getName() {
		return name;
	}
}
