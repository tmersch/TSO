/**
*/
public class CoordinatesTransformer {
	private double scale;
	private double xModifier;
	private double yModifier;

	public double getScale() {
		return scale;
	}

	public void setScale (double scale) {
		this.scale = scale;
	}

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

	public Vector2D modelToOtherPosition (Vector2D pos) {
		return new Vector2D(xModifier + getScaledSize(pos.x), yModifier + getScaledSize(pos.y));
	}

	public double getScaledSize (double distance) {
		return distance / scale;
	}
}
