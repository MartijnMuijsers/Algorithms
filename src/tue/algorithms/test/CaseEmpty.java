package tue.algorithms.test;

import tue.algorithms.implementation.general.ProblemType;
import tue.algorithms.other.Pair;
import tue.algorithms.utility.Node;

/**
 * The nodes Wilco used to originally test the engine.
 * I chose NETWORK as the problem type.
 * @author Martijn
 */
public class CaseEmpty extends FakeInputReader {
	
	@Override
	public Pair<ProblemType, Node[]> readInput() {
		return new Pair<>(ProblemType.NETWORK, new Node[] { 
		});
	}	
}
