import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
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
import java.util.Arrays;
import java.util.Random;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/** GUI representing the solar system using javafx
*/
public class GUI extends Application {
	//Width and height of the window
	private double canvasWidth = 0;		// ------------------------------------------------------------
	private double canvasHeight = 0;	// ------------------------------------------------------------
	private Vector2D dragPosStart;

	//Number of seconds between each update
	public static final double DELTA_T = 60 * 30;
	//scaling factor (number of meters in AU divided by 100)
	public static final double SCALE = 5e9;//1495978707.0*4;
	//radius of the planets
	public static final double PLANET_RADIUS = 2;
	//height of the part at the bottom
	private static final int TOP_AREA_HEIGHT = 100;
	//the gravitation constant
	public static final double G = 6.67300E-11;

	//Seconds conversion
	private static final int SEC_IN_MINUTE = 60;
  private static final int SEC_IN_HOUR = SEC_IN_MINUTE * 60;
  private static final int SEC_IN_DAY = SEC_IN_HOUR * 24;
  private static final int SEC_IN_YEAR = 31556926;
  private long elapsedSeconds = 0;

	//Array for the planets
	protected static Planet[] planets;
	private static final String[] planetNames = {				"Sun", 						"Mercury", 																			"Venus", 																			"Earth", 																			"Mars", 																		"Jupiter", 																			"Saturn", 																			"Uranus", 																			"Neptune", 										"Titan"};
	private static final double[] planetMasses = {			1.9885e30, 					3.302e23, 																			4.8685e24, 																			5.97219e24, 																		6.4171e23, 																		1.8981e27, 															5.6834e26, 																			8.6813e25, 																			1.02413e26, 					1.34553e23};
		//in kg
	protected static final double[] planetRadius = {	695700e3, 					2440e3, 																			6051.84e3, 																			6371.01e3, 																		3389.92e3, 																			71492e3, 																		60268e3, 																		25559e3, 																		24766e3, 																		2575.5e3};
		//in meters
	private static final Vector2D[] planetPositions = {			new Vector2D(0, 0, 0), 		new Vector2D(-5.872125676818924e10, -5.804127334840319e9, 4.912664883118753e9), 	new Vector2D(-1.455889118207544e10, -1.076999192416582e11, -6.376171699620709e8), 	new Vector2D(-1.486342755241585e11, 8.198905701620353e9, -7.620074742892757e4), 	new Vector2D(3.124195290400189e10, 2.298057334393066e11, 4.048651637918636e9), 	new Vector2D(-2.399320956706447e11, -7.598655149344369e11, 8.524600084986627e9), 	new Vector2D(3.516934988142877e11, -1.462721993695447e12, 1.142816489475083e10), 	new Vector2D(2.521978348972803e12, 1.568378087179974e12, -2.683449169055068e10), 	new Vector2D(4.344340662627413e12, -1.085497713760720e12, -7.777825569894868e10), new Vector2D(3.509094646023610e11, -1.461827053014912e12, 1.104487392229486e10)};
		//in meters
	private static final Vector2D[] planetVelocities = {	new Vector2D(0, 0, 0), 		new Vector2D(-5.341847047712075e3, -4.638410041355678e4, -3.300161136111004e3), 	new Vector2D(3.447098250886419e4, -4.827880810826475e3, -2.055483232947198e3), 		new Vector2D(-2.117483315641365e3, -2.984619930248100e4, 2.683290615177469e-1), 	new Vector2D(-2.309314310690604e4, 5.322963673708609e3, 6.781705506964339e2), 	new Vector2D(1.231426726093322e4, -3.320854863157825e3, -2.617042437691823e2), 		new Vector2D(8.874360410574640e3, 2.226908002447438e3, -3.922282843554251e2), 		new Vector2D(-3.634103497558742e3, 5.462106665107330e3, 6.718779593146884e1), 		new Vector2D(1.294989801625765e3, 5.303327243239019e3, -1.398313168317220e2), new Vector2D(4.602663714929883e3, -5.834636275449419e2, 1.481088959791306e3)};
		//in meters/secs

	private static SpaceProbe spaceProbe;
	private static final double voyagerMass = 800;
		//in kg
	private static final double averageVelocitySpaceProbe = 3e8;
		//in meters/secs

	private static final int[] initialTime = {18, 3, 2019};

	private CoordinatesTransformer coordinates = new CoordinatesTransformer();

	private Label timeLabel;


	/** Main method
	*/
	public static void main (String[] args) {
		launch(args);
	}

	public void start (Stage stage) {
		//Initialize the planets
		createSolarSystem();

		//CoordinateTransformer
		coordinates.setScale(SCALE);
		coordinates.setOriginXForOther(700);
		coordinates.setOriginYForOther(350);

		Scanner s = new Scanner(System.in);
		String input = "";

		while ((! input.equals("1")) && (! input.equals("2"))) {
			System.out.println("Do you want to see the GUI or shoot the space probe to Titan ? (enter 1 for the GUI, 2 if something else)");
			input = s.next();

			if (input.equals("1")) {
				//GUI part
				GraphicsContext gc = createGUI(stage);
				Timeline timeline = new Timeline();
				timeline.setCycleCount(Timeline.INDEFINITE);
				KeyFrame kf = new KeyFrame(
					Duration.millis(1),
					new EventHandler<ActionEvent>() {
						public void handle(ActionEvent ae) {
							updateFrame(gc);
						}
				});
				timeline.getKeyFrames().add(kf);
				timeline.play();
				stage.show();
			}
			else if (input.equals("2")) {
				//Space probe launch
				//Compute the current distance between the Earth and Titan (- the earth's radius, since we start on the surface of Earth)
				//Problem ! we do not know the position of Titan once we reach it !! Only the current
				double dist = new Vector2D(planets[3].getPosition()).distance(planets[9].getPosition()) - planetRadius[3];
				System.out.println("Distance between earth and " + planets[9].getName() + " is: " + dist);

				//Calculate the time needed using averageVelocitySpaceProbe
				//Problem ! The velocity will not stay constant !!!
				double time = dist/averageVelocitySpaceProbe;
				System.out.println("Time needed with a velocity of " + averageVelocitySpaceProbe + " is " + time + " secs: " + getTimeAsString(time));

				//save the current position of earth
				Vector2D earthPos = new Vector2D(planets[3].getPosition());
				System.out.println("Saved old position of Earth: " + earthPos);

				System.out.println("Working out the simulation ... gonna take " + time + " secs, and " + (time/DELTA_T) + " steps");

				//Compute the position of the planets after that time
				for (int i = 0; i < time/DELTA_T; i ++) {
					update(DELTA_T);
					if (i % 100000 == 0) {
						System.out.println("Reached step " + i + "/" + time/DELTA_T);
					}
				}

				System.out.println("Ended working out the simulation after " + time + " secs, and " + (time/DELTA_T) + " steps");

				//Compute the direction vector from the previous Earth position to Titan
				Vector2D direction = new Vector2D(planets[9].getPosition()).subtract(earthPos).normalize();
				//Then, multiply it by the averageVelocitySpaceProbe to get the starting Velocity of the spaceProbe
				Vector2D initialVelocity = new Vector2D(direction).multiply(averageVelocitySpaceProbe);

				//and the initial position of the space Probe (being the position of Earth + a vector in the direction of Titan, with length equal to the radius of Earth)
				Vector2D initialPosition = new Vector2D(earthPos).add(direction.multiply(planetRadius[3]));

				//Create the SpaceProbe
				spaceProbe = new SpaceProbe(voyagerMass, initialPosition, initialVelocity);

				//Then, see if it would manage to get to Titan
				//Reset the solar System
				createSolarSystem();

				System.out.println("Created spaceProbe and reset Solar system ...");

				//While the space probe has not crashed in a planet and is not further away from the sun than pluto, update the position of the planets and of the space probe
				while ((spaceProbe.DidNotCrash()) && (spaceProbe.getPosition().distance(planets[0].getPosition()) > 5906376272e3)) {
					updateWithSpaceProbe(DELTA_T);
				}

				//Then, after the loop ended, tell the result
				if (spaceProbe.DidNotCrash()) {
					System.out.println("The space probe got lost in space, probably outside of the solar system");
				}
				else {
					System.out.println("The space probe crashed on " + spaceProbe.crashedPlanet.getName());
				}
			}
		}
	}

	/** Draw a single frame
	*/
	protected void updateFrame(GraphicsContext gc) {
		this.canvasWidth = gc.getCanvas().getWidth();
		this.canvasHeight = gc.getCanvas().getHeight();
		gc.clearRect(0, 0, canvasWidth, canvasHeight);

		for (Planet p : planets) {
			Vector2D otherPosition = coordinates.modelToOtherPosition(p.getPosition());

			//Draw circles
			gc.setFill(Color.BLACK);
			gc.fillOval(otherPosition.x - PLANET_RADIUS, otherPosition.y - PLANET_RADIUS, PLANET_RADIUS * 2, PLANET_RADIUS * 2);

			//Draw the labels
			Text text = new Text(p.getName());
			gc.fillText(p.getName(), otherPosition.x - (text.getLayoutBounds().getWidth() / 2), otherPosition.y - PLANET_RADIUS - (text.getLayoutBounds().getHeight() / 2));
		}

		update(DELTA_T);
		timeLabel.setText(getElapsedTimeAsString());
	}

	private GraphicsContext createGUI (Stage stage) {
		//Create the borderPane
		BorderPane border = new BorderPane();
		//Create the label that shows the time
		createTimeLabel();

		//Create the horizontal box that will contain the time label
		HBox hbox = createHBox();
		//add it to top of the borderpane
		border.setTop(hbox);

		//Create the canvas
		Canvas canvas = new Canvas();
		//and set it in the center of the borderpane
		border.setCenter(canvas);
		Scene scene = new Scene(border);

		//Set the title, scene of the stage and setMaximized
		stage.setTitle("NBody simulation");
		stage.setScene(scene);
		stage.setMaximized(true);

		// Bind canvas size to stack pane size
		canvas.widthProperty().bind(stage.widthProperty());
		canvas.heightProperty().bind(stage.heightProperty().add(TOP_AREA_HEIGHT));
		return canvas.getGraphicsContext2D();
	}

	private HBox createHBox () {
		//Create the horizontal box
    HBox hbox = new HBox();
    hbox.setPadding(new Insets(15, 12, 15, 12));
    hbox.setFillHeight(true);
    hbox.getChildren().add(this.timeLabel);
    return hbox;
  }

	/** Initialize the Planet and the Moon objects
	*/
	public void createSolarSystem() {
		planets = new Planet[planetNames.length];
		for (int i = 0; i < planetNames.length; i ++) {
			planets[i] = new Planet(planetNames[i], planetMasses[i], planetPositions[i], planetVelocities[i]);
		}
	}

	/** We assume that createPlanets has been called before
		This method updates the acceleration of the planets, then the velocity and location
	*/
	public void update (double time) {
		//Reset the acceleration of all planets and moons
		for (int i = 0; i < planets.length; i ++) {
			planets[i].resetAcceleration();
		}

		//Add gravitational force from each body to each body
		for (int i = 0; i < planets.length; i ++) {
			for (int j = 0; j < planets.length; j ++) {
				if (i != j) {
					planets[i].addGToAcceleration(planets[j]);
					//planets[j].addGToAcceleration(planets[i]);
				}
			}
		}

		//Update the velocity and position of each body
		for (int i = 1; i < planets.length; i ++) {
			planets[i].updateVelocityAndPosition(time);
		}

		//Increment the seconds
		elapsedSeconds += time;
	}

	/** We assume that createPlanets has been called before
		This method updates the acceleration of the planets, then the velocity and location
		but also the acceleration, velocity and location of the spaceProbe
	*/
	public void updateWithSpaceProbe (double time) {
		//Reset the acceleration of all planets and moons
		for (int i = 0; i < planets.length; i ++) {
			planets[i].resetAcceleration();
		}
		spaceProbe.resetAcceleration();

		//Add gravitational force from each body to each body
		for (int i = 0; i < planets.length; i ++) {
			for (int j = 0; j < planets.length; j ++) {
				if (i != j) {
					planets[i].addGToAcceleration(planets[j]);
					//planets[j].addGToAcceleration(planets[i]);
				}
			}

			spaceProbe.addGToAcceleration(planets[i]);
		}

		//Update the velocity and position of each body
		for (int i = 1; i < planets.length; i ++) {
			planets[i].updateVelocityAndPosition(time);
		}
		spaceProbe.updateVelocityAndPosition(time);
	}

	private void createTimeLabel() {
		timeLabel = new Label();
		timeLabel.setPrefSize(500, 20);	// -----------------------------------------------------------------
	}

	private String getElapsedTimeAsString() {
		long years = elapsedSeconds / SEC_IN_YEAR;
    long days = (elapsedSeconds % SEC_IN_YEAR) / SEC_IN_DAY;
    long hours = ( (elapsedSeconds % SEC_IN_YEAR) % SEC_IN_DAY) / SEC_IN_HOUR;
    long minutes = ( ((elapsedSeconds % SEC_IN_YEAR) % SEC_IN_DAY) % SEC_IN_HOUR) / SEC_IN_MINUTE;
    long seconds = ( ((elapsedSeconds % SEC_IN_YEAR) % SEC_IN_DAY) % SEC_IN_HOUR) % SEC_IN_MINUTE;
    return String.format("Years:%08d, Days:%03d, Hours:%02d, Minutes:%02d, Seconds:%02d", years, days, hours, minutes, seconds);
	}

	private String getTimeAsString (double time) {
		long years = (long)(time / SEC_IN_YEAR);
    long days = (long)((time % SEC_IN_YEAR) / SEC_IN_DAY);
    long hours = (long)(((time % SEC_IN_YEAR) % SEC_IN_DAY) / SEC_IN_HOUR);
    long minutes = (long)((((time % SEC_IN_YEAR) % SEC_IN_DAY) % SEC_IN_HOUR) / SEC_IN_MINUTE);
    long seconds = (long)((((time % SEC_IN_YEAR) % SEC_IN_DAY) % SEC_IN_HOUR) % SEC_IN_MINUTE);
    return String.format("Years:%08d, Days:%03d, Hours:%02d, Minutes:%02d, Seconds:%02d", years, days, hours, minutes, seconds);
	}
}
