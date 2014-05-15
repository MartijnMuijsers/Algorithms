package tue.algorithms.implementation.concrete;

import java.util.HashSet;

import tue.algorithms.implementation.general.NetworkImplementation;
import tue.algorithms.other.Pair;
import tue.algorithms.utility.Node;
import tue.algorithms.utility.Segment;

/**
 * A solution to the network problem, that is NOT a real solution.
 * This just connects every node with a random other node, and gives no added nodes.
 * 
 * This is supposed to be to test a simulation environment.
 * @author Martijn
 */
public class NetworkRandomSegments implements NetworkImplementation {

	@Override
	public Pair<Segment[], Node[]> getOutput(Node[] input) {
		HashSet<Segment> segments = new HashSet<Segment>();
		for (Node node : input) {
			Node otherNode = input[(int) (Math.random()*input.length)];
			Segment segment = new Segment(node, otherNode);
			if (!segments.contains(segment)) {
				segments.add(segment);
			}
		}
		Segment[] resultSegments = new Segment[segments.size()];
		int i = 0;
		for (Segment segment : segments) {
			resultSegments[i] = segment;
			i++;
		}
		return new Pair<Segment[], Node[]>(resultSegments, new Node[0]);
	}
	
}
