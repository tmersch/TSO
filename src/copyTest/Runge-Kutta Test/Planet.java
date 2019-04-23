public class Planet {
	private String name;
	private double mass;
		//in kg
	private Vector2D position;
	private Vector2D velocity;
	private Vector2D acceleration;

	/** Class representing the state of the planet, the position and velocity in x and y
	*/
	public class State {
		protected double x, y, v_x, v_y;

		public State (double x, double y, double v_x, double v_y) {
			this.x = x;
			this.y = y;
			this.v_x = v_x;
			this.v_y = v_y;
		}

		public State (Vector2D pos, Vector2D vel) {
			this.x = pos.x;
			this.y = pos.y;
			this.v_x = vel.x;
			this.v_y = vel.y;
		}
	}

	/** Class representing the dervatives of the position and velocities, in x and y
	*/
	public class Derivative {
		protected double dx, dy, dv_x, dv_y;

		public Derivative (double dx, double dy, double dv_x, double dv_y) {
			this.dx = dx;
			this.dy = dy;
			this.dv_x = dv_x;
			this.dv_y = dv_y;
		}

		public Derivative (Vector2D dPos, Vector2D dVel) {
			this.dx = dPos.x;
			this.dy = dPos.y;
			this.dv_x = dVel.x;
			this.dv_y = dVel.y;
		}
	}

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

	public void updatePosition (double time, double deltaT) {
		State initialState = new State(position, velocity);

		Derivative a = initialDerivative(initialState);
		//Vector2D a_a = computeAcceleration();
		//Vector2D v_a = new Vector2D(velocity);

		Derivative b = nextDerivative(initialState, a, deltaT/2);
		//position.add(new Vector2D(v_a).multiply(deltaT/2));
		//velocity.add(new Vector2D(a_a).multiply(deltaT/2));
		//Vector2D v_b = new Vector2D(velocity);
		//Vector2D a_b = computeAcceleration();

		Derivative c = nextDerivative(initialState, b, deltaT/2);
		//position.add(new Vector2D(v_b).multiply(deltaT/2));
		//velocity.add(new Vector2D(a_b).multiply(deltaT/2));
		//Vector2D v_c = new Vector2D(velocity);
		//Vector2D a_c = computeAcceleration();

		Derivative d = nextDerivative(initialState, c, deltaT);
		//position.add(new Vector2D(v_c).multiply(deltaT));
		//velocity.add(new Vector2D(a_c).multiply(deltaT));
		//Vector2D v_d = new Vector2D(velocity);
		//Vector2D a_d = computeAcceleration();

		double dxdt = 1.0/6.0 * (a.dx + 2*(b.dx + c.dx) + d.dx);
		double dydt = 1.0/6.0 * (a.dy + 2*(b.dy + c.dy) + d.dy);
		double dv_xdt = 1.0/6.0 * (a.dv_x + 2*(b.dv_x + c.dv_x) + d.dv_x);
		double dv_ydt = 1.0/6.0 * (a.dv_y + 2*(b.dv_y + c.dv_y) + d.dv_y);

		Vector2D posAdd = new Vector2D(dxdt, dydt).multiply(deltaT);
		position.add(posAdd);
		Vector2D velocAdd = new Vector2D(dv_xdt, dv_ydt).multiply(deltaT);
		velocity.add(velocAdd);
		//position.add(new Vector2D(v_a).add(v_b.multiply(2)).add(v_c.multiply(2)).add(v_d).divide(6)).multiply(deltaT);
		//velocity.add(new Vector2D(a_a).add(a_b.multiply(2)).add(a_c.multiply(2)).add(a_d).divide(6)).multiply(deltaT);
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
