public class LandingModule {
	
	private double weight; // weight of landing module
	private Thruster[] thrusters; // The five different vectors
	private Vector position; // Vector with x and y position
	private final double GRAVITYTITAN = 1.352; // gravity acceleration on titan
	private double angle; // Landing module's angle of rotation
	private double accelMain; // Acceleration of main thruster
	private double orbit; // distance of rocket in orbit and ground on titan

	public LandingModule(double weight, Vector position) {
		this.weight = weight;
		this.position = position;
		thrusters = new Thrusters()[];
	}
	
	public void useMainThruster() {
		position.setX(position.getX() + accelMain * Math.sin(angle));
		position.setY(position.getY() + accelMain * Math.cos(angle));
	}
	
	public void useSideThruster(int side) {
		
	}

	public void simple() {
		int modulevelocity = GRAVITYTITAN*t;
	}
	
	public boolean hasLanded() {
		if (position.getY() == 0) {
			return true;
		}
		return false;
	}
	
}
