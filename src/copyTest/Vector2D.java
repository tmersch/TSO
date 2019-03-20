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
	
	public String toString () {
		return "x=" + x + ", y=" + y;
	}
}