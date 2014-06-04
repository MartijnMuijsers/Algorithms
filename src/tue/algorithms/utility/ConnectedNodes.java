package tue.algorithms.utility;

import java.lang.Comparable;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeSet;

/**
 * A mutable data structure that provides information about the graph
 * of connected nodes.
 *
 * Goals (high performance operations, in order of importance):
 * - Can a node be added without intersecting the graph?
 * - For a given node, which segments are connected to it?
 * - Add a segment
 * - Remove a segment
 */
public class ConnectedNodes {

	final private HashSet<Segment> segments;

	final private HashMap<Integer, HashSet<Segment>> nodeToSegments;

	/**
	 * Construct the ConnectedNodes
	 * Time complexity: O(1)
	 */
	public ConnectedNodes() {
		this.segments = new HashSet<Segment>();
		this.nodeToSegments = new HashMap<Integer, HashSet<Segment>>();
	}

	/**
	 * Check whether the segments intersects any of the other segments in the graph.
	 * Time complexity: O(n) in terms of segments in the existing graph.
	 *
	 * @return Whether the segment intersects the graph.
	 */
	public boolean intersectsGraph(Segment segment) {
		// TODO: Improve time complexity of this part
		for (Segment other : segments) {
			if (segment.intersectsWith(other)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Add a segment to the graph.
	 * Time complexity: O(1)
	 */
	public void addSegment(Segment segment) {
		segments.add(segment);

		int node1id = segment.getNode1Id();
		int node2id = segment.getNode2Id();
		HashSet<Segment> set1 = nodeToSegments.get(node1id);
		HashSet<Segment> set2 = nodeToSegments.get(node2id);
		if (set1 == null) {
			set1 = new HashSet<Segment>();
			nodeToSegments.put(node1id, set1);
		}
		if (set2 == null) {
			set2 = new HashSet<Segment>();
			nodeToSegments.put(node2id, set2);
		}
		set1.add(segment);
		set2.add(segment);
	}

	/**
	 * Remove a segment from the graph.
	 * Time complexity: O(1)
	 */
	public void removeSegment(Segment segment) {
		segments.remove(segment);

		HashSet<Segment> set1 = nodeToSegments.get(segment.getNode1Id());
		HashSet<Segment> set2 = nodeToSegments.get(segment.getNode2Id());
		if (set1 != null) {
			set1.remove(segment);
		}
		if (set2 != null) {
			set2.remove(segment);
		}
	}

	/**
	 * Get all connected segments for a given node.
	 * Callers should not make any assumptions on the order of the returned segments.
	 * Time complexity: O(k) in terms of the number of connected segments.
	 * Usually, the number of connected segments is very low, so the method can be
	 * considered O(1).
	 *
	 * @param node
	 * @return All segments connected to {@code node}.
	 */
	public Segment[] getSegments(Node node) {
		HashSet<Segment> segmentsSet = nodeToSegments.get(node.getId());
		if (segmentsSet == null) {
			return new Segment[0];
		}
		Segment[] segmentsArray = new Segment[segmentsSet.size()];
		return segmentsSet.toArray(segmentsArray);
	}

	/**
	 * For a given segment, get the other segment that is connected to it.
	 * This method only makes sense if the degree of the first endpoint is at most 2.
	 * @return The other segment if existent, null otherwise.
	 */
	public Segment getOtherSegment(Segment segment) {
		HashSet<Segment> segmentsSet = nodeToSegments.get(segment.getNode1().getId());
		if (segmentsSet == null) {
			return null;
		}
		for (Segment other: segmentsSet) {
			if (!other.equals(segment)) {
				return other;
			}
		}
		return null;
	}

	/**
	 * Returns the all segments that have been added to this data structure.
	 * Note that the order of segments is not specified, do not make any
	 * assumptions about it!
	 * Time complexity: O(1)
	 *
	 * @return All segments that have been added to this data structure.
	 */
	public Segment[] getAllSegments() {
		return segments.toArray(new Segment[0]);
	}

	/**
	 * Check whether two nodes are connected.
	 * Time complexity: O(k)
	 * Where k is the number of segments connected to {@code node1}.
	 * This number is usually very low, so the running time is usually O(1).
	 *
	 * @param node1
	 * @param node2
	 * @return Whether there exist a segment that connects the two nodes.
	 */
	public boolean isConnected(Node node1, Node node2) {
		HashSet<Segment> segmentsSet = nodeToSegments.get(node1.getId());
		if (segmentsSet == null) {
			return false;
		}
		for (Segment segment : segmentsSet) {
			if (segment.isEndPoint(node2)) {
				return true;
			}
		}
		return false;
	}
}
