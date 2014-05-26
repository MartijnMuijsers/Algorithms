package tue.algorithms.utility;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;

final public class MinimumSpanningTree {
	private MinimumSpanningTree() { /* just a namespace to hold the function */ }

	/**
	 * Time complexity: O(n^2) where n is nodes.length
	 *
	 * @param nodes
	 * @return Array of all possible segments for the given set of nodes.
	 */
	public static Segment[] getSegmentsPermutation(Node[] nodes) {
		Segment[] segments = new Segment[nodes.length * (nodes.length - 1)];
		int i = 0;
		for (Node node1 : nodes) {
			for (Node node2 : nodes) {
				if (node1 != node2) {
					segments[i++] = new Segment(node1, node2);
				}
			}
		}
		return segments;
	}

	/**
	 * Calculate the MST using Kruskal's algorithm.
	 * Time complexity: O(E log E) where E is the number of segments,
	 *					assuming that nodes.length = O(sqrt(E)).
	 *
	 * @param segments
	 * @param nodes
	 * @param comparator The comparator to use to sort {@code segments}.
	 * @return Array of segments, calculated via a MST algorithm.
	 */
	public static Segment[] applyMST(Segment[] segments, Node[] nodes, Comparator<Segment> comparator) {
		HashMap<Node, Integer> A = new HashMap<Node, Integer>();
		HashMap<Integer, HashSet<Node>> B = new HashMap<Integer, HashSet<Node>>();

		for (Node node : nodes) {
			A.put(node, node.getId());
			HashSet<Node> set = new HashSet<Node>();
			set.add(node);
			B.put(node.getId(), set);
		}

		Segment[] sortedSegments = segments.clone();
		Arrays.sort(sortedSegments, comparator);
		HashSet<Segment> mst = new HashSet<Segment>();
		for (Segment segment : sortedSegments) {
			int u = A.get(segment.getNode1());
			int v = A.get(segment.getNode2());
			if (u != v) {
				mst.add(segment);
				HashSet<Node> U = B.get(u);
				HashSet<Node> V = B.get(v);
				for (Node node : U) {
					A.put(node, v);
					V.add(node);
				}
				U.clear();
			}
		}

		Segment[] resultSegments = new Segment[mst.size()];
		mst.toArray(resultSegments);
		return resultSegments;
	}
}
