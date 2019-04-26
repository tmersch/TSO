/** Class representing the state of the planet, the position and velocity in x and y
*/
public class State {
  protected Vector2D position, velocity;

  public State () {
    this(0,0,0,0);
  }

  public State (double x, double y, double v_x, double v_y) {
    position = new Vector2D(x, y);
    velocity = new Vector2D(v_x, v_y);
  }

  public State (Vector2D pos, Vector2D vel) {
    position = new Vector2D(pos);
    velocity = new Vector2D(vel);
  }

  public State (State other) {
    position = new Vector2D(other.position);
    velocity = new Vector2D(other.velocity);
  }
}
