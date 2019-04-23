/** Class representing the dervatives of the position and velocities, in x and y
*/
public class Derivative {
  protected double dx, dy, dv_x, dv_y;

  public Derivative (double dx, double dy, double dv_x, double dv_y) {
    this.dx = dx;
    this.dy = dy;
    this.dv_x = dv_x;
    this.dv_y = dv_y;
  }

  public Derivative (Vector2D dPos, Vector2D dVel) {
    this.dx = dPos.x;
    this.dy = dPos.y;
    this.dv_x = dVel.x;
    this.dv_y = dVel.y;
  }
}
