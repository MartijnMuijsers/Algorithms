package tue.algorithms.utility;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;

final public class MinimumSpanningTree {
	private MinimumSpanningTree() { /* just a namespace to hold the function */ }

	/**
	 * Calculate the MST using Prim's algorithm.
	 * Time complexity: O(n^2) where n is the number of nodes.
	 * Memory complexity: O(n)
	 *
	 * @param nodes
	 * @return Array of segments, calculated via a MST algorithm.
	 */

	public static Segment[] getMST(Node[] nodes) {
		final int segmentsCount = nodes.length - 1;
		int[] parent = new int[segmentsCount];
		float[] minimumDistanceToTree = new float[segmentsCount];

		// Attach every node to the root
		for (int i = 0; i < segmentsCount; ++i) {
			// nodes[segmentsCount] is the root of the tree.
			parent[i] = segmentsCount;
			minimumDistanceToTree[i] = nodes[i].getDistanceTo(nodes[segmentsCount]);
		}
		for (int i = 0; i < segmentsCount; ++i) {
			// Find the closest node...
			int closestNodeIndex = 0;
			float smallestDistance = Integer.MAX_VALUE;
			for (int j = 0; j < segmentsCount; ++j) {
				if (minimumDistanceToTree[j] != 0 && minimumDistanceToTree[j] < smallestDistance) {
					closestNodeIndex = j;
					smallestDistance = minimumDistanceToTree[closestNodeIndex];
				}
			}
			// ... and add it to the tree.
			minimumDistanceToTree[closestNodeIndex] = 0;

			// Re-calculate the distances of all other nodes.
			for (int j = 0; j < segmentsCount; ++j) {
				if (minimumDistanceToTree[j] != 0) {
					float distance = nodes[j].getDistanceTo(nodes[closestNodeIndex]);
					if (distance < minimumDistanceToTree[j]) {
						parent[j] = closestNodeIndex;
						minimumDistanceToTree[j] = distance;
					}
				}
			}
		}
		Segment[] segments = new Segment[segmentsCount];
		for (int i = 0; i < segmentsCount; ++i) {
			segments[i] = new Segment(nodes[i], nodes[parent[i]]);
		}
		return segments;
	}
}
