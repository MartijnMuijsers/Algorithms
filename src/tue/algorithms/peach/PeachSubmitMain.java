package tue.algorithms.peach;

import tue.algorithms.implementation.general.MultipleImplementation;
import tue.algorithms.implementation.general.NetworkImplementation;
import tue.algorithms.implementation.general.ProblemType;
import tue.algorithms.implementation.general.SingleImplementation;
import tue.algorithms.other.Pair;
import tue.algorithms.utility.Node;
import tue.algorithms.utility.Segment;

/**
 * The main class of the program when submitted to Peach.
 * @author Martijn
 */
public abstract class PeachSubmitMain {
	
	/**
	 * Get an instance of the class that is chosen to solve single-curve problem cases.
	 * @return An instance of a class that extends SingleImplementation.
	 */
	public static SingleImplementation getSingleImplementation() {
		/* TODO Choose an implementation */
		return null;
	}
	
	/**
	 * Get an instance of the class that is chosen to solve multiple-curve problem cases.
	 * @return An instance of a class that extends MultipleImplementation.
	 */
	public static MultipleImplementation getMultipleImplementation() {
		/* TODO Choose an implementation */
		return null;
	}
	
	/**
	 * Get an instance of the class that is chosen to solve network problem cases.
	 * @return An instance of a class that extends NetworkImplementation.
	 */
	public static NetworkImplementation getNetworkImplementation() {
		/* TODO Choose an implementation */
		return null;
	}
	
	/**
	 * Main method, should be called by Peach.
	 * @param args Unused
	 */
	public static void main(String[] args) {
		Pair<ProblemType, Node[]> input = PeachInputReader.readInput();
		ProblemType problemType = input.first();
		Node[] nodes = input.second();
		if (problemType == ProblemType.SINGLE) {
			Segment[] output = getSingleImplementation().getOutput(nodes);
			PeachOutputWriter.writeCurveOutput(output);
		} else if (problemType == ProblemType.MULTIPLE) {
			Segment[] output = getMultipleImplementation().getOutput(nodes);
			PeachOutputWriter.writeCurveOutput(output);
		} else if (problemType == ProblemType.NETWORK) {
			Pair<Segment[], Node[]> output = getNetworkImplementation().getOutput(nodes);
			PeachOutputWriter.writeNetworkOutput(output, nodes.length);
		}
	}
	
}
