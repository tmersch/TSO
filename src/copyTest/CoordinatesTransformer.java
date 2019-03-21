public class CoordinatesTransformer {
	private double scale;
	private double originXForOther;
	private double originYForOther;

	public double getScale() {
		return scale;
	}

	public void setScale (double scale) {
		this.scale = scale;
	}

	public double getOriginXForOther () {
		return originXForOther;
	}

	public void setOriginXForOther (double originXForOther) {
		this.originXForOther = originXForOther;
	}

	public double getOriginYForOther () {
		return originYForOther;
	}

	public void setOriginYForOther (double originYForOther) {
		this.originYForOther = originYForOther;
	}

	public Vector2D modelToOtherPosition (Vector2D pos) {
		return new Vector2D(this.originXForOther + getModelToOtherDistance(pos.x), this.originYForOther + getModelToOtherDistance(pos.y));
	}
	/*
	public Vector2D modelVelocity(Vector2D v) {

	}
	*/

	public double getModelToOtherDistance (double distance) {
		return distance / scale;
	}
}
