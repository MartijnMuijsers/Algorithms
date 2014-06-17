package tue.algorithms.implementation.concrete;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import tue.algorithms.implementation.general.SingleImplementation;
import tue.algorithms.other.Conversion;
import tue.algorithms.other.OpPair;
import tue.algorithms.utility.MinimumSpanningTree;
import tue.algorithms.utility.Node;
import tue.algorithms.utility.Segment;

/**
 * Under development. Testing ground here.
 * @author Martijn
 */
public class SingleImplodingTryingFaster implements SingleImplementation {
	
	private static Comparator<OpPair<Node, Float>> nodeLikelinessComparator = new Comparator<OpPair<Node, Float>>() {
		
		@Override
		public int compare(OpPair<Node, Float> arg0,
				OpPair<Node, Float> arg1) {
			float likelihood0 = arg0.second;
			float likelihood1 = arg1.second;
			if (likelihood0 < likelihood1) {
				return 1;
			} else if (likelihood0 > likelihood1) {
				return -1;
			}
			return 0;
		}
		
	};
	
	private final boolean tryOpenEnabled = true;
	
	private HashSet<Segment> foundSegments;
	
	private CurveType type = CurveType.CLOSED;
	private enum CurveType {
		CLOSED,
		OPEN
	}
	
	@Override
	public Segment[] getOutput(Node[] input) {
		return getOutput(input, GrahamConvexHull.getConvexHull(input));
	}
	
	private Segment[] getOutput(Node[] input, HashSet<Segment> convexHullA) {
		int ll = input.length;
		/*
		Node[] input = new Node[ll];
		for (int i = 0; i < ll; i++) {
			Node n = jnput[i];
			input[i] = new Node(n.id, n.x, n.y);
		}*/
		foundSegments = new HashSet<Segment>();
		for (Segment s : convexHullA) {
			foundSegments.add(s);
		}
		HashSet<Node> nodesToDo = new HashSet<Node>();
		for (Node n : input) {
			nodesToDo.add(n);
		}
		for (Segment segment : foundSegments) {
			nodesToDo.remove(segment.node1);
			nodesToDo.remove(segment.node2);
		}
		List<Segment> likelinessesList1 = new ArrayList<Segment>(ll);
		List<Node> likelinessesList2 = new ArrayList<Node>(ll);
		List<Float> likelinessesList3 = new ArrayList<Float>(ll);
		for (Segment segment : foundSegments) {
			OpPair<Node, Float> nodeLikelinesses = buildNodeLikelinesses(segment, nodesToDo);
			likelinessesList1.add(segment);
			likelinessesList2.add(nodeLikelinesses.first);
			likelinessesList3.add(nodeLikelinesses.second);
		}
		int si = likelinessesList1.size();
		//long largestMemoryUsed = 0;
		while (nodesToDo.size() > 0) {
			/*{
				Runtime runtime = Runtime.getRuntime();
				long memoryUsed = (runtime.totalMemory() - runtime.freeMemory());
				if (memoryUsed > largestMemoryUsed) {
					largestMemoryUsed = memoryUsed;
				}
			}*/
			Segment segmentWithSmallestLikeliness = null;
			Node nodeWithSmallestLikeliness = null;
			float smallestLikeliness = Float.MAX_VALUE;
			int smallestI = -1;
			for (int i = 0; i < si; i++) {
				float likeliness = likelinessesList3.get(i);
				if (likeliness < smallestLikeliness) {
					smallestLikeliness = likeliness;
					nodeWithSmallestLikeliness = likelinessesList2.get(i);
					smallestI = i;
				}
			}
			segmentWithSmallestLikeliness = likelinessesList1.get(smallestI);
			Segment newSegment1 = new Segment(nodeWithSmallestLikeliness, segmentWithSmallestLikeliness.node1);
			Segment newSegment2 = new Segment(nodeWithSmallestLikeliness, segmentWithSmallestLikeliness.node2);
			foundSegments.add(newSegment1);
			foundSegments.add(newSegment2);
			foundSegments.remove(segmentWithSmallestLikeliness);
			nodesToDo.remove(nodeWithSmallestLikeliness);
			likelinessesList1.remove(smallestI);
			likelinessesList2.remove(smallestI);
			likelinessesList3.remove(smallestI);
			int sip = si-1;
			if (nodesToDo.size() > 0) {
				for (int i = 0; i < sip; i++) {
					Node n = likelinessesList2.get(i);
					if (n.id == nodeWithSmallestLikeliness.id) {
						OpPair<Node, Float> nodeLikelinesses = buildNodeLikelinesses(likelinessesList1.get(i), nodesToDo);
						likelinessesList2.set(i, nodeLikelinesses.first);
						likelinessesList3.set(i, nodeLikelinesses.second);
					}
				}
				{
					likelinessesList1.add(newSegment1);
					OpPair<Node, Float> nodeLikelinesses = buildNodeLikelinesses(newSegment1, nodesToDo);
					likelinessesList2.add(nodeLikelinesses.first);
					likelinessesList3.add(nodeLikelinesses.second);
				}
				{
					likelinessesList1.add(newSegment2);
					OpPair<Node, Float> nodeLikelinesses = buildNodeLikelinesses(newSegment2, nodesToDo);
					likelinessesList2.add(nodeLikelinesses.first);
					likelinessesList3.add(nodeLikelinesses.second);
				}
				si++;
			}
			
		}
		if (tryOpenEnabled) {
			while(removeTooLong()){}
		}
		removeIntersections();
		supplementFromMST(input);
		removeTooLong();
		Segment[] result = new Segment[foundSegments.size()];
		int i = 0;
		for (Segment os : foundSegments) {
			result[i] = os;
			i++;
		}
		//System.out.println("Largest memory used (MB): " + largestMemoryUsed/(1024*1024));
		return result;
	}
	
	private boolean removeTooLong() {
		float longestLength = Integer.MIN_VALUE;
		float oneToLongestLength = Integer.MIN_VALUE;
		Segment longestSegment = null;
		for (Segment segment : foundSegments) {
			float length = segment.length();
			if (length > longestLength) {
				oneToLongestLength = longestLength;
				longestLength = length;
				longestSegment = segment;
			} else if (length > oneToLongestLength) {
				oneToLongestLength = length;
			}
		}
		if (longestSegment != null) {
			if (longestLength > 1.4*oneToLongestLength) {
				foundSegments.remove(longestSegment);
				return true;
			}
		}
		return false;
	}
	
	private void supplementFromMST(Node[] input) {
		Segment[] MST = MinimumSpanningTree.getMST(input);
		List<Segment> MSTa = Conversion.toArrayList(MST);
		Collections.sort(MSTa, new Comparator<Segment>() {
			
			@Override
			public int compare(Segment obj0, Segment obj1) {
				float l0 = obj0.length();
				float l1 = obj1.length();
				if (l0 < l1) {
					return -1;
				} else if (l0 > l1) {
					return 1;
				}
				return 0;
			}
			
		});
		Map<Node, Integer> occ = new HashMap<Node, Integer>();
		for (Segment segment : foundSegments) {
			Node n1 = segment.node1;
			Node n2 = segment.node2;
			if (occ.containsKey(n1)) {
				occ.put(n1, occ.get(n1)+1);
			} else {
				occ.put(n1, 1);
			}
			if (occ.containsKey(n2)) {
				occ.put(n2, occ.get(n2)+1);
			} else {
				occ.put(n2, 1);
			}
		}
		for (Segment segment : MSTa) {
			Node n1 = segment.node1;
			Node n2 = segment.node2;
			int o1 = 0;
			if (occ.containsKey(n1)) {
				o1 = occ.get(n1);
			}
			int o2 = 0;
			if (occ.containsKey(n2)) {
				o2 = occ.get(n2);
			}
			if (o1 < 2 && o2 < 2) {
				boolean v = true;
				for (Segment oSegment : foundSegments) {
					if (segment.intersectsWith(oSegment)) {
						v = false;
					}
				}
				if (v) {
					if (occ.containsKey(n1)) {
						occ.put(n1, occ.get(n1)+1);
					} else {
						occ.put(n1, 1);
					}
					if (occ.containsKey(n2)) {
						occ.put(n2, occ.get(n2)+1);
					} else {
						occ.put(n2, 1);
					}
					foundSegments.add(segment);
				}
			}
		}
	}
	
	private void removeIntersections() {
		Map<Segment, Set<Segment>> m = new HashMap<Segment, Set<Segment>>();
		for (Segment segment : foundSegments) {
			for (Segment oSegment : foundSegments) {
				if (!segment.equals(oSegment)) {
					if (segment.intersectsWith(oSegment)) {
						Set<Segment> h;
						if (!m.containsKey(segment)) {
							h = new HashSet<Segment>();
						} else {
							h = m.get(segment);
						}
						h.add(oSegment);
						m.put(segment, h);
					}
				}
			}
		}
		while (m.size() > 0) {
			int hc = -1;
			Segment hs = null;
			for (Entry<Segment, Set<Segment>> e : m.entrySet()) {
				int c = e.getValue().size();
				if (c > hc) {
					hc = c;
					hs = e.getKey();
				}
			}
			foundSegments.remove(hs);
			Set<Segment> fr = new HashSet<Segment>();
			for (Entry<Segment, Set<Segment>> e : m.entrySet()) {
				Set<Segment> s = e.getValue();
				s.remove(hs);
				if (s.size() == 0) {
					fr.add(e.getKey());
				}
			}
			for (Segment oo : fr) {
				m.remove(oo);
			}
			m.remove(hs);
		}
	}
	
	private OpPair<Node, Float> buildNodeLikelinesses(Segment segment, HashSet<Node> nodesToDo) {
		Node smallestNode = null;
		float smallestLikeliness = 1337.13371337f;
		for (Node n : nodesToDo) {
			Node sNode1 = segment.node1;
			Node sNode2 = segment.node2;
			float dx = sNode2.x-sNode1.x;
			float dy = sNode2.y-sNode1.y;
			float segmentLength = (float) Math.sqrt(dx*dx+dy*dy);
			float nx = n.x;
			float ny = n.y;
			dx = sNode1.x-nx;
			dy = sNode1.y-ny;
			float distance1 = (float) Math.sqrt(dx*dx+dy*dy);
			dx = sNode2.x-nx;
			dy = sNode2.y-ny;
			float distance2 = (float) Math.sqrt(dx*dx+dy*dy);
			float likeliness = (distance1*distance1+distance2*distance2)/(segmentLength*segmentLength);
			if (likeliness < smallestLikeliness || smallestLikeliness == 1337.13371337f) {
				smallestLikeliness = likeliness;
				smallestNode = n;
			}
		}
		return new OpPair<Node, Float>(smallestNode, smallestLikeliness);
	}
	
}
