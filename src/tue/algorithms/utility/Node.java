package tue.algorithms.utility;

import java.util.HashMap;

/**
 * <p>
 * A node that has an id and a point location.
 * </p>
 * <p>
 * You should not create two different node instances with equal id,
 * but different coordinates,
 * in the same implementation.
 * </p>
 * <p>
 * This class is a subclass of Point.
 * </p>
 * <p>
 * This class is immutable.
 * </p>
 * <p>
 * The static part of this class provides a cache for constructed nodes,
 * so that nodes are retrievable by their id after their construction.
 * </p>
 * @author Martijn
 */
public class Node extends Point {

	/** ID of first node */
	final static public int MINIMAL_NODE_ID = 1;
	
	/* -- START Private final fields -- */
	
	/**
	 * The id of the node.
	 */
	protected final int id;
	
	/* -- END Private final fields -- */
	
	/* -- START Constructors -- */
	
	/**
	 * Create a node with the given id and coordinates (x, y).
	 * @param id The id of the node.
	 * @param x The x-coordinate of the node.
	 * @param y The y-coordinate of the node.
	 */
	public Node(int id, float x, float y) {
		super(x, y);
		assert id >= MINIMAL_NODE_ID;
		this.id = id;
		addToNodeCache(this);
	}
	
	/**
	 * Create a node with the given id and the coordinates of the given point.
	 * @param id The id of the node.
	 * @param point The point as coordinates of the node.
	 */
	public Node(int id, Point point) {
		this(id, point.getX(), point.getY());
	}
	
	/* -- END Constructors -- */
	
	/* -- START Public getters for private fields -- */
	
	/**
	 * Get the id of the node.
	 * @return The id as an integer.
	 */
	public int getId() {
		return id;
	}
	
	/* -- END Public getters for private fields -- */
	
	/* -- START Methods for conversion -- */
	
	/**
	 * Get a point representation of this node.
	 * @return The point
	 */
	public Point getPoint() {
		return new Point(getX(), getY());
	}
	
	/* -- END Methods for conversion -- */
	
	/* -- START Override equals(), hashCode() and toString() -- */
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Node) {
			Node other = (Node) obj;
			return (other.getId() == getId());
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return getId();
	}
	
	@Override
	public String toString() {
		return super.toString() + "["
			+ "id=" + getId() + ", "
			+ "x=" + getX() + ", "
			+ "y=" + getY()
			+ "]";
	}
	
	/* -- END Override equals(), hashCode() and toString() -- */
	
	/* -- START Static node cache -- */
	
	/**
	 * The cache for nodes by their id.
	 */
	private static HashMap<Integer, Node> nodeCache = new HashMap<Integer, Node>();
	
	/**
	 * Clears the node cache. No previously constructed nodes will be retrievable statically by their id after calling this.
	 */
	public static void clearNodeCache() {
		nodeCache.clear();
	}
	
	/**
	 * Get a node by its id. It must have been constructed since the last node cache clearing.
	 * @param id The id of the requested node.
	 * @return The node, if found (null otherwise).
	 */
	public static Node getById(int id) {
		return nodeCache.get(id);
	}
	
	/**
	 * Adds a node to the node cache. This should be called by the Node() constructor.
	 * If a previous node with the same id was cached, it is overwritten.
	 * @param node The node to be added to the cache.
	 */
	private static void addToNodeCache(Node node) {
		nodeCache.put(node.getId(), node);
	}
	
	/* -- END Static node cache -- */
	
}
