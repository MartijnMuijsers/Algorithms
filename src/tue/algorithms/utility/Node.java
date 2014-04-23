package tue.algorithms.utility;

/**
 * <p>
 * A node that has an id and a point location.
 * </p>
 * <p>
 * This class is a subclass of Point.
 * </p>
 * <p>
 * This class is unmutable.
 * </p>
 * @author Martijn
 */
public class Node extends Point {
	
	/* -- START Private final fields -- */
	
	/**
	 * The id of the node.
	 */
	private final int id;
	
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
		this.id = id;
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
	
}
