import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Component;
import java.awt.FlowLayout;
import java.util.Arrays;
import java.awt.Color;

public class Titan {
	private static final int WIDTH = 1200;
	private static final int HEIGHT = 800;
	
	private static final String[] planetNames = {"Sun", "Mercury", "Venus", "Earth", "Mars", "Jupiter", "Saturn", "Uranus", "Neptune"};
	private static final double[] planetMasses = {1.9885*Math.pow(10, 30), 3.30*Math.pow(10, 23), 4.87*Math.pow(10, 24), 5.97*Math.pow(10, 24), 6.42*Math.pow(10, 23), 1.898*Math.pow(10, 27), 5.68*Math.pow(10, 26), 8.68*Math.pow(10, 25), 1.02*Math.pow(10, 26)};
	
	//in 1000 km (for the real number, multiply by 1000)
	private static final double[] planetDiameters = {1391.4, 4.879, 12.104, 12.756, 6.792, 142.984, 120.536, 51.118, 49.528};
	private static final double[] aphelion = {0, 69816.9, 108939, 152100, 249200, 816620, 1514500, 3008000, 4540000};
	private static final double[] perihelion = {0, 46001.2, 107477, 147095, 206700, 740520, 1352550, 2742000, 4460000};
	private static double[][] planetPositions;
	private static final double[][] planetAverageVelocities = {{0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}};
	protected static Planet[] planets;
		//Array containing the 8 planets
		
	//Variables also accessible by other .java files to calculate the pixel size of the different planets
	private static final double sunSizePixel = 200;
	private static final double sunSizeKm = planetDiameters[0];
	protected static final double PixelPerKm = sunSizePixel/sunSizeKm;
	protected static final double G = 6.674 * Math.pow(10, -11);
		//the gravitational constant
	
	public static void main (String[] args) {
		//Creation of the Window
		JFrame mainFrame = new JFrame("Solar System");
		mainFrame.setSize(WIDTH, HEIGHT);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setLocationRelativeTo(null);
		mainFrame.setLayout(new FlowLayout());
		
		//Creation of the planet objects
		planets = new Planet[planetNames.length];
		//Provisory
		planetPositions = new double[aphelion.length][2];
		for (int i = 0; i < planetNames.length; i ++) {
			planetPositions[i][0] = 0;
			planetPositions[i][1] = (aphelion[i]+perihelion[i]);
			planets[i] = new Planet(planetNames[i], planetMasses[i], planetDiameters[i], planetPositions[i], planetAverageVelocities[i]);
			mainFrame.add(planets[i]);
		}
		
		/*Prints the list of components of the mainFrame
			Note: the components are printed out as hexcodes, thus this is not very useful
		Component[] c = mainFrame.getContentPane().getComponents();
		for (int i = 0; i < c.length; i ++) {
			System.out.println("Component: " + c);
		}
		*/
		
		mainFrame.setVisible(true);
		mainFrame.setLayout(null);
	}
}