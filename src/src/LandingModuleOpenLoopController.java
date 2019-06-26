import javafx.scene.shape.Rectangle;

public class LandingModuleOpenLoopController implements LandingModule {
    private double weight; // weight of landing module (kg)
    private double burntFuelMass; // mass of the fuel burnt so far in kg
    private Vector2D acceleration = new Vector2D(0, 0); // Acceleration on module (m/s^2)
    private Vector2D velocity; // Velocity of module (m/s)
    private Vector2D position; // Position of module (m)
    private double angle; // Landing module's angle of rotation

    private Rectangle rectangle;

    //The following constants are all for kerosene (massFlowRate's value not found anywhere, set randomly)
    private double massFlowRate = 10;                 //in kg/secs
    // exhaust velocity found on https://en.wikipedia.org/wiki/Liquid_rocket_propellant
    private final double exhaustVelocity = 3510; // 2941 at 1 atm            //in m/secs
    // oxidizer-to-fuel ratio for kerosene found on http://www.braeunig.us/space/propel.htm
    private final double keroseneOxidizerToFuelRatio = 2.29;

    private double mainForce = 2500; // Force generated by main thruster ((kg*m)/s^2)

    //Seconds conversion
	private static final int SEC_IN_MINUTE = 60;
  	private static final int SEC_IN_HOUR = SEC_IN_MINUTE * 60;
  	private static final int SEC_IN_DAY = SEC_IN_HOUR * 24;
  	private static final int SEC_IN_YEAR = 31556926;

    //the time-recording variable
    private double time;
    //number of iterations done so far from the starting position
    private int numIterations;

    // Tolerance values
    private final double TOLPOSX = 0.1;
    private final double TOLANGLE = 0.02;
    private final double TOLVELX = 0.1;
    private final double TOLVELY = 0.1;
    private final double TOLROTATION = 0.01;

    private final double GRAVITYTITAN = -1.352; // gravity acceleration on titan

    private final double landingDistance;

    private double forceUsed;

    /** Fully parametric constructor for the landing module open-loop controller
     *
     * @param weight weight of module
     * @param position position of module
     * @param velocity velocity of module
     * @param angle the angle of the module with respect to the y-axis
     */
    public LandingModuleOpenLoopController(double weight, Vector2D position, Vector2D velocity, double angle) {
        this.weight = weight;
        this.position = new Vector2D(position);
        this.velocity = new Vector2D(velocity);
        this.angle = angle;

        landingDistance = position.getY();
        forceUsed = 1.3519999916666666 * weight;//1352 - 10/landingDistance;

        //Initialize the mass of fuel burnt fuel
        burntFuelMass = 0;
    }

    /** Constructs a landing module with one less parameter than the full constructor
      * By default, we consider the angle to be 0
      *
      * @param weight weight of module
      * @param position position of module
      * @param velocity velocity of module
      */
    public LandingModuleOpenLoopController (double weight, Vector2D position, Vector2D velocity) {
        this(weight, position, velocity, 0);
    }

    /** This method calls on all necessary methods to simulate the landing from start until the actual landing
     *
     */
    public void updateModule(final double timestep) {
        time = 0;
        numIterations = 0;

        //System.out.println("Force used = " + forceUsed);
		System.out.println("Starting landing from \nposition = " + position + "\nvelocity = " + velocity + "\n");

        while (!hasLanded()) {
            updateModuleOneIteration(timestep);
        }

        time = numIterations * timestep;

        System.out.println("Landing finished with \nposition = " + position + "\nspeed " + velocity + "\nand time = \n" + getTimeAsString(time));
        System.out.println("Burnt Fuel Mass: " + this.getBurntFuelMass() + "\nPrize of burnt Fuel: " + this.getPrize());
    }

    /** Updates the landing module's acceleration, velocity and position for one iteration
      *
      */
    public void updateModuleOneIteration (final double timestep) {
        //System.out.printf("Iteration #%d: \nPosition = %s, \nVelocity = %s\n\n", numIterations, position, velocity);
        updateAcceleration(forceUsed, timestep); // Gravitational force and main thruster
        updateVelocity(timestep);
        updatePosition(timestep);
        resetAcceleration();
        numIterations ++;
    }

    /** Updates the acceleration of module
     *
     */
    private void updateAcceleration(double thrusterForce, double timestep) {
        Vector2D gravity = new Vector2D(0, GRAVITYTITAN);
        acceleration.add(gravity);

        useMainThruster(thrusterForce, timestep);
    }

    /** Use the back thruster. Changes the modules acceleration
     *
     */
    public void useMainThruster(double thrusterForceExerted, double timestep) {
	    // These are formulas from the booklet
	    // accel x = (mainForce/weight) * Math.sin(angle));
	    // accel y = (mainForce/weight) * Math.cos(angle));
	    // Different strengths of thruster
	    // Constant speed + landing
	    // Research max speed you want to reach
	    // At a certain altitude, increase power main thruster for a safe landing

        //System.out.println("Activated main thruster");
		Vector2D thrust = new Vector2D(Math.sin(Math.toRadians(angle)), Math.cos(Math.toRadians(angle))).multiply(thrusterForceExerted).divide(weight);

        //Compute the massFlowRate used to get that force
        massFlowRate = thrusterForceExerted/exhaustVelocity;

        //Compute the mass of the exhaustGas, then the oxidizer to Fuel Ratio and compute the mass of the consumed fuel
        double exhaustGasMass = massFlowRate * timestep;
        double burntOxidizerFactor = keroseneOxidizerToFuelRatio; // * burntFuelMass
        double consumedFuelMass = exhaustGasMass/(burntOxidizerFactor + 1);

        //Add the mass of the consumed fuel to the burnt fuel so far
        burntFuelMass += consumedFuelMass;

	    this.acceleration.add(thrust);
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
    public boolean hasLanded() {
        if (position.getY() <= 0.1) {
            return true;
        }

        return false;
    }

    /** Sets acceleration back to 0
     *
     */
    public void resetAcceleration() {
    	this.acceleration = new Vector2D(0,0);
    }

    /** Getter for the mass of the landing module
      */
    public double getMass() {
        return weight;
    }

    /** Getter for the mass of the fuel burnt so far
      */
    public double getBurntFuelMass () {
        return burntFuelMass;
    }

    /** Returns the prize of the kerosene burnt so far
      */
    public double getPrize() {
        return KerosenePrize.getPrizeOfKeroseneInEuros(this.getBurntFuelMass());
    }

    /** Getter for the position variable
      * @return a copy of the position variable
      */
    public Vector2D getPosition() {
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
}
