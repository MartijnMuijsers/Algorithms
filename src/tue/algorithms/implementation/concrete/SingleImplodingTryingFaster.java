package tue.algorithms.implementation.concrete;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import tue.algorithms.implementation.general.SingleImplementation;
import tue.algorithms.other.OpPair;
import tue.algorithms.utility.Node;
import tue.algorithms.utility.OpNode;
import tue.algorithms.utility.OpSegment;
import tue.algorithms.utility.Segment;

/**
 * Under development. Testing ground here.
 * @author Martijn
 */
public class SingleImplodingTryingFaster implements SingleImplementation {
	
	private static Comparator<OpPair<OpNode, Float>> nodeLikelinessComparator = new Comparator<OpPair<OpNode, Float>>() {
		
		@Override
		public int compare(OpPair<OpNode, Float> arg0,
				OpPair<OpNode, Float> arg1) {
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
	
	private boolean makeOpenCondition(float longestLength, float oneToLongestLength) {
		return longestLength >= 1.7f*oneToLongestLength;
	}
	
	private boolean tryOpenEnabled = false;
	
	private HashSet<OpSegment> foundSegments;
	
	private CurveType type = CurveType.CLOSED;
	private enum CurveType {
		CLOSED,
		OPEN
	}
	
	@Override
	public Segment[] getOutput(Node[] input) {
		return getOutput(input, GrahamConvexHull.getConvexHull(input));
	}
	
	private Segment[] getOutput(Node[] jnput, HashSet<Segment> convexHullA) {
		OpNode[] input = new OpNode[jnput.length];
		for (int i = 0; i < jnput.length; i++) {
			Node n = jnput[i];
			input[i] = new OpNode(n.id, n.x, n.y);
		}
		foundSegments = new HashSet<OpSegment>();
		for (Segment s : convexHullA) {
			foundSegments.add(s.toOpSegment());
		}
		HashSet<OpNode> nodesToDo = new HashSet<OpNode>();
		for (OpNode n : input) {
			nodesToDo.add(n);
		}
		for (OpSegment segment : foundSegments) {
			nodesToDo.remove(segment.node1);
			nodesToDo.remove(segment.node2);
		}
		int nodesToDoStill = input.length-2;
		List<OpSegment> likelinessesList1 = new ArrayList<OpSegment>(input.length);
		List<List<OpNode>> likelinessesList2 = new ArrayList<List<OpNode>>(input.length);
		List<List<Float>> likelinessesList3 = new ArrayList<List<Float>>(input.length);
		//HashMap<OpSegment, List<OpPair<OpNode, Float>>> likelinesses = new HashMap<OpSegment, List<OpPair<OpNode, Float>>>();
		for (OpSegment segment : foundSegments) {
			OpPair<List<OpNode>, List<Float>> nodeLikelinesses = buildNodeLikelinesses(segment, nodesToDo);
			likelinessesList1.add(segment);
			likelinessesList2.add(nodeLikelinesses.first);
			likelinessesList3.add(nodeLikelinesses.second);
		}
		int si = likelinessesList1.size();
		while (nodesToDoStill != 0) {
			/*{
				int a = nodesToDo.size()*foundSegments.size();
				int b = 0;
				for (int i = 0; i < si; i++) {
					b += likelinessesList2.get(i).size();
				}
				System.out.println(a + " / " + b);
			}*/
			OpSegment segmentWithSmallestLikeliness = null;
			OpNode nodeWithSmallestLikeliness = null;
			float smallestLikeliness = Integer.MAX_VALUE;
			int smallestI = -1;
			for (int i = 0; i < si; i++) {
				List<OpNode> nodeLikelinesses2 = likelinessesList2.get(i);
				List<Float> nodeLikelinesses3 = likelinessesList3.get(i);
				int s = nodeLikelinesses2.size()-1;
				OpNode element2 = nodeLikelinesses2.get(s);
				while (!nodesToDo.contains(element2)) {
					nodeLikelinesses2.remove(s);
					nodeLikelinesses3.remove(s);
					s--;
					element2 = nodeLikelinesses2.get(s);
				}
				float likeliness = nodeLikelinesses3.get(s);
				if (likeliness < smallestLikeliness) {
					smallestLikeliness = likeliness;
					nodeWithSmallestLikeliness = element2;
					smallestI = i;
				}
			}
			segmentWithSmallestLikeliness = likelinessesList1.get(smallestI);
			//System.out.println("Decided smallest " + nodeWithSmallestLikeliness.id + " , " + segmentWithSmallestLikeliness.node1.id + " , " + segmentWithSmallestLikeliness.node2.id);
			OpSegment newSegment1 = new OpSegment(nodeWithSmallestLikeliness, segmentWithSmallestLikeliness.node1);
			OpSegment newSegment2 = new OpSegment(nodeWithSmallestLikeliness, segmentWithSmallestLikeliness.node2);
			foundSegments.add(newSegment1);
			foundSegments.add(newSegment2);
			foundSegments.remove(segmentWithSmallestLikeliness);
			nodesToDoStill--;
			likelinessesList1.remove(smallestI);
			likelinessesList2.remove(smallestI);
			likelinessesList3.remove(smallestI);
			{
				likelinessesList1.add(newSegment1);
				OpPair<List<OpNode>, List<Float>> nodeLikelinesses = buildNodeLikelinesses(newSegment1, nodesToDo);
				likelinessesList2.add(nodeLikelinesses.first);
				likelinessesList3.add(nodeLikelinesses.second);
			}
			{
				likelinessesList1.add(newSegment2);
				OpPair<List<OpNode>, List<Float>> nodeLikelinesses = buildNodeLikelinesses(newSegment2, nodesToDo);
				likelinessesList2.add(nodeLikelinesses.first);
				likelinessesList3.add(nodeLikelinesses.second);
			}
			si++;
		}
		if (tryOpenEnabled) {
			tryMakeOpen(input);
		}
		Segment[] result = new Segment[foundSegments.size()];
		int i = 0;
		for (OpSegment os : foundSegments) {
			result[i] = os.toSegment();
			i++;
		}
		return result;
	}
	
	private void tryMakeOpen(OpNode[] input) {
		if (type == CurveType.CLOSED) {
			float longestLength = Integer.MIN_VALUE;
			float oneToLongestLength = Integer.MIN_VALUE;
			OpSegment longestSegment = null;
			for (OpSegment segment : foundSegments) {
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
				if (makeOpenCondition(longestLength, oneToLongestLength)) {
					foundSegments.remove(longestSegment);
					type = CurveType.OPEN;
					HashMap<OpNode, HashSet<OpSegment>> segmentsByNodes = new HashMap<OpNode, HashSet<OpSegment>>();
					for (OpNode n : input) {
						segmentsByNodes.put(n, new HashSet<OpSegment>());
					}
					for (OpSegment segment : foundSegments) {
						segmentsByNodes.get(segment.node1).add(segment);
						segmentsByNodes.get(segment.node2).add(segment);
					}
				}
			}
		}
	}
	
	private float likelinessFormula(float segmentLength, float distance1, float distance2) {
		return (distance1*distance1+distance2*distance2)/(segmentLength*segmentLength);
	}
	
	private OpPair<List<OpNode>, List<Float>> buildNodeLikelinesses(OpSegment segment, HashSet<OpNode> nodesToDo) {
		List<OpPair<OpNode, Float>> nodeLikelinesses = new ArrayList<OpPair<OpNode, Float>>();
		for (OpNode n : nodesToDo) {
			float OpSegmentLength = segment.length();
			float distance1 = segment.node1.getDistanceTo(n);
			float distance2 = segment.node2.getDistanceTo(n);
			float likeliness = likelinessFormula(OpSegmentLength, distance1, distance2);
			nodeLikelinesses.add(new OpPair<OpNode, Float>(n, likeliness));
		}
		Collections.sort(nodeLikelinesses, nodeLikelinessComparator);
		int s = nodeLikelinesses.size();
		List<OpNode> list1 = new ArrayList<OpNode>(s);
		List<Float> list2 = new ArrayList<Float>(s);
		if (segment.node1.id+segment.node2.id == 65) {
			System.out.println("For segment (" + segment.node1.id + "," + segment.node2.id + "):");
		}
		for (int i = 0; i < s; i++) {
			OpPair<OpNode, Float> pair = nodeLikelinesses.get(i);
			list1.add(pair.first);
			list2.add(pair.second);
			if (segment.node1.id+segment.node2.id == 65) {
				System.out.println(i + ": (" + pair.first.id + "," + pair.second + ")");
			}
		}
		return new OpPair<List<OpNode>, List<Float>>(list1, list2);
	}
	
}
