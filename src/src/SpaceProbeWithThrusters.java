/** Represents a space probe that can use thrusters
  */
public class SpaceProbeWithThrusters extends SpaceProbe {
    //The following constants are all for kerosene (massFlowRate's value not found anywhere, set randomly)
    private final double massFlowRate = 10;                 //in kg/secs
    // exhaust velocity found on https://en.wikipedia.org/wiki/Liquid_rocket_propellant
    private final double exhaustVelocity = 2941;            //in m/secs
    // oxidizer-to-fuel ratio for kerosene found on https://en.wikipedia.org/wiki/RP-1
    private final double oxidizerToFuelRatio = 2.56;        //for kerosene

    //The force applied on the space probe by the thrusters
    protected Vector2D thrusterForce = new Vector2D(0, 0);
    //the angle in which the spaceProbe points in
    private double angle;
    //the amount of degrees we can maximally change the angle by in one iteration
    private final double angleChange = 1;
    //the mass of the fuel
    private double fuelMass = 0;                            //in kgs

    /** Default constructor with the same parameters as the full constructor from the superclass
      * Additionnally, two new parameters:
      * @param initialAngle, the angle in which the spaceProbe points
      * @param fuelMass, the mass of the fuel
      */
    public SpaceProbeWithThrusters (String name, double mass, double fuelMass, Vector2D startingPos, Vector2D startingV, double initialAngle) {
        super(name, mass, startingPos, startingV);

        //Set the angle to the given value
        this.angle = initialAngle;

        //Set the fuelMass to the given value
        this.fuelMass = fuelMass;
    }

    /** Additional constructor with one less parameter than the full constructor: fuelMass
      */
    public SpaceProbeWithThrusters (String name, double mass, Vector2D startingPos, Vector2D startingV, double initialAngle) {
        //Set the default fuelMass to 1000 kg
        this(name, mass, 1000, startingPos, startingV, initialAngle);
    }

    /** Additional constructor with two less parameters than the full constructor: fuelMass and initialAngle
      */
    public SpaceProbeWithThrusters (String name, double mass, Vector2D startingPos, Vector2D startingV) {
        //Set the default fuelMass to 1000 kg and the default angle to 0
        this(name, mass, startingPos, startingV, 0);
    }

    /** Given a sign in which to modify the angle, applies a modification of angleChange in the correct direction
      * @param sign, the sign in which we should modify the angle
      */
    public void correctAngle (double sign) {
        modifyAngle(sign * angleChange);
    }

    /** Applies a certain modification of the angle the space probe points to
      * @param modifier, the amount by which the angle of the space probe should be modified
      */
    private void modifyAngle (double modifier) {
        angle += modifier;
    }

    /** Applies the force of the thrusters on the spaceProbe
      * Uses the formula from https://www.grc.nasa.gov/WWW/K-12/airplane/rockth.html
      * The exit pressure and the free stream pressure are considered the same as it is specified that they are the same at some design condition
      */
    public void activateThrusters () {
		//Compute the force of the thrusters
		thrusterForce = new Vector2D(angle).multiply(massFlowRate).multiply(exhaustVelocity);
	}

    @Override
    public double getMass() {
        return super.getMass() + fuelMass;
    }

    @Override
    protected void applyDerivative (Derivative d, final double deltaT) {
        //massFlowRate = mass of exhaust gas per unit of time
        //Thus, if we multiply by the time during which we apply the thruster, we should get the total mass of the exhaust gas(es)
        double exhaustGasMass = massFlowRate * deltaT;

        //Then, we compute the fuelMass burnt to get the exhaustGasMass
        double burntOxidizerFactor = oxidizerToFuelRatio; // * burntFuelMass
        double burntFuelMass = exhaustGasMass/(burntOxidizerFactor + 1);

        //Subtract the burnt mass from the total mass
        fuelMass -= burntFuelMass;

        //Add the thruster force to the derivative
        Derivative derivativeThrusterForce = new Derivative(new Vector2D(), thrusterForce);
        d.add(derivativeThrusterForce);

        //Reset the thruster force
        resetThruster();

        //Then, apply the superclass' method for applying the derivative
        super.applyDerivative(d, deltaT);
    }

    /** Reset the thruster to a null force
      */
    public void resetThruster () {
		thrusterForce = new Vector2D();
	}

    public static SpaceProbeWithThrusters createSpaceProbeWithStartingAngle (String name, double mass, CelestialBody launchPlanet, double velocity, double launchAngle) {
        //Call the superclass method to get a spaceProbe with the correct parameters
        SpaceProbe tmp = SpaceProbe.createSpaceProbeWithStartingAngle(name, mass, launchPlanet, velocity, launchAngle);

        //Transform it into a SpaceProbeWithThrusters
        return spaceProbeToSpaceProbeWithThrusters(tmp);
    }

    public static SpaceProbeWithThrusters spaceProbeToSpaceProbeWithThrusters(SpaceProbe spaceProbe) {
        String name = spaceProbe.getName();
        double mass = spaceProbe.getMass();
        Vector2D pos = spaceProbe.getPosition();
        Vector2D vel = spaceProbe.getVelocity();
        double angle = spaceProbe.getAngle();

        SpaceProbeWithThrusters res = new SpaceProbeWithThrusters(name, mass, pos, vel, angle



        );

        return res;
    }

    /** Returns a deep copy of this object
      */
    public SpaceProbeWithThrusters clone () {
        String name = this.getName();                       //safe because strings are immutable
        double mass = this.getMass() - this.fuelMass;       //this.getMass() - fuelMass to make it be only the mass of the space Probe itself
        double fuelMass = this.fuelMass;
        Vector2D pos = new Vector2D(this.getPosition());
        Vector2D vel = new Vector2D(this.getVelocity());
        double angle = this.angle;

        return new SpaceProbeWithThrusters(name, mass, fuelMass, pos, vel, angle);
    }
}
