/** Class representing the dervatives of the position and velocities, in x and y
*/
public class Derivative {
  protected Vector2D dPosition, dVelocity;

  public Derivative (double dx, double dy, double dv_x, double dv_y) {
    dPosition = new Vector2D(dx, dy);
    dVelocity = new Vector2D(dv_x, dv_y);
  }

  public Derivative (Vector2D dPos, Vector2D dVel) {
    dPosition = new Vector2D(dPos);
    dVelocity = new Vector2D(dVel);
  }
}
