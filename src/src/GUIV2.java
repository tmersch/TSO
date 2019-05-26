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
	//scaling factor
	private static final double SCALE = 5e9;
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
	private static SpaceProbe spaceProbe;							//the spaceProbe object
	private static final double voyagerMass = 800;					//in kg
	private static final double averageVelocitySpaceProbe = 48e3;	//in meters/secs

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

		Scanner s = new Scanner(System.in);
		String input = "";

		while ((!input.equals("1")) && (!input.equals("2"))) {
			System.out.println("Do you want to see the solar system simulation (enter 1) or see the landing (enter 2) ?");
			input = s.next();

			//Simulation of the solar system
			if (input.equals("1")) {
				//Initialize the planets
				createSolarSystem();

				//CoordinateTransformer
				coordinates.setScale(SCALE);
				coordinates.setModifiedX(700);
				coordinates.setModifiedY(350);

				input = "";
				while ((! input.equals("1")) && (! input.equals("2")) && (! input.equals("3"))) {
					System.out.println("Do you want to see the GUI or shoot the space probe to Titan ? (enter 1 for the GUI, 2 for the space probe launch, 3 for the space probe test)");
					input = s.next();

					if (input.equals("1")) {
						System.out.println("Which timestep do you want to use (in secs)?");
						DELTA_T = s.nextDouble();
						System.out.println("After how much time (in secs) do you want to end the simulation ?");
						double endTime = s.nextLong();
						boolean spaceProbeIncluded = false;
						int numIterations = (int)Math.ceil(endTime/DELTA_T);

						//GUI part
						gc = createGUI(stage);
						launchGUI(1, spaceProbeIncluded, numIterations);
						timeline.play();
						stage.show();

						while (!timeline.getStatus().equals(Animation.Status.STOPPED)) {
						}

						for (int i = 0; i < planets.length; i ++) {
							System.out.println("Planet " + planets[i].getName() + ": \nPosition: " + planets[i].getPosition());
						}
					}
					//Launch of space probe
					else if (input.equals("2")) {
						//Choice = 1 launches the binary search for the angle to reach Titan, choice = 2 launches a very crude direct approximation of the angle to reach Titan
						int choice = 2;

						if (DEBUG) System.out.println("Launching space probe shooting module");

						//Approach 1: Trying out an angle, then adjusting to the left or right to get closer to Titan
						if (DEBUG) System.out.println("Launching binary search angle calibration");
						double launch_angle = 259;
						boolean crashedTitan = false;
						double DISTANCE_SUN_PLUTO = 5906376272e3;
						double angleChange = 1;
						int previousMove = 0;
						int numberIterations = 0;
						double spaceProbeAngle = 0;
						double titanAngle  = 0;
						while (! crashedTitan) {
							if (DEBUG) System.out.println("Iteration #" + numberIterations);

							//Initialize the space probe and the solar system
							createSolarSystem();

							//Once we cannot get a more precise value (that is, the change of the angle angleChange is smaller than the distance of launch_angle to the next double value after launch_angle)
							if (angleChange < Math.ulp(launch_angle))
								crashedTitan = true;

							//Create a new spaceProbe launched with a new angle from planet Earth
							spaceProbe = SpaceProbe.createSpaceProbeWithStartingAngle("SpaceProbe", voyagerMass, planets[3].getPosition(), averageVelocitySpaceProbe, planetRadius[3], launch_angle);

							int method = 2;
							Vector2D oldPos;
							if (method == 1) {
								//Run the simulation
								gc = createGUI(stage);
								launchGUI(1, true);
								timeline.play();
								stage.show();
							}
							else if (method == 2) {
								//While the space probe has not crashed into a planet or its distance to the sun is greater than the distance from Neptune to the sun
								int num = 1;
								while (spaceProbe.didNotCrash() && (spaceProbe.getPosition().distance(planets[0].getPosition()) < planets[9].getPosition().distance(planets[0].getPosition()))) {
									//oldPos = new Vector2D(spaceProbe.getPosition());
									update(DELTA_T, true);
									//System.out.println("Iteration #" + numberIterations + " . " + num + ": Difference in position: \n   - " + oldPos.subtract(spaceProbe.getPosition()));
									num ++;
								}
								if (DEBUG) System.out.println(num + " iterations needed to end simulation");
							}

							//If the space probe crashed on Titan, we are done
							if (planets[9].equals(spaceProbe.getCrashedPlanet())) {
								crashedTitan = true;
								if (DEBUG) System.out.println("\n\n\nIteration #" + numberIterations + "A launch angle of " + launch_angle + " degrees got the spaceProbe to Titan.");
							}
							else {
								//If we crash into a planet, we print it to the console, then reset the variable in the spaceProbe
								if (spaceProbe.getCrashedPlanet() != null)
									if (DEBUG) System.out.println("Crashed in " + spaceProbe.getCrashedPlanet().getName());
								spaceProbe.resetCrashedPlanet();

								//Then, we compute the angles
								spaceProbeAngle = spaceProbe.getPosition().angle(planetPositions[0]);
								titanAngle = planets[9].getPosition().angle(planetPositions[0]);
								//Both angles should now be between 0 and 360 degrees
								if (DEBUG) System.out.printf("Arrival SpaceProbeAngle: %f, Titan angle: %f\n", spaceProbeAngle, titanAngle);

								int tmp = signum(spaceProbeAngle-titanAngle);
								if (Math.abs(titanAngle-spaceProbeAngle) > 180) {
									if (previousMove == -tmp) {
										if (DEBUG) { System.out.println("Move == " + tmp + ": \nSpaceProbe angle: " + spaceProbeAngle + ", Titan angle: " + titanAngle + ", ");
											if (previousMove > 0) System.out.println("   The angle should be between " + (launch_angle-previousMove*angleChange) + " and " + launch_angle);
											else System.out.println("   The angle should be between " + launch_angle + " and " + (launch_angle-previousMove*angleChange));
										}

										angleChange = angleChange/2;
									}

									launch_angle += tmp * angleChange;

									previousMove = tmp;
								}
								else if (Math.abs(titanAngle-spaceProbeAngle) > 0) {
									if (previousMove == tmp) {
										if (DEBUG) { System.out.println("Move == " + tmp + ": \nSpaceProbe angle: " + spaceProbeAngle + ", Titan angle: " + titanAngle + ", ");
											if (previousMove > 0) System.out.println("   The angle should be between " + (launch_angle-previousMove*angleChange) + " and " + launch_angle);
											else System.out.println("   The angle should be between " + launch_angle + " and " + (launch_angle-previousMove*angleChange));
										}

										angleChange = angleChange/2;
									}

									launch_angle += -tmp * angleChange;

									previousMove = -tmp;
								}
								else {
									crashedTitan = true;
								}
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
						//System.out.println("TitanAngle: " + titanAngle);
						System.out.println("'Optimal' launch angle: " + launch_angle);

						//Reset the spaceProbe with the new launch_angle
						spaceProbe = SpaceProbe.createSpaceProbeWithStartingAngle("SpaceProbe", voyagerMass, planets[3].getPosition(), averageVelocitySpaceProbe, planetRadius[3], launch_angle);

						//Launch the simulation
					  gc = createGUI(stage);
						launchGUI(1, true);			//compute next position after 10 milliseconds, and also consider the spaceProbe
						timeline.play();
						stage.show();
					}
					//Test of space probe angle
					else if (input.equals("3")) {
						Scanner S = new Scanner(System.in);
						System.out.println("Enter the angle you would like to launch the spaceProbe in: ");
						double launch_angle = S.nextDouble();

						/*
						System.out.println("Do you want a fixed number of iterations ? If yes, enter the number, otherwise enter '-1': ");
						int numberOfIterations = S.nextInt();
						*/
						//Reset the solar system
						createSolarSystem();

						//Create a new spaceProbe with the starting angle
						spaceProbe = SpaceProbe.createSpaceProbeWithStartingAngle("SpaceProbe", voyagerMass, planets[3].getPosition(), averageVelocitySpaceProbe, planetRadius[3], launch_angle);

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
					}
				}
			}
			//Landing on titan
			else if (input.equals("2")) {
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

					//open-loop controller
					if (landModChoice.equals("1")) {
						//For the open-loop controller, we make the timestep bigger as it takes a lot of time to land
						LANDINGDELTA_T = 100;

						//Initialize choice
						String choice = "";

						//Ask the user if he wants to specify a certain starting position for the landing module
						System.out.println("Do you want to specify a specific starting position ? Enter 'yes' or 'no'");
						choice = s.next();
						//If the user wants to specify a starting position, then we retrieve the x- and y-position and save it
						if (choice.equals("yes")) {
							System.out.println("Please enter the x-position and y-position separated by a space");
							double startX = s.nextDouble();
							double startY = s.nextDouble();
							landModStartPos = new Vector2D(startX, startY);
						}

						//Ask the user if he wants to specify a certain starting velocity for the landing module
						choice = "";
						System.out.println("Do you want to specify a specific starting velocity ? Enter 'yes' or 'no'");
						choice = s.next().toLowerCase();
						//If the user wants to specify a starting velocity, retrieve the input velocity and save it
						if (choice.equals("yes")) {
							System.out.println("Please enter the x-velocity and y-velocity separated by a space");
							double startXVeloc = s.nextDouble();
							double startYVeloc = s.nextDouble();
							landModStartVeloc = new Vector2D(startXVeloc, startYVeloc);
						}

						//Ask the user if he wants to specify a certain starting angle
						choice = "";
						System.out.println("Do you want to specify a specific starting angle ? Enter 'yes' or 'no'");
						choice = s.next().toLowerCase();
						//If the user wants to specify a starting angle, retrieve the input angle and save it
						if (choice.equals("yes")) {
							System.out.println("Please enter the angle");
							landModStartAngle = s.nextDouble();
						}

						landingModule = new LandingModuleOpenLoopController(landingModuleWeight, landModStartPos, landModStartVeloc, landModStartAngle);
					}
					//Feedback controller
					else if (landModChoice.equals("2")) {
						//Initialize choice
						String choice = "";

						//Ask the user if he wants to specify a certain starting position for the landing module
						System.out.println("Do you want to specify a specific starting position ? Enter 'yes' or 'no'");
						choice = s.next();
						//If the user wants to specify a starting position, then we retrieve the x- and y-position and save it
						if (choice.equals("yes")) {
							System.out.println("Please enter the x-position and y-position separated by a space");
							double startX = s.nextDouble();
							double startY = s.nextDouble();
							landModStartPos = new Vector2D(startX, startY);
						}

						//Ask the user if he wants to specify a certain starting velocity for the landing module
						choice = "";
						System.out.println("Do you want to specify a specific starting velocity ? Enter 'yes' or 'no'");
						choice = s.next().toLowerCase();
						//If the user wants to specify a starting velocity, retrieve the input velocity and save it
						if (choice.equals("yes")) {
							System.out.println("Please enter the x-velocity and y-velocity separated by a space");
							double startXVeloc = s.nextDouble();
							double startYVeloc = s.nextDouble();
							landModStartVeloc = new Vector2D(startXVeloc, startYVeloc);
						}

						//Ask the user if he wants to specify a certain starting angle
						choice = "";
						System.out.println("Do you want to specify a specific starting angle ? Enter 'yes' or 'no'");
						choice = s.next().toLowerCase();
						//If the user wants to specify a starting angle, retrieve the input angle and save it
						if (choice.equals("yes")) {
							System.out.println("Please enter the angle");
							landModStartAngle = s.nextDouble();
						}

						//Ask the user if he wants to add wind to the simulation or not
						boolean addWind = false;
						choice = "";
						System.out.println("Do you want to add wind to the simulation ? Enter 'yes' if you want to add wind, otherwise 'no'");
						choice = s.next().toLowerCase();
						//If the user wants to have wind, give it to him !!!
						if (choice.equals("yes")) {
							addWind = true;
						}

						landingModule = new LandingModuleFeedbackController(landingModuleWeight, landModStartPos, landModStartVeloc, landModStartAngle, addWind);
					}
				}

				boolean showGUI = false;
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
			}
		}
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

        //And set their initial texts
        altitudeText.setText("Altitude : ");
        verticalSpeedText.setText("Vertical Speed : ");
        timeText.setText("Elapsed time : ");

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

	/** Initialize the Planet and the Moon objects with their default properties, contained in
	*/
	public void createSolarSystem() {
		planets = new CelestialBody[planetNames.length];
		for (int i = 0; i < planetNames.length; i ++) {
			planets[i] = new CelestialBody(planetNames[i], planetMasses[i], planetPositions[i], planetVelocities[i]);
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
			if (spaceProbeIncluded) spaceProbe.updatePosition(time, step);
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
