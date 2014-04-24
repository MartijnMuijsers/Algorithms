package tue.algorithms.utility;

/**
 * <p>
 * An abstract class for an implementation of a solution for the network problem.
 * </p>
 * @author Martijn
 */
public abstract class NetworkImplementation {
	
	/**
	 * Get the solution to a problem: given nodes as input, find the segments to be best connected,
	 * where the segments should describe a network. The segments must be strictly non-intersecting.
	 * Extra nodes can be added to solve this property, the added nodes should be the second element in the resulting pair.
	 * @param input The nodes as input
	 * @return The segments and added nodes as output
	 */
	public abstract Pair<Segment[], Node[]> getOutput(Node[] input);
	
}
