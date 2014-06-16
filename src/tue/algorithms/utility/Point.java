package tue.algorithms.utility;

/**
 * <p>
 * Utility class that represents a point (x, y).
 * </p>
 * <p>
 * This class is also perfectly usable to represent a vector.
 * </p>
 * <p>
 * This class is immutable.
 * </p>
 * @author Martijn
 */
public class Point {
	
	/* -- START Private final fields -- */
	
	/**
	 * The x-coordinate of the point.
	 */
	public final float x;
	/**
	 * The y-coordinate of the point.
	 */
	public final float y;
	
	/* -- END Private final fields -- */
	
	/* -- START Constructors -- */
	
	/**
	 * Create a point with coordinates (x, y).
	 * @param x The x-coordinate of the point.
	 * @param y The y-coordinate of the point.
	 */
	public Point(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	/* -- END Constructors -- */
	
	/* -- START Public getters for private fields -- */
	
	/**
	 * Get the x-coordinate of the point.
	 * @return The x-coordinate as an float.
	 * @deprecated
	 */
	public float getX() {
		return x;
	}
	
	/**
	 * Get the y-coordinate of the point.
	 * @return The y-coordinate as an float.
	 * @deprecated
	 */
	public float getY() {
		return y;
	}
	
	/* -- END Public getters for private fields -- */
	
	/* -- START Getters for useful information -- */
	
	/**
	 * Get the length of this point when treated as a vector (distance to the origin).
	 * @return The length as a float.
	 */
	public float length() {
		return (float) Math.sqrt(this.x * this.x + this.y * this.y);
	}
	
	/**
	 * Get the angle between this point, when treated as a vector, and the x-axis.
	 * An angle of 0 means this point points to the right.
	 * An angle of Math.PI/2 means this point points upwards.
	 * If the point points to the left, then the angle is Math.PI.
	 * The angle can also be -Math.PI, when the point is slightly (epsilon) below
	 *  the negative X-axis.
	 * @return The angle as a double in the range [-Math.PI, Math.PI].
	 */
	public double getAngle() {
		return Math.atan2(this.y, this.x);
	}
	
	/**
	 * Get the angle between this point and another point.
	 * @param point The other point.
	 * @return The angle as a double.
	 */
	public double getAngleTo(Point point) {
		return getLineFromHereToPoint(point).getAngle();
	}
	
	/* -- END Getters for useful information -- */
	
	/* -- START Manipulation methods for adding and subtracting points -- */
	
	/**
	 * Get a point that is the vector sum of this point and the given point.
	 * @param point The point to add to this point.
	 * @return The sum vector point.
	 */
	public Point add(Point point) {
		return add(point.x, point.y);
	}
	
	/**
	 * Get a point that is the vector sum of this point and the given point (x, y).
	 * @param x The x-coordinate of the point to add to this point.
	 * @param y The y-coordinate of the point to add to this point.
	 * @return The sum vector point.
	 */
	public Point add(float x, float y) {
		return new Point(this.x + x, this.y + y);
	}
	
	/**
	 * Get a point that is the vector result of this point when subtracted the given point.
	 * @param point The point to subtract from this point.
	 * @return The subtraction vector point.
	 */
	public Point subtract(Point point) {
		return subtract(point.x, point.y);
	}
	
	/**
	 * Get a point that is the vector result of this point when subtracted the given point (x, y).
	 * @param x The x-coordinate of the point to subtract from this point.
	 * @param y The y-coordinate of the point to subtract from this point.
	 * @return The subtraction vector point.
	 */
	public Point subtract(float x, float y) {
		return add(-x, -y);
	}
	
	/* -- START Method to get the distance between points -- */
	
	/**
	 * Get the distance between this point and another.
	 * @param point The other point.
	 * @return The Euclidian distance as a float.
	 */
	public float getDistanceTo(Point point) {
		return this.subtract(point).length();
	}
	
	/* -- END Method to get the distance between points -- */
	
	/* -- START Methods to get the line between points -- */
	
	/**
	 * Get a line starting at this point, ending at the given point.
	 * @param point The point where the line should end.
	 * @return The line.
	 */
	public Line getLineFromHereToPoint(Point point) {
		return getLineFromHereToPoint(point.x, point.y);
	}
	
	/**
	 * Get a line starting at this point, ending at the given point (x, y).
	 * @param x The x-coordinate of the point where the line should end.
	 * @param y The y-coordinate of the point where the line should end.
	 * @return The line.
	 */
	public Line getLineFromHereToPoint(float x, float y) {
		return new Line(this.x, this.y, x, y);
	}
	
	/**
	 * Get a line starting at the given point, ending at this point.
	 * @param point The point where the line should start.
	 * @return The line.
	 */
	public Line getLineFromPointToHere(Point point) {
		return getLineFromPointToHere(point.x, point.y);
	}
	
	/**
	 * Get a line starting at the given point (x, y), ending at this point.
	 * @param x The x-coordinate of the point where the line should start.
	 * @param y The y-coordinate of the point where the line should start.
	 * @return The line.
	 */
	public Line getLineFromPointToHere(float x, float y) {
		return new Line(x, y, this.x, this.y);
	}
	
	/* -- END Methods to get the line between points -- */
	
	/* -- START Override equals(), hashCode() and toString() -- */
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Point) {
			Point other = (Point) obj;
			return (other.x == this.x && other.y == this.y);
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return (int) ((this.x * 10000) + (this.y * 10000) * (this.y * 10000));
	}
	
	@Override
	public String toString() {
		return super.toString() + "["
			+ "x=" + this.x + ", "
			+ "y=" + this.y
			+ "]";
	}
	
	/* -- END Override equals(), hashCode() and toString() -- */
	
}
