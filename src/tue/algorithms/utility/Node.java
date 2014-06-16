package tue.algorithms.utility;

import java.util.HashMap;

/**
 * A node that has an id and a point location.
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
 */
public class Node extends Point {

	/** ID of first node */
	final static public int MINIMAL_NODE_ID = 1;
	public final int id;
	
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
	 * Use .id getter instead of getId()
	 * @deprecated
	 */
	@Deprecated
	public int getId() {
		return id;
	}
	
	/* -- START Override equals(), hashCode() and toString() -- */
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Node) {
			Node other = (Node) obj;
			return other.id == id;
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return id;
	}
	
	@Override
	public String toString() {
		return super.toString() + "["
			+ "id=" + id + ", "
			+ "x=" + x + ", "
			+ "y=" + y
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
		nodeCache.put(node.id, node);
	}
	
	/* -- END Static node cache -- */
	
	/**
	 * Use minimally!!!
	 */
	public OpNode toOpNode() {
		return new OpNode(id, x, y);
	}
	
}
