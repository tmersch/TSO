public class LandingModule {
	
	private double weight; // weight of landing module
	private Vector2D velocity; // Velocity of module
	private Vector2D position; // Position of module
	private double angle; // Landing module's angle of rotation

	private Thruster[] thrusters; // The side thrusters
	private double accelMain; // Acceleration of main thruster

	// Tolerance values
	private final double TOLPOSX = 0.1;
	private final double TOLANGLE = 0.02;
	private final double TOLVELX = 0.1;
	private final double TOLVELY = 0.1;
	private final double TOLROTATION = 0.01;

	private final double GRAVITYTITAN = -1.352; // gravity acceleration on titan


	/** Constructs landing module
	 *
	 * @param weight weight of module
	 * @param position position of module
	 * @param velocity velocity of module
	 */
	public LandingModule(double weight, Vector2D position, Vector2D velocity) {
		this.weight = weight;
		this.position = position;
		thrusters = new Thrusters()[];
		this.velocity = velocity;
	}

	/** Use the back thruster
	 *
	 * @return effect on modules velocity (so de-acceleration)
	 */

	public double useMainThruster() {
		// F = m * a, a = F/m

		// These are formulas from the booklet for complicated model
		//position.setX(position.getX() + accelMain * Math.sin(angle));
		//position.setY(position.getY() + accelMain * Math.cos(angle));
	}
	
	public void useSideThruster(int side) {
		
	}

	/** Simple implementation for landing on titan, only taking gravitational acceleration into account
	 *
	 * @param timestep timestep you calculated the new position and velocity over
	 */
	public void crashOnTitan(double timestep) {
		double y0 = position.getY();
		double v0 = 0; // Assuming 0 as initial velocity for now. Need to use velocity vector later
		// For now, use y position for simple model. Will use hasLanded() method later
		while (position.getY() > 0) {

			double v1 = v0 + GRAVITYTITAN*timestep;
			double y1 = y0 + v0*timestep + .5*GRAVITYTITAN*(timestep*timestep);
			position.setY(y1);

			v0 = v1;
			y0 = y1;

			// Try to keep constant speed of 10 m/s for landing
			if (v0 > 10) {
				// Activate thrusters
			}
			// Slow down for landing
			if (y0 < 1000) {
				// Activate thrusters even more!
			}

			/* Possible Runge-Kutta implementation but probably not necessary
			double k1 = timestep * (w0 + GRAVITYTITAN*currentTime);
			double k2 = timestep * (w0 + ((2/3) * k1) + GRAVITYTITAN*(currentTime+((2/3)*timestep));
			double w1 = w0 + 0.25 * k1 + 0.75 * k2;
			position.setY(w1);
			currentTime += timestep;
			w0 = w1;*/
		}
	}

	/** Determines whether module has landed.
	 * Will need to do something with tolerance values to determine this (Red booklet)
	 * @return boolean value
 	 */

	public boolean hasLanded() {
		if () {
			return true;
		}
		return false;
	}
	
}