public class Planet {
	private String name;
	private double mass;
		//in kg
	private Vector2D position;
	private Vector2D velocity;
	private Vector2D acceleration;

	public Planet (String name, double mass, Vector2D startingPos, Vector2D startingVelocity) {
		this.name = name;
		this.mass = mass;
		this.position = startingPos;
		this.velocity = startingVelocity;
	}
	
	public String getName() {
		return name;
	}
	
	public double getMass() {
		return mass;
	}
	
	public Vector2D getPosition () {
		return position;
	}
	
	public Vector2D getVelocity() {
		return velocity;
	}
	
	public Vector2D getAcceleration() {
		return acceleration;
	}
	
	public void addGToAcceleration (Planet other) {
		//Compute the gravitational force
		//	Make a unity vector in the correct direction
		Vector2D direction = new Vector2D(this.position);
		direction.subtract(other.getPosition()).normalize().multiply(-1);
		
		//	Compute the distance between the two planets
		double dist = this.position.distance(other.getPosition());
		
		//	Calculate the gravitational force
		Vector2D force = new Vector2D(direction);
		force.multiply(GUI.G).multiply(mass).multiply(other.getMass()).divide(dist * dist);
		
		//From that, add the corresponding acceleration to the planet's acceleration
		acceleration.add(force.divide(mass));
	}
	
	public void resetAcceleration() {
		acceleration = new Vector2D();
	}
	
	public void updateVelocityAndPosition (double time) {
		//Save the velocity before applying the acceleration
		Vector2D oldVelocity = new Vector2D(this.velocity);
		
		//Calculate the final velocity
		velocity.add(new Vector2D(acceleration).multiply(time));
		
		//Update location with the averageVelocity
		position.add((new Vector2D(oldVelocity).add(velocity)).divide(2.0));
	}
}