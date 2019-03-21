public class Spacecraft extends SpaceProbe{
	private double fuelMass;

	/** Constrctor for the Spacecraft object.
		It should have all the parameters needed for the constructor of the SpaceProbe object
			+ the additional parameters for the Spacecraft specific functionalities

		These are the parameters:
			- fuelMass, which represents the mass of the fuel in the spacecraft
			- mass, which represents (1) the mass of the spacecraft WITHOUT the fuel or (2) the mass of the spacecraft WITH the fuel
			- startingPos, the starting position of the spacecraft
			- startingV, the starting velocity of the spacecraft
	*/
	public Spacecraft (double fuelMass, double mass, Vector2D startingPos, Vector2D startingV) {
		super(mass, startingPos, startingV);
		this.fuelMass = fuelMass;
	}

	/** This method should compute the result of applying a very large thrust over a very short interval of time to get an impulse
	*/
	public void impulse() {
		//Modify velocity of spaceProbe (see formula 3 of the Addendum in the Project Bible)
	}

	/** This method should compute the result of using the engines to apply thrust on the spacecraft
	*/
	public void thrust() {
		//Modify acceleration of spaceProbe (see formula 2 of the Addendum in the Project Bible)
	}
}
