public class LandingModuleOpenLoopController {
    private double weight; // weight of landing module (kg)
    private Vector2D acceleration = new Vector2D(0, 0); // Acceleration on module (m/s^2)
    private Vector2D velocity; // Velocity of module (m/s)
    private Vector2D position; // Position of module (m)
    private double angle; // Landing module's angle of rotation

    private double mainForce = 2500; // Force generated by main thruster ((kg*m)/s^2)

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
    public LandingModuleOpenLoopController(double weight, Vector2D position, Vector2D velocity) {
        this.weight = weight;
        this.position = position;
        this.velocity = velocity;
    }

    /** This method calls on all necessary methods to simulate the landing
     *
     */
    public void updateModule(final double timestep) {
        double time = 0;

		System.out.println("Starting landing from \nposition = " + position + "\nvelocity = " + velocity + "\n");

        while (!hasLanded()) {
            updateAcceleration(); // Gravitational force and main thruster (+ air resistance in future?)
            updateVelocity(timestep);
            updatePosition(timestep);
            resetAcceleration();
            time += timestep;
        }

        System.out.println("Landing finished with \nposition = " + position + "\nspeed " + velocity + "\nand time = " + time + " seconds");
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

    /** Updates the acceleration of module
     *
     */
    private void updateAcceleration() {
        boolean thrust = false;
        Vector2D gravity = new Vector2D(0, GRAVITYTITAN);
        acceleration.add(gravity);

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
        Vector2D thrust = new Vector2D(Math.sin(angle), Math.cos(angle)).multiply(mainForce).divide(weight);
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

    /** Sets acceleration back to 0
     *
     */
    public void resetAcceleration() {
    	this.acceleration = new Vector2D(0,0);
    }
}
