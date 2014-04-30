package tue.algorithms.test;

import tue.algorithms.implementation.general.ProblemType;
import tue.algorithms.other.Pair;
import tue.algorithms.utility.Node;

/**
 * The nodes Wilco used to originally test the engine.
 * I chose NETWORK as the problem type.
 * @author Martijn
 */
public class CaseWilcoViewerTest extends FakeInputReader {
	
	@Override
	public Pair<ProblemType, Node[]> readInput() {
		return new Pair<ProblemType, Node[]>(ProblemType.NETWORK, new Node[] {
				new Node(0, 0.5f, 0.5f),
				new Node(1, 0f, 0f),
				new Node(2, 0f, 1f),
				new Node(3, 1f, 1f),
				new Node(4, 1f, 0f),
	});
	}
	
}
