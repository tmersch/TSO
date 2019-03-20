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
	private Circle circle; //\\\\\\\
	protected Vector force = new Vector();



	public PlanetV4 (String name, double mass, double diameter, Vector startingPos, Vector startingV, Circle circle) {
		this.name = name;
		this.mass = mass;
		this.diameter = diameter;
		this.circle = circle;

		/* Graphical part
		circleDiameter = diameter * TitanV4.PixelPerKm;
		System.out.println("Planet " + name + ", diameter: " + diameter + ", pixelPerKm: " + TitanV4.PixelPerKm + ", circleDiameter: " + circleDiameter);
		*/

		//Position
		startPos = new Vector(startingPos);
		pos = new Vector(startingPos);
		//Velocity
		startV = new Vector(startingV);
		velocity = new Vector(startingV);
	}

	/** To draw the object
	*/
	public void paintComponent (Graphics g) {
		//Recompute the new position
		updatePos();

		//Draw the object
		Graphics2D g2 = (Graphics2D) g;

		Ellipse2D.Double circle = new Ellipse2D.Double(100 + pos.x*TitanV4.PixelPerKm-(circleDiameter/2), 100 + pos.y*TitanV4.PixelPerKm-(circleDiameter/2), circleDiameter, circleDiameter);

		System.out.println("Planet " + name + " drawn at x = " + circle.getX() + ", y = " + circle.getY() + ", diameter: " + circleDiameter);

		//g2.setStroke(new Stroke());

		g2.fill(circle);
	}

	/** Update the planet's position using the physics formulas
	*/
	public void updatePos () {
		//This part computes the new acceleration
		acceleration = new Vector();

		for (int i = 0; i < TitanV4.planets.length; i ++) {
			if (! TitanV4.planets[i].equals(this)) {
				computeGOfPlanet(TitanV4.planets[i]);
			}
		}

		//Then, we apply that acceleration for a time deltaT (which we get from TitanV4.java) on the velocity
		Vector oldVelocity = new Vector(velocity);
		velocity.add(acceleration.divide(1.495978707e11).multiply(TitanV4.deltaT));

		//And then we apply that velocity on the position to get the new position
		//We use the average velocity instead of the new velocity to get a better result for the final position (as the velocity progressively increases along the movement)
		pos.add(oldVelocity.add(velocity).divide(2).multiply(TitanV4.deltaT));
	}

	/** Auxiliary method for updatePos, this method computes the acceleration on this planet due to the given parameter Planet
	*/
	public void computeGOfPlanet (PlanetV4 p) {
		//First, establish a vector of length 1 in the correct direction of the force
		Vector direction = new Vector(pos);
		direction.substract(p.getPosition()).normalize().multiply(-1);

		//Then, multiply by the corresponding things to get the gravitational force
		acceleration.add(direction.multiply(TitanV4.G).multiply(mass).multiply(p.getMass()).divide(Math.pow(p.getPosition().distanceFrom(pos) * (1.495978707e11), 2)).divide(mass));

		//acceleration.printVector();
	}

	/** Prints the current position
	*/
	public void showPosition () {
		System.out.println("Planet " + name + " is currently at: \nx=" + (pos.x/1.49597870700e11) + ", y=" + (pos.y/1.49597870700e11) + ", z=" + (pos.z/1.49597870700e11));
		System.out.println("Velocity is \nx=" + (velocity.x/1.49597870700e11) + ", y=" + (velocity.y/1.49597870700e11) + ", z=" + (velocity.z/1.49597870700e11));
		/*
		System.out.print("Planet " + name + " is currently at: ");
		pos.printVector();
		System.out.print("Velocity is ");
		velocity.printVector();
		*/
	}

	public void updatePosition(){

		for (int i = 0; i < TitanV4.planets.length; i++) { //compute forces between all celestial corpses
			for(int j=i; j<TitanV4.planets.length; j++){
				if (i != j) {
					double upper = TitanV4.G*TitanV4.planets[i].getMass() * TitanV4.planets[j].getMass();
					double lower = TitanV4.planets[i].getPosition().distanceFrom(TitanV4.planets[j].getPosition());
					double gravitation = upper/lower;
					Vector a = new Vector();
					a = TitanV4.planets[i].getPosition().substract(TitanV4.planets[j].getPosition()).normalize().multiply(-1);

					TitanV4.planets[i].force.add(a.multiply(gravitation));
				}
			}
		}

		//compute acceleration vector for each celestial corpse but the sun
		for(int i=1; i<TitanV4.planets.length; i++){
			TitanV4.planets[i].acceleration = (TitanV4.planets[i].force.divide(TitanV4.planets[i].getMass()));
		}

		//calculate change in speed over deltaT
		for(int i=1; i<TitanV4.planets.length; i++){
			Vector oldVelocity = new Vector(TitanV4.planets[i].velocity);
			TitanV4.planets[i].velocity.add((TitanV4.planets[i].acceleration.multiply(TitanV4.deltaT)));

			Vector posChange = new Vector();
			posChange = (oldVelocity.add(velocity).divide(2)).multiply(TitanV4.deltaT);
			TitanV4.planets[i].pos.add(posChange);
		}
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
	public Circle getCircle(){   //\\\\\\\
		return circle;
	}
}
