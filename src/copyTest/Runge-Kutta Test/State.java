/** Class representing the state of the planet, the position and velocity in x and y
*/
public class State {
  protected double x, y, v_x, v_y;

  public State (double x, double y, double v_x, double v_y) {
    this.x = x;
    this.y = y;
    this.v_x = v_x;
    this.v_y = v_y;
  }

  public State (Vector2D pos, Vector2D vel) {
    this.x = pos.x;
    this.y = pos.y;
    this.v_x = vel.x;
    this.v_y = vel.y;
  }
}
