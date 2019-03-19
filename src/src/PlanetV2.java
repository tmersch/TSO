import javax.swing.JComponent;
import java.awt.Graphics2D;
import java.awt.Graphics;
import java.awt.geom.Ellipse2D;

public class PlanetV2 extends JComponent {
	private String name;
	private double mass;
	private double diameter;
	private double circleDiameter;	//Saves the size in pixels of the planet
	private Vector startPos;
		//startPos[0] is the starting x, startPos[1] is the starting y
	private Vector pos;
		//pos[0] is x, pos[1] is y
	private Vector startV;
		//startV[0] is the starting velocity on x, startV[1] is the starting velocity on y
	private Vector velocity;
		//v[0] is the velocity on x, v[1] is the velocity on y
	private Vector acceleration;
	
	
	public PlanetV2 (String name, double mass, double diameter, Vector startingPos, Vector startingV) {
		this.name = name;
		this.mass = mass;
		this.diameter = diameter;
		
		/* Graphical part
		circleDiameter = diameter * TitanV2.PixelPerKm;
		System.out.println("Planet " + name + ", diameter: " + diameter + ", pixelPerKm: " + TitanV2.PixelPerKm + ", circleDiameter: " + circleDiameter);
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
		
		Ellipse2D.Double circle = new Ellipse2D.Double(100 + pos.x*TitanV2.PixelPerKm-(circleDiameter/2), 100 + pos.y*TitanV2.PixelPerKm-(circleDiameter/2), circleDiameter, circleDiameter);
		
		System.out.println("Planet " + name + " drawn at x = " + circle.getX() + ", y = " + circle.getY() + ", diameter: " + circleDiameter);
		
		//g2.setStroke(new Stroke());
		
		g2.fill(circle);
	}
	
	/** Update the planet's position using the physics formulas
	*/
	public void updatePos () {		
		//This part computes the new acceleration
		acceleration = new Vector();
		
		for (int i = 0; i < TitanV2.planets.length; i ++) {
			if (! TitanV2.planets[i].equals(this)) {
				computeGOfPlanet(TitanV2.planets[i]);
			}
		}
		
		//Then, we apply that acceleration for a time deltaT (which we get from TitanV2.java) on the velocity
		Vector oldVelocity = new Vector(velocity);
		velocity.add(acceleration).multiply(TitanV2.deltaT).divide(1.49597870700e11);
		
		//And then we apply that velocity on the position to get the new position
		//We use the average velocity instead of the new velocity to get a better result for the final position (as the velocity progressively increases along the movement)
		pos.add(oldVelocity.add(velocity).divide(2).multiply(TitanV2.deltaT));
	}
	
	/** Auxiliary method for updatePos, this method computes the acceleration on this planet due to the given parameter Planet
	*/
	public void computeGOfPlanet (PlanetV2 p) {
		//First, establish a vector of length 1 in the correct direction of the force
		Vector direction = new Vector(pos);
		direction.substract(p.getPosition()).normalize().multiply(-1);
		
		//Then, multiply by the corresponding things to get the gravitational force
		acceleration = direction.multiply(TitanV2.G).multiply(mass).multiply(p.getMass()).divide(Math.pow(p.getPosition().distanceFrom(pos) * (1.495978707e11), 2)).divide(mass);
		
		//acceleration.printVector();
	}
	
	/** Prints the current position
	*/
	public void showPosition () {
		System.out.print("Planet " + name + " is currently at: ");
		pos.printVector();
		//System.out.print("Velocity is ");
		//velocity.printVector();
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
}