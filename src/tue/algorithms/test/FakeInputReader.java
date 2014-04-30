package tue.algorithms.test;

import tue.algorithms.implementation.general.ProblemType;
import tue.algorithms.other.Pair;
import tue.algorithms.utility.Node;

/**
 * <p>
 * Class that provides fake input, to be extended by a 'test case' class.
 * </p>
 * @author Martijn
 */
public abstract class FakeInputReader {
	
	/**
	 * Get the fake input 'test case'.
	 * @return The problem description as a pair of the type and the given nodes.
	 */
	public abstract Pair<ProblemType, Node[]> readInput();
	
}
