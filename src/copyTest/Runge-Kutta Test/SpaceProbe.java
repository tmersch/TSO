public class SpaceProbe {
	private double mass;
	private Vector2D position;
	private Vector2D velocity;
	private Vector2D acceleration = new Vector2D();
	protected Planet crashedPlanet;

	private State state;
	private Derivative derivative;

	private Derivative k1;
	private Derivative k2;
	private Derivative k3;
	private Derivative k4;

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
		return new Vector2D(state.position);
	}

	public Vector2D getVelocity() {
		return new Vector2D(state.velocity);
	}

	public Vector2D getAcceleration() {
		return new Vector2D(derivative.dVelocity);
	}

	public Derivative initialDerivative (State initial) {
		Vector2D a = computeAccelerationRK(initial);

		return new Derivative(initial.velocity.x, initial.velocity.y, a.x, a.y);
	}

	public Derivative nextDerivative (State initial, Derivative d, double deltaT) {
		State s = new State(0, 0, 0, 0);
		s.position.add(new Vector2D(initial.position.x + d.dPosition.x * deltaT, initial.position.y + d.dPosition.y * deltaT));
		s.velocity.add(new Vector2D(initial.velocity.x + d.dVelocity.x * deltaT, initial.velocity.y + d.dVelocity.y * deltaT));

		Vector2D a = computeAccelerationRK(s);
		return new Derivative(s.velocity.x, s.velocity.y, a.x, a.y);
	}

	/** This method is used to update the position of the Planets. It uses Runge-Kutta
	*/
	public void updatePosition (int step, double deltaT) {
		if (step == 1) {
			k1 = initialDerivative(state);
			k2 = nextDerivative(state, k1, deltaT/2);
		}
		else if (step == 2) {
			k3 = nextDerivative(state, k2, deltaT/2);
		}
		else if (step == 3) {
			k4 = nextDerivative(state, k3, deltaT);
		}
		else {	//step = 4
			//Compute the modifying values of x, y, v_x and v_y, according to Runge-Kutta:
			// w(i+1) = w(i) + 1/6*(k1 + 2*k2 + 2*k3 + k4);
			Vector2D dPosdt = new Vector2D().add(k1.dPosition).add(new Vector2D(k2.dPosition).multiply(2)).add(new Vector2D(k3.dPosition).multiply(2)).add(k4.dPosition).divide(6);
			Vector2D dVelocdt = new Vector2D().add(k1.dVelocity).add(new Vector2D(k2.dVelocity).multiply(2)).add(new Vector2D(k3.dVelocity).multiply(2)).add(k4.dVelocity).divide(6);

			Vector2D posAdd = new Vector2D(dPosdt).multiply(deltaT);
			state.position.add(posAdd);
			Vector2D velocAdd = new Vector2D(dVelocdt).multiply(deltaT);
			state.velocity.add(velocAdd);
		}
	}

	public Vector2D computeAccelerationRK (State state) {
		resetAcceleration();

		for (int i = 0; i < GUIV2.planets.length; i ++) {
			if (! GUIV2.planets[i].equals(this)) {
				addGToAccelerationRK(GUIV2.planets[i], state);
			}
		}

		return new Vector2D(acceleration);
	}

	public void addGToAccelerationRK (Planet other, State self) {
		//Compute the gravitational force
		//	Make a unity vector in the correct direction
		Vector2D direction = new Vector2D(self.position);
		direction.subtract(other.getPosition()).normalize().multiply(-1);

		//	Compute the distance between the two planets
		double dist = new Vector2D(self.position).distance(other.getPosition());

		//	Calculate the gravitational force
		Vector2D force = new Vector2D(direction);
		force.multiply(GUIV2.G).multiply(this.mass).multiply(other.getMass()).divide(dist * dist);

		//From that, add the corresponding acceleration to the planet's acceleration
		acceleration.add(new Vector2D(force).divide(mass));
	}

	public void resetAcceleration() {
		acceleration = new Vector2D();
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

	/* These methods are old methods used to compute trajectories using Euler's method
	public void updateVelocityAndPosition (double time) {
		//Save the velocity before applying the acceleration
		Vector2D oldVelocity = new Vector2D(this.velocity);

		//Calculate the final velocity
		velocity.add(new Vector2D(acceleration).multiply(time));

		//Update location with the averageVelocity
		position.add(new Vector2D(oldVelocity).add(velocity).divide(2.0).multiply(time));
	}

	public Vector2D computeAcceleration () {
		resetAcceleration();

		for (int i = 0; i < GUIV2.planets.length; i ++) {
			if (! GUIV2.planets[i].equals(this)) {
				addGToAcceleration(GUIV2.planets[i]);
			}
		}

		return new Vector2D(acceleration);
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
	*/
}
