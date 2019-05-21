public class LandingModule {
	
	private double weight; // weight of landing module (kg)
	private Vector2D acceleration = new Vector2D(0, 0); // Acceleration on module (m/s^2)
	private Vector2D velocity; // Velocity of module (m/s)
	private Vector2D position; // Position of module (m)
	private double angle; // Landing module's angle of rotation
    private double torque; // Torque is N*m, calculated by multiplying the position vector from center of mass to the point
    // where force is exerted and the perpendicular force at this point

	private double mainForce = 1000; // Force generated by main thruster ((kg*m)/s^2)

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
		this.velocity = velocity;
	}


	/** Use the back thruster. Changes the modules acceleration
	 *
	 */

	public void useMainThruster() {
		// These are formulas from the booklet
		// accel x = (mainForce/weight) * Math.sin(angle));
		// accel y = (mainForce/weight) * Math.cos(angle));
		// Different strengths of thruster
		// Constant speed + landing
		// Research max speed you want to reach
		// At a certain altitude, increase power main thruster for a safe landing
		Vector2D thrust = new Vector2D((mainForce/weight)*Math.sin(angle),(mainForce/weight)*Math.cos(angle));
		this.acceleration.add(thrust);
	}

    /** Use one of the sidethrusters
     *
     * @param side 0 for rotation left, 1 for rotation right
     */
	public void useSideThruster(final int side) {
	    if (side == 0) {
	        angle -= 1;
        }
        else if (side == 1){
            angle += 1;
        }
	}


    /** This method calls on all necessary methods to simulate the landing
     *
     */
    public void updateModule(final double timestep) {
    	double time = 0;
    	while (!hasLanded()) {
			updateAngle(); // Need wind model to make this useful, as it just stays upright otherwise
			updateAcceleration(); // Gravitational force and main thruster (+ air resistance in future?)
			updateVelocity(timestep);
			updatePosition(timestep);
			resetAcceleration();
			time += timestep;
		}
		System.out.println("Landing finished with speed " + velocity.getY() + " and time " + time);
    }

	/** Sets acceleration back to 0
	 *
	 */
	public void resetAcceleration() {
    	this.acceleration = new Vector2D(0,0);
	}

	/** Updates angle at which the module is positioned
	 *
	 */
	private void updateAngle() {
		// Need to somehow use the torque to update the angle at which the module is positioned
		// Use sidethruster to change the angle
		// Also may have to have some condition which positions the module so that it gets to the right x-coordinate
		if (angle < 1 && angle > -1 && position.getX() < TOLPOSX && position.getX() > -TOLPOSX) {
			// do nothing, angle doesn't need updating
		}
		if (position.getX() > 0.1) {
			useSideThruster(0);
		}
		if (position.getX() < -0.1) {
			useSideThruster(1);
		}
	}

	/** Updates the acceleration of module
	 *
	 */

	private void updateAcceleration() {
		boolean thrust = false;
		Vector2D gravity = new Vector2D(0, GRAVITYTITAN);
		acceleration.add(gravity);
		// Condition needs to be edited for vector use
		int value = 11000;

		if (position.getY() < value) {
			this.mainForce = 2500;
		}

		if (((velocity.getY()/(GRAVITYTITAN + (mainForce/weight)))*velocity.getY()*.5) < position.getY() + 25 && ((velocity.getY()/(GRAVITYTITAN + (mainForce/weight)))*velocity.getY()*.5) < position.getY() + 25) {
			thrust = true;
		}


		if (velocity.getY() > -5) {
			thrust = false;
		}

		if (position.getY() < 50) {
			thrust = true;
		}
		if (velocity.getY() > -TOLVELX) {
			thrust = false;
		}

		if (thrust) {
			useMainThruster();
		}

		// Add wind
	}

	/** Updates the velocity of the module
	 *
	 */
	public void updateVelocity(double timestep) {
		Vector2D addAccel = new Vector2D(acceleration);
		addAccel.multiply(timestep);
		this.velocity.add(addAccel);
	}

	/** Updates the position of the module
	 *
	 */
	public void updatePosition(double timestep) {
		Vector2D addVel = new Vector2D(velocity);
		addVel.multiply(timestep);
		Vector2D addAccel = new Vector2D(acceleration);
		addAccel.multiply((.5*timestep*timestep));
		this.position.add(addVel);
		this.position.add(addAccel);
	}

	/** Determines whether module has landed.
	 * Will need to do something with tolerance values to determine this (Red booklet)
	 * @return boolean value
	 */

	private boolean hasLanded() {
		if (position.getY() <= 0) {
			return true;
		}
		return false;
	}

	/** Simple implementation for landing on titan, only taking gravitational acceleration into account
	 * While not useful for future of project, might contain useful information
	 *
	 * @param timestep timestep you calculate the new position and velocity over (from experiments, I'd suggest using 0.1)
	 */
	/*public void landingTitan(final double timestep) {
		boolean thrust = false;
		double landingTime = 0;
		double y0 = position.getY();
		double v0 = 0; // Assuming 0 as initial velocity for now. Need to use velocity vector later
		// For now, use y position for simple model. Will use hasLanded() method later
		while (position.getY() > 0) {

			double v1 = v0 + GRAVITYTITAN*timestep;
			if (thrust) {
				v1 += useMainThruster() * timestep;
			}
			// s1 = s0 + v0*t + .5at^2
			double y1 = y0 + v0*timestep + .5*GRAVITYTITAN*(timestep*timestep);
			if (thrust) {
				y1 += .5 * useMainThruster() * (timestep * timestep);
			}
			position.setY(y1);

			v0 = v1;
			y0 = y1;

			// Slow down for landing
            // Time it takes to reach velocity 0 = vmax/(acceleration titan + acceleration main thruster)
            // Distance traveled during deacceleration = t * vmax * .5 (assuming linear deacceleration)
            if ((v0/(GRAVITYTITAN + useMainThruster())*v0*.5) < y0 + 500 && (v0/(GRAVITYTITAN + useMainThruster())*v0*.5) > y0 - 50) {
                thrust = true;
            }
            // Ensures it doesn't reach positive velocity (start going up again)
            if (v0 > TOLVELX) {
                thrust = false;
            }

			/* Possible Runge-Kutta implementation but probably not necessary
			double k1 = timestep * (w0 + GRAVITYTITAN*currentTime);
			double k2 = timestep * (w0 + ((2/3) * k1) + GRAVITYTITAN*(currentTime+((2/3)*timestep));
			double w1 = w0 + 0.25 * k1 + 0.75 * k2;
			position.setY(w1);
			currentTime += timestep;
			w0 = w1;*/
			/*landingTime += timestep;
		}*/
}


	

