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
        //Getter for the mass of the landing module
    public double getMass();
        //Getter for the burnt fuel
    public double getBurntFuelMass();
        //Returns the prize of the fuel burnt so far
    public double getPrize();

    //Setter
    public void setRectangle(Rectangle rect);
}
