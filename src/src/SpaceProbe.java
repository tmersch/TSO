public class SpaceProbe extends CelestialBody {
	private CelestialBody crashedPlanet;

	/** Constructor for SpaceProbe.
		It has parameters name, which is the name of the SpaceProbe,
			mass, which represents the mass of the space probe (constant, because it has no engines that could burn up fuel),
			startingPos[] which represents the starting position of the SpaceProbe,
			and startingV[] which represents the starting velocities (on x and y)
	*/
	public SpaceProbe (String name, double mass, Vector2D startingPos, Vector2D startingV) {
		super(name, mass, startingPos, startingV);
	}

	public boolean didNotCrash() {
		for (int i = 0; i < GUIV2.planets.length; i ++) {
			if (new Vector2D(GUIV2.planets[i].getPosition()).distance(this.getPosition()) <= GUIV2.planetRadius[i]) {
				crashedPlanet = GUIV2.planets[i];
				return false;
			}
		}

		return true;
	}

	public CelestialBody getCrashedPlanet() {
		return crashedPlanet;
	}

	public void resetCrashedPlanet() {
		crashedPlanet = null;
	}

	public String toString () {
		return "SpaceProbe[mass=" + this.getMass() + ", position: " + this.getPosition().toString() + ", velocity: " + this.getVelocity().toString() + "]";
	}
}
