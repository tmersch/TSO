import javafx.scene.shape.Rectangle;

// Class that handles the landing module getting from the surface of Titan back into orbit

public class LandingModuleTakeoff extends LandingModuleFeedbackController {

    private Vector2D goal;

    /** Full parametric constructor. Note that starting position and velocity is taken as Vector2D(0, 0) and angle as 0
     *  Doesn't make sense to launch with an existing velocity or with a certain angle
     *
     * @param weight weight of module
     * @param destination destination of
     * @param addWind
     */
    public LandingModuleTakeoff(double weight, Vector2D destination, boolean addWind) {
        super(weight, new Vector2D(0,0), new Vector2D(0,0), 0, addWind);
        this.goal = destination;
    }

    /** Additional constructor with one less parameter: addWind
      * By default, we set addWind to false
      */
    public LandingModuleTakeoff(double weight, Vector2D destination) {
        this(weight, destination, false);
    }

    //This method overrides updateModule() of parent
    @Override
    public void updateModule(final double timestep) {
        time = 0;
        numIterations = 0;

        System.out.println("Starting landing from \nposition = " + position + "\nvelocity = " + velocity + "\nangle = " + angle + "\nWind taken into account: " + considerWind);

        while (!reachedGoal()) {
            updateModuleOneIteration(timestep);
        }

        time = numIterations * timestep;

        System.out.println("\nGoal reached with \nposition = " + position + "\nvelocity = " + velocity + "\nangle = " + angle + "\nand time = \n  " + getTimeAsString(time));
        System.out.println("Burnt fuel Mass: " + this.getBurntFuelMass() + "\nPrize of the burnt fuel (in Euros): " + this.getPrize());
    }

    //This method overrides correctXPosition() of parent
    // TODO x-position overshoots by a large margin; try to adjust angle based on velocity as well
    @Override
    public void correctXPosition() {
        if (position.getX() == goal.getX() || velocity.getX() > 20 || velocity.getX() < -20) {
            if (angle < 0 || velocity.getX() < -20) {
                angle += angleChange;
            }
            else if (angle > 0 || velocity.getX() > 20) {
                angle -= angleChange;
            }
        }
        else if (position.getX() < goal.getX()) {
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
        else { //then position.getX() > goal.getX()
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
            else if (velocity.getX() < 0) {    //Otherwise, if we are going back to the correct x-position,
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

    //This method overrides updateAcceleration() of parent
    @Override
    public void updateAcceleration(final double timestep, final double thrusterForceUsed) {
        Vector2D gravity = new Vector2D(0, GRAVITYTITAN);
        acceleration.add(gravity);
        if(considerWind) {
            super.wind();
        }



        /*if (position.getX() > (goal.getX() + 100) && angle < 0) { // TODO Something with the x position/velocity
            useMainThruster(timestep);
        }
        else if (position.getX() < (goal.getX() - 100) && angle > 0) {
            useMainThruster(timestep);
        }*/
        if (goal.getY() - position.getY() < (velocity.getY()/(-GRAVITYTITAN)) * velocity.getY() * .5) {
            //don't thrust
        }
        else {
            useMainThruster(timestep);
        }
    }

    // Comparable to hasLanded() method of superclass, checks whether module has reached destination
    private boolean reachedGoal() {
        if (position.getY() > goal.getY()) {
            return true;
        }
        return false;
    }
}
