public class Vector2D {
	protected double x;
	protected double y;

	public Vector2D () {
	}

	public Vector2D (double x, double y) {
		this.x = x;
		this.y = y;
	}

	public Vector2D (double x, double y, double z) {
		this(x, y);
	}

	public Vector2D (Vector2D a) {
		this.x = a.x;
		this.y = a.y;
	}

	public Vector2D add (Vector2D a) {
		if (a != null) {
			x += a.x;
			y += a.y;
		}
		return this;
	}

	public Vector2D subtract (Vector2D a) {
		if (a != null) {
			x -= a.x;
			y -= a.y;
		}
		return this;
	}

	public Vector2D multiply (double f) {
		x *= f;
		y *= f;

		return this;
	}

	public Vector2D divide (double f) {
		if (f != 0) {
			x /= f;
			y /= f;
		}

		return this;
	}

	public double length () {
		return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
	}

	public Vector2D normalize() {
		return multiply(1.0/length());
	}

	public double distance (Vector2D a) {
		return Math.sqrt(Math.pow(x-a.x, 2) + Math.pow(y-a.y, 2));
	}

	/** Returns the angle in which this vector points

		@return the angle in degrees, between 0 and 360
	*/
	public double angle () {
		//Adjust the x and y positions to compute the angle with respect to the start position of Earth
		double angle = Math.toDegrees(Math.atan((x-GUI.planetPositions[3].x)/(y-GUI.planetPositions[3].y)));
		//Math.atan() returns a value between -PI/2 and PI/2, toDegrees will be between -90 and 90 degrees
		//So then, if the real value should be between 90 and 270, we need to add 180 to its value
		//If the real value is in the range [90, 270], then x is negative
		//if (x < 0) angle += 180;

		//Finally, to make the final value be between 0 and 360, we add 90 to the value
		angle += 90;

		return angle;
	}

	public String toString () {
		return "x=" + x + ", y=" + y;
	}
}
