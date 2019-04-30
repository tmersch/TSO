/** Class representing the dervatives of the position and velocities, in x and y
*/
public class Derivative {
  protected Vector2D dPosition, dVelocity;

  public Derivative () {
    this(0, 0, 0, 0);
  }

  public Derivative (double dx, double dy, double dv_x, double dv_y) {
    dPosition = new Vector2D(dx, dy);
    dVelocity = new Vector2D(dv_x, dv_y);
  }

  public Derivative (Vector2D dPos, Vector2D dVel) {
    dPosition = new Vector2D(dPos);
    dVelocity = new Vector2D(dVel);
  }

  public Derivative (Derivative other) {
    dPosition = new Vector2D(other.dPosition);
    dVelocity = new Vector2D(other.dVelocity);
  }

  /** This method adds another derivative to this one
  */
  public Derivative add (Derivative other) {
    dPosition.add(other.dPosition);
    dVelocity.add(other.dVelocity);

    return this;
  }

  /** This method multiplies this derivative by a double value
  */
  public Derivative multiply (double c) {
    dPosition.multiply(c);
    dVelocity.multiply(c);

    return this;
  }

  /** This method divides this derivative by a double value
  */
  public Derivative divide (double c) {
    dPosition.divide(c);
    dVelocity.divide(c);

    return this;
  }
}
