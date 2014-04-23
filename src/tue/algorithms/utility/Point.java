package tue.algorithms.utility;

/**
 * <p>
 * Utility class that represents a point (x, y).
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
	private final float x;
	/**
	 * The y-coordinate of the point.
	 */
	private final float y;
	
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
	 */
	public float getX() {
		return x;
	}
	
	/**
	 * Get the y-coordinate of the point.
	 * @return The y-coordinate as an float.
	 */
	public float getY() {
		return y;
	}
	
	/* -- END Public getters for private fields -- */
	
	/* -- START Manipulation methods for adding and subtracting points -- */
	
	/**
	 * Get a point that is the vector sum of this point and the given point.
	 * @param point The point to add to this point.
	 * @return The sum vector point.
	 */
	public Point add(Point point) {
		return add(point.getX(), point.getY());
	}
	
	/**
	 * Get a point that is the vector sum of this point and the given point (x, y).
	 * @param x The x-coordinate of the point to add to this point.
	 * @param y The y-coordinate of the point to add to this point.
	 * @return The sum vector point.
	 */
	public Point add(float x, float y) {
		return new Point(getX()+x, getY()+y);
	}
	
	/**
	 * Get a point that is the vector result of this point when subtracted the given point.
	 * @param point The point to subtract from this point.
	 * @return The subtraction vector point.
	 */
	public Point subtract(Point point) {
		return subtract(point.getX(), point.getY());
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
	
	/* -- END Manipulation methods for adding and subtracting points -- */
	
	/* -- START Methods to get the line between points -- */
	
	/**
	 * Get a line starting at this point, ending at the given point.
	 * @param point The point where the line should end.
	 * @return The line.
	 */
	public Line getLineFromHereToPoint(Point point) {
		return getLineFromHereToPoint(point.getX(), point.getY());
	}
	
	/**
	 * Get a line starting at this point, ending at the given point (x, y).
	 * @param x The x-coordinate of the point where the line should end.
	 * @param y The y-coordinate of the point where the line should end.
	 * @return The line.
	 */
	public Line getLineFromHereToPoint(float x, float y) {
		return new Line(getX(), getY(), x, y);
	}
	
	/**
	 * Get a line starting at the given point, ending at this point.
	 * @param point The point where the line should start.
	 * @return The line.
	 */
	public Line getLineFromPointToHere(Point point) {
		return getLineFromPointToHere(point.getX(), point.getY());
	}
	
	/**
	 * Get a line starting at the given point (x, y), ending at this point.
	 * @param x The x-coordinate of the point where the line should start.
	 * @param y The y-coordinate of the point where the line should start.
	 * @return The line.
	 */
	public Line getLineFromPointToHere(float x, float y) {
		return new Line(x, y, getX(), getY());
	}
	
	/* -- END Methods to get the line between points -- */
	
	/* -- START Override equals(), hashCode() and toString() -- */
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Point) {
			Point other = (Point) obj;
			return (other.getX() == getX() && other.getY() == getY());
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return (int) ((getX()*10000)+(getY()*10000)*(getY()*10000));
	}
	
	@Override
	public String toString() {
		return super.toString()+"["
				+"x="+getX()+", "
				+"y="+getY()
				+"]";
	}
	
	/* -- END Override equals(), hashCode() and toString() -- */
	
}
