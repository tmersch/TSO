/** Class representing the state of the planet, the position and velocity in x and y
*/
public class State {
  protected Vector2D position, velocity;
<<<<<<< HEAD

  public State () {
    this(0,0,0,0);
  }
=======
>>>>>>> a91f5e604ebeb0c7823c88970edd5e61f8231a8a

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
<<<<<<< HEAD
    velocity = new Vector2D(other.velocity);
=======
    velocity = new Vector2D(velocity);
>>>>>>> a91f5e604ebeb0c7823c88970edd5e61f8231a8a
  }
}
