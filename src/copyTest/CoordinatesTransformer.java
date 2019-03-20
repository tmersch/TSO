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
	
	public double modelToOtherX (double x) {
		return this.originXForOther + getModelToOtherDistance(x);
	}
	
	public double modelToOtherY (double y) {
		return this.originYForOther + getModelToOtherDistance(y);
	}
	
	public double getModelToOtherDistance (double distance) {
		return distance / scale;
	}
}