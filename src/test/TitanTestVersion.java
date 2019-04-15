import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Component;
import java.awt.FlowLayout;
import java.util.Arrays;
import java.awt.Color;
import javafx.scene.shape.Circle;
import javafx.scene.paint.*;

public class TitanV4 {
	private static final int WIDTH = 1200;
	private static final int HEIGHT = 800;
	
	private static final String[] planetNames = {			"Sun", 						"Mercury", 																		"Venus", 																			"Earth", 																		"Mars", 																		"Jupiter", 																		"Saturn", 																		"Uranus", 																		"Neptune"};
	private static final double[] planetMasses = {		1.9885e30, 					3.302e23, 																		4.8685e24, 																			5.97219e24, 																	6.4171e23, 																		1.8981*Math.pow(10, 27), 														5.6834e26, 																		8.6813e25, 																		1.02413e26};
		//in kg
	private static final double[] planetDiameters = {	1391.4, 					4.879, 																			12.104, 																			12.756, 																		6.792, 																			142.984, 																		120.536, 																		51.118, 																		49.528};
		//in 1000 km (for the real number, multiply by 1000)
	private static Vector[] planetPositions = {			new Vector(0, 0, 0), 		new Vector(-5.872125676818924e10, -5.804127334840319e9, 4.912664883118753e9), 	new Vector(-1.455889118207544e10, -1.076999192416582e11, -6.376171699620709e8), 	new Vector(-1.486342755241585e11, 8.198905701620353e9, -7.620074742892757e4), 	new Vector(3.124195290400189e10, 2.298057334393066e11, 4.048651637918636e9), 	new Vector(-2.399320956706447e11, -7.598655149344369e11, 8.524600084986627e9), 	new Vector(3.516934988142877e11, -1.462721993695447e12, 1.142816489475083e10), 	new Vector(2.521978348972803e12, 1.568378087179974e12, -2.683449169055068e10), 	new Vector(4.344340662627413e12, -1.085497713760720e12, -7.777825569894868e10)};
		//in meters
	private static final Vector[] planetVelocities = {	new Vector(0, 0, 0), 		new Vector(-5.341847047712075e3, -4.638410041355678e4, -3.300161136111004e3), 	new Vector(3.447098250886419e4, -4.827880810826475e3, -2.055483232947198e3), 		new Vector(-2.117483315641365e3, -2.984619930248100e4, 2.683290615177469e-1), 	new Vector(-2.309314310690604e4, 5.322963673708609e3, 6.781705506964339e2), 	new Vector(1.231426726093322e4, -3.320854863157825e3, -2.617042437691823e2), 	new Vector(8.874360410574640e3, 2.226908002447438e3, -3.922282843554251e2), 	new Vector(-3.634103497558742e3, 5.462106665107330e3, 6.718779593146884e1), 	new Vector(1.294989801625765e3, 5.303327243239019e3, -1.398313168317220e2)};
		//in meters/secs
	
	protected static PlanetV4[] planets;
		//Array containing the 8 planets
		
	//Variables also accessible by other .java files to calculate the pixel size of the different planets
	private static final double sunSizePixel = 200;
	private static final double sunSizeKm = planetDiameters[0];
	protected static final double PixelPerKm = sunSizePixel/sunSizeKm;
	protected static final double G = 6.674 * Math.pow(10, -11);
		//the gravitational constant
	protected static final double deltaT = 1e-4;
		//the time interval which we consider the acceleration is constant on
	protected static final int[] initialTime = {2019, 03, 18};
	
	protected static Vector measurePos = new Vector(planetPositions[3]);
	protected static PlanetV4 measure;
	
	private static Circle Sun = new Circle(250, 250, 3.0f);
	private static Circle Mercury = new Circle(-2.45329598+250, -0.2424887177+250, 2.0f);
	private static Circle Venus = new Circle(-0.6082511032+250, -4.499559333+250, 2.0f);
	private static Circle Earth = new Circle(-6.209742276+250, 0.3425393717+250, 2.0f);
	private static Circle Mars = new Circle(1.305247225+250, 9.60097779+250, 2.0f);
	private static Circle Jupiter = new Circle(-10.02404373+250, -31.74617022+250, 2.0f);
	private static Circle Saturn = new Circle(14.69328646+250, -9.777692602515410+250, 2.0f);
	private static Circle Uranus = new Circle(105.3649+250, 65.52474978+250, 2.0f);
	private static Circle Neptune = new Circle(181.5007728+250, -45.35065024+250, 2.0f);
	protected static Circle[] planetCircles = {Sun, Mercury, Venus, Earth, Mars, Jupiter, Saturn, Uranus, Neptune};

	
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
		createPlanets();
		
		//*  Test object
		measurePos.add(new Vector(planetDiameters[3]*500000, 0, 0));
		
		measure = new PlanetV4("Test", 1, 1, measurePos, new Vector(0, 0, 0));
		
		measure.updatePos();
		System.out.println("For the object on the surface of Earth, with mass 1 kg.");
		measure.showPosition();
		
		/*
		for (int i = 0; i < 31/deltaT; i ++) {
			if (i % (int)(1/deltaT) == 0) {
				System.out.printf("%d.%d.%d \n", initialTime[2]+(int)(i*deltaT), initialTime[1], initialTime[0]);
				planets[3].showPosition();
				System.out.println();
			}
			
			planets[3].updatePos();
		}
		*/
		
		/*
		mainFrame.setVisible(true);
		mainFrame.setLayout(null);
		*/
	}
	
	/** Creates the Planet objects
	*/
	public static void createPlanets() {
		planets = new PlanetV4[planetNames.length];
		for (int i = 0; i < planetNames.length; i ++) {
			planets[i] = new PlanetV4(planetNames[i], planetMasses[i], planetDiameters[i], planetPositions[i], planetVelocities[i], planetCircles[i]);
			//mainFrame.add(planets[i]);
		}
	}
	
	/** Updates the position of all the planets
		First, computes the sum of the gravitational forces on each planet exerted by each other planet
		
		then computes the corresponding acceleration for each planet,
		and from there the new velocity and position for each planet
	*/
	public static void updatePosition() {
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
}