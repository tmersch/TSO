/** Represents a space probe that can use thrusters
  */
public class SpaceProbeWithThrusters extends SpaceProbe {
    //The following constants are all for kerosene (massFlowRate's value not found anywhere, set randomly)
    private double massFlowRate = 10;                 //in kg/secs
    // exhaust velocity found on https://en.wikipedia.org/wiki/Liquid_rocket_propellant
    private final double exhaustVelocity = 3510; //2941 for 1 atm            //in m/secs
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

    // the planet or moon we want to reach
    private CelestialBody target;
    private boolean targetSet = false;
    private double orbitRadius = 1200000;       //in meters

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

    /** Computes, then applies a force of the thruster such that the space probe should get into orbit
      */
    public void activateThrusterToReachOrbit (CelestialBody target, double orbitRad, Vector2D currentAcceleration, double timestep) {
        //Compute the velocity needed to stay in orbit
        double velocity = Math.sqrt((GUI.G * target.getMass())/orbitRad);
        //Compute the angle of the direction from this spaceProbe to the target body
        double angle = new Vector2D(target.getPosition()).subtract(this.getPosition()).angle(new Vector2D());
        //Compute the angle of the target's velocity
        double targetVelAngle = new Vector2D(target.getVelocity()).angle(new Vector2D());
        //Set the resultAngle to the angle between the spaceProbe and the target - 90
        double resultAngle = angle - 90;
        //If their difference is not close, then we flip resultAngle by 180 degrees
        if (((targetVelAngle - resultAngle)%360 > 90 && (targetVelAngle-resultAngle)%360 < 360-90) || ((targetVelAngle - resultAngle)%360 < -90 && (targetVelAngle - resultAngle)%360 > -360+90)) {
            resultAngle += 180;
        }
        //And then make sure the angle is between 0 and 360
        resultAngle %= 360;
        if (resultAngle < 0) {
            resultAngle += 360;
        }

        //Create a new result velocity with respect to the target body, which is using the computed angle and velocity
        Vector2D resultVelocityWithRespectToTarget = new Vector2D(resultAngle).multiply(velocity);

        //Then add the result velocity and the target body's velocity together
        Vector2D targetVelocity = new Vector2D(target.getVelocity()).add(resultVelocityWithRespectToTarget);

        //Then, given the current velocity and the targetVelocity, compute the necessary acceleration to get from the current velocity to the resulting velocity
        Vector2D accelerationToGetIntoOrbit = new Vector2D(this.getVelocity()).multiply(-1).add(targetVelocity).divide(timestep);
        Vector2D normalizedAcceleration = new Vector2D(accelerationToGetIntoOrbit);

        //Compute the angle and factor of the acceleration
        angle = accelerationToGetIntoOrbit.angle(new Vector2D());
        double thrusterForce = (accelerationToGetIntoOrbit.getX()/normalizedAcceleration.getX())/this.getMass();

        //Compute the massFlowRate used to get that force
        massFlowRate = thrusterForce/exhaustVelocity;

        //massFlowRate = mass of exhaust gas per unit of time
        //Thus, if we multiply by the time during which we apply the thruster, we should get the total mass of the exhaust gas(es)
        double exhaustGasMass = massFlowRate * timestep;

        //Then, we compute the fuelMass burnt to get the exhaustGasMass
        double burntOxidizerFactor = keroseneOxidizerToFuelRatio; // * burntFuelMass
        double consumedFuelMass = exhaustGasMass/(burntOxidizerFactor + 1);

        //Subtract the burnt mass from the total mass
        this.burntFuelMass += consumedFuelMass;
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

    /** Sets the target planet we want to reach
      */
    public void setTarget (CelestialBody target) {
        this.target = target;
        targetSet = true;
    }

    /** Resets the target planet and the boolean targetSet
      */
    public void resetTarget () {
        this.target = null;
        targetSet = false;
    }

    @Override
    protected void applyDerivative (Derivative d, final double deltaT) {
        if (targetSet) {
            if (this.getPosition().distance(target.getPosition()) <= orbitRadius) {
                activateThrusterToReachOrbit(target, this.getPosition().distance(target.getPosition()), new Vector2D(d.dVelocity), deltaT);
            }
        }

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

    /** Computes, then applies a force of the thruster such that the space probe should get into orbit
      */
    public SpaceProbeWithThrusters createSpaceProbeInOrbit (CelestialBody target, double orbitRad, double timestep) {
        State currentState = new State(this.getPosition(), this.getVelocity());
        Vector2D currentAcceleration = computeAccelerationRK(currentState);

        //Compute the velocity needed to stay in orbit
        double velocity = Math.sqrt((GUI.G * target.getMass())/orbitRad);
        //Compute the angle of the direction from this spaceProbe to the target body
        double spaceProbeToTargetAngle = new Vector2D(target.getPosition()).subtract(this.getPosition()).angle(new Vector2D());
        //Compute the angle of the target's velocity
        double targetVelAngle = new Vector2D(target.getVelocity()).angle(new Vector2D());
        //Set the resultAngle to the angle between the spaceProbe and the target - 90
        double resultAngle = spaceProbeToTargetAngle - 90;
        //If their difference is not close, then we flip resultAngle by 180 degrees
        if (((targetVelAngle - resultAngle)%360 > 90 && (targetVelAngle-resultAngle)%360 < 360-90) || ((targetVelAngle - resultAngle)%360 < -90 && (targetVelAngle - resultAngle)%360 > -360+90)) {
            resultAngle += 180;
        }
        //And then make sure the angle is between 0 and 360
        resultAngle %= 360;
        if (resultAngle < 0) {
            resultAngle += 360;
        }

        System.out.println("Velocity: " + velocity);

        //Create a new result velocity with respect to the target body, which is using the computed angle and velocity
        Vector2D resultVelocityWithRespectToTarget = new Vector2D(resultAngle).multiply(velocity);

        //Then add the result velocity and the target body's velocity together
        Vector2D resultVelocity = new Vector2D(target.getVelocity()).add(resultVelocityWithRespectToTarget);

        //Then, given the current velocity and the targetVelocity, compute the necessary acceleration to get from the current velocity to the resulting velocity
        Vector2D accelerationToGetIntoOrbit = new Vector2D(this.getVelocity()).multiply(-1).add(resultVelocity).divide(timestep);
        Vector2D normalizedAcceleration = new Vector2D(accelerationToGetIntoOrbit);

        //Compute the angle and factor of the acceleration
        double angle = accelerationToGetIntoOrbit.angle(new Vector2D());
        double thrusterForce = (accelerationToGetIntoOrbit.getX()/normalizedAcceleration.getX())/this.getMass();

        //Compute the massFlowRate used to get that force
        massFlowRate = thrusterForce/exhaustVelocity;

        //massFlowRate = mass of exhaust gas per unit of time
        //Thus, if we multiply by the time during which we apply the thruster, we should get the total mass of the exhaust gas(es)
        double exhaustGasMass = massFlowRate * timestep;

        //Then, we compute the fuelMass burnt to get the exhaustGasMass
        double burntOxidizerFactor = keroseneOxidizerToFuelRatio; // * burntFuelMass
        double consumedFuelMass = exhaustGasMass/(burntOxidizerFactor + 1);

        //Set the spaceProbe to have the desired parameters
        Vector2D pos = new Vector2D(spaceProbeToTargetAngle);//.multiply(orbitRad);
        System.out.println(pos);
        System.out.println(pos.length());
        pos.multiply(orbitRad).add(target.getPosition());;
        System.out.println("Orbit radius: " + orbitRad + ", \nposition: " + pos + "\nLength of pos: " + pos.length() + "\nDistance from pos to target: " + pos.distance(target.getPosition()));
        Vector2D vel = new Vector2D(resultVelocity);
        System.out.println("Result velocity: " + vel);

        //Subtract the burnt mass from the total mass
        this.burntFuelMass += consumedFuelMass;

        return new SpaceProbeWithThrusters(this.getName(), this.getSpaceProbeMass(), pos, vel);
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
            System.out.println("Time taken: " + GUI.getElapsedTimeAsString());
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
