/** Transforms coordinates to scale them and translate them
  * Used to translate positions from the solar system simulation or the landing to pixel positions on the GUI
  */
public class CoordinatesTransformer {
	private double scale;
	private Vector2D posModifier = new Vector2D();
	private boolean invertYPosition;

	/** Full parametric constructor
	  *
	  * @param scale the scale used to scale the given positions
	  * @param posModifier the position translation applied to the scaled positions
	  * @param invertYPosition a boolean for inverting the y position
	  */
	public CoordinatesTransformer (double scale, Vector2D posModifier, boolean invertYPosition) {
		this.scale = scale;
		this.posModifier = new Vector2D(posModifier);
		this.invertYPosition = invertYPosition;
	}

	/** Additional constructor with one less parameter than the full constructor: invertYPosition
	  * calls the full parametric constructor with default value for invertYPosition
	  */
	public CoordinatesTransformer (double scale, Vector2D posModifier) {
		this(scale, posModifier, true);
	}

	/** Additional constructor with no parameters: neither scale, nor posModifier, nor invertYPosition
	  * calls the other additional constructor with default values for scale and posModifier
	  */
	public CoordinatesTransformer () {
		this(1, new Vector2D());
	}

	/** Return the scale that the coordinates transformer is set to
	  */
	public double getScale() {
		return scale;
	}

	/** Set the scale to a certain value
	  */
	public void setScale (double scale) {
		this.scale = scale;
	}

	/** Return the position modifier of this coordinates transformer
	  */
	public Vector2D getModifiedPos () {
		return new Vector2D(posModifier);
	}

	/** Set the position modifier to a certain value
	  */
	public void setModifiedPos (Vector2D newModifiedPos) {
		posModifier = new Vector2D(newModifiedPos);
	}

	/** Return the boolean representing whether the scaled position should have the y-position inverted or not
	  */
	public boolean getInvertYPosition () {
		return invertYPosition;
	}

	/** Set the boolean for whether to invert the y-position or not
	  */
	public void setInvertYPosition (boolean invertYPosition) {
		this.invertYPosition = invertYPosition;
	}

	/** Modify the given position to scale it by "scale" and translate it by "posModifier"
	  */
	public Vector2D modelToOtherPosition (Vector2D pos) {
		//First, retrieve the position modifier
		Vector2D otherModelPos = new Vector2D(posModifier);

		//And add the actual position scaled to the corresponding value,
		Vector2D scaledPos = new Vector2D(getScaledPos(pos));

		//Invert the y-position  if the boolean says to
		if (invertYPosition) {
			scaledPos.setY(- scaledPos.getY());
		}

		//Sum the two, and return the result
		otherModelPos.add(scaledPos);

		return otherModelPos;
	}

	/** Return a position scaled using the set scale
	  */
	public Vector2D getScaledPos (Vector2D unscaledPos) {
		return new Vector2D(unscaledPos).divide(scale);
	}
}
