import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Component;
import java.awt.FlowLayout;
import java.util.Arrays;
import java.awt.Color;

public class TitanV3 {
	private static final int WIDTH = 1200;
	private static final int HEIGHT = 800;
	
	private static final String[] planetNames = {"Sun", "Mercury", "Venus", "Earth", "Mars", "Jupiter", "Saturn", "Uranus", "Neptune"};
	private static final double[] planetMasses = {1.9885*Math.pow(10, 30), 3.30*Math.pow(10, 23), 4.87*Math.pow(10, 24), 5.97*Math.pow(10, 24), 6.42*Math.pow(10, 23), 1.898*Math.pow(10, 27), 5.68*Math.pow(10, 26), 8.68*Math.pow(10, 25), 1.02*Math.pow(10, 26)};
	
	//in 1000 km (for the real number, multiply by 1000)
	private static final double[] planetDiameters = {1391.4, 4.879, 12.104, 12.756, 6.792, 142.984, 120.536, 51.118, 49.528};
	
	private static double[][] planetPositions = {{0, 0, 0}, {-58721256768.189260, -5804127334.840320, 4912664883.118753}, {-14558891182.075445, -107699919241.658260, -637617169.962071}, {-148634275524.158570, 8198905701.620354, -76200.747429}, {31241952904.001892, 229805733439.306600, 4048651637.918637}, {-239932095670.644680, -759865514934.437100, 8524600084.986628}, {351693498814.287800, -1462721993695.446800, 11428164894.750835}, {2521978348972.804000, 1568378087179.973600, -26834491690.550697}, {4344340662627.413000, -1085497713760.720300, -77778255698.948700}};
		//The same as right below, but in meters instead of AU
	//private static double[][] planetPositions = {{0, 0, 0}, {-0.3925273567960567, -0.03879819483847988, 0.03283913641371604}, {-0.09732017651020915, -0.7199294932321404, -0.004262207523265710}, {-0.9935587640964904, 0.05480629946974475, -0.0000005093705349706397}, {0.2088395560566083, 1.536156446371844, 0.02706356460138197}, {-1.603846996938872, -5.079387235786619, 0.05698343195059011}, {2.350925833159521, -9.777692602515410, 0.07639256388661175}, {16.85838399418344,  10.48395996441127, -0.1793774975872748}, {29.04012364814637, -7.256104038656750, -0.5199155264376948}};
		//These are in AU (astronomical unit), which is 149597870700 meters ...
	//private static final double[][] planetVelocities = {{0, 0, 0}, {-0.003085174827440397, -0.02678905961013325, -0.001906002544192568}, {0.01990865829058800, -0.002788334487005553, -0.001187140903113389}, {-0.001222948947169834, -0.01723762248532030, 0.0000001549730006627249}, {-0.01333740617497093, 0.003074268766369706, 0.0003916762672222440}, {0.007112084459265169, -0.001917954171635386, -0.0001511468482529501}, {0.005125372011552628, 0.001286146992007010, -0.0002265307895743247}, {-0.002098870396482691, 0.003154630568316460, 0.00003880419915949317}, {0.0007479191938823906, 0.003062927778796595, -0.00008075934314926571}};
		//in AU/day ?
	private static final double[][] planetVelocities = {{0, 0, 0}, {-461535584.922323, -4007586275.731307, -285133922.159991}, {2978292888.765867, -417128902.055408, -177593751.326638}, {-182950558.471414, -2578711619.734359, 23183.630915}, {-1995247564.436683, 459904061.408424, 58593935.580172}, {1063952691.344630, -286921860.176836, -22611246.661657}, {766744739.473649, 192404851.411459, -33888523.768309}, {-313986542.189075, 471926015.865273, 5805025.568479}, {111887118.860466, 458207473.815851, -12081425.774261}};
		//the same as above, but in meters/s instead of AU
	
	protected static PlanetV3[] planets;
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
		planets = new PlanetV3[planetNames.length];
		for (int i = 0; i < planetNames.length; i ++) {
			planets[i] = new PlanetV3(planetNames[i], planetMasses[i], planetDiameters[i], planetPositions[i], planetVelocities[i]);
			//mainFrame.add(planets[i]);
		}
		
		for (int i = 0; i < 11; i ++) {
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