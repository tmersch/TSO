import java.lang.*;

public class Vector{

    protected double x;
    protected double y;
    protected double z;

    public Vector(){
    }

    public Vector(double x, double y, double z){
        this.x = x;
        this.y = y;
        this.z = z;
    }
	
	public Vector (Vector a) {
		x = a.x;
		y = a.y;
		z = a.z;
	}

    public Vector add(Vector a){
		x += a.x;
		y += a.y;
		z += a.z;

        return this;
    }

    public Vector substract(Vector a){
		x -= a.x;
		y -= a.y;
		z -= a.z;

        return this;
    }

    public Vector multiply(double a){
        x *= a;
        y *= a;
        z *= a;

        return this;
    }

    public Vector divide(double a){
        if(a != 0){
            x /= a;
            y /= a;
            z /= a;
        }
		
        return this;
    }
	
	public Vector normalize() {
		this.divide(1/length());
		
		return this;
	}
	
	public double length() {
		return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2));
	}

    public double distanceFrom(Vector a){
        double first = Math.pow((this.x - a.x), 2);
        double second = Math.pow((this.y - a.y), 2);
        double third = Math.pow((this.z - a.z), 2);

        double distance = Math.sqrt(first + second + third);
        return distance;
    }

    public void printVector(){
        System.out.println("x = " + x + ", y = " + y + ", z = " + z);
    }
}
