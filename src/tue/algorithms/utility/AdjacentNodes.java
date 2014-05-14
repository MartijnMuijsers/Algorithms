package tue.algorithms.utility;

import java.lang.Comparable;
import java.util.HashMap;
import java.util.Arrays;

/**
 * An immutable data structure that constructs in O(n^2) time
 * and provides the nearest neighbors in O(1) time.
 */
public class AdjacentNodes {

	/**
	 * Mapping from a nodeId to a list of adjacent nodes.
	 */
	private HashMap<Integer, NodeDistancePair[]> adjMap;

	public AdjacentNodes(final Node[] nodes) {
		adjMap = new HashMap<Integer, NodeDistancePair[]>(nodes.length);
		for (Node node : nodes) {
			// Array that holds all nodes except for the current one
			NodeDistancePair[] nodeDistancePairs = new NodeDistancePair[nodes.length - 1];
			int i = 0;
			for (Node otherNode : nodes) {
				if (node != otherNode) {
					float distance = node.getDistanceTo(otherNode);
					if (distance < 0) distance = -distance;
					nodeDistancePairs[i++] = new NodeDistancePair(otherNode, distance);
				}
			}
			Arrays.sort(nodeDistancePairs);
			adjMap.put(node.getId(), nodeDistancePairs);
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

	public class NodeDistancePair implements Comparable<NodeDistancePair> {
		public final Node node;
		public final float distance;

		protected NodeDistancePair(Node node, float distance) {
			this.node = node;
			this.distance = distance;
		}

		/**
		 * Comparator that sorts in ascending order.
		 */
		@Override
		public int compareTo(NodeDistancePair other) {
			float diff = other.distance - distance;
			return diff == 0 ? 0 : diff < 0 ? 1 : -1;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof NodeDistancePair) {
				NodeDistancePair other = (NodeDistancePair) obj;
				return node.equals(other.node);
			}
			return false;
		}

		@Override
		public String toString() {
			return "[node=" + node.toString() + ", distance=" + distance + "]";
		}
	}
}
