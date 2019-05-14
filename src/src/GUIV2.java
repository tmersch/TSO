import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import java.util.Scanner;

/** GUI representing the solar system using javafx
*/
public class GUIV2 extends Application {
	//Width and height of the window
	private double canvasWidth = 0;		// ------------------------------------------------------------
	private double canvasHeight = 0;	// ------------------------------------------------------------

	//Number of seconds between each update
	public static final double DELTA_T = 60 * 30;
	//scaling factor
	public static final double SCALE = 5e9;
	//radius of the planets
	public static final double PLANET_RADIUS = 2;
	//height of the part at the top
	private static final int TOP_AREA_HEIGHT = 100;
	//the gravitational constant
	public static final double G = 6.67300E-11;

	//Seconds conversion
	private static final int SEC_IN_MINUTE = 60;
  private static final int SEC_IN_HOUR = SEC_IN_MINUTE * 60;
  private static final int SEC_IN_DAY = SEC_IN_HOUR * 24;
  private static final int SEC_IN_YEAR = 31556926;
	//and the long keeping track of the number of seconds elapsed from the start
  private long elapsedSeconds = 0;

	//Array containing the planets/moons
	protected static CelestialBody[] planets;
	//Arrays containing all the information about the planets/moons
	// Indexes: --------------------------------    	0							1							2							3							4							5								6								7								8								9							10						11
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

	//The space probe object
	private static SpaceProbe spaceProbe;
	private static final double voyagerMass = 800;
		//in kg
	private static final double averageVelocitySpaceProbe = 40e3;
		//in meters/secs

	//GUI parts
	private Timeline timeline;
	private CoordinatesTransformer coordinates = new CoordinatesTransformer();
	private GraphicsContext gc;
	private Label timeLabel;
		//the startTime of the simulation according to System.nanoTime()
	private long startTime = -1;
		//number of iterations in the current simulation
	private int numIterations;
	private boolean showNumIterations = false;


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

		while ((! input.equals("1")) && (! input.equals("2")) && (! input.equals("3"))) {
			System.out.println("Do you want to see the GUI or shoot the space probe to Titan ? (enter 1 for the GUI, 2 for the space probe launch, 3 for the space probe test)");
			input = s.next();

			if (input.equals("1")) {
				//GUI part
				gc = createGUI(stage);
				launchGUI(1, false);
				timeline.play();
				stage.show();
			}
			//Launch of space probe
			else if (input.equals("2")) {
				//Choice = 1 launches the binary search for the angle to reach Titan, choice = 2 launches a very crude direct approximation of the angle to reach Titan
				int choice = 1;

				System.out.println("Launching space probe shooting module");

				//Approach 1: Trying out an angle, then adjusting to the left or right to get closer to Titan
				if (choice == 1) {
					System.out.println("Launching binary search angle calibration");
					double launch_angle = 259;
					boolean crashedTitan = false;
					double DISTANCE_SUN_PLUTO = 5906376272e3;
					double angleChange = 1;
					int previousMove = 0;
					int numberIterations = 0;
					double spaceProbeAngle = 0;
					double titanAngle  = 0;
					while (! crashedTitan) {
						System.out.println("Iteration #" + numberIterations);

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
							System.out.println(num + " iterations needed to end simulation");
						}

						//If the space probe crashed on Titan, we are done
						if (planets[9].equals(spaceProbe.getCrashedPlanet())) {
							crashedTitan = true;
							System.out.println("\n\n\nIteration #" + numberIterations + "A launch angle of " + launch_angle + " degrees got the spaceProbe to Titan.");
						}
						else {
							//If we crash into a planet, we print it to the console, then reset the variable in the spaceProbe
							if (spaceProbe.getCrashedPlanet() != null)
								System.out.println("Crashed in " + spaceProbe.getCrashedPlanet().getName());
							spaceProbe.resetCrashedPlanet();

							//Then, we compute the angles
							spaceProbeAngle = spaceProbe.getPosition().angle(planetPositions[0]);
							titanAngle = planets[9].getPosition().angle(planetPositions[0]);
							//Both angles should now be between 0 and 360 degrees
							System.out.printf("Arrival SpaceProbeAngle: %f, Titan angle: %f\n", spaceProbeAngle, titanAngle);

							/* IDEA !!! Instead of doing spaceProbeAngle > titanAngle, try to see modulo 360 if it is closer to the lef or the right
							*/

							if (spaceProbeAngle > titanAngle) {
								//the spaceProbe is to the right of Titan (or in the exact opposite direction)
								//Thus, we make the angle smaller

								//If the previous angle change was the other way, we can reduce the angleChange by half
								if (previousMove == 1) {
									System.out.println("Move == -1: \nSpaceProbe angle: " + spaceProbeAngle + ", Titan angle: " + titanAngle + ", \n   The angle should be between " + (launch_angle-angleChange) + " and " + launch_angle);
									angleChange = angleChange/2;
								}

								//Then, apply the angleChange to the angle
								launch_angle -= angleChange;

								//And set the latest made move to -1 (decreasing the angle, turning to the right)
								previousMove = -1;
							}
							else if (spaceProbeAngle < titanAngle){
								//The spaceProbe is to the left of Titan
								//Thus, we make the angle bigger

								//If the previous angle change was the other way, we can reduce the angle by half
								if (previousMove == -1) {
									System.out.println("Move == 1: \nSpaceProbe angle: " + spaceProbeAngle + ", Titan angle: " + titanAngle + ", \n   The angle should be between " + launch_angle + " and " + (launch_angle+angleChange));
									angleChange = angleChange/2;
								}

								//Then, apply the angleChange to the angle
								launch_angle += angleChange;

								//And set the latest made move to +1 (increasing the angle, turning to the left)
								previousMove = 1;
							}
							else {
								crashedTitan = true;
								System.out.println("SpaceProbeAngle = titanAngle !");
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
					System.out.println("\n\nSpaceProbeAngle: " + spaceProbeAngle);
					System.out.println("TitanAngle: " + titanAngle);
					System.out.println("'Optimal' launch angle: " + launch_angle);

					//Reset the spaceProbe with the new launch_angle
					spaceProbe = SpaceProbe.createSpaceProbeWithStartingAngle("SpaceProbe", voyagerMass, planets[3].getPosition(), averageVelocitySpaceProbe, planetRadius[3], launch_angle);

					//Launch the simulation
				  gc = createGUI(stage);
					launchGUI(1, true);			//compute next position after 10 milliseconds, and also consider the spaceProbe
					timeline.play();
					stage.show();
				}
				// Approach 2: BIGGEST APPROXIMATION EVER
				else if (choice == 2) {
					//Compute the current distance between the Earth and Titan (- the earth's radius, since we start on the surface of Earth)
					//Problem ! we do not know the position of Titan once we reach it !! Only the current
					double dist = new Vector2D(planets[3].getPosition()).distance(planets[9].getPosition()) - planetRadius[3];

					//Calculate the time needed using averageVelocitySpaceProbe
					//Problem ! The velocity will not stay constant !!!
					long time = (long)(dist/averageVelocitySpaceProbe);

					System.out.println("Expected time to get to Titan with velocity " + averageVelocitySpaceProbe + ", is: " + getTimeAsString(time));

					//save the current position of earth
					Vector2D earthPos = new Vector2D(planets[3].getPosition());

					//Compute the position of the planets after that time
					for (int i = 0; i < time/DELTA_T; i ++) {
						update(DELTA_T, true);
					}

					//Compute the direction vector from the previous Earth position to Titan
					Vector2D direction = new Vector2D(planets[9].getPosition()).subtract(earthPos).normalize();
					//Then, multiply it by the averageVelocitySpaceProbe to get the starting Velocity of the spaceProbe
					Vector2D initialVelocity = new Vector2D(direction).multiply(averageVelocitySpaceProbe);

					//and the initial position of the space Probe (being the position of Earth + a vector in the direction of Titan, with length equal to the radius of Earth)
					Vector2D initialPosition = new Vector2D(earthPos).add(direction.multiply(planetRadius[3]));

					//Create the SpaceProbe
					spaceProbe = new SpaceProbe("SpaceProbe", voyagerMass, initialPosition, initialVelocity);

					//Then, see if it would manage to get to Titan
					//Reset the solar System
					createSolarSystem();

					//Also show the second simulation with the Space probe
					gc = createGUI(stage);
					launchGUI(1, true);
					timeline.play();
					stage.show();
				}
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
}
