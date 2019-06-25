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
public class GUI extends Application {
	// !!! Variables only used by the GUI of the solar simulation !!!
	//Number of seconds between each update
	private static double DELTA_T = 60 * 30;
	//debug boolean (un)locking println statements
	private final boolean DEBUG = true;

	private static final double SCALE = 5e9;		//Scaling factor in meters/pixels
	//radius of the planets
	public static final double PLANET_RADIUS = 2;
	//height of the part at the top
	private static final int TOP_AREA_HEIGHT = 100;

	//GUI parts only used for the solar system simulation GUI
	private Label timeLabel;
	private long startTime = -1;				//variable used internally
	private int numIterations;					//number of iterations in the current simulation
	private boolean showNumIterations = false;

	//Everything about the planets
		//Array containing the planets/moons
	public static CelestialBody[] planets;
		//Arrays containing all the information about the planets/moons
		// Indexes: ----------------------------    	0							1							2							3							4							5								6								7								8								9							10						11
	private static final String[] planetNames = {		"Sun", 				"Mercury", 		"Venus",			"Earth",			"Mars", 			"Jupiter", 			"Saturn", 			"Uranus", 			"Neptune", 			"Titan", 			"Moon", 			"Ganymede" };
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
	//the mass of voyager
	private static final double spaceProbeMass = 800;											//in kg
	private static final double averageVelocitySpaceProbe = 47e3;								//in meters/secs
	private static final double averageVelocitySpaceProbeWithThrusters = 45e3;					//in meters/secs
	private static final double averageVelocitySpaceProbeReturnTravel = 10e3;					//in meters/secs
	private static final double averageVelocitySpaceProbeWithThrustersReturnTravel = 17e3;		//in meters/secs
	private static final String spaceProbeName = "SpaceProbe";
	private static final double spaceProbeStartingHeight = 1000;					//in meters

	//the gravitational constant
	public static final double G = 6.67300E-11;		//in meters^3 * kg^(-1) * secs^(-1)

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
  	private static final int SEC_IN_YEAR = (int)(SEC_IN_DAY * 365.25);
	//and the long keeping track of the number of seconds elapsed from the start
  	private static long elapsedSeconds = 0;

	//GUI parts shared by the two GUI's
	private Timeline timeline;
	private CoordinatesTransformer coordinates = new CoordinatesTransformer();
	private GraphicsContext gc;
	private Vector2D movingStartPosition;

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

		//Initialize the CoordinateTransformer object with the correct parameters
		//By default, we set the parameters for the solar system simulation GUI (meaning that the coordinatesTransformer is set to work for the solar system GUI)
		coordinates.setScale(SCALE);
		coordinates.setModifiedPos(new Vector2D(700, 350));		//makes the center of the solar system be somewhere around the center of the screen

		//Initialize variables
		Scanner s = new Scanner(System.in);
		String input = "";
		boolean choiceMade = false;

		//As long as the User does not make a valid choice, we repeat asking him what he wants to do
		while (!choiceMade) {
			System.out.println("Do you want to see the solar system simulation (enter 1), the travel from Earth to Titan (enter 2), the landing on Titan (enter 3) or travel back from Titan to Earth (enter 4) ?");
			//Set input to the user's input
			input = s.next();

			switch (input) {
				case "1":				//Simulation of the solar system
					//Initialize the planets
					createSolarSystem();

					//Let the user select a custom timestep (after which interval of simulated time do we update the position of the planets)
					System.out.println("Which timestep do you want to use (in secs)? Default timestep is " + DELTA_T);
					DELTA_T = s.nextDouble();			//Default is 1800 (half an hour)

					//Set a boolean reflecting that there is no space probe in this mode
					boolean spaceProbeIncluded = false;

					//Launch the GUI
					gc = createGUI(stage);
					launchGUI(1, spaceProbeIncluded);
					timeline.play();
					stage.show();

					//Set choideMade to true, meaning that a simulation has been done and we do not need to ask the user what he wants to choose another time
					choiceMade = true;
					//And break to exit the switch statement
					break;
				case "2":					//Travel from Earth to Titan
					//Initialize the solar system (the planets array)
					createSolarSystem();

					//Initialize some variables for the launchAngleAdjustmentSearch method
					double startLaunchAngle = 0;
					double startAngleChange = 5;
					int originPlanetIndex = 3;				//going from Earth (=planets[3])
					int destinationPlanetIndex = 9;			//to Titan (= planets[9])
					double idealAngle = 0;
					String info = "";

					//Ask the User whether he would like to use a SpaceProbe or a SpaceProbeWithThrusters and save a corresponding object in the "spaceProbe" variable
					while ((! info.equals("1")) && (! info.equals("2"))) {
						System.out.println("Would you like to launch a normal space probe (without thrusters, enter 1) or a space probe with thruster (enter 2) ?");
						info = s.next();

						switch (info) {
							//SpaceProbe
							case "1":
								//Compute the ideal angle
								startLaunchAngle = 271.03781144252537;
								idealAngle = launchAngleAdjustmentSearchSpaceProbe(originPlanetIndex, destinationPlanetIndex, startLaunchAngle, startAngleChange, averageVelocitySpaceProbe);

								//Reset the solar system and create a new space probe with the ideal angle
								createSolarSystem();
								spaceProbe = SpaceProbe.createSpaceProbeWithStartingAngle(spaceProbeName, spaceProbeMass, planets[originPlanetIndex], averageVelocitySpaceProbe, idealAngle, spaceProbeStartingHeight);

								break;
							//SpaceProbeWithThrusters
							case "2":
								//Compute the ideal starting angle
								startLaunchAngle = 267.3762536061466;//38.86503178087327 is an alternative
								idealAngle = launchAngleAdjustmentSearchSpaceProbeWithThrusters(originPlanetIndex, destinationPlanetIndex, startLaunchAngle, startAngleChange, averageVelocitySpaceProbeWithThrusters);

								//Reset the solar system and create a new space probe with thrusters with the ideal angle
								createSolarSystem();
								spaceProbe = SpaceProbeWithThrusters.createSpaceProbeWithStartingAngle(spaceProbeName, spaceProbeMass, planets[originPlanetIndex], averageVelocitySpaceProbeWithThrusters, idealAngle, spaceProbeStartingHeight);

								break;
						}
					}

					//Launch the simulation
					gc = createGUI(stage);
					launchGUI(1, true);			//compute next position after 1 milliseconds, and also consider the spaceProbe
					timeline.play();
					stage.show();

					choiceMade = true;
					break;
				case "3": 					//Landing on titan
					//Initialize the landModChoice variable to an empty string
					String landModChoice = "";
					//And initialize the starting position, starting velocity and starting angle to default values
					Vector2D landModStartPos = new Vector2D(0, 500000);
					Vector2D landModStartVeloc = new Vector2D(0, 0);
					double landModStartAngle = 0;

					//Ask the user which kind of controller he would like to have
					while ((!landModChoice.equals("1")) && (!landModChoice.equals("2")) && (!landModChoice.equals("3"))) {
						System.out.println("Which landing module controller do you want to use (enter 1 for the open-loop controller, 2 for the feedback controller without parachtue, 3 for the feedback controller with parachute)?");
						landModChoice = s.next();

						//Choose which landing module to create, and create a corresponding
						switch (landModChoice) {
							case "1": 					//open-loop controller
								//For the open-loop controller, we could make the timestep bigger as it takes a lot of time to land
								//LANDINGDELTA_T = 100;		//in secs

								//Initialize selection
								String selection = "";

								while (!selection.equals("yes") && !selection.equals("no")) {
									//Ask the user if he wants to specify a certain starting position for the landing module
									System.out.println("Do you want to specify a specific starting position ? Enter 'yes' or 'no'");
									selection = s.next().toLowerCase();

									//If the user wants to specify a starting position, then we retrieve the x- and y-position and save it
									if (selection.equals("yes")) {
										System.out.println("Please enter the x-position and y-position separated by a space");
										double startX = s.nextDouble();
										double startY = s.nextDouble();
										landModStartPos = new Vector2D(startX, startY);
									}
								}

								//Reset selection
								selection = "";
								while (!selection.equals("yes") && !selection.equals("no")) {
									//Ask the user if he wants to specify a certain starting velocity for the landing module
									System.out.println("Do you want to specify a specific starting velocity ? Enter 'yes' or 'no'");
									selection = s.next().toLowerCase();

									//If the user wants to specify a starting velocity, retrieve the input velocity and save it
									if (selection.equals("yes")) {
										System.out.println("Please enter the x-velocity and y-velocity separated by a space");
										double startXVeloc = s.nextDouble();
										double startYVeloc = s.nextDouble();
										landModStartVeloc = new Vector2D(startXVeloc, startYVeloc);
									}
								}

								selection = "";
								while (!selection.equals("yes") && !selection.equals("no")) {
									//Ask the user if he wants to specify a certain starting angle
									System.out.println("Do you want to specify a specific starting angle ? Enter 'yes' or 'no'");
									selection = s.next().toLowerCase();
									//If the user wants to specify a starting angle, retrieve the input angle and save it
									if (selection.equals("yes")) {
										System.out.println("Please enter the angle");
										landModStartAngle = s.nextDouble();
									}
								}

								landingModule = new LandingModuleOpenLoopController(landingModuleWeight, landModStartPos, landModStartVeloc, landModStartAngle);

								break;
							case "2":					//Feedback controller
								//Initialize selection
								selection = "";

								while (!selection.equals("yes") && !selection.equals("no")) {
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
								}

								//Reset selection
								selection = "";
								while (!selection.equals("yes") && !selection.equals("no")) {
									//Ask the user if he wants to specify a certain starting velocity for the landing module
									System.out.println("Do you want to specify a specific starting velocity ? Enter 'yes' or 'no'");
									selection = s.next().toLowerCase();
									//If the user wants to specify a starting velocity, retrieve the input velocity and save it
									if (selection.equals("yes")) {
										System.out.println("Please enter the x-velocity and y-velocity separated by a space");
										double startXVeloc = s.nextDouble();
										double startYVeloc = s.nextDouble();
										landModStartVeloc = new Vector2D(startXVeloc, startYVeloc);
									}
								}

								//Reset selection
								selection = "";
								while (!selection.equals("yes") && !selection.equals("no")) {
									//Ask the user if he wants to specify a certain starting angle
									System.out.println("Do you want to specify a specific starting angle ? Enter 'yes' or 'no'");
									selection = s.next().toLowerCase();
									//If the user wants to specify a starting angle, retrieve the input angle and save it
									if (selection.equals("yes")) {
										System.out.println("Please enter the angle");
										landModStartAngle = s.nextDouble();
									}
								}

								//Reset selection and initialize addWind
								selection = "";
								boolean addWind = false;
								//Initialize the default windStrength and thrusterForce
								double windStrength = LandingModuleFeedbackController.getMaxWindStrength();
								double thrusterForce = LandingModuleFeedbackController.getThrusterForce();

								while (!selection.equals("yes") && !selection.equals("no")) {
									//Ask the user if he wants to add wind to the simulation or not
									System.out.println("Do you want to add wind to the simulation ? Enter 'yes' if you want to add wind, otherwise 'no'");
									selection = s.next().toLowerCase();

									//If the user wants to have wind, ask if the User wants to set more parameters
									if (selection.equals("yes")) {
										addWind = true;

										//Initialize windChoice
										String windChoice = "";

										//Ask the User whether he wants to set the maximum acceleration of the wind
										while (!windChoice.equals("yes") && !windChoice.equals("no")) {
											System.out.println("Do you want to set the maximum acceleration of the wind ?");
											windChoice = s.next().toLowerCase();
											if (windChoice.equals("yes")) {
												System.out.println("What should the maximum acceleration of the wind be ?");
												windStrength = s.nextDouble();
											}
										}

										//Reset windChoice
										windChoice = "";

										//Ask the User whether to set a custom main thruster force or not
										while (!windChoice.equals("yes") && !windChoice.equals("no")) {
											System.out.println("Do you want to set the force of the main thruster ?");
											windChoice = s.next().toLowerCase();
											if (windChoice.equals("yes")) {
												System.out.println("What should the force of the main thruster be ?");
												thrusterForce = s.nextDouble();
											}
										}
									}
								}

								//Create a landing module with the given parameters, and set the maxWindStrength and thrusterForce
								landingModule = new LandingModuleFeedbackController(landingModuleWeight, landModStartPos, landModStartVeloc, landModStartAngle, addWind);
								((LandingModuleFeedbackController)landingModule).setMaxWindStrength(windStrength);
								((LandingModuleFeedbackController)landingModule).setThrusterForce(thrusterForce);

								break;
							case "3":					//Feedback controller with parachute
								//Initialize the startPosition and the startVelocity for the feedback controller with parachute
								landModStartPos = new Vector2D(0, 155000);
								landModStartVeloc = new Vector2D(0, -400);

								//Initialize selection
								selection = "";

								while (!selection.equals("yes") && !selection.equals("no")) {
									//Ask the user if he wants to specify a certain starting position for the landing module
									System.out.println("Do you want to specify a specific starting position ? Enter 'yes' or 'no'");
									selection = s.next().toLowerCase();
									//If the user wants to specify a starting position, then we retrieve the x- and y-position and save it
									if (selection.equals("yes")) {
										System.out.println("Please enter the x-position and y-position separated by a space");
										double startX = s.nextDouble();
										double startY = s.nextDouble();
										landModStartPos = new Vector2D(startX, startY);
									}
								}

								//Reset selection
								selection = "";

								while (!selection.equals("yes") && !selection.equals("no")) {
									//Ask the user if he wants to specify a certain starting velocity for the landing module
									System.out.println("Do you want to specify a specific starting velocity ? Enter 'yes' or 'no'");
									selection = s.next().toLowerCase();
									//If the user wants to specify a starting velocity, retrieve the input velocity and save it
									if (selection.equals("yes")) {
										System.out.println("Please enter the x-velocity and y-velocity separated by a space");
										double startXVeloc = s.nextDouble();
										double startYVeloc = s.nextDouble();
										landModStartVeloc = new Vector2D(startXVeloc, startYVeloc);
									}
								}

								//Reset selection
								selection = "";

								while (!selection.equals("yes") && !selection.equals("no")) {
									//Ask the user if he wants to specify a certain starting angle
									System.out.println("Do you want to specify a specific starting angle ? Enter 'yes' or 'no'");
									selection = s.next().toLowerCase();
									//If the user wants to specify a starting angle, retrieve the input angle and save it
									if (selection.equals("yes")) {
										System.out.println("Please enter the angle");
										landModStartAngle = s.nextDouble();
									}
								}

								//Reset selection and initialize addWind
								addWind = false;
								selection = "";
								windStrength = LandingModuleFeedbackControllerWithParachute.getMaxWindStrength();
								thrusterForce = LandingModuleFeedbackControllerWithParachute.getThrusterForce();

								while (!selection.equals("yes") && !selection.equals("no")) {
									//Ask the user if he wants to add wind to the simulation or not
									System.out.println("Do you want to add wind to the simulation ? Enter 'yes' if you want to add wind, otherwise 'no'");
									selection = s.next().toLowerCase();
									//If the user wants to have wind, give it to him !!!
									if (selection.equals("yes")) {
										addWind = true;

										//Initialize windChoice
										String windChoice = "";

										//Ask the User whether to set a custom maximum wind acceleration
										while (!windChoice.equals("yes") && !windChoice.equals("no")) {
											System.out.println("Do you want to set the maximum acceleration of the wind ?");
											windChoice = s.next().toLowerCase();
											if (windChoice.equals("yes")) {
												System.out.println("What should the maximum acceleration of the wind be ?");
												windStrength = s.nextDouble();
											}
										}

										//Reset windChoice
										windChoice = "";

										//Ask the User whether to set a custom main thruster force
										while (!windChoice.equals("yes") && !windChoice.equals("no")) {
											System.out.println("Do you want to set the force of the main thruster ?");
											windChoice = s.next().toLowerCase();
											if (windChoice.equals("yes")) {
												System.out.println("What should the force of the main thruster be ?");
												thrusterForce = s.nextDouble();
											}
										}
									}
								}

								//Create a landing module with feedback controller and using a parachute at the set parameters
								landingModule = new LandingModuleFeedbackControllerWithParachute(landingModuleWeight, landModStartPos, landModStartVeloc, landModStartAngle, addWind);
								((LandingModuleFeedbackControllerWithParachute)landingModule).setMaxWindStrength(windStrength);
								((LandingModuleFeedbackControllerWithParachute)landingModule).setThrusterForce(thrusterForce);

								break;
						}
					}

					//Ask the User whether to show the GUI for Landing or not
					boolean showGUI = false;
					boolean choseWhetherToShowLandingGUI = false;

					//While not chosen
					while (! choseWhetherToShowLandingGUI) {
						//Allow the User to enter an input,
						System.out.println("Do you want to see the Landing GUI (enter 1) or only the result of the Landing (enter 2) ?");
						String showLandingGUI = s.next();

						//Then, if it is a correct input, do the corresponding thing
						switch (showLandingGUI) {
							case "1":
								showGUI = true;
								choseWhetherToShowLandingGUI = true;
								break;
							case "2":
								showGUI = false;
								choseWhetherToShowLandingGUI = true;
								break;
						}
					}

					//If we show the GUI
					if (showGUI) {
						//Set the coordinatesTransformer with the correct parameters for the landing GUI
				        coordinates.setScale(LANDINGSCALE);
						coordinates.setModifiedPos(new Vector2D(LANDINGYAXISWIDTH, LANDINGXAXISHEIGHT));

				        //And launch the landing GUI
				        gc = createLandingGUI(stage, landingModule);
				        launchLandingGUI(1);
				        stage.show();
				        timeline.play();
					}
					//Otherwise, we update the landing module until it lands
					else {
						//Update the landingModule until the landing, and the method then prints the result
						landingModule.updateModule(LANDINGDELTA_T);
					}

					//Set choiceMade to true to signify that a simulation has been done, and we do not need to ask the User what he wants to choose anymore
					choiceMade = true;
					break;
				case "4":					//Return of the spaceProbe from Titan to earth
					//Initialize some variables for the launchAngleAdjustmentSearch method
					startLaunchAngle = 0;
					startAngleChange = 5;
					originPlanetIndex = 9;			//going from Titan (= planets[9])
					destinationPlanetIndex = 3;		//to Earth (= planets[3])
					info = "";

					//Ask the User whether he would like to use a SpaceProbe or a SpaceProbeWithThrusters and save a corresponding object in the "spaceProbe" variable
					while ((! info.equals("1")) && (! info.equals("2"))) {
						System.out.println("Would you like to launch a normal space probe (without thrusters, enter 1) or a space probe with thruster (enter 2) ?");
						info = s.next();

						switch (info) {
							//SpaceProbe
							case "1":
								//Compute the ideal angle
								startLaunchAngle = 111.71980492616017;
								idealAngle = launchAngleAdjustmentSearchSpaceProbe(originPlanetIndex, destinationPlanetIndex, startLaunchAngle, startAngleChange, averageVelocitySpaceProbeReturnTravel);

								//Reset the solar system and create a new space probe with the ideal angle
								createSolarSystem();
								spaceProbe = SpaceProbe.createSpaceProbeWithStartingAngle(spaceProbeName, spaceProbeMass, planets[originPlanetIndex], averageVelocitySpaceProbeReturnTravel, idealAngle, spaceProbeStartingHeight);

								break;
							//SpaceProbeWithThrusters
							case "2":
								//Compute the ideal starting angle
								startLaunchAngle = 111.71979572901293;
								idealAngle = launchAngleAdjustmentSearchSpaceProbeWithThrusters(originPlanetIndex, destinationPlanetIndex, startLaunchAngle, startAngleChange, averageVelocitySpaceProbeWithThrustersReturnTravel);

								//Reset the solar system and create a new space probe with thrusters with the ideal angle
								createSolarSystem();
								spaceProbe = SpaceProbeWithThrusters.createSpaceProbeWithStartingAngle(spaceProbeName, spaceProbeMass, planets[originPlanetIndex], averageVelocitySpaceProbeWithThrustersReturnTravel, idealAngle, spaceProbeStartingHeight);

								break;
						}
					}

					//Make sure that we also simulate the space probe
					spaceProbeIncluded = true;

					//Launch the simulation
					gc = createGUI(stage);
					launchGUI(1, spaceProbeIncluded);
					timeline.play();
					stage.show();

					//This is to exit the switch statement and the outside while loop
					choiceMade = true;
					break;

				// Case 5 TO DELETE
				//

				case "5":					//Test of space probe angle
					//Compute the ideal massFlowRate for the launch to reach the speed we would like
					double spaceProbeVeloc = averageVelocitySpaceProbe;
					int originPlanet = 3;

					double[] result = computeMassFlowRateToReachVelocityAtHeight(spaceProbeVeloc, spaceProbeStartingHeight, originPlanet);

					System.out.printf("\n\nIdealMassFlowRate: %f, \nReached velocity: %f VS wanted velocity: %f, \nReached height: %f VS wanted height: %f\nFuel mass given to the space probe: %f\nFuel mass burnt: %f", result[0], result[1], spaceProbeVeloc, result[2], spaceProbeStartingHeight, result[3], result[4]);

					/*
					originPlanetIndex = 3;
					info = "";

					//Ask the User what angle he would like to shoot the spaceProbe in
					System.out.println("Enter the angle you would like to launch the spaceProbe in: ");
					double launch_angle = s.nextDouble();

					//Reset/create the solar system
					createSolarSystem();

					//Ask the User whether he would like to use a SpaceProbe or a SpaceProbeWithThrusters and save a corresponding object in the "spaceProbe" variable
					while ((! info.equals("1")) && (! info.equals("2"))) {
						System.out.println("Would you like to launch a normal space probe (without thrusters, enter 1) or a space probe with thruster (enter 2) ?");
						info = s.next();

						switch (info) {
							//SpaceProbe
							case "1":
								//Create a new space probe with the ideal angle
								spaceProbe = SpaceProbe.createSpaceProbeWithStartingAngle(spaceProbeName, spaceProbeMass, planets[originPlanetIndex], averageVelocitySpaceProbe, launch_angle, spaceProbeStartingHeight);

								break;
							//SpaceProbeWithThrusters
							case "2":
								//Create a new space probe with thrusters with the given angle
								spaceProbe = SpaceProbeWithThrusters.createSpaceProbeWithStartingAngle(spaceProbeName, spaceProbeMass, planets[originPlanetIndex], averageVelocitySpaceProbeWithThrusters, launch_angle, spaceProbeStartingHeight);

								break;
						}
					}

					//Set the variable showNumIterations to whether we want to see the number of iterations or not
					showNumIterations = true;

					//Include the space probe
					spaceProbeIncluded = true;

					//Show the simulation of the solar system with the space probe
					gc = createGUI(stage);
					launchGUI(1, true);
					timeline.play();
					stage.show();
					*/

					//This part is needed to exit the switch and outside while-loop
					choiceMade = true;
					break;
			}
		}
	}

	/** Try to compute the massFlowRate needed for the spaceProbeWithThrusters to reach the given velocity at the given height
	  *
	  * @return a double array, with at index 0 the massFlowRate needed to get to a velocity saved on index 1, at a height of index 2, and the quantity of fuel given to the space probe saved at index 3, with the quantity of burnt fuel at index 4
	  */
	private double[] computeMassFlowRateToReachVelocityAtHeight (final double velocity, final double height, final int originPlanetIndex) {
		//Set the timestep to a smaller timestep (here 1 sec)
		DELTA_T = 1;
		//Set the launchAngle
		double angle = 0;
		//Set the tolerance factor for the height and for the velocity
		final double TOL_HEIGHT = 0.01;		// 1/100 of the height as tolerance
		final double TOL_VELOC = 0.01;
		final double TOL_FUEL_MASS = 10;	//10 kgs
		//Set the starting fuel mass and fuelMass change
		double fuelMass = SpaceProbeWithThrusters.getDefaultFuelMass();
		double fuelMassChange = fuelMass*1/10;
		int fuelChangeMove = 0;
		int previousFuelChangeMove = 0;

		//Start off with the massFlowRate from SpaceProbeWithThrusters
		double idealMassFlowRate = SpaceProbeWithThrusters.getMassFlowRate();
		double massFlowRateChange = 1.0/100 * idealMassFlowRate;
		int currentMove = 0;
		int previousMove = 0;

		//Make a launch
		boolean idealMassFlowRateReached = false;
		int numIterations = 0;
		while (! idealMassFlowRateReached) {
			System.out.println("Iteration #" + numIterations + ", massFlowRate: " + idealMassFlowRate + ", fuelMass: " + fuelMass);

			//Reset the solar system
			createSolarSystem();

			//Create a spaceProbeWithThrusters from originPlanet
			spaceProbe = SpaceProbeWithThrusters.createSpaceProbeWithStartingAngle(spaceProbeName, spaceProbeMass, planets[originPlanetIndex], 0, angle, height);
			//We launch it in a specific angle, with only the velocity of the originPlanet and specify the fuelMass
			spaceProbe = new SpaceProbeWithThrusters(spaceProbeName, spaceProbeMass, fuelMass, spaceProbe.getPosition(), spaceProbe.getVelocity().add(planets[originPlanetIndex].getVelocity()), ((SpaceProbeWithThrusters)spaceProbe).getAngle());

			//Set the massFlowRate
			SpaceProbeWithThrusters.setMassFLowRate(idealMassFlowRate);

			//Run the simulation until a height of "height" is reached or the velocity goes back down towards the originPlanet
			while (planets[originPlanetIndex].getPosition().distance(spaceProbe.getPosition()) < height && (spaceProbe.getVelocity().angle(planets[originPlanetIndex].getPosition()) > 90 && spaceProbe.getVelocity().angle(planets[originPlanetIndex].getPosition()) < 270)) {
				((SpaceProbeWithThrusters)spaceProbe).activateThrusters();
				update(DELTA_T, true);
			}

			//Evaluate the simulation
			//If we are not in the searched range of height, then
			double spaceProbeHeight = spaceProbe.getPosition().subtract(planets[originPlanetIndex].getPosition()).length();
			double spaceProbeVelocity = spaceProbe.getVelocity().length();
			if (spaceProbeHeight < height*(1-TOL_HEIGHT) || spaceProbeHeight > height*(1+TOL_HEIGHT)) {
				if (numIterations < 10) System.out.println("1");
				//If the spaceProbe is below the searched height, we need to use the thrusters more (thus more fuel)
				if (spaceProbeHeight < height*(1-TOL_HEIGHT)) {
					if (numIterations < 10) System.out.println("	1.1");
					fuelChangeMove = +1;

					//If the previous fuel change was the opposite as this one, we reduce the fuelMassChange to finetune the needed fuel
					if (fuelChangeMove == -previousFuelChangeMove) {
						fuelMassChange /= 2;
					}

					//Apply the move
					fuelMass += fuelChangeMove * fuelMassChange;

					//We set the previous move to the move we just did
					previousFuelChangeMove = fuelChangeMove;
				}
				//Else if we are above the searched height and the leftover fuelMass is bigger than the tolerance for fuel, we reduce the quantity of fuel
				else if (((SpaceProbeWithThrusters)spaceProbe).getFuelMass() > TOL_FUEL_MASS) {
					if (numIterations < 10) System.out.println("	.2");
					fuelChangeMove = -1;

					//If the previous fuel change was the opposite as this one, we reduce the fuelMassChange to finetune the needed fuel
					if (fuelChangeMove == -previousFuelChangeMove) {
						fuelMassChange /= 2;
					}

					//Apply the move
					fuelMass += fuelChangeMove * fuelMassChange;

					//We set the previous move to the move we just did
					previousFuelChangeMove = fuelChangeMove;
				}
				else {
					if (numIterations < 10) System.out.println("	.3");
					//If we are not in the tolerated height range, we decrease the massFlowRate, because that means we passed over the given height and didn't have too much fuel
					currentMove = -1;

					//If we do the opposite action to the previous one,
					if (currentMove == -previousMove) {
						massFlowRateChange /= 2;
					}

					//Apply the current move
					idealMassFlowRate += currentMove * massFlowRateChange;

					//Then set the previous move to the value for this move, in prevision for the next iteration
					previousMove = currentMove;
				}
			}
			else if (spaceProbe.getVelocity().length() < velocity*(1-TOL_VELOC)) {
				if (numIterations < 10) System.out.println("2");
				//If we have a lower velocity as required, augment the massFlowRate
				currentMove = +1;

				//If we do the opposite action to the previous one,
				if (currentMove == -previousMove && currentMove != 0) {
					massFlowRateChange /= 2;
				}

				//Apply the move
				idealMassFlowRate += currentMove * massFlowRateChange;

				//Then set the previous move to the value for this move, in prevision for the next iteration
				previousMove = currentMove;
			}
			else if (spaceProbe.getVelocity().length() > velocity*(1+TOL_VELOC)) {
				if (numIterations < 10) System.out.println("3");
				//If we have a bigger velocity as required, decrease the massFlowRate
				currentMove = -1;

				//If we do the opposite action to the previous one,
				if (currentMove == -previousMove && currentMove != 0) {
					massFlowRateChange /= 2;
				}

				//Apply the current move
				idealMassFlowRate += currentMove * massFlowRateChange;

				//Then set the previous move to the value for this move, in prevision for the next iteration
				previousMove = currentMove;
			}
			else {
				System.out.println("Perfect conditions");
				idealMassFlowRateReached = true;
			}

			//If the massFlowRateChange is smaller than the smallest double value bigger than idealMassFlowRate, then we refined the massFlowRate as much as we could and we can be happy with the result
			if (massFlowRateChange < Math.ulp(idealMassFlowRate)) {
				System.out.println("Finetuning finished: " + massFlowRateChange);
				idealMassFlowRateReached = true;
			}

			numIterations ++;
		}

		//Return the result
		double[] result = new double[5];
		result[0] = idealMassFlowRate;																	//index 0, the ideal mass flow rate for the launch
		result[1] = spaceProbe.getVelocity().length();													//index 1, the reached velocity
		result[2] = spaceProbe.getPosition().distance(planets[originPlanetIndex].getPosition());		//index 2, the reached height
		result[3] = fuelMass;																			//index 3, the fuelMass given to the spaceProbe
		result[4] = ((SpaceProbeWithThrusters)spaceProbe).getBurntFuelMass();							//index 4, the burntFuelMass

		return result;
	}

	/** Overloads method launchAngleAdjustmentSearch with one less parameter than the original: the boolean DEBUG
	  */
	private double launchAngleAdjustmentSearchSpaceProbe (final int originPlanetIndex, final int destinationPlanetIndex, final double startLaunchAngle, final double startAngleChange, final double spaceProbeVelocity) {
		boolean DEBUG_MODE_ON = true;
		return launchAngleAdjustmentSearchSpaceProbe(originPlanetIndex, destinationPlanetIndex, startLaunchAngle, startAngleChange, spaceProbeVelocity, DEBUG_MODE_ON);
	}

	/** Computes the optimal launch angle for the spaceProbe to reach the destinationPlanet starting from the originPlanet
	  * The idea is basically to start with the given launchAngle from the origin planet,
	  * then run the simulation until either the spaceProbe crashes or gets further away from the originPlanet than the destinationPlanet is.
	  * Then, evaluate the result and if the spaceProbe is too much to the left, make the angle smaller, else if it is too much to the right, make the angle bigger
	  * once either the angle is as precise as it could be or the spaceProbe crashes on Titan, we found the supposedly best possible angle (without using any thrust during the travel)
	  *
	  * @param originPlanet, the planet from which the spaceProbe starts off
	  * @param destinationPlanet, the planet at which the spaceProbe should arrive
	  * @param startLaunchAngle, a starting guess for the correct launch angle
	  * @param startAngleChange, the starting amount of degrees by which the launch angle is modified at each iteration
	  * @param spaceProbeVelocity, the total velocity of the spaceProbe at the starting position
	  * @param DEBUG, a boolean unlocking debug print statements
	  */
	private double launchAngleAdjustmentSearchSpaceProbe (final int originPlanetIndex, final int destinationPlanetIndex, final double startLaunchAngle, final double startAngleChange, final double spaceProbeVelocity, final boolean DEBUG) {
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
			//Initialize the space probe and the solar system
			createSolarSystem();

			//Create variables representing the originPlanet, the destinationPlanet and the Sun
			CelestialBody Sun = planets[0];
			CelestialBody originPlanet = planets[originPlanetIndex];
			CelestialBody destinationPlanet = planets[destinationPlanetIndex];

			//Create a new spaceProbe launched with a new angle from planet Earth
			spaceProbe = SpaceProbe.createSpaceProbeWithStartingAngle(spaceProbeName, spaceProbeMass, originPlanet, spaceProbeVelocity, launch_angle, spaceProbeStartingHeight);

			int num = 0;
			//As long as the spaceProbe has not crashed into a planet, or gone further away from the originPlanet than the destinationPlanet's distance from the originPlanet or gone further away from the Sun than Pluto's distance to the Sun
			while (spaceProbe.didNotCrash() && (spaceProbe.getPosition().distance(originPlanet.getPosition()) < destinationPlanet.getPosition().distance(originPlanet.getPosition())) && spaceProbe.getPosition().distance(Sun.getPosition()) < DISTANCE_SUN_PLUTO)  {
				//Update the position and the number of iterations
				update(DELTA_T, true);
				num ++;
			}

			//If the space probe crashed on Titan, we are done
			if (planets[destinationPlanetIndex].equals(spaceProbe.getCrashedPlanet())) {
				crashedDestinationPlanet = true;
				if (DEBUG) System.out.println("\n\n\nIteration #" + numberIterations + "\nA launch angle of " + launch_angle + " degrees got the spaceProbe to Titan.\nNumber of iterations to reach it: " + num);
			}
			else {
				//If we crash into a planet, we print it to the console, then reset the variable in the spaceProbe
				if (spaceProbe.getCrashedPlanet() != null)
					spaceProbe.resetCrashedPlanet();

				//Then, we compute the angles with respect to a certain CelestialBody
				Vector2D referencePoint = new Vector2D(planetPositions[originPlanetIndex]);//planetPositions[0]);

				spaceProbeAngle = spaceProbe.getPosition().angle(referencePoint);
				destinationPlanetAngle = planets[destinationPlanetIndex].getPosition().angle(referencePoint);

				//Save an intermediate value, the sign of the difference of the angles
				int tmp = signum(spaceProbeAngle-destinationPlanetAngle);

				if (Math.abs(destinationPlanetAngle-spaceProbeAngle) > 180) {
					//If the previous move was the opposite move,
					if (previousMove == -tmp) {
						//We reduce the angleChange by half
						angleChange = angleChange/2;
					}

					//In any case, modify the launch_angle
					launch_angle += tmp * angleChange;
					//and save this move as the previous move for the next iteration
					previousMove = tmp;
				}
				else if (Math.abs(destinationPlanetAngle-spaceProbeAngle) > 0) {
					//If the previous move was the opposite move,
					if (previousMove == tmp) {
						//We reduce the angleChange by half
						angleChange = angleChange/2;
					}

					//In any case, modify the launch_angle
					launch_angle += -tmp * angleChange;
					//and save this move as the previous move for the next iteration
					previousMove = -tmp;
				}
				else {
					//The two angles are the same, but I will still modify the angle in order for angleChange to become smaller and smaller and to refine the launch_angle even more
					//Modify the angle in some direction
					launch_angle += tmp * angleChange;

					//and set the previous move for the next iteration as the move just made
					previousMove = tmp;
				}
			}

			//Once we cannot get a more precise value (that is, the change of the angle angleChange is smaller than the distance of launch_angle to the next double value after launch_angle)
			if (angleChange < Math.ulp(launch_angle)) {
				crashedDestinationPlanet = true;
			}

			if (DEBUG) System.out.println("New space probe launch angle: " + launch_angle);

			numberIterations ++;
			elapsedSeconds = 0;
		}

		//Save the ideal angle
		double result = launch_angle;

		//Now, we print out the supposedly ideal angle if debugging mode is on
		if (DEBUG) System.out.println("'Optimal' launch angle: " + result);

		return result;
	}

	/** Overloads method launchAngleAdjustmentSearch with one less parameter than the original: the boolean DEBUG
	  */
	private double launchAngleAdjustmentSearchSpaceProbeWithThrusters (final int originPlanetIndex, final int destinationPlanetIndex, final double startLaunchAngle, final double startAngleChange, final double spaceProbeVelocity) {
		boolean DEBUG_MODE_ON = true;
		return launchAngleAdjustmentSearchSpaceProbeWithThrusters(originPlanetIndex, destinationPlanetIndex, startLaunchAngle, startAngleChange, spaceProbeVelocity, DEBUG_MODE_ON);
	}

	/** Computes the optimal launch angle for the spaceProbe to reach the destinationPlanet starting from the originPlanet
	  * The idea is basically to start with the given launchAngle from the origin planet,
	  * then run the simulation until either the spaceProbe crashes or gets further away from the originPlanet than the destinationPlanet is.
	  * Then, evaluate the result and if the spaceProbe is too much to the left, make the angle smaller, else if it is too much to the right, make the angle bigger
	  * once either the angle is as precise as it could be or the spaceProbe crashes on Titan, we found the supposedly best possible angle (without using any thrust during the travel)
	  *
	  * @param originPlanet, the planet from which the spaceProbe starts off
	  * @param destinationPlanet, the planet at which the spaceProbe should arrive
	  * @param startLaunchAngle, a starting guess for the correct launch angle
	  * @param startAngleChange, the starting amount of degrees by which the launch angle is modified at each iteration
	  * @param spaceProbeVelocity, the total velocity of the spaceProbe at the starting position
	  * @param DEBUG, a boolean unlocking debug print statements
	  */
	private double launchAngleAdjustmentSearchSpaceProbeWithThrusters (final int originPlanetIndex, final int destinationPlanetIndex, final double startLaunchAngle, final double startAngleChange, final double spaceProbeVelocity, final boolean DEBUG) {
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
			//Initialize the space probe and the solar system
			createSolarSystem();

			//Create variables representing the originPlanet, the destinationPlanet and the Sun
			CelestialBody Sun = planets[0];
			CelestialBody originPlanet = planets[originPlanetIndex];
			CelestialBody destinationPlanet = planets[destinationPlanetIndex];

			//Create a new spaceProbe launched with a new angle from planet Earth
			spaceProbe = SpaceProbeWithThrusters.createSpaceProbeWithStartingAngle(spaceProbeName, spaceProbeMass, originPlanet, spaceProbeVelocity, launch_angle, spaceProbeStartingHeight);

			int num = 0;
			//As long as the spaceProbe has not crashed into a planet, or gone further away from the originPlanet than the destinationPlanet's distance from the originPlanet or gone further away from the Sun than Pluto's distance to the Sun
			while (spaceProbe.didNotCrash() && (spaceProbe.getPosition().distance(originPlanet.getPosition()) < destinationPlanet.getPosition().distance(originPlanet.getPosition())) && spaceProbe.getPosition().distance(Sun.getPosition()) < DISTANCE_SUN_PLUTO)  {
				//Update the position and the number of iterations
				update(DELTA_T, true);
				num ++;
			}

			//If the space probe crashed on Titan, we are done
			if (planets[destinationPlanetIndex].equals(spaceProbe.getCrashedPlanet())) {
				crashedDestinationPlanet = true;
				if (DEBUG) System.out.println("\n\n\nIteration #" + numberIterations + "\nA launch angle of " + launch_angle + " degrees got the spaceProbe to Titan.\nNumber of iterations to reach it: " + num);
			}
			else {
				//If we crash into a planet, we print it to the console, then reset the variable in the spaceProbe
				if (spaceProbe.getCrashedPlanet() != null)
					spaceProbe.resetCrashedPlanet();

				//Then, we compute the angles with respect to a certain CelestialBody
				Vector2D referencePoint = new Vector2D(planetPositions[originPlanetIndex]);//planetPositions[0]);

				spaceProbeAngle = spaceProbe.getPosition().angle(referencePoint);
				destinationPlanetAngle = planets[destinationPlanetIndex].getPosition().angle(referencePoint);

				//Save an intermediate value, the sign of the difference of the angles
				int tmp = signum(spaceProbeAngle-destinationPlanetAngle);

				if (Math.abs(destinationPlanetAngle-spaceProbeAngle) > 180) {
					//If the previous move was the opposite move,
					if (previousMove == -tmp) {
						//We reduce the angleChange by half
						angleChange = angleChange/2;
					}

					//In any case, modify the launch_angle
					launch_angle += tmp * angleChange;
					//and save this move as the previous move for the next iteration
					previousMove = tmp;
				}
				else if (Math.abs(destinationPlanetAngle-spaceProbeAngle) > 0) {
					//If the previous move was the opposite move,
					if (previousMove == tmp) {
						//We reduce the angleChange by half
						angleChange = angleChange/2;
					}

					//In any case, modify the launch_angle
					launch_angle += -tmp * angleChange;
					//and save this move as the previous move for the next iteration
					previousMove = -tmp;
				}
				else {
					//The two angles are the same, but I will still modify the angle in order for angleChange to become smaller and smaller and to refine the launch_angle even more
					//Modify the angle in some direction
					tmp = 1;		//Set tmp to 1, because normally it should be 0 in this case
					launch_angle += tmp * angleChange;

					//and set the previous move for the next iteration as the move just made
					previousMove = tmp;
				}
			}

			//Once we cannot get a more precise value (that is, the change of the angle angleChange is smaller than the distance of launch_angle to the next double value after launch_angle)
			if (angleChange < Math.ulp(launch_angle)) {
				if (DEBUG) System.out.println("Final angleChange = " + angleChange);
				crashedDestinationPlanet = true;
			}

			if (DEBUG) System.out.println("New space probe launch angle: " + launch_angle);

			numberIterations ++;
			elapsedSeconds = 0;
		}

		//Save the ideal angle
		double result = launch_angle;

		//Now, we print out the supposedly ideal angle if debugging mode is on
		if (DEBUG) System.out.println("'Optimal' launch angle: " + result);

		return result;
	}

	/** Overloads method launchAngleAdjustmentSearch with one less parameter than the original: the boolean DEBUG
	  */
	private double launchOrbitAdjustmentSearch (final int originPlanetIndex, final int destinationPlanetIndex, final double initialStartTime, final double initialStartTimeIncrement, final double spaceProbeVelocity) {
		boolean DEBUG_MODE_ON = true;
		return launchOrbitAdjustmentSearch(originPlanetIndex, destinationPlanetIndex, initialStartTime, initialStartTimeIncrement, spaceProbeVelocity, DEBUG_MODE_ON);
	}

	/** Computes the optimal start time for the spaceProbe to get into orbit around the destination planet
	  * @param originPlanet, the planet from which the spaceProbe starts off
	  * @param destinationPlanet, the planet at which the spaceProbe should arrive
	  * @param startTime, a starting guess for the correct time to start the Hohmann Transfer
	  * @param startTimeIncrement, the starting amount of seconds by which we modify the starting time at each iteration
	  * @param spaceProbeVelocity, the total velocity of the spaceProbe at the starting position
	  * @param DEBUG, a boolean unlocking debug print statements
	  */
	private double launchOrbitAdjustmentSearch (final int originPlanetIndex, final int destinationPlanetIndex, final double initialStartTime, final double initialStartTimeIncrement, final double spaceProbeVelocity, final boolean DEBUG) {
		//Initialize some variables
		boolean gotIntoOrbit = false;
		double startTime = initialStartTime;
		double startTimeModifier = initialStartTimeIncrement;

		//Reset the solar system, then create a memento for the starting position
		createSolarSystem();
		SolarSystemMemento latestStatePreviousIteration = new SolarSystemMemento(planets);

		//As long as we do not get into orbit,
		while (! gotIntoOrbit) {
			//Load the latest state from the previous iteration
			loadMemento(latestStatePreviousIteration);

			//Run the simulation for startTime seconds, without the spaceProbe
			int time = 0;
			while (time < startTimeModifier) {
				update(DELTA_T, false);
				time += startTime;
			}

			//Create a new spaceProbe to start the Hohmann Transfer from this position
			spaceProbe = SpaceProbeWithThrusters.createSpaceProbeHohmannTransfer(planets[originPlanetIndex], planets[destinationPlanetIndex], DELTA_T);
			FlightPlan plan = ((SpaceProbeWithThrusters)spaceProbe).getFlightPlan();
		}


		/* Previous stuff, can look at it for inspiration but that's it
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
			//Initialize the space probe and the solar system
			createSolarSystem();

			//Create variables representing the originPlanet, the destinationPlanet and the Sun
			CelestialBody Sun = planets[0];
			CelestialBody originPlanet = planets[originPlanetIndex];
			CelestialBody destinationPlanet = planets[destinationPlanetIndex];

			//Create a new spaceProbe launched with a new angle from planet Earth
			spaceProbe = SpaceProbe.createSpaceProbeWithStartingAngle(spaceProbeName, spaceProbeMass, originPlanet, spaceProbeVelocity, launch_angle);

			int num = 0;
			//As long as the spaceProbe has not crashed into a planet, or gone further away from the originPlanet than the destinationPlanet's distance from the originPlanet or gone further away from the Sun than Pluto's distance to the Sun
			while (spaceProbe.didNotCrash() && (spaceProbe.getPosition().distance(originPlanet.getPosition()) < destinationPlanet.getPosition().distance(originPlanet.getPosition())) && spaceProbe.getPosition().distance(Sun.getPosition()) < DISTANCE_SUN_PLUTO)  {
				//Update the position and the number of iterations
				update(DELTA_T, true);
				num ++;
			}

			//If the space probe crashed on the destination planet, we are done
			if (spaceProbe.getPosition().distance(orbitPositionFromPlanet(planets[destinationPlanetIndex])) < planets[destinationPlanetIndex].getRadius()) {
				crashedDestinationPlanet = true;
				if (DEBUG) System.out.println("\n\n\nIteration #" + numberIterations + "\nA launch angle of " + launch_angle + " degrees got the spaceProbe near of Titan.\nNumber of iterations to reach it: " + num);
			}
			else {
				//If we crash into a planet, we print it to the console, then reset the variable in the spaceProbe
				if (spaceProbe.getCrashedPlanet() != null)
					spaceProbe.resetCrashedPlanet();

				//Then, we compute the angles with respect to a certain CelestialBody
				Vector2D referencePoint = new Vector2D(planetPositions[originPlanetIndex]);//planetPositions[0]);

				spaceProbeAngle = spaceProbe.getPosition().angle(referencePoint);
				destinationPlanetAngle = orbitPositionFromPlanet(planets[destinationPlanetIndex]).angle(referencePoint);

				//Save an intermediate value, the sign of the difference of the angles
				int tmp = signum(spaceProbeAngle-destinationPlanetAngle);

				if (Math.abs(destinationPlanetAngle-spaceProbeAngle) > 180) {
					//If the previous move was the opposite move,
					if (previousMove == -tmp) {
						//We reduce the angleChange by half
						angleChange = angleChange/2;
					}

					//In any case, modify the launch_angle
					launch_angle += tmp * angleChange;
					//and save this move as the previous move for the next iteration
					previousMove = tmp;
				}
				else if (Math.abs(destinationPlanetAngle-spaceProbeAngle) > 0) {
					//If the previous move was the opposite move,
					if (previousMove == tmp) {
						//We reduce the angleChange by half
						angleChange = angleChange/2;
					}

					//In any case, modify the launch_angle
					launch_angle += -tmp * angleChange;
					//and save this move as the previous move for the next iteration
					previousMove = -tmp;
				}
				else {
					//The two angles are the same, but I will still modify the angle in order for angleChange to become smaller and smaller and to refine the launch_angle even more
					//Modify the angle in some direction
					launch_angle += tmp * angleChange;

					//and set the previous move for the next iteration as the move just made
					previousMove = tmp;
				}
			}

			//Once we cannot get a more precise value (that is, the change of the angle angleChange is smaller than the distance of launch_angle to the next double value after launch_angle)
			if (angleChange < Math.ulp(launch_angle)) {
				if (DEBUG) System.out.println("Final angleChange = " + angleChange);
				crashedDestinationPlanet = true;
			}

			if (DEBUG) System.out.println("New space probe launch angle: " + launch_angle);

			numberIterations ++;
			elapsedSeconds = 0;
		}

		//Save the ideal angle
		double result = launch_angle;

		//Now, we print out the supposedly ideal angle if debugging mode is on
		if (DEBUG) System.out.println("'Optimal' launch angle: " + result);

		return result;
		*/

		return 0;
	}

	/** Auxiliary method for launchOrbitAdjustmentSearch
	  */
	private Vector2D orbitPositionFromPlanet (CelestialBody planet) {
		CelestialBody Sun = planets[0];
		//Compute the angle of the position of the planet with respect to the Sun (center of the solar system)
		double angle = planet.getPosition().angle(Sun.getPosition());

		//Compute the distance between the Sun and the planet
		double distPlanetSun = Sun.getPosition().distance(planet.getPosition());

		//Then, compute the angle we need to modify the current angle of the Planet by to get a position next to the planet
		double angleModifier = planet.getRadius()/distPlanetSun;

		//Finally, compute the new angle
		double newAngle = angle - angleModifier;

		//Compute the vector at a distance distPlanetSun of the sun with an angle newAngle
		Vector2D resultPos = new Vector2D(newAngle).multiply(distPlanetSun);

		return resultPos;
	}

	/** Overloads method launchAngleAdjustmentSearchImproved with one less parameter than the original: the boolean DEBUG
	  */
	private FlightPlan launchAngleAdjustmentSearchImproved (final int originPlanetIndex, final int destinationPlanetIndex, final double startLaunchAngle, final double startAngleChange, final double spaceProbeVelocity) {
		boolean DEBUG_MODE_ON = true;
		return launchAngleAdjustmentSearchImproved(originPlanetIndex, destinationPlanetIndex, startLaunchAngle, startAngleChange, spaceProbeVelocity, DEBUG_MODE_ON);
	}

	/** NOT WORKING
	  * Computes the optimal launch angle for the spaceProbe to get into orbit of the destinationPlanet starting from the originPlanet
	  * Should work out the flightPlan with which to get into orbit around Titan
	  * Idea is to try to compute the best angle to reach Titan, then use the thruster towards the end of the fligt to correct the position in order to get into orbit around Titan
	  *
	  * @param originPlanet, the planet from which the spaceProbe starts off
	  * @param destinationPlanet, the planet at which the spaceProbe should arrive
	  * @param startLaunchAngle, a starting guess for the correct launch angle
	  * @param startAngleChange, the starting amount of degrees by which the launch angle is modified at each iteration
	  * @param spaceProbeVelocity, the total velocity of the spaceProbe at the starting position
	  * @param DEBUG, a boolean unlocking debug print statements
	  */
	private FlightPlan launchAngleAdjustmentSearchImproved (final int originPlanetIndex, final int destinationPlanetIndex, final double startLaunchAngle, final double startAngleChange, final double spaceProbeVelocity, final boolean DEBUG) {
		System.out.println("1");

		//Use launchAngleAdjustmentSearch() to get the ideal angle without thrust
		final double IDEAL_ANGLE = 256.2480755655964;	//launchAngleAdjustmentSearch(originPlanetIndex, destinationPlanetIndex, startLaunchAngle, startAngleChange, spaceProbeVelocity, DEBUG);
		System.out.println("2");
		spaceProbe = SpaceProbeWithThrusters.createSpaceProbeWithStartingAngle(spaceProbeName, spaceProbeMass, planets[originPlanetIndex], spaceProbeVelocity, IDEAL_ANGLE, spaceProbeStartingHeight);
		//final Vector2D INITIAL_POSITION = spaceProbe.getPosition();
		//final Vector2D INITIAL_VELOCITY = spaceProbe.getVelocity();
		//the mass is saved in spaceProbeMass
		//the name is saved in spaceProbeName
		final SpaceProbeWithThrusters correctStartingConditionsSpaceProbe = (SpaceProbeWithThrusters) spaceProbe;//new SpaceProbeWithThrusters(spaceProbeName, spaceProbeMass, INITIAL_POSITION, INITIAL_VELOCITY, IDEAL_ANGLE);

		//Make one simulation to compute the final position, the final velocity and the number of iterations
		//Set starting conditions for the simulation
		createSolarSystem();
		spaceProbe = correctStartingConditionsSpaceProbe.clone();
		System.out.println("3");
		//Run the simulation
		int numIterations = 0;
		while (spaceProbe.didNotCrash()) {
			//Update the position of all the objects and the number of iterations
			update(DELTA_T, true);		//the true is a boolean for whether the spaceProbe is included or not
			numIterations ++;
		}

		System.out.println("4");

		//Then, save the position, number of iterations, spaceProbe position and velocity
		final int NO_THRUSTERS_NUM_ITERATIONS = numIterations;
		final Vector2D FINAL_POSITION = new Vector2D(spaceProbe.getPosition());
		final Vector2D FINAL_VELOCITY = new Vector2D(spaceProbe.getVelocity());

		//Do a second iteration, and make a list/array of mementos for the 10/100 or sth last iterations
		//Reset starting conditions
		createSolarSystem();
		spaceProbe = correctStartingConditionsSpaceProbe.clone();
		numIterations = 0;
		//Run the simulation and save the state of the solar system towards the end of the simulation (the end being the spaceProbe crashing on Titan)
		//Set the number days saved in the mementos to a value, then if there are not even as many days in the trip, we set the number of days to the number of days that the trip lasted
		int numDaysSaved = 100;
		if (NO_THRUSTERS_NUM_ITERATIONS * DELTA_T <= numDaysSaved * SEC_IN_DAY) {
			numDaysSaved = (int)(NO_THRUSTERS_NUM_ITERATIONS * DELTA_T)/SEC_IN_DAY;
		}

		System.out.println("5");

		//Set the solarSystemMemento to contain as many iterations as to have saved the state of the last X days
		SolarSystemMemento[] solarSystemStates = new SolarSystemMemento[(int)(numDaysSaved * (((double)SEC_IN_DAY)/DELTA_T))];
		int nextMementoIndex = 0;
		while (spaceProbe.didNotCrash()) {
			//Update the position of all the objects and the number of iterations
			update(DELTA_T, true);		//the true is a boolean for whether the spaceProbe is included or not
			numIterations ++;

			//IF we are in the numDaysSaved last days, then we save the current state of things
			if (numIterations > NO_THRUSTERS_NUM_ITERATIONS - solarSystemStates.length) {
				solarSystemStates[nextMementoIndex] = new SolarSystemMemento(planets, spaceProbe);
				nextMementoIndex ++;
			}
		}


		System.out.println("6");

		//Initialize the flightPlan to a new inactive flightplan with as many iterations as we have without using the thrusters before crashing into Titan
		FlightPlan plan = FlightPlan.createNewInactivePlan(NO_THRUSTERS_NUM_ITERATIONS);

		double mu = G * planets[destinationPlanetIndex].getMass();
		double radius = planets[destinationPlanetIndex].getRadius();
		for (double i = 1.1; i <= 2.0; i += 0.1) {
			double v = Math.sqrt(mu/(i*radius));

			System.out.println("With a radius of " + i + " times the radius of Titan (= " + (i*radius) + "), the velocity would have to be: " + v);
		}

		//Now we try running simulations starting at a late position, where we try using the thrusters a little to try to get to Titan
		/*int currentIteration = solarSystemStates.length - 1;
		while (currentIteration >= 0) {
			//We load the memeto we are currently at
			loadMemento(solarSystemStates[currentIteration]);

			//Also depends on what exactly our aim is ...
			//

			//Try using the thrusters a bit and record that change in the FlightPlan

			//Then finish the simulation until some point (NO_THRUSTERS_NUM_ITERATIONS iterations maybe ?)

			//Then evaluate the simulation
			//	Either good change, we keep it then go on to the next change
			//	Or bad change, and we revert it by using plan.resetIterationToInactive(iterationNum)
		}
		*/

		//Other idea:
		/* Computing the smallest distance to the destination planet we get with the ideal angle, d_min
		//NOTE !!! PROBLEM !!! we pass like 5*10^6 meters away from Titan's surface, too far away for gravity assist ???
			Then compute the speed we would need to have to enter orbit using the formula http://www.uphysicsc.com/2013-GM-A-538.PDF
				v = sqrt((G * M)/r)
					with M being the mass of the planet you would like to orbit around,
					 r the radius of the orbit we would like to have (which we could set as the closest distance we get to Titan)
					 and G the constant

			This would then be v_orbit

			And then we would just need to run the simulation, then once we are at a distance d_min from the destination planet, we modify the velocity in order to reach the desired velocity
		*/

		//Return the resulting FlightPlan with the "perfect" trajectory
		//This is a placeholder to avoid getting a "missing return statement" error
		return null;
	}

	/** Auxiliary method for launchAngleAdjustmentSearchImproved()
	  * Given a solar system memento, it loads it into the global planets and spaceProbe variable
	  *
	  * @param memento the solarSystemMemento which we want to load into memory
	  */
	private void loadMemento (SolarSystemMemento memento) {
		planets = memento.getPlanetsState();
		spaceProbe = memento.getSpaceProbeState();
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
			if (i < 9) {
				planets[i] = new CelestialBody(planetNames[i], planetMasses[i], planetPositions[i], planetVelocities[i], planetRadius[i]);
			}
			else {
				planets[i] = new Moon(planetNames[i], planetMasses[i], planetPositions[i], planetVelocities[i], planetRadius[i]);
			}
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

		//And the dragging ability
		canvas.setOnDragDetected((event) -> movingStartPosition = new Vector2D(event.getX(), event.getY()));
        canvas.setOnMouseDragged((event) -> {
            if (movingStartPosition != null) {
                Vector2D movingCurrentPosition = new Vector2D(event.getX(), event.getY());
                movingCurrentPosition.subtract(movingStartPosition);
                movingStartPosition = new Vector2D(event.getX(), event.getY());

				coordinates.setModifiedPos(coordinates.getModifiedPos().add(movingCurrentPosition));
                //coordinates.setModifiedX(coordinates.getModifiedX() + movingCurrentPosition.getX());
                //coordinates.setModifiedY(coordinates.getModifiedY() + movingCurrentPosition.getY());
            }
        });
        canvas.setOnMouseReleased((event) -> movingStartPosition = null);

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
			redrawCanvas(gc, isSpaceProbeIncluded, waitStartTime);
		}
	}

	/** Draw the planets at their new positions, then update them
		@param gc, the graphicsContext on which we draw the planets' positions
		@param spaceProbeIncluded determines whether the space probe also has to be taken into account or not
	*/
	private void redrawCanvas(GraphicsContext gc, boolean spaceProbeIncluded, double waitTime) {
		//Draw over to get a wide board
		this.canvasWidth = gc.getCanvas().getWidth();
		this.canvasHeight = gc.getCanvas().getHeight();
		gc.clearRect(0, 0, canvasWidth, canvasHeight);

		//Draw all the planets
		for (int i = 0; i < planets.length; i ++) {
			//Model the position of the planets to the position in the GUI by using the CoordinateTransformer
			Vector2D otherPosition = coordinates.modelToOtherPosition(planets[i].getPosition());

			//Draw black circles representing the planets, with a radius of PLANET_RADIUS
			gc.setFill(Color.BLACK);
			gc.fillOval(otherPosition.x - PLANET_RADIUS, otherPosition.y - PLANET_RADIUS, PLANET_RADIUS * 2, PLANET_RADIUS * 2);

			//Draw the labels
			Vector2D textPos = planets[i].getLabelPositionModifier();
			gc.fillText(planets[i].getName(), otherPosition.x + textPos.getX(), otherPosition.y + textPos.getY());
		}

		//if the space probe is also simulated, then
		if (spaceProbeIncluded) {
			//get the space probe's position on the GUI by using the CoordinateTransformer
			Vector2D spaceProbePosition = coordinates.modelToOtherPosition(spaceProbe.getPosition());

			//draw a black circle with the same radius as the planets ... could be changed later on
			gc.setFill(Color.BLACK);
			gc.fillOval(spaceProbePosition.x - PLANET_RADIUS, spaceProbePosition.y - PLANET_RADIUS, PLANET_RADIUS * 2, PLANET_RADIUS * 2);

			//draw a fitting label, using the name of the space probe object
			Text text = new Text(spaceProbe.getName());
			gc.fillText(spaceProbe.getName(), spaceProbePosition.x - (text.getLayoutBounds().getWidth() / 2), spaceProbePosition.y - PLANET_RADIUS - (text.getLayoutBounds().getHeight() / 2));

			//SHOULD BE COMMENTED OUT IN THE FINAL VERSION
			// Optionnally, print the distance between the spaceProbe and Earth when it is smaller than 5*10^8 to see the closest it gets to Earth
			if (spaceProbe.didNotCrash() && spaceProbe.getPosition().distance(planets[9].getPosition()) < 1e8) {
				System.out.println("Distance to Titan: " + (spaceProbe.getPosition().distance(planets[9].getPosition()) - planets[9].getRadius()));
				System.out.println("Distance to the center of Titan: " + (spaceProbe.getPosition().distance(planets[9].getPosition())));
			}
		}

		//If the starting wait time is over, update the positions of the planets, otherwise not
		if (System.nanoTime() >= startTime + waitTime*Math.pow(10, 9)) {
			update(DELTA_T, spaceProbeIncluded);
		}

		//set the text of the timelabel and make the timeLabel large enough so the text fits in the label
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
		//Keep track of the number of iterations
		numIterations ++;
	}

	/** Formats the elapsedSeconds variable for displaying the time elapsed since the beginning of the simulation

		@return a nicely formatted string expressing the elapsedSeconds variable in years, days, minutes and seconds
	*/
	public static String getElapsedTimeAsString() {
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
