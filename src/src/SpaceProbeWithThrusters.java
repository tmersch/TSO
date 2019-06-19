/** Represents a space probe that can use thrusters
  */
public class SpaceProbeWithThrusters extends SpaceProbe {
    //The following constants are all for kerosene (massFlowRate's value not found anywhere, set randomly)
    private final double massFlowRate = 10;                 //in kg/secs
    // exhaust velocity found on https://en.wikipedia.org/wiki/Liquid_rocket_propellant
    private final double exhaustVelocity = 2941;            //in m/secs
    // oxidizer-to-fuel ratio for kerosene found on https://en.wikipedia.org/wiki/RP-1
    private final double keroseneOxidizerToFuelRatio = 2.56;

    //The force applied on the space probe by the thrusters
    protected Vector2D thrusterForce = new Vector2D(0, 0);
    // shows whether we use the thruster or not in this iteration
    private boolean useThruster = false;
    //the angle in which the spaceProbe points in
    private double angle;
    //the amount of degrees we can maximally change the angle by in one iteration
    private final double angleChange = 1;
    //the mass of the fuel
    private final double fuelMass;                          //in kgs
    //the mass of the fuel burnt so far
    private double burntFuelMass;                           //in kgs

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
        //and initialize the burntFuelMass to 0 (or the typical fuel usage to escape the gravity of Earth)
        burntFuelMass = 0;
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

        useThruster = true;

        //For debugging/testing purposes
        //System.out.println("Thruster force: " + new Vector2D(thrusterForce).divide(this.getMass()));
	}

    /** Computes the distance the landing module will travel before coming to a stop
	  *	@return a double value representing the distance needed to brake down to y-velocity = 0
	  */
	private double computeBrakingDistance(final double thrusterForce) {
		//Compute the time needed to brake to a velocity of 0
		double timeToGetToVelocity0 = computeBrakingTime(thrusterForce);

		//Apply formula: distance = time * maxVelocity * 1/2
		double brakingDistance = timeToGetToVelocity0 * this.getVelocity().getY() * 0.5;

		return brakingDistance;
	}

	/** Computes the braking time
	  * @return the time necessary for the landing module to slow down to velocity = 0 from the current velocity
	  */
	private double computeBrakingTime(final double thrusterForce) {
		//Apply formula timeToGetToVelocity0 = maxVelocity / (gravityAcceleration + thrustAcceleration)
		// where thrustAcceleration is computed as
        double thrustAcceleration = massFlowRate * exhaustVelocity;

		double brakingTime = this.getVelocity().getY() / thrustAcceleration;

		return brakingTime;
	}

    /** Returns the total mass of the spaceProbe
      */
    @Override
    public double getMass() {
        return getSpaceProbeMass() + getFuelMass();
    }

    /** Returns the leftover fuelMass
      */
    public double getFuelMass() {
        return (fuelMass - burntFuelMass);
    }

    /** Returns the mass of the fuel consumed so far
      */
    public double getBurntFuelMass () {
        return burntFuelMass;
    }

    /** Returns the current prize of the consumed fuel in Euros
      */
    public double getFuelPrize() {
        return KerosenePrize.getPrizeOfKeroseneInEuros(this.burntFuelMass);
    }

    /** Return only the mass of the SpaceProbe, without taking into account the fuel
      */
    public double getSpaceProbeMass () {
        return super.getMass();
    }

    @Override
    protected void applyDerivative (Derivative d, final double deltaT) {
        if (useThruster) {
            //massFlowRate = mass of exhaust gas per unit of time
            //Thus, if we multiply by the time during which we apply the thruster, we should get the total mass of the exhaust gas(es)
            double exhaustGasMass = massFlowRate * deltaT;

            //Then, we compute the fuelMass burnt to get the exhaustGasMass
            double burntOxidizerFactor = keroseneOxidizerToFuelRatio; // * burntFuelMass
            double consumedFuelMass = exhaustGasMass/(burntOxidizerFactor + 1);

            //Subtract the burnt mass from the total mass
            this.burntFuelMass += consumedFuelMass;

            //Divide the thrusterForce by the mass to get the actual acceleration caused by the thrusters
            Vector2D thrusterAccel = new Vector2D(thrusterForce).divide(this.getMass());

            //Add the thruster force to the derivative
            Derivative derivativeThrusterForce = new Derivative(new Vector2D(), thrusterAccel);
            d.add(derivativeThrusterForce);

            //Reset the thruster force
            resetThruster();
        }

        //Then, apply the superclass' method for applying the derivative
        super.applyDerivative(d, deltaT);
    }

    /** Reset the thruster to a null force
      */
    public void resetThruster () {
		thrusterForce = new Vector2D();
        useThruster = false;
	}

    /** Creates a spaceProbe launched from launchPlanet in the correct angle
      */
    public static SpaceProbeWithThrusters createSpaceProbeWithStartingAngle (String name, double mass, CelestialBody launchPlanet, double velocity, double launchAngle) {
        //Call the superclass method to get a spaceProbe with the correct parameters
        SpaceProbe tmp = SpaceProbe.createSpaceProbeWithStartingAngle(name, mass, launchPlanet, velocity, launchAngle);

        //Transform it into a SpaceProbeWithThrusters
        return spaceProbeToSpaceProbeWithThrusters(tmp, launchAngle);
    }

    /** Transforms a given spaceProbe object into a spaceProbeWithThrusters
      */
    public static SpaceProbeWithThrusters spaceProbeToSpaceProbeWithThrusters(SpaceProbe spaceProbe, double launchAngle) {
        String name = spaceProbe.getName();
        double mass = spaceProbe.getMass();
        Vector2D pos = spaceProbe.getPosition();
        Vector2D vel = spaceProbe.getVelocity();
        double angle = launchAngle;

        SpaceProbeWithThrusters res = new SpaceProbeWithThrusters(name, mass, pos, vel, angle);

        return res;
    }

    @Override
    /** Checks whether the space probe crashed into the given planet
	  */
	public boolean didNotCrash (CelestialBody p) {
		if (new Vector2D(p.getPosition()).distance(this.getPosition()) <= p.getRadius()) {
			crashedPlanet = p;
			positionWithRespectToCrashedPlanet = new Vector2D(this.getPosition()).subtract(crashedPlanet.getPosition());
			crashed = true;
            System.out.println("Burnt fuel Mass: " + this.getBurntFuelMass());
			return false;
		}
		else {
			return true;
		}
	}

    /** Returns a deep copy of this object at its current state
      */
    @Override
    public SpaceProbeWithThrusters clone () {
        String name = this.getName();                       //safe because strings are immutable
        double mass = this.getMass() - this.fuelMass;       //this.getMass() - fuelMass to make it be only the mass of the space Probe itself
        double fuelMass = this.fuelMass;
        double burntFuelMass = this.burntFuelMass;
        Vector2D pos = this.getPosition();
        Vector2D vel = this.getVelocity();
        double angle = this.angle;

        SpaceProbeWithThrusters res = new SpaceProbeWithThrusters(name, mass, fuelMass, pos, vel, angle);
        res.burntFuelMass = burntFuelMass;

        return res;
    }
}
