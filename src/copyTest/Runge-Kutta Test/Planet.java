/** This class represents planets with their specific properties (that we need), that is
	- the name,
	- the mass,
	- the position,
	- the velocity,
	- the acceleration
*/
public class Planet {
	private String name;
	private double mass;
		//in kg
	private Vector2D position;
		//in meters
	private Vector2D velocity;
		//in meters/secs
	private Vector2D acceleration;
		//in meters/secs^2

	/** Default constructor with all parameters provided:
		@param name, the planet's name
		@param mass, the planets's mass
		@param startingPos, the planets's position at t=0
		@param startingVelocity, the planet's velocity at t=0
	*/
	public Planet (String name, double mass, Vector2D startingPos, Vector2D startingVelocity) {
		this.name = name;
		this.mass = mass;
		this.position = new Vector2D(startingPos);
		this.velocity = new Vector2D(startingVelocity);
	}

	public String getName() {
		return name;
	}

	public double getMass() {
		return mass;
	}

	public Vector2D getPosition () {
		return new Vector2D(position);
	}

	public Vector2D getVelocity() {
		return velocity;
	}

	public Vector2D getAcceleration() {
		return acceleration;
	}

	public Derivative initialDerivative (State initial) {
		Vector2D a = computeAcceleration2(initial);

		return new Derivative(initial.v_x, initial.v_y, a.x, a.y);
	}

	public Derivative nextDerivative (State initial, Derivative d, double deltaT) {
		State s = new State(0, 0, 0, 0);
		s.x = initial.x + d.dx * deltaT;
		s.y = initial.y + d.dy * deltaT;
		s.v_x = initial.v_x + d.dv_x * deltaT;
		s.v_y = initial.v_y + d.dv_y * deltaT;

		Vector2D a = computeAcceleration2(initial);
		return new Derivative(s.v_x, s.v_y, a.x, a.y);
	}

	public void updatePosition (double deltaT) {
		State initialState = new State(position, velocity);

		Derivative k1 = initialDerivative(initialState);
		Derivative k2 = nextDerivative(initialState, k1, deltaT/2);
		Derivative k3 = nextDerivative(initialState, k2, deltaT/2);
		Derivative k4 = nextDerivative(initialState, k3, deltaT);

		double dxdt = 1.0/6.0 * (k1.dx + 2*(k2.dx + k3.dx) + k4.dx);
		double dydt = 1.0/6.0 * (k1.dy + 2*(k2.dy + k3.dy) + k4.dy);
		double dv_xdt = 1.0/6.0 * (k1.dv_x + 2*(k2.dv_x + k3.dv_x) + k4.dv_x);
		double dv_ydt = 1.0/6.0 * (k1.dv_y + 2*(k2.dv_y + k3.dv_y) + k4.dv_y);

		Vector2D posAdd = new Vector2D(dxdt, dydt).multiply(deltaT);
		position.add(posAdd);
		Vector2D velocAdd = new Vector2D(dv_xdt, dv_ydt).multiply(deltaT);
		velocity.add(velocAdd);
	}

	public Vector2D computeAcceleration2 (State state) {
		resetAcceleration();

		for (int i = 0; i < GUIV2.planets.length; i ++) {
			if (! GUIV2.planets[i].equals(this)) {
				addGToAcceleration2(GUIV2.planets[i], state);
			}
		}

		return new Vector2D(acceleration);
	}

	public void addGToAcceleration2 (Planet other, State self) {
		//Compute the gravitational force
		//	Make a unity vector in the correct direction
		Vector2D direction = new Vector2D(self.x, self.y);
		direction.subtract(other.getPosition()).normalize().multiply(-1);

		//	Compute the distance between the two planets
		double dist = new Vector2D(self.x, self.y).distance(other.getPosition());

		//	Calculate the gravitational force
		Vector2D force = new Vector2D(direction);
		force.multiply(GUIV2.G).multiply(this.mass).multiply(other.getMass()).divide(dist * dist);

		//From that, add the corresponding acceleration to the planet's acceleration
		acceleration.add(new Vector2D(force).divide(mass));
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
}
