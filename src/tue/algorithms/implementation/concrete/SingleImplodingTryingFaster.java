package tue.algorithms.implementation.concrete;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;

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
	
	private static Comparator<OpPair<OpNode, Float>> opNodeLikelinessComparator = new Comparator<OpPair<OpNode, Float>>() {
		
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
	
	private HashSet<OpSegment> foundOpSegments;
	
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
			input[i] = new OpNode(n.getId(), n.getX(), n.getY());
		}
		foundOpSegments = new HashSet<OpSegment>();
		for (Segment s : convexHullA) {
			foundOpSegments.add(s.toOpSegment());
		}
		HashSet<OpNode> opNodesToDo = new HashSet<OpNode>();
		for (OpNode n : input) {
			opNodesToDo.add(n);
		}
		for (OpSegment opSegment : foundOpSegments) {
			opNodesToDo.remove(opSegment.node1);
			opNodesToDo.remove(opSegment.node2);
		}
		HashMap<OpSegment, List<OpPair<OpNode, Float>>> likelinesses = new HashMap<OpSegment, List<OpPair<OpNode, Float>>>();
		for (OpSegment opSegment : foundOpSegments) {
			List<OpPair<OpNode, Float>> opNodeLikelinesses = buildOpNodeLikelinesses(opSegment, opNodesToDo);
			likelinesses.put(opSegment, opNodeLikelinesses);
		}
		while (opNodesToDo.size() != 0) {
			{
				int a = opNodesToDo.size()*foundOpSegments.size();
				int b = 0;
				for (Entry<OpSegment, List<OpPair<OpNode, Float>>> entry : likelinesses.entrySet()) {
					b += entry.getValue().size();
				}
				System.out.println(a + " / " + b);
			}
			OpSegment opSegmentWithSmallestLikeliness = null;
			OpNode opNodeWithSmallestLikeliness = null;
			float smallestLikeliness = Integer.MAX_VALUE;
			for (Entry<OpSegment, List<OpPair<OpNode, Float>>> entry : likelinesses.entrySet()) {
				List<OpPair<OpNode, Float>> opNodeLikelinesses = entry.getValue();
				int s = opNodeLikelinesses.size()-1;
				OpPair<OpNode, Float> opPair = opNodeLikelinesses.get(s);
				while (!opNodesToDo.contains(opPair.first)) {
					opNodeLikelinesses.remove(s);
					s--;
					opPair = opNodeLikelinesses.get(s);
				}
				float likeliness = opPair.second;
				if (likeliness < smallestLikeliness) {
					smallestLikeliness = likeliness;
					opSegmentWithSmallestLikeliness = entry.getKey();
					opNodeWithSmallestLikeliness = opPair.first;
				}
			}
			OpSegment newOpSegment1 = new OpSegment(opNodeWithSmallestLikeliness, opSegmentWithSmallestLikeliness.node1);
			OpSegment newOpSegment2 = new OpSegment(opNodeWithSmallestLikeliness, opSegmentWithSmallestLikeliness.node2);
			foundOpSegments.add(newOpSegment1);
			foundOpSegments.add(newOpSegment2);
			foundOpSegments.remove(opSegmentWithSmallestLikeliness);
			opNodesToDo.remove(opNodeWithSmallestLikeliness);
			likelinesses.remove(opSegmentWithSmallestLikeliness);
			likelinesses.put(newOpSegment1, buildOpNodeLikelinesses(newOpSegment1, opNodesToDo));
			likelinesses.put(newOpSegment2, buildOpNodeLikelinesses(newOpSegment2, opNodesToDo));
		}
		if (tryOpenEnabled) {
			tryMakeOpen(input);
		}
		Segment[] result = new Segment[foundOpSegments.size()];
		int i = 0;
		for (OpSegment os : foundOpSegments) {
			result[i] = os.toSegment();
			i++;
		}
		return result;
	}
	
	private void tryMakeOpen(OpNode[] input) {
		if (type == CurveType.CLOSED) {
			float longestLength = Integer.MIN_VALUE;
			float oneToLongestLength = Integer.MIN_VALUE;
			OpSegment longestOpSegment = null;
			for (OpSegment opSegment : foundOpSegments) {
				float length = opSegment.length();
				if (length > longestLength) {
					oneToLongestLength = longestLength;
					longestLength = length;
					longestOpSegment = opSegment;
				} else if (length > oneToLongestLength) {
					oneToLongestLength = length;
				}
			}
			if (longestOpSegment != null) {
				if (makeOpenCondition(longestLength, oneToLongestLength)) {
					foundOpSegments.remove(longestOpSegment);
					type = CurveType.OPEN;
					HashMap<OpNode, HashSet<OpSegment>> opSegmentsByOpNodes = new HashMap<OpNode, HashSet<OpSegment>>();
					for (OpNode n : input) {
						opSegmentsByOpNodes.put(n, new HashSet<OpSegment>());
					}
					for (OpSegment opSegment : foundOpSegments) {
						opSegmentsByOpNodes.get(opSegment.node1).add(opSegment);
						opSegmentsByOpNodes.get(opSegment.node2).add(opSegment);
					}
				}
			}
		}
	}
	
	private float likelinessFormula(float opSegmentLength, float distance1, float distance2) {
		return (distance1*distance1+distance2*distance2)/(opSegmentLength*opSegmentLength);
	}
	
	private List<OpPair<OpNode, Float>> buildOpNodeLikelinesses(OpSegment opSegment, HashSet<OpNode> opNodesToDo) {
		List<OpPair<OpNode, Float>> opNodeLikelinesses = new ArrayList<OpPair<OpNode, Float>>();
		for (OpNode n : opNodesToDo) {
			float OpSegmentLength = opSegment.length();
			float distance1 = opSegment.node1.getDistanceTo(n);
			float distance2 = opSegment.node2.getDistanceTo(n);
			float likeliness = likelinessFormula(OpSegmentLength, distance1, distance2);
			opNodeLikelinesses.add(new OpPair<OpNode, Float>(n, likeliness));
		}
		Collections.sort(opNodeLikelinesses, opNodeLikelinessComparator);
		return opNodeLikelinesses;
	}
	
}
