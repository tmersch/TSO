/** This class represents a moon with the same properties as a planet, see CelestialBody.java for more details
  */
public class Moon extends CelestialBody {
    /** Default fully parametric constructor with all the parameters of the superclass' constructor
      */
    public Moon (String name, double mass, Vector2D startingPos, Vector2D startingVelocity, double radius) {
        super(name, mass, startingPos, startingVelocity, radius);
    }

    @Override
    public Vector2D getLabelPositionModifier () {
        //Compute the position modifier of the text such that the text appears left of the moon
        double distanceBetweenTextAndPlanet = 5;
        Vector2D pos = new Vector2D(-GUIV2.PLANET_RADIUS-text.getLayoutBounds().getWidth()-distanceBetweenTextAndPlanet, 0);

        return pos;
    }
}
