public class Vector2D {
	protected double x;
	protected double y;

	public Vector2D () {
		this(0, 0);
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

	/** Constructs a vector representing a certain angle (in degrees), of total length 1
	*/
	public Vector2D (double angle) {
		this(Math.cos(Math.toRadians(angle)), Math.sin(Math.toRadians(angle)));
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
	public double angle (Vector2D origin) {
		//Adjust the x and y positions to compute the angle with respect to the given Vector2D's position
		double acos = Math.toDegrees(Math.acos((x-origin.x)/(this.distance(origin))));
			// value between 0 and Pi, so in degrees, between 0 and 180
		double asin = Math.toDegrees(Math.asin((y-origin.y)/(this.distance(origin))));
			//value between -Pi/2 and Pi/2, so in degrees, between -90 and 90
			//NOTE !!! I put a "-" in the expression of arcsine to get a "normal" value for it (positive if the object is higher than the reference (the sun in this case))

		double resultAngle;
		if (acos >= 0 && acos <= 90 && asin >= 0) {
			resultAngle = acos;						//First quadrant
		}
		else if (acos > 90) {						//Second or third quadrant
			resultAngle = acos;						//Second quadrant

			if (asin < 0) {
				resultAngle = acos + 90;			//Third quadrant
			}
		}
		else {
			resultAngle = asin + 360;					//Fourth quadrant
		}

		return resultAngle;
	}

	/** Returns the value of the double x variable
	*/
	public double getX() {
		return x;
	}

	/** Returns the value of the double y value
	*/
	public double getY() {
		return y;
	}

	/** Sets y to a new value
	  */
	public void setX(double newX) {
		x = newX;
	}

	/** Sets x to a new value
	  */
	public void setY(double newY) {
		y = newY;
	}

	public String toString () {
		return "x=" + x + ", y=" + y;
	}
}
