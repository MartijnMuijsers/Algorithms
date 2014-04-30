package tue.algorithms.implementation.concrete;

import java.util.HashSet;

import tue.algorithms.implementation.general.MultipleImplementation;
import tue.algorithms.utility.Node;
import tue.algorithms.utility.Segment;

/**
 * A solution to the multiple-curve problem, that is NOT a real solution.
 * This just connects every node with a random other node.
 * 
 * This is supposed to be to test a simulation environment.
 * @author Martijn
 */
public class MultipleRandomSegments extends MultipleImplementation {

	@Override
	public Segment[] getOutput(Node[] input) {
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
		return resultSegments;
	}
	
}
