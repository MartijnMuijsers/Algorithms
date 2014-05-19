package tue.algorithms.implementation.concrete;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

import tue.algorithms.implementation.general.SingleImplementation;
import tue.algorithms.other.Conversion;
import tue.algorithms.other.Debug;
import tue.algorithms.other.Pair;
import tue.algorithms.utility.Node;
import tue.algorithms.utility.Segment;

/**
 * Under development. Testing ground here.
 * @author Martijn
 */
public class SingleImploding implements SingleImplementation {
	
	/* -- START Parameters as formulas -- */
	
	/** Where x = distance **/
	public static float distanceImpactOnLikelihoodFormula(float x) {
		return 100000*pow(x, 2);
	}
	
	/** Where x = angular difference to expected **/
	public static float angularDifferenceToExpectedImpactOnLikelihoodFormula(float x) {
		return 10*pow(x, 2);
	}
	
	/** Where x = actual angular differences **/
	public static float angularDifferenceToEachOtherImpactOnLikelihoodFormula(float x) {
		return pow(x, 2);
	}
	
	/** Where x = distance impact, and y = angle impact (expected), and z = angle impact (to each other) **/
	public static float likelihoodFormula(float x, float y, float z) {
		return x+y+z;
	}
	
	public static boolean likelihoodCondition(float newLikelihood, float oldLikelihood) {
		return newLikelihood < oldLikelihood*0.3;
	}
	
	public static final boolean mozesEnabled = false;
	
	/* -- END Parameters as formulas -- */
	
	public static final float pi = (float) (Math.PI);
	public static final float pi2 = (float) (Math.PI*2);
	
	@Override
	public Segment[] getOutput(Node[] input) {
		HashSet<Segment> foundSegments = ConvexHull.getConvexHull(input);
		HashSet<Node> nodesToDo = new HashSet<Node>();
		for (Node n : input) {
			nodesToDo.add(n);
		}
		for (Segment segment : foundSegments) {
			nodesToDo.remove(segment.getNode1());
			nodesToDo.remove(segment.getNode2());
		}
		HashMap<Segment, ArrayList<Pair<Node, Float>>> likelinesses = new HashMap<Segment, ArrayList<Pair<Node, Float>>>();
		for (Segment segment : foundSegments) {
			ArrayList<Pair<Node, Float>> nodeLikelinesses = buildNodeLikelinesses(segment, nodesToDo);
			likelinesses.put(segment, nodeLikelinesses);
		}
		while (nodesToDo.size() != 0) {
			//Debug.log("Start run with: " + foundSegments.size() + " " + nodesToDo.size() + " " + likelinesses.size());
			Segment segmentWithSmallestLikeliness = null;
			float smallestLikeliness = 1000000000;
			for (Entry<Segment, ArrayList<Pair<Node, Float>>> entry : likelinesses.entrySet()) {
				ArrayList<Pair<Node, Float>> nodeLikelinesses = entry.getValue();
				Pair<Node, Float> pair = nodeLikelinesses.get(0);
				while (!nodesToDo.contains(pair.first())) {
					nodeLikelinesses.remove(0);
					pair = nodeLikelinesses.get(0);
				}
				float likeliness = pair.second();
				if (likeliness < smallestLikeliness) {
					smallestLikeliness = likeliness;
					segmentWithSmallestLikeliness = entry.getKey();
				}
			}
			Node node = likelinesses.get(segmentWithSmallestLikeliness).get(0).first();
			Segment newSegment1 = new Segment(node, segmentWithSmallestLikeliness.getNode1());
			Segment newSegment2 = new Segment(node, segmentWithSmallestLikeliness.getNode2());
			foundSegments.add(newSegment1);
			foundSegments.add(newSegment2);
			foundSegments.remove(segmentWithSmallestLikeliness);
			nodesToDo.remove(node);
			likelinesses.remove(segmentWithSmallestLikeliness);
			likelinesses.put(newSegment1, buildNodeLikelinesses(newSegment1, nodesToDo));
			likelinesses.put(newSegment2, buildNodeLikelinesses(newSegment2, nodesToDo));
		}
		
		if (mozesEnabled) {
			// should not change size variable afterwards, so made it final to prevent mistakes
			final int size = foundSegments.size();
			Segment[] segmentsInOrder;
			Node[] nodesInOrder;
			{
				Pair<Segment[], Node[]> constructedSingleCurve = constructSingleCurve(foundSegments, size, input);
				segmentsInOrder = constructedSingleCurve.first();
				nodesInOrder = constructedSingleCurve.second();
			}
			for (int i = 0; i < size; i++) {
				Node n = nodesInOrder[i];
				Segment oldEgyptSegment1 = segmentsInOrder[wrap(i-1, size)];
				Segment oldEgyptSegment2 = segmentsInOrder[i];
				Segment oldEgyptSegment3 = segmentsInOrder[wrap(i+1, size)];
				Segment oldEgyptSegment4 = segmentsInOrder[wrap(i+2, size)];
				float oldEgyptLikelihood = getLikelihood(oldEgyptSegment1, oldEgyptSegment2, oldEgyptSegment3)+getLikelihood(oldEgyptSegment2, oldEgyptSegment3, oldEgyptSegment4);
				Segment newEgyptSegment = new Segment(nodesInOrder[wrap(i-1, size)], nodesInOrder[wrap(i+1, size)]);
				float newEgyptLikelihood = getLikelihood(oldEgyptSegment1, newEgyptSegment, oldEgyptSegment4);
				for (int j = 0; j < size; j++) {
					//Debug.log("size="+size+" i="+i+" j="+j);
					Segment oldIsraelSegment1 = segmentsInOrder[wrap(j-1, size)];
					Segment oldIsraelSegment2 = segmentsInOrder[wrap(j, size)];
					Segment oldIsraelSegment3 = segmentsInOrder[wrap(j+1, size)];
					//Debug.log("oldEgypt: "+oldEgyptSegment1.getNode1Id()+"->"+oldEgyptSegment1.getNode2Id()+" , "+oldEgyptSegment2.getNode1Id()+"->"+oldEgyptSegment2.getNode2Id()+" , "+oldEgyptSegment3.getNode1Id()+"->"+oldEgyptSegment3.getNode2Id()+" , "+oldEgyptSegment4.getNode1Id()+"->"+oldEgyptSegment4.getNode2Id()+" , ");
					//Debug.log("oldIsrael: "+oldIsraelSegment1.getNode1Id()+"->"+oldIsraelSegment1.getNode2Id()+" , "+oldIsraelSegment2.getNode1Id()+"->"+oldIsraelSegment2.getNode2Id()+" , "+oldIsraelSegment3.getNode1Id()+"->"+oldIsraelSegment3.getNode2Id());
					if (noneEquals(oldEgyptSegment1, oldEgyptSegment2, oldEgyptSegment3, oldEgyptSegment4, oldIsraelSegment1, oldIsraelSegment2, oldIsraelSegment3)) {
						float oldIsraelLikelihood = getLikelihood(oldIsraelSegment1, oldIsraelSegment2, oldIsraelSegment3);
						Node israelNode2 = null;
						Node israelNode3 = null;
						{
							// find the good israel nodes
							israelNode2 = getNodeThatIsInBoth(oldIsraelSegment1, oldIsraelSegment2);
							israelNode3 = getNodeThatIsInBoth(oldIsraelSegment2, oldIsraelSegment3);
						}
						Segment newIsraelSegment1 = new Segment(israelNode2, n);
						Segment newIsraelSegment2 = new Segment(n, israelNode3);
						float newIsraelLikelihood = getLikelihood(oldIsraelSegment1, newIsraelSegment1, newIsraelSegment2)+getLikelihood(newIsraelSegment1, newIsraelSegment2, oldIsraelSegment3);
						float oldLikelihood = oldEgyptLikelihood+oldIsraelLikelihood;
						float newLikelihood = newEgyptLikelihood+newIsraelLikelihood;
						if (likelihoodCondition(newLikelihood, oldLikelihood)) { // wel heel extreem drastisch dit: 'fixt' praktisch ALLES! TODO
							Debug.log("Alright! Likelihoods: " + newLikelihood + " < " + oldLikelihood);
							Debug.log("Old Egypt likelihood 1:");
							getLikelihood(oldEgyptSegment1, oldEgyptSegment2, oldEgyptSegment3);
							Debug.release("likelihood");
							Debug.log("Old Egypt likelihood 2:");
							getLikelihood(oldEgyptSegment2, oldEgyptSegment3, oldEgyptSegment4);
							Debug.release("likelihood");
							Debug.log("Old Israel likelihood:");
							getLikelihood(oldIsraelSegment1, oldIsraelSegment2, oldIsraelSegment3);
							Debug.release("likelihood");
							//Debug
							// TODO verander dit want om dit opnieuw te constructen is echt idioot
							foundSegments.remove(oldEgyptSegment2);
							foundSegments.remove(oldEgyptSegment3);
							foundSegments.add(newEgyptSegment);
							foundSegments.remove(oldIsraelSegment2);
							foundSegments.add(newIsraelSegment1);
							foundSegments.add(newIsraelSegment2);
							// en hier is het vreselijke stuk: 'reconstruct de hele shit maar weer' zei Martijn!
							{
								Pair<Segment[], Node[]> constructedSingleCurve = constructSingleCurve(foundSegments, size, input);
								segmentsInOrder = constructedSingleCurve.first();
								nodesInOrder = constructedSingleCurve.second();
							}
							// en we gaan verder he
							i = -1;
							break;
						}
					}
				}
			}
		}
		return Conversion.toArray(foundSegments, new Segment[foundSegments.size()]);
	}

	private static float likelinessFormula(float segmentLength, float distance1, float distance2) {
		return (distance1*distance1+distance2*distance2)/(segmentLength*segmentLength);
	}
	
	private static ArrayList<Pair<Node, Float>> buildNodeLikelinesses(Segment segment, HashSet<Node> nodesToDo) {
		ArrayList<Pair<Node, Float>> nodeLikelinesses = new ArrayList<Pair<Node, Float>>();
		for (Node n : nodesToDo) {
			float segmentLength = segment.length();
			float distance1 = segment.getNode1().getDistanceTo(n);
			float distance2 = segment.getNode2().getDistanceTo(n);
			float likeliness = likelinessFormula(segmentLength, distance1, distance2);
			nodeLikelinesses.add(new Pair<Node, Float>(n, likeliness));
		}
		Collections.sort(nodeLikelinesses, new Comparator<Pair<Node, Float>>() {
			
			@Override
			public int compare(Pair<Node, Float> arg0,
					Pair<Node, Float> arg1) {
				float likelihood0 = arg0.second();
				float likelihood1 = arg1.second();
				if (likelihood0 < likelihood1) {
					return -1;
				} else if (likelihood0 > likelihood1) {
					return 1;
				}
				return 0;
			}
			
		});
		return nodeLikelinesses;
	}
	
	/** Makes sure an integer is in the range (by applying (inefficient) mathematical modulo) [0, range) **/
	public static int wrap(int value, int range) {
		while (value < 0) {
			value += range;
		}
		while (value >= range) {
			value -= range;
		}
		return value;
	}
	
	public static Node getNodeThatIsInBoth(Segment segment1, Segment segment2) {
		HashMap<Node, Integer> H = new HashMap<Node, Integer>();
		H.put(segment1.getNode1(), 0);
		H.put(segment1.getNode2(), 0);
		H.put(segment2.getNode1(), 0);
		H.put(segment2.getNode2(), 0);
		H.put(segment1.getNode1(), H.get(segment1.getNode1())+1);
		H.put(segment1.getNode2(), H.get(segment1.getNode2())+1);
		H.put(segment2.getNode1(), H.get(segment2.getNode1())+1);
		H.put(segment2.getNode2(), H.get(segment2.getNode2())+1);
		for (Entry<Node, Integer> entry : H.entrySet()) {
			if (entry.getValue() == 2) {
				return entry.getKey();
			}
		}
		return null;
	}
	
	public static Pair<Segment[], Node[]> constructSingleCurve(HashSet<Segment> foundSegments, final int size, Node[] input) {
		HashMap<Node, HashSet<Segment>> segmentsByNodes = new HashMap<Node, HashSet<Segment>>();
		for (Node n : input) {
			segmentsByNodes.put(n, new HashSet<Segment>());
		}
		for (Segment segment : foundSegments) {
			segmentsByNodes.get(segment.getNode1()).add(segment);
			segmentsByNodes.get(segment.getNode2()).add(segment);
		}
		Segment[] segmentsInOrder = new Segment[size];
		Node[] nodesInOrder = new Node[segmentsInOrder.length];
		segmentsInOrder[0] = foundSegments.iterator().next();
		nodesInOrder[0] = segmentsInOrder[0].getNode2();
		for (int i = 1; i < segmentsInOrder.length; i++) {
			Segment previousSegment = segmentsInOrder[i-1];
			Node nodeToStartFrom = nodesInOrder[i-1];
			Segment newSegment = null;
			for (Segment segment : segmentsByNodes.get(nodeToStartFrom)) {
				if (!segment.equals(previousSegment)) {
					newSegment = segment;
					break;
				}
			}
			segmentsInOrder[i] = newSegment;
			Node newNode1 = newSegment.getNode1();
			Node newNode2 = newSegment.getNode2();
			nodesInOrder[i] = (newNode1.equals(nodeToStartFrom))?(newNode2):(newNode1);
		}
		return new Pair<Segment[], Node[]>(segmentsInOrder, nodesInOrder);
	}
	
	float getLikelihood(Segment segment1, Segment segment2, Segment segment3) {
		if (!segment1.getNode2().equals(segment2.getNode1())) {
			if (!segment2.getNode2().equals(segment3.getNode1())) {
				segment2 = segment2.invertDirection();
			} else {
				segment1 = segment1.invertDirection();
			}
		}
		Node goingTo = segment3.getNode2();
		Node standingAt = segment3.getNode1();
		Node comingFrom = segment2.getNode1();
		Node lookingBack = segment1.getNode1();
		double angleBetweenComingFromAndStandingAt = comingFrom.getAngleTo(standingAt);
		double angleBetweenLookingBackAndComingFrom = lookingBack.getAngleTo(comingFrom);
		double angularDifferenceSum = abs(angleBetweenComingFromAndStandingAt)+abs(angleBetweenLookingBackAndComingFrom);
		double angularDifferenceBetweenThoseTwo = getAngularDifference(angleBetweenComingFromAndStandingAt, angleBetweenLookingBackAndComingFrom);
		double expectedAngle;
		if (anglesSortOfEqual(angleBetweenComingFromAndStandingAt-angularDifferenceBetweenThoseTwo, angleBetweenLookingBackAndComingFrom)) {
			expectedAngle = angleBetweenLookingBackAndComingFrom-angularDifferenceBetweenThoseTwo;
		} else {
			expectedAngle = angleBetweenLookingBackAndComingFrom+angularDifferenceBetweenThoseTwo;
		}
		float distanceBetweenComingFromAndStandingAt = comingFrom.getDistanceTo(standingAt);
		float distanceBetweenLookingBackAndComingFrom = lookingBack.getDistanceTo(comingFrom);
		float distanceDifference = distanceBetweenLookingBackAndComingFrom-distanceBetweenComingFromAndStandingAt;
		if (distanceDifference < 0) {
			distanceDifference *= -1;
		}
		float expectedDistance = distanceBetweenComingFromAndStandingAt*distanceBetweenComingFromAndStandingAt/distanceBetweenLookingBackAndComingFrom;
		Debug.startHold("likelihood");
		Debug.log("getLikelihood: " + distanceBetweenLookingBackAndComingFrom + " -> " + distanceBetweenComingFromAndStandingAt + " = " + expectedDistance + " / " + goingTo.getDistanceTo(standingAt) + " , " + angleBetweenLookingBackAndComingFrom + " -> " + angleBetweenComingFromAndStandingAt + " = " + expectedAngle + " / " + standingAt.getAngleTo(goingTo));
		Debug.stopHold();
		return likelihoodFormula(distanceImpactOnLikelihoodFormula(invertIfSmallerThanOne(goingTo.getDistanceTo(standingAt)/expectedDistance)), angularDifferenceToExpectedImpactOnLikelihoodFormula(((float) (getAngularDifference(expectedAngle, standingAt.getAngleTo(goingTo))/Math.PI))), angularDifferenceToEachOtherImpactOnLikelihoodFormula(((float) (angularDifferenceSum/Math.PI))));
	}
	
	public static float pow(float a, float b) {
		return (float) Math.pow(a, b);
	}
	
	/** CHANGED SINCE SingleSpider.java !!! **/
	public static double getAngularDifference(double angle1, double angle2) {
		double dif = angle1-angle2;
		while (dif < 0) {
			dif += pi2;
		}
		while (dif >= pi2) {
			dif -= pi2;
		}
		if (dif >= pi) {
			dif = pi2-dif;
		}
		return Math.min(dif, pi2-dif);
	}
	
	public static boolean anglesSortOfEqual(double a1, double a2) {
		while (a1 < 0) {
			a1 += pi2;
		}
		while (a1 >= pi2) {
			a1 -= pi2;
		}
		while (a2 < 0) {
			a2 += pi2;
		}
		while (a2 >= pi2) {
			a2 -= pi2;
		}
		double dif = a1-a2;
		if (Math.abs(dif) < 0.00000000001) {
			return true;
		}
		if (Math.abs(dif-pi2) < 0.00000000001) {
			return true;
		}
		if (Math.abs(dif+pi2) < 0.00000000001) {
			return true;
		}
		return false;
	}
	
	public static float invertIfSmallerThanOne(float x) {
		if (x < 1) {
			return 1/x;
		}
		return x;
	}
	
	public boolean noneEquals(Segment... objects) {
		for (int i = 0; i < objects.length; i++) {
			for (int j = i+1; j < objects.length; j++) {
				if (objects[i].equals(objects[j]) || objects[i].equals(objects[j].invertDirection())) {
					return false;
				}
			}
		}
		return true;
	}
	
	public static double abs(double x) {
		return (x<0)?(-x):x;
	}
	
}
