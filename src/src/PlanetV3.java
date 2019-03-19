import javax.swing.JComponent;
import java.awt.Graphics2D;
import java.awt.Graphics;
import java.awt.geom.Ellipse2D;

public class PlanetV3 extends JComponent {
	private String name;
	private double mass;
	private double diameter;
	private double circleDiameter;	//Saves the size in pixels of the planet
	private double[] startPos;
		//startPos[0] is the starting x, startPos[1] is the starting y
	private double[] pos;
		//pos[0] is x, pos[1] is y
	private double[] startV;
		//startV[0] is the starting velocity on x, startV[1] is the starting velocity on y
	private double[] velocity;
		//v[0] is the velocity on x, v[1] is the velocity on y
	private double[] acceleration;
	
	
	public PlanetV3 (String name, double mass, double diameter, double[] startingPos, double[] startingV) {
		this.name = name;
		this.mass = mass;
		this.diameter = diameter;
		
		/* Graphical part
		circleDiameter = diameter * TitanV3.PixelPerKm;
		System.out.println("Planet " + name + ", diameter: " + diameter + ", pixelPerKm: " + TitanV3.PixelPerKm + ", circleDiameter: " + circleDiameter);
		*/
		
		//Position
		startPos = new double[startingPos.length];
		for (int i = 0; i < startingPos.length; i++) {
			startPos[i] = startingPos[i];
		}
		pos = new double[startingPos.length];
		for (int i = 0; i < startingPos.length; i++) {
			pos[i] = startingPos[i];
		}
		//Velocity
		startV = new double[startingV.length];
		for (int i = 0; i < startingV.length; i++) {
			startV[i] = startingV[i];
		}
		velocity = new double[startingV.length];
		for (int i = 0; i < startingPos.length; i++) {
			velocity[i] = startingV[i];
		}
		//Acceleration
		acceleration = new double[startingV.length];
	}
	
	/** To draw the object
	*/
	public void paintComponent (Graphics g) {
		//Recompute the new position
		updatePos();
		
		//Draw the object
		Graphics2D g2 = (Graphics2D) g;
		
		Ellipse2D.Double circle = new Ellipse2D.Double(100 + pos[0]*TitanV3.PixelPerKm-(circleDiameter/2), 100 + pos[1]*TitanV3.PixelPerKm-(circleDiameter/2), circleDiameter, circleDiameter);
		
		System.out.println("Planet " + name + " drawn at x = " + circle.getX() + ", y = " + circle.getY() + ", diameter: " + circleDiameter);
		
		//g2.setStroke(new Stroke());
		
		g2.fill(circle);
	}
	
	/** Update the planet's position using the physics formulas
	*/
	public void updatePos () {		
		//This part computes the new acceleration
		for (int i = 0; i < acceleration.length; i ++) {
			acceleration[i] = 0;
		}
		
		for (int i = 0; i < TitanV3.planets.length; i ++) {
			if (! TitanV3.planets[i].equals(this)) {
				computeGOfPlanet(TitanV3.planets[i]);
			}
		}
		
		//Then, we apply that acceleration for a time deltaT (which we get from TitanV3.java) on the velocity
		double[] oldVelocity = new double[velocity.length];
		for (int i = 0; i < velocity.length; i ++) {
			oldVelocity[i] = velocity[i];
			velocity[i] += (acceleration[i] * TitanV3.deltaT);
		}
		
		//And then we apply that velocity on the position to get the new position
		for (int i = 0; i < pos.length; i ++) {
			//We use the average velocity instead of the new velocity to get a better result for the final position (as the velocity progressively increases along the movement)
			pos[i] += ((velocity[i] + oldVelocity[i])/2) * TitanV3.deltaT;
		}
	}
	
	/** Auxiliary method for updatePos, this method computes the acceleration on this planet due to the given parameter Planet
	*/
	public void computeGOfPlanet (PlanetV3 p) {
		double[] oldAcceleration = new double[acceleration.length];
		for (int i = 0; i < acceleration.length; i ++) {
			oldAcceleration[i] = acceleration[i];
			acceleration[i] += (TitanV3.G * (mass * p.getMass()) * ((p.getPosition()[i] - pos[i])/Math.pow(Math.abs(p.getPosition()[i] - pos[i]), 3)))/mass;
			
			//System.out.println("\nResult " + i + ": " + (acceleration[i]-oldAcceleration[i]));
		}
	}
	
	/** Prints the current position
	*/
	public void showPosition () {
		System.out.printf("Planet %s is currently at: \nx = %6.15e, y = %6.15e, z = %6.15e\n", name, (pos[0]/1.49597870700e11), (pos[1]/1.49597870700e11), (pos[2]/1.49597870700e11));
		System.out.printf("Velocity is \nx = %6.15e, y = %6.15e, z = %6.15e\n", (velocity[0]/1.49597870700e11), (velocity[1]/1.49597870700e11), (velocity[2]/1.49597870700e11));
	}
	
	/** Returns the mass of the planet
	*/
	public double getMass() {
		return mass;
	}
	
	/** Returns the position of the planet
	*/
	public double[] getPosition() {
		return pos;
	}
	
	/** Returns the name of the planet
	*/
	public String getName() {
		return name;
	}
}