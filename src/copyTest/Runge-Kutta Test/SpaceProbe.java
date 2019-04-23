public class SpaceProbe {
	private double mass;
	private Vector2D position;
	private Vector2D velocity;
	private Vector2D acceleration = new Vector2D();
	protected Planet crashedPlanet;

	/** Constructor for SpaceProbe.
		It has parameters mass, which represents the mass of the space probe (constant, because it has no engines that could burn up fuel),
			startingPos[] which represents the starting position of the SpaceProbe,
			and startingV[] which represents the starting velocities (on x and y)
	*/
	public SpaceProbe (double mass, Vector2D startingPos, Vector2D startingV) {
		this.mass = mass;
		position = new Vector2D(startingPos);
		velocity = new Vector2D(startingV);
	}

	public double getMass() {
		return mass;
	}

	public Vector2D getPosition () {
		return position;
	}

	public Vector2D getVelocity() {
		return velocity;
	}

	public Vector2D getAcceleration() {
		return acceleration;
	}

	public void addGToAcceleration (Planet other) {
		//Compute the gravitational force
		//	Make a unity vector in the correct direction
		Vector2D direction = new Vector2D(this.position);
		direction.subtract(other.getPosition()).normalize().multiply(-1);

		//	Compute the distance between the two planets
		double dist = this.position.distance(other.getPosition());

		//	Calculate the gravitational force
		Vector2D force = new Vector2D(direction);
		force.multiply(GUIV2.G).multiply(this.mass).multiply(other.getMass()).divide(dist * dist);

		//From that, add the corresponding acceleration to the planet's acceleration
		acceleration.add(new Vector2D(force).divide(mass));
	}

	public void resetAcceleration() {
		acceleration = new Vector2D();
	}

	public void updateVelocityAndPosition (double time) {
		//Save the velocity before applying the acceleration
		Vector2D oldVelocity = new Vector2D(this.velocity);

		//Calculate the final velocity
		velocity.add(new Vector2D(acceleration).multiply(time));

		//Update location with the averageVelocity
		position.add(new Vector2D(oldVelocity).add(velocity).divide(2.0).multiply(time));
	}

	public boolean didNotCrash() {
		for (int i = 0; i < GUIV2.planets.length; i ++) {
			if (new Vector2D(GUIV2.planets[i].getPosition()).distance(position) <= GUIV2.planetRadius[i]) {
				crashedPlanet = GUIV2.planets[i];
				return false;
			}
		}

		return true;
	}

	public Planet getCrashedPlanet() {
		return crashedPlanet;
	}

	public void resetCrashedPlanet() {
		crashedPlanet = null;
	}

	public String toString () {
		return "SpaceProbe[mass=" + mass + ", position: " + position.toString() + ", velocity: " + velocity.toString() + "]";
	}
}
