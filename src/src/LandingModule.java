import javafx.scene.shape.Rectangle;

public interface LandingModule {
    //Landing simulation
        //Simulates the whole landing, from start until landing
    public void updateModule (final double timestep);
        //Simulates a single iteration of the landing
    public void updateModuleOneIteration (final double timestep);

    //Check if the landing module has landed
        //Returns a boolean expressing whether the landing Module has landed or not
    public boolean hasLanded ();

    //Getters
        //Getter for the position of the landing module
    public Vector2D getPosition ();
        //Getter for the velocity of the landing module
    public Vector2D getVelocity ();
        //Getter for the angle of the landing module
    public double getAngle();
        //Getter for the Rectangle object
    public Rectangle getRectangle();

    //Setter
    public void setRectangle(Rectangle rect);
}
