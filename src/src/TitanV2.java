import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Component;
import java.awt.FlowLayout;
import java.util.Arrays;
import java.awt.Color;

public class TitanV2 {
	private static final int WIDTH = 1200;
	private static final int HEIGHT = 800;
	
	private static final String[] planetNames = {"Sun", "Mercury", "Venus", "Earth", "Mars", "Jupiter", "Saturn", "Uranus", "Neptune"};
	private static final double[] planetMasses = {1.9885*Math.pow(10, 30), 3.30*Math.pow(10, 23), 4.87*Math.pow(10, 24), 5.97*Math.pow(10, 24), 6.42*Math.pow(10, 23), 1.898*Math.pow(10, 27), 5.68*Math.pow(10, 26), 8.68*Math.pow(10, 25), 1.02*Math.pow(10, 26)};
	
	//in 1000 km (for the real number, multiply by 1000)
	private static final double[] planetDiameters = {1391.4, 4.879, 12.104, 12.756, 6.792, 142.984, 120.536, 51.118, 49.528};
	
	private static Vector[] planetPositions = {new Vector(0, 0, 0), new Vector(-0.3925273567960567, -0.03879819483847988, 0.03283913641371604), new Vector(-0.09732017651020915, -0.7199294932321404, -0.004262207523265710), new Vector(-0.9935587640964904, 0.05480629946974475, -0.0000005093705349706397), new Vector(0.2088395560566083, 1.536156446371844, 0.02706356460138197), new Vector(-1.603846996938872, -5.079387235786619, 0.05698343195059011), new Vector(2.350925833159521, -9.777692602515410, 0.07639256388661175), new Vector(16.85838399418344,  10.48395996441127, -0.1793774975872748), new Vector(29.04012364814637, -7.256104038656750, -0.5199155264376948)};
		//These are in AU (astronomical unit), which is 149597870700 meters ...
	private static final Vector[] planetVelocities = {new Vector(0, 0, 0), new Vector(-0.003085174827440397, -0.02678905961013325, -0.001906002544192568), new Vector(0.01990865829058800, -0.002788334487005553, -0.001187140903113389), new Vector(-0.001222948947169834, -0.01723762248532030, 0.0000001549730006627249), new Vector(-0.01333740617497093, 0.003074268766369706, 0.0003916762672222440), new Vector(0.007112084459265169, -0.001917954171635386, -0.0001511468482529501), new Vector(0.005125372011552628, 0.001286146992007010, -0.0002265307895743247), new Vector(-0.002098870396482691, 0.003154630568316460, 0.00003880419915949317), new Vector(0.0007479191938823906, 0.003062927778796595, -0.00008075934314926571)};
		//in AU/day ?
	protected static PlanetV2[] planets;
		//Array containing the 8 planets
		
	//Variables also accessible by other .java files to calculate the pixel size of the different planets
	private static final double sunSizePixel = 200;
	private static final double sunSizeKm = planetDiameters[0];
	protected static final double PixelPerKm = sunSizePixel/sunSizeKm;
	protected static final double G = 6.674 * Math.pow(10, -11);
		//the gravitational constant
	protected static final double deltaT = 1;
		//the time interval which we consider the acceleration is constant on
	protected static final int[] initialTime = {2019, 03, 18};
	
	public static void main (String[] args) {
		//Creation of the Window
		/*
		JFrame mainFrame = new JFrame("Solar System");
		mainFrame.setSize(WIDTH, HEIGHT);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setLocationRelativeTo(null);
		mainFrame.setLayout(new FlowLayout());
		*/
		
		//Creation of the planet objects
		planets = new PlanetV2[planetNames.length];
		for (int i = 0; i < planetNames.length; i ++) {
			planets[i] = new PlanetV2(planetNames[i], planetMasses[i], planetDiameters[i], planetPositions[i], planetVelocities[i]);
			//mainFrame.add(planets[i]);
		}
		
		for (int i = 0; i < 31; i ++) {
			System.out.printf("%d.%d.%d \n", initialTime[2]+i, initialTime[1], initialTime[0]);
			planets[3].showPosition();
			planets[3].updatePos();
			System.out.println();
		}		
		
		/*
		mainFrame.setVisible(true);
		mainFrame.setLayout(null);
		*/
	}
}