import javax.swing.JComponent;
import java.awt.Graphics2D;
import java.awt.Graphics;
import java.awt.geom.Ellipse2D;

public class Planet extends JComponent {
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
	private double[] v;
		//v[0] is the velocity on x, v[1] is the velocity on y
	
	public Planet (String name, double mass, double diameter, double[] startingPos, double[] startingV) {
		this.name = name;
		this.mass = mass;
		this.diameter = diameter;
		circleDiameter = diameter * Titan.PixelPerKm;
		System.out.println("Planet " + name + ", diameter: " + diameter + ", pixelPerKm: " + Titan.PixelPerKm + ", circleDiameter: " + circleDiameter);
		
		startPos = new double[startingPos.length];
		startPos[0] = startingPos[0];
		startPos[1] = startingPos[1];
		pos = new double[startingPos.length];
		pos[0] = startingPos[0];
		pos[1] = startingPos[1];
		startV = new double[startingV.length];
		startV[0] = startingV[0];
		startV[1] = startingV[1];
		v = new double[startingV.length];
		v[0] = startingV[0];
		v[1] = startingV[1];
	}
	
	/** To draw the object
	*/
	public void paintComponent (Graphics g) {
		//Recompute the new position
		updatePos();
		
		//Draw the object
		Graphics2D g2 = (Graphics2D) g;
		
		Ellipse2D.Double circle = new Ellipse2D.Double(100 + pos[0]*Titan.PixelPerKm-(circleDiameter/2), 100 + pos[1]*Titan.PixelPerKm-(circleDiameter/2), circleDiameter, circleDiameter);
		
		System.out.println("Planet " + name + " drawn at x = " + circle.getX() + ", y = " + circle.getY() + ", diameter: " + circleDiameter);
		
		g2.setStroke(new Stroke());
		
		g2.fill(circle);
	}
	
	/** Update the planet's position using the physics formulas
	*/
	public void updatePos () {
		pos[0] = pos[0];
		pos[1] = pos[1];
	}
	
	/** Returns the mass of the planet
	*/
	public double getMass() {
		return mass;
	}
	
	/** Returns the position of the planet
	*/
	public double getPosition() {
		return pos;
	}
}