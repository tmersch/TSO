import javafx.scene.shape.Rectangle;

public class LandingModuleFeedbackController implements LandingModule {
    protected double weight; // weight of landing module (kg)
    protected double burntFuelMass;       //in kgs
    protected Vector2D acceleration = new Vector2D(0, 0); // Acceleration on module (m/s^2)
    protected Vector2D velocity; // Velocity of module (m/s)
    protected Vector2D position; // Position of module (m)
    protected double angle; // Landing module's angle of rotation

    //The following constants are all for kerosene (massFlowRate's value not found anywhere, set randomly)
    protected double massFlowRate = 10;                 //in kg/secs
    // exhaust velocity found on https://en.wikipedia.org/wiki/Liquid_rocket_propellant
    protected final double exhaustVelocity = 3510; //2941 at 1 atm            //in m/secs
    // oxidizer-to-fuel ratio for kerosene found on https://en.wikipedia.org/wiki/RP-1
    protected final double keroseneOxidizerToFuelRatio = 2.56;

    protected Rectangle rectangle;

    protected double time;
    protected int numIterations;

    // Tolerance values
    protected final double TOLPOSX = 0.1;
    protected final double TOLANGLE = 0.02;
    protected final double TOLVELX = 0.1;
    protected final double TOLVELY = 0.1;
    protected final double TOLROTATION = 0.01;

    //Seconds conversion
    protected static final int SEC_IN_MINUTE = 60;
    protected static final int SEC_IN_HOUR = SEC_IN_MINUTE * 60;
    protected static final int SEC_IN_DAY = SEC_IN_HOUR * 24;
    protected static final int SEC_IN_YEAR = 31556926;

    protected final double GRAVITYTITAN = -1.352; // gravity acceleration on titan

    protected static double mainForce = 2500; // Force generated by main thruster ()(kg*m)/s^2)
    protected final double MAXANGLE = 45;	//maximum angle the spaceship should have with respect to the y-axis (in degrees)
    protected double MINMAINFORCE = mainForce * Math.cos(Math.toRadians(MAXANGLE));

    protected final double MINANGLEXCORRECTION = 2;	//minimum angle to keep outside of the [-TOLPOSX, TOLPOSX] x-position

    protected final boolean considerWind;
    protected static double MAXWINDFORCE = 2;

    protected double angleChange = 1;            //the amount of degrees which we maximally change at each iteration

    protected int numMaxAngleIterations = 0;		//number of times when the landing module's x-position is out of the interval [-TOLPOSX, TOLPOSX] and the angle should have gone over MAXANGLE (or under -MAXANGLE)

    /** Fully parametric constructor for the landing module feedback controller
     *
     * @param weight weight of module
     * @param position position of module
     * @param velocity velocity of module
	 * @param angle the angle of the module with respect to the y-axis
     * @param addWind whether we add randomized wind to the simulation or not
     */
    public LandingModuleFeedbackController (double weight, Vector2D position, Vector2D velocity, double angle, boolean addWind) {
        this.weight = weight;
 		this.position = new Vector2D(position);
 		this.velocity = new Vector2D(velocity);
 		this.angle = angle;
        this.considerWind = addWind;

        //Set the burntFuelMass to 0 by default
        this.burntFuelMass = 0;
    }

    /** Constructs a landing module with one less parameter than the full constructor
      * By default, we do NOT add wind to the simulation
      *
      * @param weight weight of module
      * @param position position of module
      * @param velocity velocity of module
	  * @param angle the angle of the module with respect to the y-axis
      */
	public LandingModuleFeedbackController (double weight, Vector2D position, Vector2D velocity, double angle) {
 		this(weight, position, velocity, angle, false);
 	}

    /** Constructs a landing module with one less parameter than the full constructor
      * By default, we consider the angle to be 0
      *
      * @param weight weight of module
      * @param position position of module
      * @param velocity velocity of module
 	  * @param addWind whether we add randomized wind to the simulation or not
      */
    public LandingModuleFeedbackController (double weight, Vector2D position, Vector2D velocity, boolean addWind) {
        this(weight, position, velocity, 0, addWind);
    }

	/** Constructs a landing module with two less parameters than the full constructor
      * By default, we do NOT add wind to the simulation and consider the angle to be 0
      *
      * @param weight weight of module
      * @param position position of module
      * @param velocity velocity of module
      */
    public LandingModuleFeedbackController (double weight, Vector2D position, Vector2D velocity) {
		this(weight, position, velocity, 0, false);
    }

    public LandingModuleFeedbackController() {
        this(800, new Vector2D(0,0), new Vector2D(0,0), 0, false);
    }

    /** This method calls on all necessary methods to simulate the landing
     *
     */
    public void updateModule(final double timestep) {
        time = 0;
		numIterations = 0;

		System.out.println("Starting landing from \nposition = " + position + "\nvelocity = " + velocity + "\nangle = " + angle + "\nWind taken into account: " + considerWind);

        while (!hasLanded()) {
	        updateModuleOneIteration(timestep);
        }

		time = numIterations * timestep;

		System.out.println("\nLanding finished with \nposition = " + position + "\nvelocity = " + velocity + "\nangle = " + angle + "\nand time = \n  " + getTimeAsString(time));
        System.out.println("Burnt fuel Mass: " + this.getBurntFuelMass() + "\nPrize of the burnt fuel (in Euros): " + this.getPrize());
	}

    /** Updates the landing module's acceleration, velocity and position for one iteration
      *
      */
	public void updateModuleOneIteration(final double timestep) {
		//System.out.printf("Iteration #%d: \n", numIterations);
		correctXPosition();
		updateAcceleration(timestep, MINMAINFORCE);	// Gravitational force and main thruster (+ air resistance in future?)
		updateVelocity(timestep);
		updatePosition(timestep);
		resetAcceleration();
		numIterations ++;
		//System.out.println();
	}

	/** Should correct the x-position by turning the landing module (and thus modify the angle)
	  */
	public void correctXPosition() {
		if (position.getX() >= -TOLPOSX && position.getX() <= TOLPOSX) {
            if (angle < -TOLANGLE) {
				angle += angleChange;
			}
			else if (angle > TOLANGLE) {
				angle -= angleChange;
			}
			//else the angle is ok and nothing has to be done
		}
		else if (position.getX() < -TOLPOSX) {
            if (angle < 0) {
                angle += angleChange;
            }
			else if (velocity.getX() < 0) {					//if we continue to go away from the correct x-position,
				if (angle < MAXANGLE) {					//try to make the angle bigger to go back to the correct x-position
					if (angle+angleChange <= MAXANGLE) {
						angle += angleChange;
					}
					else {
						angle = MAXANGLE;
						numMaxAngleIterations ++;
					}
				}
				else {
					numMaxAngleIterations ++;
				}
			}
			else if (velocity.getX() > 0) {										//otherwise, if we are going back to the correct x-position,
				if (angle > MINANGLEXCORRECTION) {		//try to make the angle smaller if numMaxAngleIterations == 0
					if (numMaxAngleIterations == 0) {
						if (angle-angleChange > MINANGLEXCORRECTION) {
							angle -= angleChange;
						}
						else {
							angle = MINANGLEXCORRECTION;
						}
					}
                    else {
                        numMaxAngleIterations --;
                    }
				}
                else {
                    if (angle+angleChange <= MINANGLEXCORRECTION) {
                        angle += angleChange;
                    }
                    else {
                        angle = MINANGLEXCORRECTION;
                    }
                }
			}
            else {                                          //x-velocity is 0, but x-position is < -TOLPOSX
                if (angle < MAXANGLE) {                     //try to make the angle bigger, but don't make it bigger than MAXANGLE (angle is >= 0)
                    if (angle+angleChange < MAXANGLE) {
                        angle += angleChange;
                    }
                    else {
                        angle = MAXANGLE;
                    }
                }
            }
		}
		else { //then position.getX() > TOLPOSX
            if (angle > 0) {
                angle -= angleChange;
            }
			else if (velocity.getX() > 0) {					//If we are going away from the correct x-position,
				if (angle > -MAXANGLE) {				//try to make the angle more negative to correct the x-position
					if (angle-angleChange >= -MAXANGLE) {
						angle -= angleChange;
					}
					else {
						angle = -MAXANGLE;
						numMaxAngleIterations ++;
					}
				}
				else {
					numMaxAngleIterations ++;
				}
			}
			else if (velocity.getX() < 0) {			       //Otherwise, if we are going back to the correct x-position,
				if (angle < -MINANGLEXCORRECTION) {		   //try to make the angle less negative if numMaxAngleIterations == 0
					if (numMaxAngleIterations == 0) {
						if (angle+angleChange < -MINANGLEXCORRECTION) {
							angle += angleChange;
						}
						else {
							angle = -MINANGLEXCORRECTION;
						}
					}
					else {
						numMaxAngleIterations --;
					}
				}
                else {
                    if (angle-angleChange >= - MINANGLEXCORRECTION) {
                        angle -= angleChange;
                    }
                    else {
                        angle = -MINANGLEXCORRECTION;
                    }
                }
			}
            else {          //x-velocity is 0, but the x-position is bigger than TOLPOSX
                if (angle > -MAXANGLE) {                    //try to make the angle smaller, but don't make it smaller than -MAXANGLE (angle is <= 0)
                    if (angle-angleChange > -MAXANGLE) {
                        angle -= angleChange;
                    }
                    else {
                        angle = -MAXANGLE;
                    }
                }
            }
		}
	}

	/** Updates the acceleration of module
	 */
	public void updateAcceleration(final double timestep, final double thrusterForceUsed) {
        boolean thrust = false;
        Vector2D gravity = new Vector2D(0, GRAVITYTITAN);
        acceleration.add(gravity);

        if (considerWind) {
            wind();
        }

        //If the braking distance we would need to get to a full stop is smaller than the next position, we activate the thruster
        if (computeBrakingDistance(thrusterForceUsed) < position.getY() + velocity.getY()*timestep) {
            thrust = true;
        }
        //Unless the velocity is smaller than 5 m/secs in direction of Titan
        if (velocity.getY() > -5) {
            thrust = false;
        }
        //But we still use the thrusters to brake when we are close to the ground, even if the velocity gets smaller than 5 m/secs
        if (position.getY() < 50) {
            thrust = true;
        }
        //However, if the velocity gets smaller than the tolerance for the velocity on y, we stop using the thrusters
        if (velocity.getY() > -TOLVELY) {
            thrust = false;
        }

        //If the previous checks determined that we had to use the thruster, we use it
        if (thrust) {
            useMainThruster(timestep);
        }
    }

    /** Creates a randomized force of a maximum strength of MAXWINDFORCE in a random direction and adds it to the current acceleration
	  */
	protected void wind() {
		  Vector2D wind = new Vector2D(Math.random()*2 - 1, Math.random()*2-1).normalize().multiply(Math.random() * MAXWINDFORCE);

		  acceleration.add(wind);
	  }

	/** Computes the distance the landing module will travel before coming to a stop
	  *	@return a double value representing the distance needed to brake down to y-velocity = 0
	  */
	private double computeBrakingDistance(final double thrusterForce) {
		//Compute the time needed to brake to a velocity of 0
		double timeToGetToVelocity0 = computeBrakingTime(thrusterForce);

		//Apply formula: distance = time * maxVelocity * 1/2
		double brakingDistance = timeToGetToVelocity0 * velocity.getY() * 0.5;

		return brakingDistance;
	}

	/** Computes the braking time
	  * NOTE !!! This computation will be affected by the addition of the wind !!!
	  * @return the time necessary for the landing module to slow down to velocity = 0
	  */
	private double computeBrakingTime(final double thrusterForce) {
		//Apply formula timeToGetToVelocity0 = maxVelocity / (gravityAcceleration + thrustAcceleration)
		// where thrustAcceleration is computed as
		double brakingTime = velocity.getY() / (GRAVITYTITAN + (thrusterForce/weight));

		return brakingTime;
	}

    /** Use the back thruster. Changes the modules acceleration
     *
     */
    public void useMainThruster(final double timestep) {
        //Determine the thrusterForceto to apply in order to always have a force equal to minMainForce on the y-axis
        double thrusterForceExerted = MINMAINFORCE/Math.cos(Math.toRadians(angle));

        //Then apply that force on the x- and y-axes to get the acceleration using the following formulas from the booklet:
	    // accel x = (mainForce/weight) * Math.sin(angle));
	    // accel y = (mainForce/weight) * Math.cos(angle));
        //The angle is 0 if it goes straight down
        Vector2D thrust = new Vector2D(Math.sin(Math.toRadians(angle)), Math.cos(Math.toRadians(angle))).multiply(thrusterForceExerted).divide(weight);

        //Compute the massFlowRate used to get that force
        massFlowRate = thrusterForceExerted/exhaustVelocity;

        //Compute the mass of the exhaustGas, then the oxidizer to Fuel Ratio and compute the mass of the consumed fuel
        double exhaustGasMass = massFlowRate * timestep;
        double burntOxidizerFactor = keroseneOxidizerToFuelRatio; // * burntFuelMass
        double consumedFuelMass = exhaustGasMass/(burntOxidizerFactor + 1);

        //Add the mass of the consumed fuel to the burnt fuel so far
        burntFuelMass += consumedFuelMass;

        //And add that acceleration to the global acceleration for this iteration
        this.acceleration.add(thrust);
    }

    /** Returns the mass of the landingModule
      */
    public double getMass() {
        return weight;
    }

    /** Getter for the mass of the burnt fuel
      */
    public double getBurntFuelMass () {
        return burntFuelMass;
    }

    /** Returns the prize of the kerosene burnt during the trip in Euros
      */
    public double getPrize () {
        return KerosenePrize.getPrizeOfKeroseneInEuros(this.getBurntFuelMass());
    }

    /** Updates the velocity of the module
     *
     */
    public void updateVelocity(final double timestep) {
        Vector2D addAccel = new Vector2D(acceleration);
        addAccel.multiply(timestep);
        this.velocity.add(addAccel);
    }

    /** Updates the position of the module
     *
     */
    public void updatePosition(final double timestep) {
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
    public boolean hasLanded() {
        if (position.getY() <= 0) {
            return true;
        }

        return false;
    }

    /** Sets acceleration back to 0
     *
     */
    public void resetAcceleration() {
    	this.acceleration = new Vector2D(0, 0);
    }

    /** Getter for the position variable
      * @return a copy of the position variable
      */
	public Vector2D getPosition () {
		return new Vector2D(position);
	}

    /** Getter for the velocity variable
      * @return a copy of the velocity variable
      */
    public Vector2D getVelocity() {
		return new Vector2D(velocity);
	}

    /** Getter for the angle variable
      * @return the value angle variable
      */
    public double getAngle() {
        return angle;
    }

	/** Mainly for debugging purposes, could be deleted in the end product

		@param time, a given number of seconds

		@return a nicely formatted string expressing the time parameter in years, days, minutes and seconds
	*/
	protected String getTimeAsString (double time) {
		long years = (long)(time / SEC_IN_YEAR);
    	long days = (long)((time % SEC_IN_YEAR) / SEC_IN_DAY);
    	long hours = (long)(((time % SEC_IN_YEAR) % SEC_IN_DAY) / SEC_IN_HOUR);
    	long minutes = (long)((((time % SEC_IN_YEAR) % SEC_IN_DAY) % SEC_IN_HOUR) / SEC_IN_MINUTE);
    	double seconds = (((time % SEC_IN_YEAR) % SEC_IN_DAY) % SEC_IN_HOUR) % SEC_IN_MINUTE;
    	return String.format("Years:%08d, Days:%03d, Hours:%02d, Minutes:%02d, Seconds:%02.4f", years, days, hours, minutes, seconds);
	}

    /** Setter for the rectangle
      *
      * @param rect the new rectangle to set the "rectangle" variable to
      */
    public void setRectangle (Rectangle rect) {
        rectangle = rect;
    }

    /** Getter for the rectangle variable
      * @return the rectangle
      */
    public Rectangle getRectangle () {
        return rectangle;
    }

    public void setMaxWindStrength (double newWind) {
        MAXWINDFORCE = newWind;
    }

    public void setThrusterForce (double thForce) {
        mainForce = thForce;
        MINMAINFORCE = mainForce * Math.cos(Math.toRadians(MAXANGLE));
    }

    public static double getMaxWindStrength () {
        return MAXWINDFORCE;
    }

    public static double getThrusterForce () {
        return mainForce;
    }
}
