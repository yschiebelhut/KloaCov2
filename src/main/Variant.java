package main;

/**
 * @author Matrikel-Nr. 3354235
 */
public enum Variant {
	ALPHA("B.1.1.7"),
	BETA("B.1.351"),
	GAMMA("P.1"),
	DELTA("B.1.617"),
	LAMBDA("C.37");

	private String designation;

	Variant(String designation) {
		this.designation = designation;
	}

	public String getDesignation() {
		return designation;
	}

	@Override
	public String toString() {
		return this.name() + " (" + designation + ")";
	}
}
