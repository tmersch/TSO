import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.Animation;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.scene.Group;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import java.util.Scanner;
import javafx.scene.text.*; 		//needed ?
import javafx.scene.transform.Rotate;
import javafx.scene.chart.NumberAxis;
import javafx.geometry.Side;

/** GUI representing the solar system using javafx
*/
public class GUIV2 extends Application {
	// !!! Variables only used by the GUI of the solar simulation !!!
	//Number of seconds between each update
	private static double DELTA_T = 60 * 30;
	//debug boolean (un)locking println statements
	private final boolean DEBUG = true;

	private static final double SCALE = 5e9;		//Scaling factor in meters/pixels
	//radius of the planets
	private static final double PLANET_RADIUS = 2;
	//height of the part at the top
	private static final int TOP_AREA_HEIGHT = 100;

	//GUI parts only used for the solar system simulation GUI
	private Label timeLabel;
	private long startTime = -1;				//variable used internally
	private int numIterations;					//number of iterations in the current simulation
	private boolean showNumIterations = false;

	//Everything about the planets
		//Array containing the planets/moons
	protected static CelestialBody[] planets;
		//Arrays containing all the information about the planets/moons
		// Indexes: ----------------------------    	0							1							2							3							4							5								6								7								8								9							10						11
	private static final String[] planetNames = {		"Sun", 				"Mercury", 		"Venus",			"Earth",			"Mars", 			"Jupiter", 			"Saturn", 			"Uranus", 			"Neptune", 			"Titan", 			"Moon", 			"Ganymede"};
		//names of the planets
	private static final double[] planetMasses = {	1.9885e30, 		3.302e23, 		4.8685e24, 		5.97219e24, 	6.4171e23, 		1.8981e27, 			5.6834e26, 			8.6813e25, 			1.02413e26, 		1.34553e23, 	7.349e22, 		1.482e23};
		//masses in kg
	protected static final double[] planetRadius = {695700e3, 		2440e3, 			6051.84e3, 		6371.01e3, 		3389.92e3, 		71492e3, 				60268e3, 				25559e3, 				24766e3, 				2575.5e3, 		1737.53e3,		2.634e6};
		//average radius in meters
	protected static final Vector2D[] planetPositions = {	new Vector2D(0, 0, 0), 	new Vector2D(-5.872125676818924e10, -5.804127334840319e9, 4.912664883118753e9), 	new Vector2D(-1.455889118207544e10, -1.076999192416582e11, -6.376171699620709e8), 	new Vector2D(-1.486342755241585e11, 8.198905701620353e9, -7.620074742892757e4), 	new Vector2D(3.124195290400189e10, 2.298057334393066e11, 4.048651637918636e9), 	new Vector2D(-2.399320956706447e11, -7.598655149344369e11, 8.524600084986627e9), 	new Vector2D(3.516934988142877e11, -1.462721993695447e12, 1.142816489475083e10), 	new Vector2D(2.521978348972803e12, 1.568378087179974e12, -2.683449169055068e10), 	new Vector2D(4.344340662627413e12, -1.085497713760720e12, -7.777825569894868e10), new Vector2D(3.509094646023610e11, -1.461827053014912e12, 1.104487392229486e10), new Vector2D(-1.488847132490647e11, 8.460303130865488e9, 1.051062554109981e7), new Vector2D(-2.394535094933694e11, -7.589080066586609e11, 8.567293856946945e9)};
		//positions at initialTime in meters
	private static final Vector2D[] planetVelocities = {	new Vector2D(0, 0, 0), 		new Vector2D(-5.341847047712075e3, -4.638410041355678e4, -3.300161136111004e3), 	new Vector2D(3.447098250886419e4, -4.827880810826475e3, -2.055483232947198e3), 		new Vector2D(-2.117483315641365e3, -2.984619930248100e4, 2.683290615177469e-1), 	new Vector2D(-2.309314310690604e4, 5.322963673708609e3, 6.781705506964339e2), 	new Vector2D(1.231426726093322e4, -3.320854863157825e3, -2.617042437691823e2), 		new Vector2D(8.874360410574640e3, 2.226908002447438e3, -3.922282843554251e2), 		new Vector2D(-3.634103497558742e3, 5.462106665107330e3, 6.718779593146884e1), 		new Vector2D(1.294989801625765e3, 5.303327243239019e3, -1.398313168317220e2), new Vector2D(4.602663714929883e3, -5.834636275449419e2, 1.481088959791306e3), new Vector2D(-2.872352106787649e3, -3.061966993302498e4, 9.000888638350801e1), new Vector2D(2.600098840800917e3, 1.559203853272592e3, -2.073416903427829e2)};
		//velocity at initialTime in meters/secs

	private static final int[] initialTime = {18, 3, 2019};
		//the day for which the positions and velocities of the planets are taken

	//All the variables concerning the spaceprobe
	private static SpaceProbe spaceProbe;											//the spaceProbe object
	private static final double voyagerMass = 800;									//in kg
	private static final double averageVelocitySpaceProbe = 48e3;					//in meters/secs
	private static final double averageVelocitySpaceProbeReturnTravel = 10e3;		//in meters/secs

	//the gravitational constant
	public static final double G = 6.67300E-11;

	// !!! Variables used only by the landing GUI !!!
	private static final double LANDINGSCALE = 2.0e3;	//in meters/pixel
	private static double LANDINGDELTA_T = 1;			//in seconds

	//Variables concerning the size of the window and the position of the x- and y-axes concerning the landing
	private final int LANDINGWINDOWWIDTH = 1920;
    private final int LANDINGWINDOWHEIGHT = 1080;
    private final int LANDINGXAXISHEIGHT = 700;
    private final int LANDINGYAXISWIDTH = 750;
	//Variables concerning the size of the rectangle displayed representing the landing module
	protected final int RECT_WIDTH = 10;
    protected final int RECT_HEIGHT = 20;

	//Text variables to show the current state of the landing module
	private Text altitudeText;
    private Text verticalSpeedText;
    private Text timeText;

	//The landingModule
	private LandingModule landingModule;
	private final int landingModuleWeight = 800;			//in kgs

    // !!! Variables used by both GUI's !!!
	//Seconds conversion
	private static final int SEC_IN_MINUTE = 60;
  	private static final int SEC_IN_HOUR = SEC_IN_MINUTE * 60;
  	private static final int SEC_IN_DAY = SEC_IN_HOUR * 24;
  	private static final int SEC_IN_YEAR = 31556926;
	//and the long keeping track of the number of seconds elapsed from the start
  	private long elapsedSeconds = 0;

	//GUI parts shared by the two GUI's
	private Timeline timeline;
	private CoordinatesTransformer coordinates = new CoordinatesTransformer();
	private GraphicsContext gc;

	//Width and height of the window
	private double canvasWidth = 0;
	private double canvasHeight = 0;

	/** Main method, gets called when the program is executed
	*/
	public static void main (String[] args) {
		launch(args);
	}

	/** This method is called by JavaFX GUI when starting the program
	*/
	public void start (Stage stage) {
		//Initialize the planets
		createSolarSystem();

		//CoordinateTransformer
		coordinates.setScale(SCALE);
		coordinates.setModifiedX(700);
		coordinates.setModifiedY(350);

		//Initialize variables
		Scanner s = new Scanner(System.in);
		String input = "";
		boolean choiceMade = false;

		//As long as the User does not make a valid choice, we repeat asking him what he wants to do
		do {
			System.out.println("Do you want to see the solar system simulation, travel to Titan or back (enter 1) or see the landing (enter 2) ?");
			//Set input to the user's input
			input = s.next();

			switch (input) {
				case "1":				//Simulation of the solar system
					//Initialize the planets
					createSolarSystem();

					//Initialize the CoordinateTransformer to have the coordinate system's center be somewhere in the center of the screen
					coordinates.setScale(SCALE);
					coordinates.setModifiedX(700);
					coordinates.setModifiedY(350);

					//Initialize a new string and a new boolean for the user input
					String choice = "";
					boolean validInput = false;
					do {
						System.out.println("Do you want to see the GUI of the solar simulation (enter 1), shoot the space probe to Titan (enter 2), shoot the spaceProbe from Titan back to Earth (enter 3) or test the spaceProbe launch (enter 4) ?");
						choice = s.next();

						switch (choice) {
							case "1":
								//Solar system simulation

								//Let the user select a custom timestep (after which interval of simulated time do we update the position of the planets)
								System.out.println("Which timestep do you want to use (in secs)?");
								DELTA_T = s.nextDouble();

								//Optionally, let the User select an ending time for the simulation (in simulated time)
								//System.out.println("After how much time (in secs) do you want to end the simulation ?");
								//double endTime = s.nextLong();
								//int numIterations = (int)Math.ceil(endTime/DELTA_T);

								boolean spaceProbeIncluded = false;

								//GUI part
								gc = createGUI(stage);
								launchGUI(1, spaceProbeIncluded/*, numIterations*/);
								timeline.play();
								stage.show();

								/*	Debugging part used for printing the planet's position after a certain time
								while (!timeline.getStatus().equals(Animation.Status.STOPPED)) {
								}

								for (int i = 0; i < planets.length; i ++) {
									System.out.println("Planet " + planets[i].getName() + ": \nPosition: " + planets[i].getPosition());
								}
								*/

								//Set validInput to true and break the switch statement to end the loop of asking the User to choose what he wants to do
								validInput = true;
								break;
							//Launch of space probe from Earth to Titan
							case "2":
								//Initialize some variables for the launchAngleBinarySearch method
								double startLaunchAngle = 259.84639616224865;			//Should already be the end result
								double startAngleChange = 0.1;
								int originPlanetIndex = 3;
								int destinationPlanetIndex = 9;

								//Reset the spaceProbe with the new launch_angle
								spaceProbe = launchAngleBinarySearch(originPlanetIndex, destinationPlanetIndex, startLaunchAngle, startAngleChange, averageVelocitySpaceProbe);

								//Launch the simulation
							  	gc = createGUI(stage);
								launchGUI(1, true);			//compute next position after 10 milliseconds, and also consider the spaceProbe
								timeline.play();
								stage.show();

								validInput = true;
								break;
							//Return of the spaceProbe from Titan to earth
							case "3":
								//Initialize some variables for the launchAngleBinarySearch method
								startLaunchAngle = 121.57024271172554;				//110.25108742952477;					//Should already be the final resulting value (for velocity = 30 km/s)
								startAngleChange = 0.1;
								originPlanetIndex = 9;
								destinationPlanetIndex = 3;

								//Reset the spaceProbe with the new launch_angle
								spaceProbe = launchAngleBinarySearch(originPlanetIndex, destinationPlanetIndex, startLaunchAngle, startAngleChange, averageVelocitySpaceProbeReturnTravel);

								//Launch the simulation
							  	gc = createGUI(stage);
								launchGUI(1, true);			//compute next position after 10 milliseconds, and also consider the spaceProbe
								timeline.play();
								stage.show();

								validInput = true;
								break;
							//Test of space probe angle
							case "4":
								Scanner S = new Scanner(System.in);
								System.out.println("Enter the angle you would like to launch the spaceProbe in: ");
								double launch_angle = S.nextDouble();

								/*
								System.out.println("Do you want a fixed number of iterations ? If yes, enter the number, otherwise enter '-1': ");
								int numberOfIterations = S.nextInt();
								*/
								//Reset the solar system
								createSolarSystem();

								CelestialBody originPlanet = planets[9];

								//Create a new spaceProbe with the starting angle
								spaceProbe = SpaceProbe.createSpaceProbeWithStartingAngle("SpaceProbe", voyagerMass, originPlanet, averageVelocitySpaceProbe, launch_angle);

								//Show the simulation of the solar system with the space probe
								showNumIterations = true;
								gc = createGUI(stage);
								//if (numberOfIterations == -1) {
									launchGUI(1, true);
								/*} else {
									launchGUI(1, true, numberOfIterations);
								} */
								timeline.play();
								stage.show();

								validInput = true;
								break;
						}
					} while (! validInput);

					//Set choideMade to true, meaning that a simulation has been done and we do not need to ask the user what he wants to choose another time
					choiceMade = true;
					//And break to exit the switch statement
					break;
			case "2": 					//Landing on titan
				//Initialize the landModChoice variable to an empty string
				String landModChoice = "";
				//And initialize the starting position, starting velocity and starting angle to default values
				Vector2D landModStartPos = new Vector2D(0, 1200000);
				Vector2D landModStartVeloc = new Vector2D(0, 0);
				double landModStartAngle = 0;

				//Ask the user which kind of controller he would like to have
				while ((!landModChoice.equals("1")) && (!landModChoice.equals("2"))) {
					System.out.println("Which landing module controller do you want to use (enter 1 for the open-loop controller, 2 for the feedback controller)?");
					landModChoice = s.next();

					switch (landModChoice) {
						case "1": 					//open-loop controller
							//For the open-loop controller, we could make the timestep bigger as it takes a lot of time to land
							//LANDINGDELTA_T = 100;

							//Initialize selection
							String selection = "";

							//Ask the user if he wants to specify a certain starting position for the landing module
							System.out.println("Do you want to specify a specific starting position ? Enter 'yes' or 'no'");
							selection = s.next();
							//If the user wants to specify a starting position, then we retrieve the x- and y-position and save it
							if (selection.equals("yes")) {
								System.out.println("Please enter the x-position and y-position separated by a space");
								double startX = s.nextDouble();
								double startY = s.nextDouble();
								landModStartPos = new Vector2D(startX, startY);
							}

							//Ask the user if he wants to specify a certain starting velocity for the landing module
							selection = "";
							System.out.println("Do you want to specify a specific starting velocity ? Enter 'yes' or 'no'");
							selection = s.next().toLowerCase();
							//If the user wants to specify a starting velocity, retrieve the input velocity and save it
							if (selection.equals("yes")) {
								System.out.println("Please enter the x-velocity and y-velocity separated by a space");
								double startXVeloc = s.nextDouble();
								double startYVeloc = s.nextDouble();
								landModStartVeloc = new Vector2D(startXVeloc, startYVeloc);
							}

							//Ask the user if he wants to specify a certain starting angle
							selection = "";
							System.out.println("Do you want to specify a specific starting angle ? Enter 'yes' or 'no'");
							selection = s.next().toLowerCase();
							//If the user wants to specify a starting angle, retrieve the input angle and save it
							if (selection.equals("yes")) {
								System.out.println("Please enter the angle");
								landModStartAngle = s.nextDouble();
							}

							landingModule = new LandingModuleOpenLoopController(landingModuleWeight, landModStartPos, landModStartVeloc, landModStartAngle);

							break;
						case "2":					//Feedback controller
							//Initialize selection
							selection = "";

							//Ask the user if he wants to specify a certain starting position for the landing module
							System.out.println("Do you want to specify a specific starting position ? Enter 'yes' or 'no'");
							selection = s.next();
							//If the user wants to specify a starting position, then we retrieve the x- and y-position and save it
							if (selection.equals("yes")) {
								System.out.println("Please enter the x-position and y-position separated by a space");
								double startX = s.nextDouble();
								double startY = s.nextDouble();
								landModStartPos = new Vector2D(startX, startY);
							}

							//Ask the user if he wants to specify a certain starting velocity for the landing module
							selection = "";
							System.out.println("Do you want to specify a specific starting velocity ? Enter 'yes' or 'no'");
							selection = s.next().toLowerCase();
							//If the user wants to specify a starting velocity, retrieve the input velocity and save it
							if (selection.equals("yes")) {
								System.out.println("Please enter the x-velocity and y-velocity separated by a space");
								double startXVeloc = s.nextDouble();
								double startYVeloc = s.nextDouble();
								landModStartVeloc = new Vector2D(startXVeloc, startYVeloc);
							}

							//Ask the user if he wants to specify a certain starting angle
							selection = "";
							System.out.println("Do you want to specify a specific starting angle ? Enter 'yes' or 'no'");
							selection = s.next().toLowerCase();
							//If the user wants to specify a starting angle, retrieve the input angle and save it
							if (selection.equals("yes")) {
								System.out.println("Please enter the angle");
								landModStartAngle = s.nextDouble();
							}

							//Ask the user if he wants to add wind to the simulation or not
							boolean addWind = false;
							selection = "";
							System.out.println("Do you want to add wind to the simulation ? Enter 'yes' if you want to add wind, otherwise 'no'");
							selection = s.next().toLowerCase();
							double windStrength = 2;
							double thrusterForce = 2500;
							//If the user wants to have wind, give it to him !!!
							if (selection.equals("yes")) {
								addWind = true;

								String windChoice = "";
								System.out.println("Do you want to set the maximum acceleration of the wind ?");
								windChoice = s.next().toLowerCase();
								if (windChoice.equals("yes")) {
									System.out.println("What should the maximum acceleration of the wind be ?");
									windStrength = s.nextDouble();
								}

								System.out.println("Do you want to set the force of the main thruster ?");
								windChoice = s.next().toLowerCase();
								if (windChoice.equals("yes")) {
									System.out.println("What should the force of the main thruster be ?");
									thrusterForce = s.nextDouble();
								}
							}

							landingModule = new LandingModuleFeedbackController(landingModuleWeight, landModStartPos, landModStartVeloc, landModStartAngle, addWind);
							((LandingModuleFeedbackController)landingModule).setMaxWindStrength(windStrength);
							((LandingModuleFeedbackController)landingModule).setThrusterForce(thrusterForce);

							break;
					}
				}

				boolean showGUI = true;
				if (showGUI) {
					//Set the coordinatesTransformer with the correct parameters for the landing GUI
			        coordinates.setScale(LANDINGSCALE);
			        coordinates.setModifiedX(LANDINGYAXISWIDTH);
			        coordinates.setModifiedY(LANDINGXAXISHEIGHT);

			        //And launch the landing GUI
			        gc = createLandingGUI(stage, landingModule);
			        launchLandingGUI(1);
			        stage.show();
			        timeline.play();
				}
				else {
					landingModule.updateModule(LANDINGDELTA_T);
				}

				//Set choiceMade to true to signify that a simulation has been done, and we do not need to ask the User what he wants to choose anymore
				choiceMade = true;
				break;
			}
		} while (! choiceMade);
	}

	/** Overloads method launchAngleBinarySearch with one less parameter than the original: the boolean DEBUG
	  */
	private SpaceProbe launchAngleBinarySearch (final int originPlanetIndex, final int destinationPlanetIndex, final double startLaunchAngle, final double startAngleChange, final double spaceProbeVelocity) {
		boolean DEBUG_MODE_ON = false;
		return launchAngleBinarySearch(originPlanetIndex, destinationPlanetIndex, startLaunchAngle, startAngleChange, spaceProbeVelocity, DEBUG_MODE_ON);
	}

	/** Computes the optimal launch angle for the spaceProbe to reach the destinationPlanet starting from the originPlanet
	  *
	  * @param originPlanet, the planet from which the spaceProbe starts off
	  * @param destinationPlanet, the planet at which the spaceProbe should arrive
	  * @param startLaunchAngle, a starting guess for the correct launch angle
	  * @param startAngleChange, the starting amount of degrees by which the launch angle is modified at each iteration
	  * @param spaceProbeVelocity, the total velocity of the spaceProbe at the starting position
	  * @param DEBUG, a boolean unlocking debug print statements
	  */
	private SpaceProbe launchAngleBinarySearch (final int originPlanetIndex, final int destinationPlanetIndex, final double startLaunchAngle, final double startAngleChange, final double spaceProbeVelocity, final boolean DEBUG) {
		//Initialize variables
		double launch_angle = startLaunchAngle;
		double angleChange = startAngleChange;
		boolean crashedDestinationPlanet = false;
		double DISTANCE_SUN_PLUTO = 5906376272e3;
		int previousMove = 0;
		int numberIterations = 0;
		double spaceProbeAngle = 0;
		double destinationPlanetAngle  = 0;

		//As long as we have not reached our goal of crashing into destinationPlanet
		while (! crashedDestinationPlanet) {
			if (DEBUG) System.out.println("Iteration #" + numberIterations);

			//Initialize the space probe and the solar system
			createSolarSystem();

			//Create variables representing the originPlanet, the destinationPlanet and the Sun
			CelestialBody Sun = planets[0];
			CelestialBody originPlanet = planets[originPlanetIndex];
			CelestialBody destinationPlanet = planets[destinationPlanetIndex];

			//Create a new spaceProbe launched with a new angle from planet Earth
			spaceProbe = SpaceProbe.createSpaceProbeWithStartingAngle("SpaceProbe", voyagerMass, originPlanet, spaceProbeVelocity, launch_angle);

			int num = 0;
			//As long as the spaceProbe has not crashed into a planet, or gone further away from the originPlanet than the destinationPlanet's distance from the originPlanet or gone further away from the Sun than Pluto's distance to the Sun
			while (spaceProbe.didNotCrash() && (spaceProbe.getPosition().distance(originPlanet.getPosition()) < destinationPlanet.getPosition().distance(originPlanet.getPosition())) && spaceProbe.getPosition().distance(Sun.getPosition()) < DISTANCE_SUN_PLUTO)  {
				//Update the position and the number of iterations
				update(DELTA_T, true);
				num ++;
			}

			if (DEBUG) System.out.println(num + " iterations needed to end simulation");

			//If the space probe crashed on Titan, we are done
			if (planets[destinationPlanetIndex].equals(spaceProbe.getCrashedPlanet())) {
				crashedDestinationPlanet = true;
				System.out.println("\n\n\nIteration #" + numberIterations + "A launch angle of " + launch_angle + " degrees got the spaceProbe to Titan.");
			}
			else {
				//If we crash into a planet, we print it to the console, then reset the variable in the spaceProbe
				if (spaceProbe.getCrashedPlanet() != null)
					if (DEBUG) System.out.println("Crashed in " + spaceProbe.getCrashedPlanet().getName());
				spaceProbe.resetCrashedPlanet();

				//Then, we compute the angles with respect to a certain CelestialBody
				Vector2D referencePoint = new Vector2D(planetPositions[originPlanetIndex]);//planetPositions[0]);

				spaceProbeAngle = spaceProbe.getPosition().angle(referencePoint);
				destinationPlanetAngle = planets[destinationPlanetIndex].getPosition().angle(referencePoint);

				//Both angles should now be between 0 and 360 degrees
				if (DEBUG) System.out.printf("Arrival SpaceProbeAngle: %f, Titan angle: %f\n", spaceProbeAngle, destinationPlanetAngle);

				int tmp = signum(spaceProbeAngle-destinationPlanetAngle);

				if (Math.abs(destinationPlanetAngle-spaceProbeAngle) > 180) {
					if (previousMove == -tmp) {
						if (DEBUG) { System.out.println("Move == " + tmp + ": \nSpaceProbe angle: " + spaceProbeAngle + ", Titan angle: " + destinationPlanetAngle + ", ");
							if (previousMove > 0) System.out.println("   The angle should be between " + (launch_angle-previousMove*angleChange) + " and " + launch_angle);
							else System.out.println("   The angle should be between " + launch_angle + " and " + (launch_angle-previousMove*angleChange));
						}

						angleChange = angleChange/2;
					}

					launch_angle += tmp * angleChange;

					previousMove = tmp;
				}
				else if (Math.abs(destinationPlanetAngle-spaceProbeAngle) > 0) {
					if (previousMove == tmp) {
						if (DEBUG) { System.out.println("Move == " + tmp + ": \nSpaceProbe angle: " + spaceProbeAngle + ", Titan angle: " + destinationPlanetAngle + ", ");
							if (previousMove > 0) System.out.println("   The angle should be between " + (launch_angle-previousMove*angleChange) + " and " + launch_angle);
							else System.out.println("   The angle should be between " + launch_angle + " and " + (launch_angle-previousMove*angleChange));
						}

						angleChange = angleChange/2;
					}

					launch_angle += -tmp * angleChange;

					previousMove = -tmp;
				}
				else {
					//The two angles are the same, but I will still modify the angle in order for angleChange to become smaller and smaller and to refine the launch_angle even more
					//Modify the angle in some direction
					launch_angle += tmp * angleChange;

					previousMove = tmp;
				}
			}

			//Once we cannot get a more precise value (that is, the change of the angle angleChange is smaller than the distance of launch_angle to the next double value after launch_angle)
			if (angleChange < Math.ulp(launch_angle)) {
				/*if (DEBUG) */System.out.println("Final angleChange = " + angleChange);
				crashedDestinationPlanet = true;
			}

			System.out.println("New space probe launch angle: " + launch_angle);

			numberIterations ++;
			elapsedSeconds = 0;
		}

		//Now, launch_angle has a supposedly optimal angle to reach Titan, and we show the result
		//Reset the solar System
		createSolarSystem();

		//Print out all angles ...
		//System.out.println("\n\nSpaceProbeAngle: " + spaceProbeAngle);
		//System.out.println("destinationPlanetAngle: " + destinationPlanetAngle);
		System.out.println("'Optimal' launch angle: " + launch_angle);

		//Reset the spaceProbe with the new launch_angle
		SpaceProbe result = SpaceProbe.createSpaceProbeWithStartingAngle("SpaceProbe", voyagerMass, planets[originPlanetIndex], spaceProbeVelocity, launch_angle);

		return result;
	}

	/** Overloads method launchAngleBinarySearch with one less parameter than the original: the boolean DEBUG
	  */
	private SpaceProbeWithThrusters launchAngleBinarySearchImproved (final int originPlanetIndex, final int destinationPlanetIndex, final double startLaunchAngle, final double startAngleChange, final double spaceProbeVelocity) {
		boolean DEBUG_MODE_ON = false;
		return launchAngleBinarySearchImproved(originPlanetIndex, destinationPlanetIndex, startLaunchAngle, startAngleChange, spaceProbeVelocity, DEBUG_MODE_ON);
	}

	/** Computes the optimal launch angle for the spaceProbe to reach the destinationPlanet starting from the originPlanet
	  *
	  * @param originPlanet, the planet from which the spaceProbe starts off
	  * @param destinationPlanet, the planet at which the spaceProbe should arrive
	  * @param startLaunchAngle, a starting guess for the correct launch angle
	  * @param startAngleChange, the starting amount of degrees by which the launch angle is modified at each iteration
	  * @param spaceProbeVelocity, the total velocity of the spaceProbe at the starting position
	  * @param DEBUG, a boolean unlocking debug print statements
	  */
	private SpaceProbeWithThrusters launchAngleBinarySearchImproved (final int originPlanetIndex, final int destinationPlanetIndex, final double startLaunchAngle, final double startAngleChange, final double spaceProbeVelocity, final boolean DEBUG) {
		//Reset the solar system
		createSolarSystem();

		//Use launchAngleBinarySearch() to get a SpaceProbe launched in the correct angle
		SpaceProbe spaceP = launchAngleBinarySearch(originPlanetIndex, destinationPlanetIndex, startLaunchAngle, startAngleChange, spaceProbeVelocity, DEBUG);

		//Transform the spaceProbe into a spaceProbeWithThrusters given the spaceProbe and the originPlanet which we start from
		SpaceProbeWithThrusters spaceProbeOriginal = SpaceProbeWithThrusters.spaceProbeToSpaceProbeWithThrusters(spaceP);

		//Then, use it to launch simulations
		SpaceProbeWithThrusters spaceProbeClone = spaceProbeOriginal.clone();

		//Run simulations where we try using the thrusters at some positions, then evaluate the result

		//Pseudo-code could be sth like that:
		/*
		for  (int i = last; i >= first; i --) {
			- try orienting the Rocket in the correct direction to get closer to the destinationPlanet
			- finish this simulation
			- evaluate this simulation:
				+ we hit the destinationPlanet: good, do we still want to continue searching to try to get into orbit ?
				+ we are left of the destination:
					correct more / less the trajectory
				+ we are right of the destination:
					correct less / more the trajectory
		}
		*/

		//Return the resulting spaceProbe with the "perfect" stats
		//This is a placeholder to avoid getting a "missing return statement" error
		return spaceProbeClone;
	}


	/** Creates the different GUI elements necessary for the landing GUI, or at least initializes them
      */
    private GraphicsContext createLandingGUI (Stage stage, LandingModule landingMod) {
        //Create the borderPane
        BorderPane border = new BorderPane();

        //Create the different labels showing information
        altitudeText = new Text();
        altitudeText.setFont(new Font(15));
        altitudeText.translateXProperty().set(40);
        altitudeText.translateYProperty().set(30);

        verticalSpeedText = new Text();
        verticalSpeedText.setFont(new Font(15));
        verticalSpeedText.translateXProperty().set(40);
        verticalSpeedText.translateYProperty().set(60);

        timeText = new Text();
        timeText.setFont(new Font(15));
        timeText.translateXProperty().set(40);
        timeText.translateYProperty().set(90);

		//Create the rectangle representing the landing module at it's starting position
		Vector2D pos = new Vector2D(landingMod.getPosition().getX(), -landingMod.getPosition().getY());      //-landingModule because the y-axis if inverted in java fx
		Vector2D otherPosition = coordinates.modelToOtherPosition(pos);
		Rectangle rect = new Rectangle(otherPosition.getX() - RECT_WIDTH, otherPosition.getY() - RECT_HEIGHT, RECT_WIDTH, RECT_HEIGHT);
		rect.setFill(Color.BLACK);
		landingMod.setRectangle(rect);

		//Create a rotate object, and set it the correct angle and position of center of rotation
		Rotate rectRotation = new Rotate();
		rectRotation.setAngle(landingModule.getAngle());
		rectRotation.setPivotX(otherPosition.getX());
		rectRotation.setPivotY(otherPosition.getY());

		//Apply the rotation to the rectangle
		landingModule.getRectangle().getTransforms().add(rectRotation);

		//And set the label's initial texts
        altitudeText.setText("Altitude: " + landingModule.getPosition().getY() + ", x-position: " + landingModule.getPosition().getX() +  ", angle: " + landingModule.getAngle());
        verticalSpeedText.setText("Vertical speed: " + landingModule.getVelocity().getY() + ", horizontal speed: " + landingModule.getVelocity().getX());
        timeText.setText("Elapsed time: " + getTimeAsString(elapsedSeconds));

        //Draw the line marking the ground of Titan
        Line line = new Line(0, LANDINGXAXISHEIGHT, LANDINGWINDOWWIDTH, LANDINGXAXISHEIGHT);

        //Create the canvas
        Canvas canvas = new Canvas();
        //add the zooming capability
        canvas.setOnScroll((event) -> {
            if (event.getDeltaY() > 0) {
                coordinates.setScale(coordinates.getScale() * 0.9);
            } else {
                coordinates.setScale(coordinates.getScale() * 1.1);
            }
        });

        //and set it in the center of the borderpane
        border.setCenter(canvas);
        Scene scene = new Scene(border);
        border.getChildren().addAll(line, altitudeText, verticalSpeedText, timeText, rect);

        //Set the title, scene of the stage and set the window to full screen
        stage.setTitle("Landing");
        stage.setScene(scene);
        stage.setMaximized(true);

        // Bind canvas size to stack pane size
        canvas.widthProperty().bind(stage.widthProperty());
        canvas.heightProperty().bind(stage.heightProperty());
        return canvas.getGraphicsContext2D();
    }

	/** Makes the last preparations for launching the landing GUI
      *
      */
    private void launchLandingGUI (double updateInterval) {
        //Reset the seconds counter
        elapsedSeconds = 0;

        //Create the new timeline
        timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);

        //Create a keyFrame to update the landing frame
        KeyFrame kf = new KeyFrame(
          Duration.millis(updateInterval),
          new EventHandler<ActionEvent> () {
              public void handle (ActionEvent e) {
                  updateLandingGUI(gc);
              }
          }
        );

        //And link it to the timeline
        timeline.getKeyFrames().add(kf);
    }

	/** Draws the landing module at it's current position, then calls another method of the landing module class to update the position of the spaceProbe
      *
      */
    private void updateLandingGUI(GraphicsContext gc) {
        //Clear the window's previous contents, not sure if it's really needed
        double canvasWidth = gc.getCanvas().getWidth();
        double canvasHeight = gc.getCanvas().getHeight();
        gc.clearRect(0, 0, canvasWidth, canvasHeight);

        //Retrieve the landing module's position and modifiy it to pixel coordinates
        Vector2D pos = new Vector2D(landingModule.getPosition().getX(), -landingModule.getPosition().getY());      //-landingModule because the y-axis if inverted in java fx
        Vector2D otherPosition = coordinates.modelToOtherPosition(pos);

		//Set the rectangle representing the landingModule to the correct x- and y-position
		landingModule.getRectangle().setX(otherPosition.getX() - RECT_WIDTH);
		landingModule.getRectangle().setY(otherPosition.getY() - RECT_HEIGHT);

		//Retrieve the rotate object, and set it the new angle and new position
		Rotate rectRotation = (Rotate)landingModule.getRectangle().getTransforms().get(0);
		rectRotation.setAngle(landingModule.getAngle());
		rectRotation.setPivotX(otherPosition.getX());
		rectRotation.setPivotY(otherPosition.getY());

        //Set the labels
        altitudeText.setText("Altitude: " + landingModule.getPosition().getY() + ", x-position: " + landingModule.getPosition().getX() +  ", angle: " + landingModule.getAngle());
        verticalSpeedText.setText("Vertical speed: " + landingModule.getVelocity().getY() + ", horizontal speed: " + landingModule.getVelocity().getX());
        timeText.setText("Elapsed time: " + getTimeAsString(elapsedSeconds));

        // update rectPos if the landing module has not yet landed
        if (! landingModule.hasLanded()) {
            landingModule.updateModuleOneIteration(LANDINGDELTA_T);
        }
        else { //else, the landing module has landed and we stop the simulation
            timeline.stop();
        }

        //Keep track of the time elapsed since the start of the simulation
        elapsedSeconds += LANDINGDELTA_T;
    }

	/** Initialize the Planet and the Moon objects with their default properties, contained in the planet... arrays
	*/
	public void createSolarSystem() {
		planets = new CelestialBody[planetNames.length];
		for (int i = 0; i < planetNames.length; i ++) {
			planets[i] = new CelestialBody(planetNames[i], planetMasses[i], planetPositions[i], planetVelocities[i], planetRadius[i]);
		}
	}

	/** Initializes the GUI elements and sets some of their parameters

		@param Stage, the GUI

		@return GraphicsContext, the graphicsContext of the stage
	*/
	private GraphicsContext createGUI (Stage stage) {
		//Create the borderPane
		BorderPane border = new BorderPane();

		//Create the label that shows the time
		timeLabel = new Label();
		timeLabel.setPrefSize(500, 20);

		//Create the horizontal box that will contain the time label
		HBox hbox = new HBox();
		//set its parameters and add it the timeLabel
		hbox.setPadding(new Insets(15, 12, 15, 12));
		hbox.setFillHeight(true);
		hbox.getChildren().add(this.timeLabel);

		//add it to top of the borderpane
		border.setTop(hbox);

		//Create the canvas
		Canvas canvas = new Canvas();
		//add the zooming capability
		canvas.setOnScroll((event) -> {
				if (event.getDeltaY() > 0) {
						coordinates.setScale(coordinates.getScale() * 0.9);
				} else {
						coordinates.setScale(coordinates.getScale() * 1.1);
				}
		});

		//and set it in the center of the borderpane
		border.setCenter(canvas);
		Scene scene = new Scene(border);

		//Set the title, scene of the stage and setMaximized
		stage.setTitle("The road to Titan");
		stage.setScene(scene);
		stage.setMaximized(true);

		// Bind canvas size to stack pane size
		canvas.widthProperty().bind(stage.widthProperty());
		canvas.heightProperty().bind(stage.heightProperty().add(TOP_AREA_HEIGHT));
		return canvas.getGraphicsContext2D();
	}

	/** Makes the last preparations for the GUI
			@param updateInterval, the interval in milliseconds between each frame-update
			@param spaceProbePresent, this boolean value represents whether the spaceProbe is taken into account in the simulation or not
	*/
	public void launchGUI (double updateInterval, boolean spaceProbePresent) {
		//Reset the number of iterations of the current simulation
		numIterations = 0;

		//Create the objects needed for the GUI, here the Timeline
		timeline = new Timeline();
		timeline.setCycleCount(Timeline.INDEFINITE);

		KeyFrame kf = new KeyFrame(
			Duration.millis(updateInterval),
			new SolarSystemUpdater(spaceProbePresent));

		timeline.getKeyFrames().add(kf);
	}

	/** Alternative version! Makes the last preparations for the GUI
			@param updateInterval, the interval in milliseconds between each frame-update
			@param spaceProbePresent, this boolean value represents whether the spaceProbe is taken into account in the simulation or not
			@param waitAtStartTime, the number of seconds to wait before the planets start moving
	*/
	public void launchGUI (double updateInterval, boolean spaceProbePresent, double waitAtStartTime) {
		//Reset the number of iterations of the current simulation
		numIterations = 0;

		//Create the objects needed for the GUI, here the Timeline
		timeline = new Timeline();
		timeline.setCycleCount(Timeline.INDEFINITE);

		KeyFrame kf = new KeyFrame(
			Duration.millis(updateInterval),
			new SolarSystemUpdater(spaceProbePresent, waitAtStartTime));

		timeline.getKeyFrames().add(kf);
	}

	public void launchGUI (double updateInterval, boolean spaceProbePresent, int endNumIterations) {
			//Reset the number of iterations of the current simulation
			numIterations = 0;

			//Create the objects needed for the GUI, here the Timeline
			timeline = new Timeline();
			timeline.setCycleCount(endNumIterations);

			KeyFrame kf = new KeyFrame(
				Duration.millis(updateInterval),
				new SolarSystemUpdater(spaceProbePresent));

			timeline.getKeyFrames().add(kf);
	}

	public void launchGUI (double updateInterval, boolean spaceProbePresent, double waitAtStartTime, int endNumIterations) {
		//Reset the number of iterations of the current simulation
		numIterations = 0;

		//Create the objects needed for the GUI, here the Timeline
		timeline = new Timeline();
		timeline.setCycleCount(endNumIterations);

		KeyFrame kf = new KeyFrame(
			Duration.millis(updateInterval),
			new SolarSystemUpdater(spaceProbePresent, waitAtStartTime));

		timeline.getKeyFrames().add(kf);
	}

	/** This class is used to create EventHandlers for keyframes to feed to the timeline ...
		Whenever it is called, it updates the solar system
	*/
	class SolarSystemUpdater implements EventHandler<ActionEvent> {
		private boolean isSpaceProbeIncluded;
		private double waitStartTime = 0;
		private boolean updatedFrameOnce = false;

		/** Default constructor for this class
			@param spaceProbeIncluded specifies whether the spaceProbe is part of the simulation or not (and, as such, if it needs to be simulated too or not)
		*/
		public SolarSystemUpdater (boolean spaceProbeIncluded) {
			isSpaceProbeIncluded = spaceProbeIncluded;
			startTime = System.nanoTime();
		}

		public SolarSystemUpdater (boolean spaceProbeIncluded, double waitAtStartTime) {
			this(spaceProbeIncluded);
			waitStartTime = waitAtStartTime;
		}

		public void handle (ActionEvent e) {
			updateFrame(gc, isSpaceProbeIncluded, waitStartTime);
		}
	}

	/** Draw the planets at their new positions, then update them
		@param gc, the graphicsContext on which we draw the planets' positions
		@param spaceProbeIncluded determines whether the space probe also has to be taken into account or not
	*/
	private void updateFrame(GraphicsContext gc, boolean spaceProbeIncluded, double waitTime) {
		this.canvasWidth = gc.getCanvas().getWidth();
		this.canvasHeight = gc.getCanvas().getHeight();
		gc.clearRect(0, 0, canvasWidth, canvasHeight);

		for (CelestialBody p : planets) {
			Vector2D otherPosition = coordinates.modelToOtherPosition(p.getPosition());

			//Draw circles
			gc.setFill(Color.BLACK);
			gc.fillOval(otherPosition.x - PLANET_RADIUS, otherPosition.y - PLANET_RADIUS, PLANET_RADIUS * 2, PLANET_RADIUS * 2);

			//Draw the labels
			Text text = new Text(p.getName());
			gc.fillText(p.getName(), otherPosition.x - (text.getLayoutBounds().getWidth() / 2), otherPosition.y - PLANET_RADIUS - (text.getLayoutBounds().getHeight() / 2));
		}

		//if the space probe is also simulated, then
		if (spaceProbeIncluded) {
			//Draw the space probe
			Vector2D spaceProbePosition = coordinates.modelToOtherPosition(spaceProbe.getPosition());

			//draw circle for import junit.framework.TestCase;
			gc.setFill(Color.BLACK);
			gc.fillOval(spaceProbePosition.x - PLANET_RADIUS, spaceProbePosition.y - PLANET_RADIUS, PLANET_RADIUS * 2, PLANET_RADIUS * 2);

			//draw a fitting label
			Text text = new Text(spaceProbe.getName());
			gc.fillText("Space Probe", spaceProbePosition.x - (text.getLayoutBounds().getWidth() / 2), spaceProbePosition.y - PLANET_RADIUS - (text.getLayoutBounds().getHeight() / 2));

			// Optionnally, print the distance between the spaceProbe and Earth when it is smaller than 5*10^8 to see the closest it gets to Earth
			if (spaceProbe.getPosition().distance(planets[3].getPosition()) < 1e8) {
				System.out.println("Distance to Earth: " + (spaceProbe.getPosition().distance(planets[3].getPosition()) - planets[3].getRadius()));
				System.out.println("Distance to the center of Earth: " + (spaceProbe.getPosition().distance(planets[3].getPosition())));
			}
		}

		numIterations ++;

		//If the starting wait time is over, update the positions of the planets, otherwise not
		if (System.nanoTime() >= startTime + waitTime*Math.pow(10, 9)) {
			update(DELTA_T, spaceProbeIncluded);
		}

		//set the text of the timelabel and make the timeLabel large enough to
		timeLabel.setText(getElapsedTimeAsString());
		if (showNumIterations)
			timeLabel.setText(timeLabel.getText() + "				Number of iterations: " + numIterations);
		timeLabel.setPrefWidth(new Text(timeLabel.getText()).getLayoutBounds().getWidth());
	}

	/** This method updates acceleration, then the velocity and the position of the planets
		It is assumed that createPlanets has been called before

		@param time, the time interval which we update the position after
		@param spaceProbeIncluded indicates whether the space probe's position is also updated or not
	*/
	private void update(double time, boolean spaceProbeIncluded) {
		for (int step = 0; step < 8; step ++) {
			for (int i = 0; i < planets.length; i ++) {
				planets[i].updatePosition(time, step);
			}
			if (spaceProbeIncluded && spaceProbe.didNotCrash()) spaceProbe.updatePosition(time, step);
		}

		//Increment the seconds
		elapsedSeconds += time;
	}

	/** Formats the elapsedSeconds variable for displaying the time elapsed since the beginning of the simulation

		@return a nicely formatted string expressing the elapsedSeconds variable in years, days, minutes and seconds
	*/
	private String getElapsedTimeAsString() {
		long years = elapsedSeconds / SEC_IN_YEAR;
		long days = (elapsedSeconds % SEC_IN_YEAR) / SEC_IN_DAY;
	    long hours = ( (elapsedSeconds % SEC_IN_YEAR) % SEC_IN_DAY) / SEC_IN_HOUR;
	    long minutes = ( ((elapsedSeconds % SEC_IN_YEAR) % SEC_IN_DAY) % SEC_IN_HOUR) / SEC_IN_MINUTE;
	    long seconds = ( ((elapsedSeconds % SEC_IN_YEAR) % SEC_IN_DAY) % SEC_IN_HOUR) % SEC_IN_MINUTE;
	    return String.format("Years:%08d, Days:%03d, Hours:%02d, Minutes:%02d, Seconds:%02d", years, days, hours, minutes, seconds);
	}

	/** Mainly for debugging purposes, could be deleted in the end product

		@param time, a given number of seconds

		@return a nicely formatted string expressing the time parameter in years, days, minutes and seconds
	*/
	private String getTimeAsString (long time) {
		long years = time / SEC_IN_YEAR;
    	long days = (time % SEC_IN_YEAR) / SEC_IN_DAY;
    	long hours = ((time % SEC_IN_YEAR) % SEC_IN_DAY) / SEC_IN_HOUR;
    	long minutes =  (((time % SEC_IN_YEAR) % SEC_IN_DAY) % SEC_IN_HOUR) / SEC_IN_MINUTE;
    	long seconds =  (((time % SEC_IN_YEAR) % SEC_IN_DAY) % SEC_IN_HOUR) % SEC_IN_MINUTE;
    	return String.format("Years:%08d, Days:%03d, Hours:%02d, Minutes:%02d, Seconds:%02d", years, days, hours, minutes, seconds);
	}

	/** Auxiliary method

			@param double n, a double number
			@return the sign of the double parameter
	*/
	private int signum (double n) {
		return (int)(Math.abs(n)/n);
	}
}
