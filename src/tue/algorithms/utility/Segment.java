package tue.algorithms.utility;

/**
 * <p>
 * A segment that connects two nodes.
 * </p>
 * <p>
 * This class is a subclass of Line.
 * </p>
 * <p>
 * This class is immutable.
 * </p>
 * @author Martijn
 */
public class Segment extends Line {
	
	protected final Node node1;
	protected final Node node2;

	/**
	 * Create a segment from node1 to node2.
	 * @param node1 The node the segment starts at.
	 * @param node2 The node the segments ends at.
	 */
	public Segment(Node node1, Node node2) {
		super(node1, node2);
		this.node1 = node1;
		this.node2 = node2;
	}
	
	/**
	 * Create a segment from the node with id node1id to the node with id node2id.
	 * @param node1id The id of the node the segment starts at.
	 * @param node2id The id of the node the segment ends at.
	 * @deprecated
	 */
	public Segment(int node1id, int node2id) {
		this(Node.getById(node1id), Node.getById(node2id));
	}
	
	/* -- START Public getters for private fields -- */
	
	/**
	 * Get the id of the node the segment starts at.
	 * @return The id as an integer.
	 */
	public int getNode1Id() {
		return node1.id;
	}
	
	/**
	 * Get the id of the node the segment ends at.
	 * @return The id as an integer.
	 */
	public int getNode2Id() {
		return node2.id;
	}
	
	/* -- END Public getters for private fields -- */
	
	/* -- START Getters for node representations of private fields -- */
	
	/**
	 * Get the node the segment starts at.
	 * @return The node.
	 */
	public Node getNode1() {
		return node1;
	}
	
	/**
	 * Get the node the segment ends at.
	 * @return The node.
	 */
	public Node getNode2() {
		return node2;
	}

	/**
	 * @param node
	 * @return Whether the segment has {@code node} as one of its end points.
	 */
	public boolean isEndPoint(Node node) {
		return node1.id == node.id || node2.id == node.id;
	}

	/**
	 * @param node
	 * @return The other endpoint.
	 * @pre {@code node} is one of the endpoints.
	 */
	public Node getOtherEndpoint(Node node) {
		return node.id != node1.id ? node1 : node2;
	}
	
	/* -- END Getters for node representations of private fields -- */
	
	/* -- START Manipulation method to invert line -- */

	/**
	 * Get a view of this line such that the line "ends" at {@code node}
	 * @param node
	 * @return The segment
	 */
	public Segment endAt(Node node) {
		return originAt(getOtherEndpoint(node));
	}

	/** 
	 * Get a view of this line such that the line "starts" at {@code node}.
	 *
	 * @param node The point that is used as start point
	 * @pre node must be one of the endpoints of this line, i.e.
	 *  {@code isEndPoint(node) == true}
	 */
	public Segment originAt(Node node) {
		if (node1.x == node.x && node1.y == node.y) {
			return this;
		} else {
			return invertDirection();
		}   
	}
	
	/**
	 * Get a segment with the direction inverted: the created segment will start where this segment ends, and end where this segment starts.
	 * @return The segment with inverted direction.
	 */
	@Override
	public Segment invertDirection() {
		return new Segment(node2, node1);
	}
	
	/* -- END Manipulation method to invert line -- */
	
	/* -- START Override equals(), hashCode() and toString() -- */
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Segment) {
			Segment other = (Segment) obj;
			return other.node1.id == node1.id && other.node2.id == node2.id;
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return node1.id * 12000 + node2.id;
	}
	
	@Override
	public String toString() {
		return super.toString() + "["
			+ "id1=" + node1.id + ", "
			+ "x1=" + node1.x + ", "
			+ "y1=" + node1.y + ", "
			+ "id2=" + node2.id + ", "
			+ "x2=" + node2.x + ", "
			+ "y2=" + node2.y
			+ "]";
	}
	
	/* -- END Override equals(), hashCode() and toString() -- */
	/**
	 * Use minimally!!!
	 */
	public OpSegment toOpSegment() {
		return new OpSegment(node1.toOpNode(), node2.toOpNode());
	}
	
}
