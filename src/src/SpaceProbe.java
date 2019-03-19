public class SpaceProbe {
	private double mass;
	private double[] pos;
	private double[] velocities;
	private double[] acceleration = {0, 0};
	
	/** Constructor for SpaceProbe.
		It has parameters mass, which represents the mass of the space probe (constant, because it has no engines that could burn up fuel),
			startingPos[] which represents the starting position of the SpaceProbe, 
			and startingV[] which represents the starting velocities (on x and y)
	*/
	public SpaceProbe (double mass, double[] startingPos, double startingV) {
		this.mass = mass;
		pos = new double[startingPos.length];
		pos[0] = startingPos[0];
		pos[1] = startingPos[1];
		velocities = new double[startingV.length];
		velocities[0] = startingV[0];
		velocities[1] = startingV[1];
	}
	
	/** Computes the forces exerted on the space probe by the planets (and later on also moons ?)
			then saves it in acceleration
	*/
	public void updatePosition () {
		for (int i = 0; i < Titan.planets.length; i ++) {
			computeGOfPlanet(Titan.planets[i]);
		}
	}
	
	/** Computes the gravitational force exerted on a space probe by the given planet
			using Newton's law of universal gravity
	*/
	public void computeGOfPlanet(Planet p) {
		for (int i = 0; i < acceleration.length; i ++) {
			acceleration[i] += Titan.G * (mass * p.getMass()) * ((p.getPosition()[i] - pos[i])/Math.pow(Math.abs(p.getPosition()[i] - pos[i]), 3));
		}
	}
}