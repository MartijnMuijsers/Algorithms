package tue.algorithms.utility;

/**
 * Deprecated. Use Segment instead of Line.
 */
@Deprecated
public class Line extends Segment {
	/**
	 * Create a line from the point (x1, y1) to the point (x2, y2).
	 * @deprecated
	 */
	@Deprecated
	public Line(float x1, float y1, float x2, float y2) {
		super(new Node(Node.FAKE_NODE_ID, x1, y1), new Node(Node.FAKE_NODE_ID, x2, y2));
	}
	
	/**
	 * Create a line from point1 to point2.
	 * @param point1 The point the line starts at.
	 * @param point2 The point the line ends at.
	 */
	public Line(Node point1, Node point2) {
		super(point1, point2);
	}
	
	/**
	 * Deprecated. Use .getNode1() instead and change "Point" to "Node"
	 * Get the point the line starts at.
	 * @return The point.
	 * @deprecated
	 */
	@Deprecated
	public Point getPoint1() {
		return new Point(getX1(), getY1());
	}
	
	/**
	 * Deprecated. Use .getNode2() instead and change "Point" to "Node"
	 * Get the point the line ends at.
	 * @return The point.
	 * @deprecated
	 */
	@Deprecated
	public Point getPoint2() {
		return new Point(getX2(), getY2());
	}
	
}
