package tue.algorithms.utility;

import java.lang.Comparable;
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
