package tue.algorithms.implementation.general;

import tue.algorithms.utility.Node;
import tue.algorithms.utility.Segment;

/**
 * <p>
 * An abstract class for an implementation of a solution for the multiple-curve problem.
 * </p>
 * @author Martijn
 */
public abstract class MultipleImplementation {
	
	/**
	 * Get the solution to a problem: given nodes as input, find the segments to be best connected,
	 * where the segments should form one or more curves, each open or closed.
	 * @param input The nodes as input
	 * @return The segments as output
	 */
	public abstract Segment[] getOutput(Node[] input);
	
}
