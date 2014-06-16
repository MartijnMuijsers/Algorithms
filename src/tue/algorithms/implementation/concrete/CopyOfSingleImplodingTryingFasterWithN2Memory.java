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
public class CopyOfSingleImplodingTryingFasterWithN2Memory implements SingleImplementation {
	
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
		int ll = jnput.length;
		OpNode[] input = new OpNode[ll];
		for (int i = 0; i < ll; i++) {
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
		List<OpSegment> likelinessesList1 = new ArrayList<OpSegment>(ll);
		List<List<OpNode>> likelinessesList2 = new ArrayList<List<OpNode>>(ll);
		List<List<Float>> likelinessesList3 = new ArrayList<List<Float>>(ll);
		for (OpSegment segment : foundSegments) {
			OpPair<List<OpNode>, List<Float>> nodeLikelinesses = buildNodeLikelinesses(segment, nodesToDo);
			likelinessesList1.add(segment);
			likelinessesList2.add(nodeLikelinesses.first);
			likelinessesList3.add(nodeLikelinesses.second);
		}
		int si = likelinessesList1.size();
		long largestMemoryUsed = 0;
		while (nodesToDo.size() > 0) {
			{
				Runtime runtime = Runtime.getRuntime();
				long memoryUsed = (runtime.totalMemory() - runtime.freeMemory());
				if (memoryUsed > largestMemoryUsed) {
					largestMemoryUsed = memoryUsed;
				}
			}
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
			OpSegment newSegment1 = new OpSegment(nodeWithSmallestLikeliness, segmentWithSmallestLikeliness.node1);
			OpSegment newSegment2 = new OpSegment(nodeWithSmallestLikeliness, segmentWithSmallestLikeliness.node2);
			foundSegments.add(newSegment1);
			foundSegments.add(newSegment2);
			foundSegments.remove(segmentWithSmallestLikeliness);
			nodesToDo.remove(nodeWithSmallestLikeliness);
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
			while(removeTooLong(input)){}
		}
		Segment[] result = new Segment[foundSegments.size()];
		int i = 0;
		for (OpSegment os : foundSegments) {
			result[i] = os.toSegment();
			i++;
		}
		System.out.println("Largest memory used (MB): " + largestMemoryUsed/(1024*1024));
		return result;
	}
	
	private boolean removeTooLong(OpNode[] input) {
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
				//if (makeOpenCondition(longestLength, oneToLongestLength)) {TODO
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
			//}TODO
		}
		return false;//TODO
	}
	
	private OpPair<List<OpNode>, List<Float>> buildNodeLikelinesses(OpSegment segment, HashSet<OpNode> nodesToDo) {
		List<OpPair<OpNode, Float>> nodeLikelinesses = new ArrayList<OpPair<OpNode, Float>>();
		for (OpNode n : nodesToDo) {
			float dx = segment.x2-segment.x1;
			float dy = segment.y2-segment.y1;
			float segmentLength = (float) Math.sqrt(dx*dx+dy*dy);
			float nx = n.x;
			float ny = n.y;
			OpNode sNode1 = segment.node1;
			dx = sNode1.x-nx;
			dy = sNode1.y-ny;
			float distance1 = (float) Math.sqrt(dx*dx+dy*dy);
			OpNode sNode2 = segment.node2;
			dx = sNode2.x-nx;
			dy = sNode2.y-ny;
			float distance2 = (float) Math.sqrt(dx*dx+dy*dy);
			nodeLikelinesses.add(new OpPair<OpNode, Float>(n, (distance1*distance1+distance2*distance2)/(segmentLength*segmentLength)));
		}
		Collections.sort(nodeLikelinesses, nodeLikelinessComparator);
		int s = nodeLikelinesses.size();
		List<OpNode> list1 = new ArrayList<OpNode>(s);
		List<Float> list2 = new ArrayList<Float>(s);
		for (int i = 0; i < s; i++) {
			OpPair<OpNode, Float> pair = nodeLikelinesses.get(i);
			list1.add(pair.first);
			list2.add(pair.second);
		}
		return new OpPair<List<OpNode>, List<Float>>(list1, list2);
	}
	
}
