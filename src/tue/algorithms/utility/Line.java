package tue.algorithms.utility;

/**
 * <p>
 * Utility class that represents a line from one point to another point.
 * </p>
 * <p>
 * This class is immutable.
 * </p>
 * @author Martijn
 */
public class Line {
	
	/* -- START Private final fields -- */
	
	/**
	 * The x-coordinate of the point the line starts at.
	 */
	protected final float x1;
	/**
	 * The y-coordinate of the point the line starts at.
	 */
	protected final float y1;
	/**
	 * The x-coordinate of the point the line ends at.
	 */
	protected final float x2;
	/**
	 * The y-coordinate of the point the line ends at.
	 */
	protected final float y2;
	
	/* -- END Private final fields -- */
	
	/* -- START Constructors -- */
	
	/**
	 * Create a line from the point (x1, y1) to the point (x2, y2).
	 * @param x1 The x-coordinate of the point the line starts at.
	 * @param y1 The y-coordinate of the point the line starts at.
	 * @param x2 The x-coordinate of the point the line ends at.
	 * @param y2 The y-coordinate of the point the line ends at.
	 */
	public Line(float x1, float y1, float x2, float y2) {
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
	 * @return The x-coordinate as an float.
	 */
	public float getX1() {
		return x1;
	}
	
	/**
	 * Get the y-coordinate of the point the line starts at.
	 * @return The y-coordinate as an float.
	 */
	public float getY1() {
		return y1;
	}
	
	/**
	 * Get the x-coordinate of the point the line ends at.
	 * @return The x-coordinate as an float.
	 */
	public float getX2() {
		return x2;
	}
	
	/**
	 * Get the y-coordinate of the point the line ends at.
	 * @return The y-coordinate as an float.
	 */
	public float getY2() {
		return y2;
	}
	
	/* -- END Public getters for private fields -- */
	
	/* -- START Getters for point representations of private fields -- */
	
	/**
	 * Get the point the line starts at.
	 * @return The point.
	 */
	public Point getPoint1() {
		return new Point(getX1(), getY1());
	}
	
	/**
	 * Get the point the line ends at.
	 * @return The point.
	 */
	public Point getPoint2() {
		return new Point(getX2(), getY2());
	}
	
	/* -- END Getters for point representations of private fields -- */
	
	/* -- START Getters for useful information -- */
	
	/**
	 * Get the length of the line.
	 * @return The length as a float.
	 */
	public float length() {
		return (float) Math.sqrt((getX2()-getX1())*(getX2()-getX1())+(getY2()-getY1())*(getY2()-getY1()));
	}
	
	/* -- END Getters for useful information -- */
	
	/* -- START Manipulation method to invert line -- */
	
	/**
	 * Get a line with the direction inverted: the created line will start where this line ends, and end where this line starts.
	 * @return The line with inverted direction.
	 */
	public Line invertDirection() {
		return new Line(getX2(), getY2(), getX1(), getY1());
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
		return (int) ((getX1()*10000)+(getY1()*10000)*(getY1()*10000)+(getX2()*10000)*(getX2()*10000)*(getX2()*10000)+(getY2()*10000)*(getY2()*10000)*(getY2()*10000)*(getY2()*10000));
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
