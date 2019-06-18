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

	/*
	public double getModifiedX () {
		return xModifier;
	}

	public void setModifiedX (double modifiedX) {
		xModifier = modifiedX;
	}

	public double getModifiedY () {
		return yModifier;
	}

	public void setModifiedY (double modifiedY) {
		yModifier = modifiedY;
	}
	*/

	public Vector2D modelToOtherPosition (Vector2D pos) {
		return new Vector2D(posModifier).add(getScaledPos(pos));
		//return new Vector2D(xModifier + getScaledSize(pos.x), yModifier + getScaledSize(pos.y));
	}

	public Vector2D getScaledPos (Vector2D unscaledPos) {
		return new Vector2D(unscaledPos).divide(scale);
	}

	/*
	public double getScaledSize (double distance) {
		return distance / scale;
	}
	*/
}
