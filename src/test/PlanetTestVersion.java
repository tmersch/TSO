import javax.swing.JComponent;
import java.awt.Graphics2D;
import java.awt.Graphics;
import java.awt.geom.Ellipse2D;
import javafx.scene.shape.Circle;
import javafx.scene.paint.*;

public class PlanetV4 extends JComponent {
	private String name;
	private double mass;
	private double diameter;
	private double circleDiameter;	//Saves the size in pixels of the planet
	private Vector startPos;
		//startPos[0] is the starting x, startPos[1] is the starting y
	protected Vector pos;
		//pos[0] is x, pos[1] is y
	private Vector startV;
		//startV[0] is the starting velocity on x, startV[1] is the starting velocity on y
	protected Vector velocity;
		//v[0] is the velocity on x, v[1] is the velocity on y
	protected Vector acceleration;
	private Circle circle;
	protected Vector force = new Vector();
	
	public PlanetV4 (String name, double mass, double diameter, Vector startingPos, Vector startingV, Circle circle) {
		this.name = name;
		this.mass = mass;
		this.diameter = diameter;
		this.circle = circle;
		
		//Position
		startPos = new Vector(startingPos);
		pos = new Vector(startingPos);
		//Velocity
		startV = new Vector(startingV);
		velocity = new Vector(startingV);
	}
	
	/** Update the planet's position using the physics formulas
	*/
	public void updatePos () {		
		//This part computes the new acceleration
		acceleration = new Vector();
		
		/*
		for (int i = 0; i < TitanV4.planets.length; i ++) {
			if (! TitanV4.planets[i].equals(this)) {
				computeGOfPlanet(TitanV4.planets[i]);
			}
		}
		*/
		
		computeGOfPlanet(TitanV4.planets[3]);
		
		//Then, we apply that acceleration for a time deltaT (which we get from TitanV4.java) on the velocity
		Vector oldVelocity = new Vector(velocity);
		//System.out.println("1. Velocity before: ");
		//oldVelocity.printVector();
		
		//System.out.println("DeltaT: " + TitanV4.deltaT);
		
		velocity.add(acceleration.multiply(TitanV4.deltaT));
		
		//System.out.println("2. Velocity: ");
		//velocity.printVector();
		
		//System.out.println("Acceleration * deltaT: ");
		//acceleration.printVector();
		
		//System.out.println("Position before: ");
		//pos.printVector();
		
		//And then we apply that velocity on the position to get the new position
		//We use the average velocity instead of the new velocity to get a better result for the final position (as the velocity progressively increases along the movement)
		pos.add(oldVelocity.add(velocity).divide(2).multiply(TitanV4.deltaT));
	
		//System.out.println("3. Velocity: ");
		//velocity.printVector();
	}
	
	/** Auxiliary method for updatePos, this method computes the acceleration on this planet due to the given parameter Planet
	*/
	public void computeGOfPlanet (PlanetV4 p) {
		//First, establish a vector of length 1 in the correct direction of the force
		Vector direction = new Vector(pos);
		direction.substract(p.getPosition()).normalize().multiply(-1);

		//Then, multiply by the corresponding things to get the gravitational force
		acceleration.add(direction.multiply(TitanV4.G).multiply(mass).multiply(p.getMass()).divide(Math.pow(pos.distanceFrom(p.getPosition()), 2)).divide(mass));

		//acceleration.printVector();
	}
	
	/** Prints the current position
	*/
	public void showPosition () {
		System.out.println("Object " + name + " is currently at (in meters): \nx=" + (pos.x) + ", y=" + (pos.y) + ", z=" + (pos.z));
		System.out.println("Velocity is (in meters/secs)\nx=" + (velocity.x) + ", y=" + (velocity.y) + ", z=" + (velocity.z));
		/*
		System.out.print("Planet " + name + " is currently at: ");
		pos.printVector();
		System.out.print("Velocity is ");
		velocity.printVector();
		*/
	}
	
	/** Returns the mass of the planet
	*/
	public double getMass() {
		return mass;
	}
	
	/** Returns the position of the planet
	*/
	public Vector getPosition() {
		return pos;
	}
	
	/** Returns the name of the planet
	*/
	public String getName() {
		return name;
	}
	
	/**Returns the circle object representing the planet
	*/
	public Circle getCircle(){
		return circle;
	}
}