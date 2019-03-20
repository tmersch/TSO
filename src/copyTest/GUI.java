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

/** GUI representing the solar system using javafx
*/
public class GUI extends Application {
	//Width and height of the window
	private double canvasWidth = 0;		// ------------------------------------------------------------
	private double canvasHeight = 0;	// ------------------------------------------------------------
	private Vector2D dragPosStart;

	//Number of seconds between each update
	public static final double DELTA_T = 60;
	//scaling factor (number of meters in AU divided by 100)
	public static final double SCALE = 1495978707;
	//radius of the planets
	public static final double PLANET_RADIUS = 10;
	//height of the part at the bottom
	private static final int BOTTOM_AREA_HEIGHT = 100;
	//the gravitation constant
	public static final double G = 6.67300E-11;

	//Seconds conversion
	private static final int SEC_IN_MINUTE = 60;
  private static final int SEC_IN_HOUR = SEC_IN_MINUTE * 60;
  private static final int SEC_IN_DAY = SEC_IN_HOUR * 24;
  private static final int SEC_IN_YEAR = 31556926;
  private long elapsedSeconds = 0;

	//Array for the planets
	private Planet[] planets;
	private static final String[] planetNames = {				"Sun", 						"Mercury", 																			"Venus", 																			"Earth", 																			"Mars", 																		"Jupiter", 																			"Saturn", 																			"Uranus", 																			"Neptune"};
	private static final double[] planetMasses = {			1.9885e30, 					3.302e23, 																			4.8685e24, 																			5.97219e24, 																		6.4171e23, 																		1.8981*Math.pow(10, 27), 															5.6834e26, 																			8.6813e25, 																			1.02413e26};
		//in kg
	private static Vector2D[] planetPositions = {			new Vector2D(0, 0, 0), 		new Vector2D(-5.872125676818924e10, -5.804127334840319e9, 4.912664883118753e9), 	new Vector2D(-1.455889118207544e10, -1.076999192416582e11, -6.376171699620709e8), 	new Vector2D(-1.486342755241585e11, 8.198905701620353e9, -7.620074742892757e4), 	new Vector2D(3.124195290400189e10, 2.298057334393066e11, 4.048651637918636e9), 	new Vector2D(-2.399320956706447e11, -7.598655149344369e11, 8.524600084986627e9), 	new Vector2D(3.516934988142877e11, -1.462721993695447e12, 1.142816489475083e10), 	new Vector2D(2.521978348972803e12, 1.568378087179974e12, -2.683449169055068e10), 	new Vector2D(4.344340662627413e12, -1.085497713760720e12, -7.777825569894868e10)};
		//in meters
	private static final Vector2D[] planetVelocities = {	new Vector2D(0, 0, 0), 		new Vector2D(-5.341847047712075e3, -4.638410041355678e4, -3.300161136111004e3), 	new Vector2D(3.447098250886419e4, -4.827880810826475e3, -2.055483232947198e3), 		new Vector2D(-2.117483315641365e3, -2.984619930248100e4, 2.683290615177469e-1), 	new Vector2D(-2.309314310690604e4, 5.322963673708609e3, 6.781705506964339e2), 	new Vector2D(1.231426726093322e4, -3.320854863157825e3, -2.617042437691823e2), 		new Vector2D(8.874360410574640e3, 2.226908002447438e3, -3.922282843554251e2), 		new Vector2D(-3.634103497558742e3, 5.462106665107330e3, 6.718779593146884e1), 		new Vector2D(1.294989801625765e3, 5.303327243239019e3, -1.398313168317220e2)};
		//in meters/secs

	private CoordinatesTransformer coordinates = new CoordinatesTransformer();

	private FPSCounter fps = new FPSCounter();
	private Label timeLabel;
  private Label fpsLabel;
  private Label scaleLabel;


	/** Main method
	*/
	public static void main (String[] args) {
		launch(args);
	}

	public void start (Stage stage) {
		//Initialize the planets
		createPlanets();

		//CoordinateTransformer
		coordinates.setScale(SCALE);
		coordinates.setOriginXForOther(500);
		coordinates.setOriginYForOther(500);
		GraphicsContext gc = createGUI(stage);
		Timeline timeline = new Timeline();
		timeline.setCycleCount(Timeline.INDEFINITE);
		KeyFrame kf = new KeyFrame(
				Duration.millis(0.1),
				new EventHandler<ActionEvent>() {
					public void handle(ActionEvent ae) {
						updateFrame(gc);
					}
				});
		timeline.getKeyFrames().add(kf);
		timeline.play();
		stage.show();
	}

	/** Draw a single frame
	*/
	private void updateFrame(GraphicsContext gc) {
		this.canvasWidth = gc.getCanvas().getWidth();
		this.canvasHeight = gc.getCanvas().getHeight();
		gc.clearRect(0, 0, canvasWidth, canvasHeight);

		for (Planet p : planets) {
			double otherX = coordinates.modelToOtherX(p.getPosition().x);
			double otherY = coordinates.modelToOtherY(p.getPosition().y);

			//Draw circles
			gc.setFill(Color.BLACK);
			gc.fillOval(otherX - PLANET_RADIUS, otherY - PLANET_RADIUS, PLANET_RADIUS * 2, PLANET_RADIUS * 2);

			//Draw the labels
			Text text = new Text(p.getName());
			gc.fillText(p.getName(), otherX - (text.getLayoutBounds().getWidth() / 2), otherY - PLANET_RADIUS - (text.getLayoutBounds().getHeight() / 2));
		}

		update(DELTA_T);
		timeLabel.setText(getElapsedTimeAsString());
	}

	private GraphicsContext createGUI (Stage stage) {
		BorderPane border = new BorderPane();
		createTimeLabel();
		HBox hbox = createHBox();
		border.setBottom(hbox);
		Canvas canvas = createCanvas();
		border.setCenter(canvas);
		stage.setTitle("NBody simulation");
		Scene scene = new Scene(border);
		stage.setScene(scene);
		stage.setMaximized(true);

		// Bind canvas size to stack pane size
		canvas.widthProperty().bind(stage.widthProperty());
		canvas.heightProperty().bind(stage.heightProperty().subtract(BOTTOM_AREA_HEIGHT));
		return canvas.getGraphicsContext2D();
	}

	private Canvas createCanvas () {
		Canvas canvas = new Canvas();

		// dragging of map
        canvas.setOnDragDetected((event) -> this.dragPosStart = new Vector2D(event.getX(), event.getY(), 0));
        canvas.setOnMouseDragged((event) -> {
            if (this.dragPosStart != null) {
                Vector2D dragPosCurrent = new Vector2D(event.getX(), event.getY(), 0);
                dragPosCurrent.subtract(this.dragPosStart);
                dragPosStart = new Vector2D(event.getX(), event.getY(), 0);
                coordinates.setOriginXForOther(coordinates.getOriginXForOther() + dragPosCurrent.x);
                coordinates.setOriginYForOther(coordinates.getOriginYForOther() + dragPosCurrent.y);
            }
        });
        canvas.setOnMouseReleased((event) -> this.dragPosStart = null);

        // zooming (scaling)
        canvas.setOnScroll((event) -> {
            if (event.getDeltaY() > 0) {
                coordinates.setScale(coordinates.getScale() * 0.9);
            } else {
                coordinates.setScale(coordinates.getScale() * 1.1);
            }
        });

		return canvas;
	}

	private HBox createHBox () {
        HBox hbox = new HBox();
        hbox.setPadding(new Insets(15, 12, 15, 12));
        hbox.setSpacing(10);   // Gap between nodes
        hbox.setStyle("-fx-background-color: #336699;");
        hbox.setFillHeight(true);
        hbox.getChildren().add(this.timeLabel);
        hbox.getChildren().add(this.fpsLabel);
        hbox.getChildren().add(this.scaleLabel);
        return hbox;
    }

	/** Initialize the Planet objects
	*/
	protected void createPlanets() {
		for (int i = 0; i < planetNames.length; i ++) {
			planets[i] = new Planet(planetNames[i], planetMasses[i], planetPositions[i], planetVelocities[i]);
		}
	}

	/** We assume that createPlanets has been called before
		This method updates the acceleration of the planets, then the velocity and location
	*/
	public void update (double time) {
		//Reset the acceleration of all planets
		for (int i = 0; i < planets.length; i ++) {
			planets[i].resetAcceleration();
		}

		//Add gravitational force from each body to each body
		for (int i = 0; i < planets.length; i ++) {
			for (int j = i+1; j < planets.length; j ++) {
				planets[i].addGToAcceleration(planets[j]);
			}
		}

		//Update the velocity and position of each body
		for (int i = 0; i < planets.length; i ++) {
			planets[i].updateVelocityAndPosition(time);
		}

		//Increment the seconds
		elapsedSeconds += time;
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
}
