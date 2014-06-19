package tue.algorithms.utility;

import java.lang.Comparable;
import java.util.HashMap;
import java.util.Arrays;

/**
 * An immutable data structure that constructs in O(n^2) time
 * and provides the nearest 10 neighbors in O(1) time.
 */
public class AdjacentNodes {

	/**
	 * Mapping from a nodeId to a list of adjacent nodes.
	 */
	private HashMap<Integer, NodeDistancePair[]> adjMap;

	public AdjacentNodes(final Node[] nodes) {
		this(nodes, 10);
	}
	public AdjacentNodes(final Node[] nodes, final int maximumLengthOfAdjacencyList) {
		adjMap = new HashMap<Integer, NodeDistancePair[]>(nodes.length);
		// Number of nodes min one because an adjacency list can be at most nodes.length - 1.
		int nodesLengthMinOne = nodes.length - 1;
		int ndpLength = Math.min(nodesLengthMinOne, maximumLengthOfAdjacencyList);
		for (Node node : nodes) {
			// Array that holds all nodes except for the current one
			final NodeDistancePair[] nodeDistancePairs = new NodeDistancePair[nodesLengthMinOne];
			int k = 0;
			for (Node otherNode : nodes) {
				if (node != otherNode) {
					float distance = node.getDistanceTo(otherNode);
					if (distance < 0) distance = -distance;
					nodeDistancePairs[k++] = new NodeDistancePair(otherNode, distance);
				}
			}
			// Now select the first |ndpLength| NodeDistancePairs.
			for (int i = 0; i < ndpLength; ++i) {
				int indexOfSmallestValue = i;
				NodeDistancePair smallestNDP = nodeDistancePairs[i];
				for (int j = i + 1; j < nodesLengthMinOne; ++j) {
					if (nodeDistancePairs[j].distance < smallestNDP.distance) {
						indexOfSmallestValue = j;
						smallestNDP = nodeDistancePairs[j];
					}
				}
				nodeDistancePairs[indexOfSmallestValue] = nodeDistancePairs[i];
				nodeDistancePairs[i] = smallestNDP;
			}
			adjMap.put(node.getId(), Arrays.copyOfRange(nodeDistancePairs, 0, ndpLength));
		}
	}

	/**
	 * Get a list of adjacent nodes.
	 * @param node Node for which the {@code m} nearest neighbors are returned.
	 * @return List of adjacent nodes, sorted in ascending order (nearest first).
	 */
	public NodeDistancePair[] getAdjacentNodes(Node node) {
		return getAdjacentNodes(node.getId());
	}

	/**
	 * Get a list of adjacent nodes.
	 * @param nodeId Id of node for which the {@code m} nearest neighbors are returned.
	 * @pre {nodeId}
	 * @return List of adjacent nodes, sorted in ascending order (nearest first).
	 * @throws RuntimeException if the nodeId is invalid.
	 */
	public NodeDistancePair[] getAdjacentNodes(int nodeId) {
		NodeDistancePair[] nodes = adjMap.get(nodeId);
		if (nodes == null) {
			throw new RuntimeException("No node found with ID " + nodeId);
		}
		return nodes;
	}
}
