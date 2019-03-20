import java.lang.*;

public class Vector2D{

    protected double x;
    protected double y;

    public Vector2D(){
    }

    public Vector2D(double x, double y){
        this.x = x;
        this.y = y;
    }
	
	public Vector2D(double x, double y, double z){
        this(x, y);
    }
	
	public Vector2D (Vector2D a) {
		x = a.x;
		y = a.y;
	}

    public Vector2D add(Vector2D a){
		x += a.x;
		y += a.y;

        return this;
    }

    public Vector2D substract(Vector2D a){
		x -= a.x;
		y -= a.y;

        return this;
    }

    public Vector2D multiply(double a){
        x *= a;
        y *= a;

        return this;
    }

    public Vector2D divide(double a){
        if(a != 0){
            x /= a;
            y /= a;
        }
		
        return this;
    }
	
	public Vector2D normalize() {
		this.divide(1/length());
		
		return this;
	}
	
	public double length() {
		return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
	}

    public double distanceFrom(Vector2D a){
        double first = Math.pow((this.x - a.x), 2);
        double second = Math.pow((this.y - a.y), 2);

        double distance = Math.sqrt(first + second);
        return distance;
    }

    public void printVector2D(){
        System.out.println("x = " + x + ", y = " + y);
    }
}
