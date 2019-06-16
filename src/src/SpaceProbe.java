public class SpaceProbe extends CelestialBody {
	private Vector2D positionWithRespectToCrashedPlanet;
	private CelestialBody crashedPlanet;
	private boolean crashed;

	/** Default constructor for SpaceProbe with all parameters
		It has parameters
			@param name which is the name of the SpaceProbe,
			@param mass which represents the mass of the space probe (constant, because it has no engines that could burn up fuel),
			@param startingPos[] which represents the starting position of the SpaceProbe and
			@param startingV[] which represents the starting velocities (on x and y)
	  */
	public SpaceProbe (String name, double mass, Vector2D startingPos, Vector2D startingV) {
		super(name, mass, startingPos, startingV);

		//Initialize crashed to false
		crashed = false;
	}

	/** Additional indirect constructor for SpaceProbe
		It has some parameters that are the same than in the default constructor (name and mass), but also new parameters:
			launchPlanetPos, the position of the center of the planet the spaceProbe is launched from,
			velocity, the starting speed of the spaceProbe,
			launchAngle, the angle which the spaceProbe is launched in (angle in degrees),
			and planetRadius, the radius of the planet the spaceProbe is launched from
	*/
	public static SpaceProbe createSpaceProbeWithStartingAngle (String name, double mass, CelestialBody launchPlanet, double velocity, double launchAngle) {
		//Get a scaler according to the angle
		Vector2D angleScaler = new Vector2D(launchAngle);

		//Then compute the initialPosition and initialVelocity of the spaceProbe
		Vector2D initialPosition = new Vector2D(launchPlanet.getPosition());
			//NOTE ! We are starting in the direction of launchAngle, at a distance of 1000 meters of the surface
		initialPosition.add(new Vector2D(angleScaler).multiply(1000 + launchPlanet.getRadius()));
		Vector2D initialVelocity = new Vector2D(angleScaler).multiply(velocity);


		//Finally, call the default constructor with the computed values
		return new SpaceProbe(name, mass, initialPosition, initialVelocity);
	}

	/** This method computes whether the spaceProbe crashed into a planet or not.
		If the spaceProbe crashed into a planet, it stores the planet in which the spaceProbe crashed in "crashedPlanet"
	*/
	public boolean didNotCrash() {
		if (crashed) {
			return false;
		} else {
			for (int i = 0; i < GUIV2.planets.length; i ++) {
				if (new Vector2D(GUIV2.planets[i].getPosition()).distance(this.getPosition()) <= GUIV2.planets[i].getRadius()) {
					crashedPlanet = GUIV2.planets[i];
					positionWithRespectToCrashedPlanet = new Vector2D(this.getPosition()).subtract(crashedPlanet.getPosition());
					crashed = true;
					return false;
				}
			}

			return true;
		}
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
		crashed = false;
	}

	/** Returns the result of the super-class method unless the spacecraft has crashed on a planet
	  * If the spaceProbe has crashed on a planet, we make it follow the planet's position,
	  * 	with a difference in position equals to the relative position of the spaceProbe with respect to the planet
	  */
	@Override
	public Vector2D getPosition () {
		if (! crashed) {
			return super.getPosition();
		}
		else {
			Vector2D crashedPos = new Vector2D(crashedPlanet.getPosition()).add(positionWithRespectToCrashedPlanet);
			return crashedPos;
		}
	}

	@Override
	public String toString () {
		return "SpaceProbe[mass=" + this.getMass() + ", position: " + this.getPosition().toString() + ", velocity: " + this.getVelocity().toString() + "]";
	}

	/** Returns a deep copy of this object at its current state
	  */
	@Override
	public SpaceProbe clone() {
		//Retrieve this object's current properties and save them
		String name = this.getName();			//Safe, because strings are immutable
		double mass = this.getMass();
		Vector2D pos = this.getPosition();		//Safe, because the constructor uses defensive copying
		Vector2D vel = this.getVelocity();

		//Then use these values to create a new SpaceProbe with the same properties
		return new SpaceProbe(name, mass, pos, vel);
	}
}
