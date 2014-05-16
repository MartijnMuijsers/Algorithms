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

	// The set of segments, optimized for querying near-horizontal respectively
	// near-vertical segments. segmentsQH must be kept in sync with segmentsQV.
	final private TreeSet<Segment> segmentsQH;
	final private TreeSet<Segment> segmentsQV;

	final private HashMap<Integer, HashSet<Segment>> nodeToSegments;

	/**
	 * Construct the ConnectedNodes
	 * Time complexity: O(1)
	 */
	public ConnectedNodes() {
		this.segmentsQH = new TreeSet<Segment>(new HorizontalSegmentQueryComparator());
		this.segmentsQV = new TreeSet<Segment>(new VerticalSegmentQueryComparator());
		this.nodeToSegments = new HashMap<Integer, HashSet<Segment>>();
	}

	/**
	 * Check whether the segments intersects any of the other segments in the graph.
	 * Time complexity: O(log n + k)
	 * where n = total number of segments and k = number of elements in query interval.
	 * In the worst case, k = n because all segments are within the query interval.
	 * This algorithm performs very well for short segments and segments that are
	 * almost flat (horizontally / vertically), because it runs a binary search on
	 * the full set of segments to identify boundaries of the search interval,
	 * then it searches for intersections in a linear way.
	 * NOTE: Due to a bad implementation, this method fails to identify the left
	 * boundary, so it is a bit inefficient for lines at the bottom-right corner of
	 * the graph.
	 *
	 * @return Whether the segment intersects the graph.
	 */
	public boolean intersectsGraph(Segment segment) {
		float slope = segment.getSlope();
		boolean isNearVertical = slope > 1 || slope < -1;

		float maximum;
		TreeSet<Segment> segments;
		if (isNearVertical) {
			maximum = segment.getMaxX();
			segments = segmentsQV;
		} else {
			maximum = segment.getMaxY();
			segments = segmentsQH;
		}

		// Use tailSet to skip all elements "before" segment.
		// TODO: Use binary search to identify the starting point instead
		// of starting at the front of the list
		for (Segment other : segments) {
			if (maximum < (isNearVertical ? other.getMinX() : other.getMinY())) {
				// Every element "after" segment will never intersect segment.
				return false;
			}
			if (segment.intersectsWith(other)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Add a segment to the graph.
	 * Time complexity: O(log n)
	 * (in terms of number of segments in existing graph.)
	 */
	public void addSegment(Segment segment) {
		segmentsQH.add(segment);
		segmentsQV.add(segment);

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
	 * Time complexity: O(log n)
	 * (in terms of number of segments in existing graph.)
	 */
	public void removeSegment(Segment segment) {
		segmentsQH.remove(segment);
		segmentsQV.remove(segment);

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
	 * Returns the all segments that have been added to this data structure.
	 * Note that the order of segments is not specified, do not make any
	 * assumptions about it!
	 * Time complexity: O(1)
	 *
	 * @return All segments that have been added to this data structure.
	 */
	public Segment[] getAllSegments() {
		return segmentsQH.toArray(new Segment[0]);
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

	/**
	 * A comparator optimized for quering vertical segments.
	 * Ordered by ascending min-X-coordinate.
	 */
	private class VerticalSegmentQueryComparator implements Comparator<Segment> {
		@Override
		public int compare(Segment s1, Segment s2) {
			float diff = s1.getMinX() - s2.getMinX();
			if (diff < 0) {
				// s1 is at the left of s2
				return -1;
			}
			if (diff > 0) {
				// s1 is at the right of s2
				return 1;
			}
			return s1.equals(s2) ? 0 : -1;
		}
	}

	/**
	 * A comparator optimized for quering horizontal segments.
	 * Ordered by ascending min-Y-coordinate.
	 */
	private class HorizontalSegmentQueryComparator implements Comparator<Segment> {
		@Override
		public int compare(Segment s1, Segment s2) {
			float diff = s1.getMinY() - s2.getMinY();
			if (diff < 0) {
				// s1 is visually below s2
				return -1;
			}
			if (diff > 0) {
				// s1 is visually above s2
				return 1;
			}
			return s1.equals(s2) ? 0 : -1;
		}
	}
}
