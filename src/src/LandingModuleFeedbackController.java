import javafx.scene.shape.Rectangle;

public class LandingModuleFeedbackController implements LandingModule {
    private double weight; // weight of landing module (kg)
    private Vector2D acceleration = new Vector2D(0, 0); // Acceleration on module (m/s^2)
    private Vector2D velocity; // Velocity of module (m/s)
    private Vector2D position; // Position of module (m)
    private double angle; // Landing module's angle of rotation

    private Rectangle rectangle;

	private double time;
	private int numIterations;

    // Tolerance values
    private final double TOLPOSX = 0.1;
    private final double TOLANGLE = 0.02;
    private final double TOLVELX = 0.1;
    private final double TOLVELY = 0.1;
    private final double TOLROTATION = 0.01;

    //Seconds conversion
	private static final int SEC_IN_MINUTE = 60;
  	private static final int SEC_IN_HOUR = SEC_IN_MINUTE * 60;
  	private static final int SEC_IN_DAY = SEC_IN_HOUR * 24;
  	private static final int SEC_IN_YEAR = 31556926;

    private final double GRAVITYTITAN = -1.352; // gravity acceleration on titan

	private double mainForce = 2500; // Force generated by main thruster ()(kg*m)/s^2)
    private final double MAXANGLE = 45;	//maximum angle the spaceship should have with respect to the y-axis (in degrees)
	private double MINMAINFORCE = mainForce * Math.cos(Math.toRadians(MAXANGLE));

	private final double MINANGLEXCORRECTION = 2;	//minimum angle to keep outside of the [-TOLPOSX, TOLPOSX] x-position

    private final boolean considerWind;
	private double MAXWINDFORCE = 2;

	private double angleChange = 1;            //the amount of degrees which we maximally change at each iteration

	private int numMaxAngleIterations = 0;		//number of times when the landing module's x-position is out of the interval [-TOLPOSX, TOLPOSX] and the angle should have gone over MAXANGLE (or under -MAXANGLE)

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
	  *
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
							angle -= MINANGLEXCORRECTION;
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
	 *
	 */
	private void updateAcceleration(final double timestep, final double thrusterForceUsed) {
        boolean thrust = false;
        Vector2D gravity = new Vector2D(0, GRAVITYTITAN);
        acceleration.add(gravity);

        if (considerWind) {
            wind();
        }

        if (computeBrakingDistance(thrusterForceUsed) < position.getY() + Math.abs(velocity.getY())*timestep && computeBrakingDistance(thrusterForceUsed) < position.getY() + Math.abs(velocity.getY())*timestep) {
            thrust = true;
        }

        if (velocity.getY() > -5) {
            thrust = false;
        }
        if (position.getY() < 50) {
            /*
            if (velocity.getY() > -5) {
                thrust = false;
            }
            else {
            */
                thrust = true;
            //}
        }
        if (velocity.getY() > -TOLVELY) {
            thrust = false;
        }

        if (thrust) {
            useMainThruster(thrusterForceUsed);
        }
    }

    /** Creates a randomized force of a maximum strength of MAXWINDFORCE in a random direction and adds it to the current acceleration
	  *
	  */
	private void wind() {
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
    public void useMainThruster(double thrusterForceExerted) {
	    // These are formulas from the booklet
	    // accel x = (mainForce/weight) * Math.sin(angle));
	    // accel y = (mainForce/weight) * Math.cos(angle));
	    // Different strengths of thruster
	    // Constant speed + landing
	    // Research max speed you want to reach
	    // At a certain altitude, increase power main thruster for a safe landing

		//System.out.println("Activated main thruster");
		Vector2D thrust = new Vector2D(Math.sin(Math.toRadians(angle)), Math.cos(Math.toRadians(angle))).multiply(thrusterForceExerted).divide(weight);
	    this.acceleration.add(thrust);
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
	private String getTimeAsString (double time) {
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
}
