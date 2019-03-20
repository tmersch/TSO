public class SolarSystem extends Canvas {
	public SolarSystem () {
	}
	
	private void draw () {
		double width = getWidth();
		double height = getHeight();
		
		GraphicsContext gc = getGraphicsContext2D();
		gc.clearRect(0, 0, width, height);
	}
	
	public double prefWidth (double height) {
		return getWidth();
	}
	
	public double prefHeight (double width) {
		return getHeight();
	}
}