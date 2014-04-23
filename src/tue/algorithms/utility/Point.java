package tue.algorithms.utility;

/**
 * <p>
 * Utility class that represents a point (x, y).
 * </p>
 * <p>
 * This class is unmutable.
 * </p>
 * @author Martijn
 */
public class Point {
	
	/**
	 * Create a point with coordinates (x, y).
	 * @param x The x-coordinate of the point.
	 * @param y The y-coordinate of the point.
	 */
	public Point(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	/**
	 * The x-coordinate of the point.
	 */
	private final int x;
	/**
	 * The y-coordinate of the point.
	 */
	private final int y;
	
	/**
	 * Get the x-coordinate of the point.
	 * @return The x-coordinate as an integer.
	 */
	public int getX() {
		return x;
	}
	
	/**
	 * Get the y-coordinate of the point.
	 * @return The y-coordinate as an integer.
	 */
	public int getY() {
		return y;
	}
	
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
	public Point add(int x, int y) {
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
	public Point subtract(int x, int y) {
		return add(-x, -y);
	}
	
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
	public Line getLineFromHereToPoint(int x, int y) {
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
	public Line getLineFromPointToHere(int x, int y) {
		return new Line(x, y, getX(), getY());
	}
	
}
