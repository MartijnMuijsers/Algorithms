package tue.algorithms.utility;

import java.util.HashMap;

/**
 * A node that has an id and a point location (x,y).
 * The constructor is used in two scenarios:
 * 1. Nodes are loaded from a file.
 * 2. New Nodes are created (added, e.g. in the Network algorithm).
 *
 * For the first scenario, all IDs MUST be sequential, and added in sequence (lowest ID first).
 * For the second use case, use the constructor without ID.
 * The IDs MUST start at |MINIMAL_NODE_ID|.
 *
 * This class is immutable.
 */
public class Node {

	/** ID of first node */
	final static private int MINIMAL_NODE_ID = 1;
	private static int nextNodeId = MINIMAL_NODE_ID;

	/** ID of fake nodes, i.e. nodes that are only needed for calculations but never output. */
	final static public int FAKE_NODE_ID = MINIMAL_NODE_ID - 1;

	public final int id;
	public final float x;
	public final float y;
	
	/**
	 * Create a node with the given id and coordinates (x, y).
	 * @param id The id of the node.
	 * @param x The x-coordinate of the node.
	 * @param y The y-coordinate of the node.
	 */
	public Node(float x, float y) {
		this(nextNodeId, x, y);
	}

	/**
	 * Only use this constructor to add a new node.
	 * Nodes MUST be created in sequence, starting at the lowest Node ID.
	 */
	public Node(int id, float x, float y) {
		this.id = id;
		this.x = x;
		this.y = y;
		if (id != FAKE_NODE_ID) {
			assert id == nextNodeId;
			++nextNodeId;
			addToNodeCache(this);
		}
	}
	
	/**
	 * Use .id getter instead of getId()
	 * @deprecated
	 */
	@Deprecated
	public int getId() {
		return id;
	}

    /**
     * Use .x
     * @deprecated
     */
    @Deprecated
    public float getX() {
        return x;
    }

    /**
     * Use .y
     * @deprecated
     */
    @Deprecated
    public float getY() {
        return y;
    }

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
    public double getAngleTo(Node point) {
		return Math.atan2(point.y - y, point.x - x);
    }

    /**
     * Get the distance between this point and another.
     * @param point The other point.
     * @return The Euclidian distance as a float.
     */
    public float getDistanceTo(Node point) {
        float x = this.x - point.x;
        float y = this.y - point.y;
        return (float) Math.sqrt(x * x + y * y);
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
		nextNodeId = MINIMAL_NODE_ID;
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
