import javax.swing.JComponent;
import java.awt.Graphics2D;
import java.awt.Graphics;
import java.awt.geom.Ellipse2D;
import javafx.scene.shape.Circle;
import javafx.scene.paint.*;

public class PlanetV5 extends JComponent {
	private String name;
	private double mass;
	private double diameter;
	private double circleDiameter;	//Saves the size in pixels of the planet
	private Vector2D startPos;
		//startPos[0] is the starting x, startPos[1] is the starting y
	protected Vector2D pos;
		//pos[0] is x, pos[1] is y
	private Vector2D startV;
		//startV[0] is the starting velocity on x, startV[1] is the starting velocity on y
	protected Vector2D velocity;
		//v[0] is the velocity on x, v[1] is the velocity on y
	protected Vector2D acceleration;
	private Circle circle; //\\\\\\\
	protected Vector2D force = new Vector2D();



	public PlanetV5 (String name, double mass, double diameter, Vector2D startingPos, Vector2D startingV, Circle circle) {
		this.name = name;
		this.mass = mass;
		this.diameter = diameter;
		this.circle = circle;

		/* Graphical part
		circleDiameter = diameter * TitanV5.PixelPerKm;
		System.out.println("Planet " + name + ", diameter: " + diameter + ", pixelPerKm: " + TitanV5.PixelPerKm + ", circleDiameter: " + circleDiameter);
		*/

		//Position
		startPos = new Vector2D(startingPos);
		pos = new Vector2D(startingPos);
		//Velocity
		startV = new Vector2D(startingV);
		velocity = new Vector2D(startingV);
	}

	/** Update the planet's position using the physics formulas
	*/
	public void updatePos () {
		//This part computes the new acceleration
		acceleration = new Vector2D();

		for (int i = 0; i < TitanV5.planets.length; i ++) {
			if (! TitanV5.planets[i].equals(this)) {
				computeGOfPlanet(TitanV5.planets[i]);
			}
		}

		//Then, we apply that acceleration for a time deltaT (which we get from TitanV5.java) on the velocity
		Vector2D oldVelocity = new Vector2D(velocity);
		velocity.add(acceleration.multiply(TitanV5.deltaT));

		//And then we apply that velocity on the position to get the new position
		//We use the average velocity instead of the new velocity to get a better result for the final position (as the velocity progressively increases along the movement)
		pos.add(oldVelocity.add(velocity).divide(2).multiply(TitanV5.deltaT));
	}

	/** Auxiliary method for updatePos, this method computes the acceleration on this planet due to the given parameter Planet
	*/
	public void computeGOfPlanet (PlanetV5 p) {
		//First, establish a vector of length 1 in the correct direction of the force
		Vector2D direction = new Vector2D(pos);
		direction.substract(p.getPosition()).normalize().multiply(-1);

		//Then, multiply by the corresponding things to get the gravitational force
		acceleration.add(direction.multiply(TitanV5.G).multiply(mass).multiply(p.getMass()).divide(Math.pow(p.getPosition().distanceFrom(pos) * (1.495978707e11), 2)).divide(mass));

		//acceleration.printVector2D();
	}

	/** Prints the current position
	*/
	public void showPosition () {
		System.out.println("Object " + name + " is currently at (in meters): \nx=" + (pos.x) + ", y=" + (pos.y));
		System.out.println("Velocity is (in meters/secs)\nx=" + (velocity.x) + ", y=" + (velocity.y));
		/*
		System.out.print("Planet " + name + " is currently at: ");
		pos.printVector2D();
		System.out.print("Velocity is ");
		velocity.printVector2D();
		*/
	}

	public void updatePosition(){

		for (int i = 0; i < TitanV5.planets.length; i++) { //compute forces between all celestial corpses
			for(int j=i; j<TitanV5.planets.length; j++){
				if (i != j) {
					double upper = TitanV5.G*TitanV5.planets[i].getMass() * TitanV5.planets[j].getMass();
					double lower = TitanV5.planets[i].getPosition().distanceFrom(TitanV5.planets[j].getPosition());
					double gravitation = upper/lower;
					Vector2D a = new Vector2D();
					a = TitanV5.planets[i].getPosition().substract(TitanV5.planets[j].getPosition()).normalize().multiply(-1);

					TitanV5.planets[i].force.add(a.multiply(gravitation));
				}
			}
		}

		//compute acceleration vector for each celestial corpse but the sun
		for(int i=1; i<TitanV5.planets.length; i++){
			TitanV5.planets[i].acceleration = (TitanV5.planets[i].force.divide(TitanV5.planets[i].getMass()));
		}

		//calculate change in speed over deltaT
		for(int i=1; i<TitanV5.planets.length; i++){
			Vector2D oldVelocity = new Vector2D(TitanV5.planets[i].velocity);
			TitanV5.planets[i].velocity.add((TitanV5.planets[i].acceleration.multiply(TitanV5.deltaT)));

			Vector2D posChange = new Vector2D();
			posChange = (oldVelocity.add(velocity).divide(2)).multiply(TitanV5.deltaT);
			TitanV5.planets[i].pos.add(posChange);
		}
	}

	/** Returns the mass of the planet
	*/
	public double getMass() {
		return mass;
	}

	/** Returns the position of the planet
	*/
	public Vector2D getPosition() {
		return pos;
	}

	/** Returns the name of the planet
	*/
	public String getName() {
		return name;
	}
	/**Returns the circle object representing the planet
	*/
	public Circle getCircle(){   //\\\\\\\
		return circle;
	}
}