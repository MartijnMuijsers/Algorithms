package tue.algorithms.peach;


import tue.algorithms.other.Pair;
import tue.algorithms.utility.Node;
import tue.algorithms.utility.Segment;

/**
 * Class that writes Peach output.
 * @author Martijn
 */
public abstract class PeachOutputWriter {
	
	/**
	 * Writes the output to a curve problem case, either single-curve or multiple-curve.
	 * @param output The segments that were found as a solution.
	 */
	public static void writeCurveOutput(Segment[] output) {
		System.out.println(output.length + " number of segments");
		for (Segment segment : output) {
			System.out.println(segment.getNode1Id() + " " + segment.getNode2Id());
		}
	}
	
	/**
	 * Writes the output to a network problem case.
	 * The ids of the added nodes are ignored: they are replaced by new correct ids.
	 * @param output The segments and added nodes that were found as a solution.
	 * @param originalNodeAmount The original amount of nodes in the problem description.
	 */
	public static void writeNetworkOutput(Pair<Segment[], Node[]> output, int originalNodeAmount) {
		Segment[] segments = output.first();
		Node[] addedNodes = output.second();
		System.out.println(addedNodes.length + " number of extra points");
		int id = originalNodeAmount;
		for (Node node : addedNodes) {
			id++;
			System.out.println(id + " " + node.getX() + " " + node.getY());
		}
		writeCurveOutput(segments);
	}
	
}
