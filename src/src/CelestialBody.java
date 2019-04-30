/** This class represents planets with their specific properties (that we need), that is
        - the name,
        - the mass,
        - the position,
        - the velocity,
        - the acceleration
*/
public class CelestialBody {
        private String name;
        private double mass;
                //in kg

        private Vector2D position;
                //in meters
        private Vector2D velocity;
                //in meters/secs
        private Vector2D acceleration;
                //in meters/secs^2

        private State initialState;
          //contains the position and velocity
        private Derivative derivative;
          //contains the velocity (dx/dt) and acceleration (dv/dt)

        private State intermediateState;
          //represents the intermediateState in the middle of Runge-Kutta

        //the four k1 to k4 are used in the Runge-Kutta method
        private Derivative k1;
        private Derivative k2;
        private Derivative k3;
        private Derivative k4;

        /** Default constructor with all parameters provided:
                @param name, the planet's name
                @param mass, the planets's mass
                @param startingPos, the planets's position at t=0
                @param startingVelocity, the planet's velocity at t=0
        */
        public CelestialBody (String name, double mass, Vector2D startingPos, Vector2D startingVelocity) {
                //Save the different parameter's values
                this.name = name;
                this.mass = mass;
                this.position = new Vector2D(startingPos);
                this.velocity = new Vector2D(startingVelocity);

                //Then, create the state of the planet, and bind the position and velocity vectors to "initialState"
                initialState = new State();
                initialState.position = this.position;
                initialState.velocity = this.velocity;

                //And initialize the derivative
                derivative = new Derivative();
        }

        public String getName() {
                return name;
        }

        public double getMass() {
                return mass;
        }

        public Vector2D getPosition () {
                return new Vector2D(position);
        }

        public Vector2D getVelocity() {
                return new Vector2D(velocity);
        }

        public Vector2D getAcceleration() {
                return new Vector2D(acceleration);
        }

        public State getIntermediateState() {
          return new State(intermediateState);
        }

        public Derivative nextDerivative (State initial, Derivative d, double deltaT, int step) {
          if (step == 0) {
            intermediateState = new State(0, 0, 0, 0);
            intermediateState.position = new Vector2D(initial.position).add(new Vector2D(d.dPosition).multiply(deltaT));
            intermediateState.velocity = new Vector2D(initial.velocity).add(new Vector2D(d.dVelocity).multiply(deltaT));

            return new Derivative();
          }
          else {
            Vector2D a = computeAccelerationRK(initial);
            return new Derivative(intermediateState.velocity, a);
          }
        }

        public void updatePosition (double deltaT, int step) {
          if (step/2 == 0) {
            k1 = nextDerivative(initialState, new Derivative(), 0, step%2);
          }
          else if (step/2 == 1) {
            k2 = nextDerivative(initialState, k1, deltaT/2, step%2);
          }
          else if (step/2 == 2) {
            k3 = nextDerivative(initialState, k2, deltaT/2, step%2);
          }
          else if (step/2 == 3) {
            k4 = nextDerivative(initialState, k3, deltaT, step%2);

            //Final step
            if (step%2 == 1) {
              derivative = (new Derivative(k1).add((new Derivative(k2).add(k3)).multiply(2)).add(k4)).divide(6);

              initialState.applyDerivative(derivative, deltaT);
            }
          }
        }

        public Vector2D computeAccelerationRK (State state) {
                resetAcceleration();

                for (int i = 0; i < GUIV2.planets.length; i ++) {
                        if (! GUIV2.planets[i].equals(this)) {
                                addGToAccelerationRK(GUIV2.planets[i], state);
                        }
                }

                return new Vector2D(acceleration);
        }

        public void addGToAccelerationRK (CelestialBody other, State self) {
                //Compute the gravitational force
                //      Make a unity vector in the correct direction
                Vector2D direction = new Vector2D(self.position);
                direction.subtract(other.getIntermediateState().position).normalize().multiply(-1);

                //      Compute the distance between the two planets
                double dist = new Vector2D(self.position).distance(other.getIntermediateState().position);

                //      Calculate the gravitational force
                Vector2D force = new Vector2D(direction);
                force.multiply(GUIV2.G).multiply(this.mass).multiply(other.getMass()).divide(dist * dist);

                //From that, add the corresponding acceleration to the planet's acceleration
                acceleration.add(new Vector2D(force).divide(mass));
        }


        public void resetAcceleration() {
                acceleration = new Vector2D();
        }

        /* These methods are old methods used for Euler's method
        public Vector2D computeAcceleration () {
                resetAcceleration();

                for (int i = 0; i < GUIV2.planets.length; i ++) {
                        if (! GUIV2.planets[i].equals(this)) {
                                addGToAcceleration(GUIV2.planets[i]);
                        }
                }

                return new Vector2D(acceleration);
        }

        public void addGToAcceleration (CelestialBody other) {
                //Compute the gravitational force
                //      Make a unity vector in the correct direction
                Vector2D direction = new Vector2D(this.position);
                direction.subtract(other.getPosition()).normalize().multiply(-1);

                //      Compute the distance between the two planets
                double dist = this.position.distance(other.getPosition());

                //      Calculate the gravitational force
                Vector2D force = new Vector2D(direction);
                force.multiply(GUIV2.G).multiply(this.mass).multiply(other.getMass()).divide(dist * dist);

                //From that, add the corresponding acceleration to the planet's acceleration
                acceleration.add(new Vector2D(force).divide(mass));
        }

        public void updateVelocityAndPosition (double time) {
                //Save the velocity before applying the acceleration
                Vector2D oldVelocity = new Vector2D(this.velocity);

                //Calculate the final velocity
                velocity.add(new Vector2D(acceleration).multiply(time));

                //Update location with the averageVelocity
                position.add(new Vector2D(oldVelocity).add(velocity).divide(2.0).multiply(time));
        }
        */
}
