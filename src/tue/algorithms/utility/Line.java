package tue.algorithms.utility;

/**
 * <p>
 * Utility class that represents a line from one point to another point.
 * </p>
 * <p>
 * This class is unmutable.
 * </p>
 * @author Martijn
 */
public class Line {
	
	/* -- START Private final fields -- */
	
	/**
	 * The x-coordinate of the point the line starts at.
	 */
	private final int x1;
	/**
	 * The y-coordinate of the point the line starts at.
	 */
	private final int y1;
	/**
	 * The x-coordinate of the point the line ends at.
	 */
	private final int x2;
	/**
	 * The y-coordinate of the point the line ends at.
	 */
	private final int y2;
	
	/* -- END Private final fields -- */
	
	/* -- START Constructors -- */
	
	/**
	 * Create a line from the point (x1, y1) to the point (x2, y2).
	 * @param x1 The x-coordinate of the point the line starts at.
	 * @param y1 The y-coordinate of the point the line starts at.
	 * @param x2 The x-coordinate of the point the line ends at.
	 * @param y2 The y-coordinate of the point the line ends at.
	 */
	public Line(int x1, int y1, int x2, int y2) {
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
	}
	
	/**
	 * Create a line from point1 to point2.
	 * @param point1 The point the line starts at.
	 * @param point2 The point the line ends at.
	 */
	public Line(Point point1, Point point2) {
		this(point1.getX(), point1.getY(), point2.getX(), point2.getY());
	}
	
	/* -- END Constructors -- */
	
	/* -- START Public getters for private fields -- */
	
	/**
	 * Get the x-coordinate of the point the line starts at.
	 * @return The x-coordinate as an integer.
	 */
	public int getX1() {
		return x1;
	}
	
	/**
	 * Get the y-coordinate of the point the line starts at.
	 * @return The y-coordinate as an integer.
	 */
	public int getY1() {
		return y1;
	}
	
	/**
	 * Get the x-coordinate of the point the line ends at.
	 * @return The x-coordinate as an integer.
	 */
	public int getX2() {
		return x2;
	}
	
	/**
	 * Get the y-coordinate of the point the line ends at.
	 * @return The y-coordinate as an integer.
	 */
	public int getY2() {
		return y2;
	}
	
	/* -- END Public getters for private fields -- */
	
	/* -- START Getters for point representations of private fields -- */
	
	/**
	 * Get the point the line starts at.
	 * @return The point.
	 */
	public Point getPoint1() {
		return new Point(x1, y1);
	}
	
	/**
	 * Get the point the line ends at.
	 * @return The point.
	 */
	public Point getPoint2() {
		return new Point(x2, y2);
	}
	
	/* -- END Getters for point representations of private fields -- */
	
	/* -- START Manipulation method to invert line -- */
	
	/**
	 * Get a line with the direction inverted: the created line will start where this line ends, and end where this line starts.
	 * @return The point.
	 */
	public Line invertDirection() {
		return new Line(x2, y2, x1, y1);
	}
	
	/* -- END Manipulation method to invert line -- */
	
	/* -- START Override equals(), hashCode() and toString() -- */
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Line) {
			Line other = (Line) obj;
			return (other.getX1() == getX1() && other.getY1() == getY1() && other.getX2() == getX2() && other.getY2() == getY2());
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return getX1()+getY1()*getY1()+getX2()*getX2()*getX2()+getY2()*getY2()*getY2()*getY2();
	}
	
	@Override
	public String toString() {
		return super.toString()+"["
				+"x1="+getX1()+", "
				+"y1="+getY1()+", "
				+"x2="+getX2()+", "
				+"y2="+getY2()
				+"]";
	}
	
	/* -- END Override equals(), hashCode() and toString() -- */
	
}
