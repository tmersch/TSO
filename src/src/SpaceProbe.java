public class SpaceProbe extends CelestialBody {
	private CelestialBody crashedPlanet;
	private Vector2D thrusters = new Vector2D();

	/** Default constructor for SpaceProbe
		It has parameters
			name, which is the name of the SpaceProbe,
			mass, which represents the mass of the space probe (constant, because it has no engines that could burn up fuel),
			startingPos[] which represents the starting position of the SpaceProbe,
			and startingV[] which represents the starting velocities (on x and y)
	*/
	public SpaceProbe (String name, double mass, Vector2D startingPos, Vector2D startingV) {
		super(name, mass, startingPos, startingV);
	}

	public void activateThrustersInAngle (double angle) {
		//Adjust the thrusters in the correct angle
		thrusters = new Vector2D(angle);
		//Then, scale them by their strength
		//thrusters.multiply();														//NEEDS TO BE MULTIPLIED BY THE TOTAL FORCE EXERTED BY THE THRUSTERS
	}

	public void shutThrustersOff () {
		thrusters = new Vector2D();
	}

	/** Additional indirect constructor for SpaceProbe
		It has some parameters that are the same than in the default constructor (name and mass), but also new parameters:
			launchPlanetPos, the position of the center of the planet the spaceProbe is launched from,
			velocity, the starting speed of the spaceProbe,
			launchAngle, the angle which the spaceProbe is launched in (angle in degrees),
			and planetRadius, the radius of the planet the spaceProbe is launched from
	*/
	public static SpaceProbe createSpaceProbeWithStartingAngle (String name, double mass, Vector2D launchPlanetPos, double velocity, double planetRadius, double launchAngle) {
		//Get a scaler according to the angle
		Vector2D angleScaler = new Vector2D(launchAngle);

		//Then compute the initialPosition and initialVelocity of the spaceProbe
		Vector2D initialPosition = new Vector2D(launchPlanetPos);
			//NOTE ! We are starting in the direction of launchAngle, at a distance of 1000 meters of the surface
		initialPosition.add(new Vector2D(angleScaler).multiply(1000 +  planetRadius));
		Vector2D initialVelocity = new Vector2D(angleScaler).multiply(velocity);


		//Finally, call the default constructor with the computed values
		return new SpaceProbe(name, mass, initialPosition, initialVelocity);
	}

	/** Needs to also take into account the force exerted by the thrusters
	*/
	@Override
	public Vector2D computeAccelerationRK (State state) {
		Vector2D accel = super.computeAccelerationRK(state);

		//Add the force exerted by the thrusters (or 0 if the thrusters are off)
		accel.add(thrusters);

		return accel;
	}

	/** This method computes whether the spaceProbe crashed into a planet or not.
		If the spaceProbe crashed into a planet, it stores the planet in which the spaceProbe crashed in "crashedPlanet"
	*/
	public boolean didNotCrash() {
		for (int i = 0; i < GUIV2.planets.length; i ++) {
			if (new Vector2D(GUIV2.planets[i].getPosition()).distance(this.getPosition()) <= GUIV2.planetRadius[i]) {
				crashedPlanet = GUIV2.planets[i];
				return false;
			}
		}

		return true;
	}

	/** Returns the value of the variable crashedPlanet
	*/
	public CelestialBody getCrashedPlanet() {
		return crashedPlanet;
	}

	/** Resets the variable crashedPlanet's value
	*/
	public void resetCrashedPlanet() {
		crashedPlanet = null;
	}

	public String toString () {
		return "SpaceProbe[mass=" + this.getMass() + ", position: " + this.getPosition().toString() + ", velocity: " + this.getVelocity().toString() + "]";
	}
}
