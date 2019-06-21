/**
*/
public class CoordinatesTransformer {
	private double scale;
	private Vector2D posModifier = new Vector2D();
	private double xModifier;
	private double yModifier;

	public double getScale() {
		return scale;
	}

	public void setScale (double scale) {
		this.scale = scale;
	}

	public Vector2D getModifiedPos () {
		return new Vector2D(posModifier);
	}

	public void setModifiedPos (Vector2D newModifiedPos) {
		posModifier = new Vector2D(newModifiedPos);
	}

	public Vector2D modelToOtherPosition (Vector2D pos) {
		//First, retrieve the position modifier
		Vector2D otherModelPos = new Vector2D(posModifier);

		//And add the actual position scaled to the corresponding value,
		Vector2D scaledPos = new Vector2D(getScaledPos(pos));
		//which we flip the y-position of, since the negative y goes up and a positive y down
		scaledPos.setY(- scaledPos.getY());

		//Sum the two, and return the result
		otherModelPos.add(scaledPos);

		return otherModelPos;
	}

	public Vector2D getScaledPos (Vector2D unscaledPos) {
		return new Vector2D(unscaledPos).divide(scale);
	}
}
