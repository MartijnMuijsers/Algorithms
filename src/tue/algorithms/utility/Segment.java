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
	
	/* -- START Private final fields -- */
	
	/**
	 * The id of the node the segment starts at.
	 */
	protected final int node1id;
	/**
	 * The id of the node the segment ends at.
	 */
	protected final int node2id;
	
	/* -- END Private final fields -- */
	
	/* -- START Constructors -- */
	
	/**
	 * Create a segment from node1 to node2.
	 * @param node1 The node the segment starts at.
	 * @param node2 The node the segments ends at.
	 */
	public Segment(Node node1, Node node2) {
		super(node1, node2);
		this.node1id = node1.getId();
		this.node2id = node2.getId();
	}
	
	/**
	 * Create a segment from the node with id node1id to the node with id node2id.
	 * @param node1id The id of the node the segment starts at.
	 * @param node2id The id of the node the segment ends at.
	 */
	public Segment(int node1id, int node2id) {
		this(Node.getById(node1id), Node.getById(node2id));
	}
	
	/* -- END Constructors -- */
	
	/* -- START Public getters for private fields -- */
	
	/**
	 * Get the id of the node the segment starts at.
	 * @return The id as an integer.
	 */
	public int getNode1Id() {
		return node1id;
	}
	
	/**
	 * Get the id of the node the segment ends at.
	 * @return The id as an integer.
	 */
	public int getNode2Id() {
		return node2id;
	}
	
	/* -- END Public getters for private fields -- */
	
	/* -- START Getters for node representations of private fields -- */
	
	/**
	 * Get the node the segment starts at.
	 * This method relies on the node cache to retrieve the node with node1id.
	 * @return The node.
	 */
	public Node getNode1() {
		return Node.getById(node1id);
	}
	
	/**
	 * Get the node the segment ends at.
	 * This method relies on the node cache to retrieve the node with node2id.
	 * @return The node.
	 */
	public Node getNode2() {
		return Node.getById(node2id);
	}

	/**
	 * @param node
	 * @return Whether the segment has {@code node} as one of its end points.
	 */
	public boolean isEndPoint(Node node) {
		int nodeId = node.getId();
		return node1id == nodeId || node2id == nodeId;
	}

	/**
	 * @param node
	 * @return The other endpoint.
	 * @pre {@code node} is one of the endpoints.
	 */
	public Node getOtherEndpoint(Node node) {
		return node.getId() != node1id ? getNode1() : getNode2();
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
		if (x1 == node.getX() && y1 == node.getY()) {
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
		return new Segment(getNode2Id(), getNode1Id());
	}
	
	/* -- END Manipulation method to invert line -- */
	
	/* -- START Override equals(), hashCode() and toString() -- */
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Segment) {
			Segment other = (Segment) obj;
			return (other.getNode1Id() == getNode1Id() && other.getNode2Id() == getNode2Id());
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return getNode1Id() * 12000 + getNode2Id();
	}
	
	@Override
	public String toString() {
		return super.toString() + "["
			+ "id1=" + getNode1Id() + ", "
			+ "x1=" + getX1() + ", "
			+ "y1=" + getY1() + ", "
			+ "id2=" + getNode2Id() + ", "
			+ "x2=" + getX2() + ", "
			+ "y2=" + getY2()
			+ "]";
	}
	
	/* -- END Override equals(), hashCode() and toString() -- */
	
	/**
	 * Use minimally!!!
	 */
	public OpSegment toOpSegment() {
		return new OpSegment(getNode1().toOpNode(), getNode2().toOpNode());
	}
	
}
