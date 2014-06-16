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
public class SingleImplodingMemory implements SingleImplementation {
	
	/* -- START Parameters as formulas -- */
	
	/** Where x = distance **/
	public float distanceImpactOnLikelihoodFormula(float x) {
		return x;
	}
	
	/** Where x = angular difference to expected **/
	public float angularDifferenceToExpectedImpactOnLikelihoodFormula(float x) {
		return x;
	}
	
	/** Where x = actual angular differences **/
	public float angularDifferenceToEachOtherImpactOnLikelihoodFormula(float x) {
		return x;
	}
	
	/** Where x = total distance **/
	public float totalDistanceImpactOnLikelihoodFormula(float x) {
		return x;
	}
	
	/** Where x = distance impact, and y = angle impact (expected), and z = angle impact (to each other), and d = total distance impact **/
	public float likelihoodFormula(float x, float y, float z, float d) {
		//return 1.0f*x+1.0f*y+5f*z+8f*d;//werkt voor bla
		//return 1.0f*x+3.0f*y+3.0f*z+15.0f*d;//werkt voor bla EN tree
		//return 5.0f*x+5.0f*y+3.0f*z+30.0f*d;//werkt voor M
		//return 3.0f*x+10.0f*y+10.0f*z+35.0f*d;
		//return 3.0f*x+10.0f*y+3.0f*z+500.0f*d;
		return 1.0f*x+3.0f*y+3.0f*z+500.0f*d;
		//return d;
		//return 4.211009f*x+6.2477064f*y+4.1376147f*z+10.165137f*d;//gebaseerd op "Average occurence" na calibration van een heel aantal van de standaard test cases
	}
	
	public boolean likelihoodCondition(float newLikelihood, float oldLikelihood) {
		if (newLikelihood < oldLikelihood) {
			if (mozesTrials >= mozesTrialsMax) {
				return false;
			}
			mozesTrials++;
			return true;
		}
		return false;
	}
	
	public float howLikelyFormula(float newLikelihood, float oldLikelihood) {
		return newLikelihood/oldLikelihood;
	}
	
	public boolean makeOpenCondition(float longestLength, float oneToLongestLength) {
		return longestLength >= 1.7f*oneToLongestLength;
	}
	
	public boolean tryOpenEnabled = false;
	
	public boolean mozesEnabled = false;
	
	public boolean mozesBeforeTryOpenEnabled = false;
	public boolean mozesAfterTryOpenEnabled = false;
	
	public boolean mozesRedSeaEnabled = false;
	public boolean mozesSwitchEnabled = false;
	
	public final int mozesTrialsMax = 100;
	
	/** while testing Mozes options
	public boolean tryOpenEnabled = true;
	
	public boolean mozesEnabled = true;
	
	public boolean mozesBeforeTryOpenEnabled = true;
	public boolean mozesAfterTryOpenEnabled = true;
	
	public boolean mozesRedSeaEnabled = true;
	public boolean mozesSwitchEnabled = false;
	
	public final int mozesTrialsMax = 100;
	 */
	
	/* -- END Parameters as formulas -- */
	
	public final float pi = (float) (Math.PI);
	public final float pi2 = (float) (Math.PI*2);
	
	public int mozesTrials;
	
	public boolean mozesRedSeaEnabledRightNow;
	public boolean mozesSwitchEnabledRightNow;
	
	private HashSet<Segment> foundSegments;
	
	private void foundSegmentsAdd(Segment segment) { foundSegments.add(segment); }
	private void foundSegmentsRemove(Segment segment) { foundSegments.remove(segment); }
	
	public CurveType type = CurveType.CLOSED;
	public enum CurveType {
		CLOSED,
		OPEN
	}
	
	private static SingleImplodingMemory instance;
	public static SingleImplodingMemory getInstance() {
		if (instance == null) {
			new SingleImplodingMemory();
		}
		return instance;
	}
	
	public SingleImplodingMemory() {
		instance = this;
	}
	
	@Override
	public Segment[] getOutput(Node[] input) {
		return getOutput(input, ConvexHull.getConvexHull(input));
	}
	
	public Segment[] getOutput(Node[] input, HashSet<Segment> convexHull) {
		class Memory {
			public Segment originalSegment;
			public HashSet<Node> predecessorNodes;
			public float likeliness;
			public Segment newSegment1;
			public Segment newSegment2;
			public Memory(Segment originalSegment, HashSet<Node> predecessorNodes, float likeliness, Segment newSegment1, Segment newSegment2) {
				this.originalSegment = originalSegment;
				this.predecessorNodes = predecessorNodes;
				this.likeliness = likeliness;
				this.newSegment1 = newSegment1;
				this.newSegment2 = newSegment2;
			}
		}
		mozesTrials = 0;
		foundSegments = Conversion.toHashSet(convexHull);
		HashSet<Node> nodes = new HashSet<Node>();
		for (Node n : input) {
			nodes.add(n);
		}
		HashMap<Node, Memory> foundNodes = new HashMap<Node, Memory>();
		for (Segment segment : foundSegments) {
			Node node1 = segment.getNode1();
			Node node2 = segment.getNode2();
			if (!foundNodes.containsKey(node1)) {
				foundNodes.put(node1, new Memory(null, new HashSet<Node>(), 0, null, null));
			}
			if (!foundNodes.containsKey(node2)) {
				foundNodes.put(node2, new Memory(null, new HashSet<Node>(), 0, null, null));
			}
		}
		HashMap<Segment, ArrayList<Pair<Node, Float>>> likelinesses = new HashMap<Segment, ArrayList<Pair<Node, Float>>>();
		for (Segment segment : foundSegments) {
			ArrayList<Pair<Node, Float>> nodeLikelinesses = buildNodeLikelinesses(segment, nodes);
			likelinesses.put(segment, nodeLikelinesses);
		}
		while (foundNodes.size() != input.length) { // TODO so now when enough segments have been found, we can't correct errors anymore though
			Segment segmentWithSmallestLikeliness = null;
			Node nodeWithSmallestLikeliness = null;
			float smallestLikeliness = 1000000000;
			for (Entry<Segment, ArrayList<Pair<Node, Float>>> entry : likelinesses.entrySet()) {
				ArrayList<Pair<Node, Float>> nodeLikelinesses = entry.getValue();
				int i = 0;
				while (i < nodeLikelinesses.size()) {
					Pair<Node, Float> pair = nodeLikelinesses.get(i);
					Node node = pair.first();
					float likeliness = pair.second();
					if (likeliness < smallestLikeliness) {
						boolean valid = true;
						if (foundNodes.containsKey(node)) {
							Memory memory = foundNodes.get(node);
							if (memory.originalSegment == null) {
								valid = false;
							} else {
								valid = (likeliness < memory.likeliness);
							}
						}
						if (valid) {
							smallestLikeliness = likeliness;
							segmentWithSmallestLikeliness = entry.getKey();
							nodeWithSmallestLikeliness = node;
							break;
						}
					}
					i++;
				}
			}
			HashSet<Segment> newSegments = new HashSet<Segment>();
			HashSet<Segment> oldSegments = new HashSet<Segment>();
			if (foundNodes.containsKey(nodeWithSmallestLikeliness)) {
				System.out.println("Yes");
				HashSet<Node> toRemoveFromFoundNodes = new HashSet<Node>();
				for (Entry<Node, Memory> entry : foundNodes.entrySet()) {
					Memory memory = entry.getValue();
					Node node = entry.getKey();
					if (memory.predecessorNodes.contains(nodeWithSmallestLikeliness)) {
						oldSegments.add(memory.newSegment1);
						oldSegments.add(memory.newSegment2);
						toRemoveFromFoundNodes.add(node);
					} else if (node.equals(nodeWithSmallestLikeliness)) {
						oldSegments.add(memory.newSegment1);
						oldSegments.add(memory.newSegment2);
					}
				}
				for (Node node : toRemoveFromFoundNodes) {
					foundNodes.remove(node);
				}
				Segment newSegment1 = new Segment(nodeWithSmallestLikeliness, segmentWithSmallestLikeliness.getNode1());
				Segment newSegment2 = new Segment(nodeWithSmallestLikeliness, segmentWithSmallestLikeliness.getNode2());
				newSegments.add(newSegment1);
				newSegments.add(newSegment2);
				newSegments.add(foundNodes.get(nodeWithSmallestLikeliness).originalSegment);
				oldSegments.add(segmentWithSmallestLikeliness);
				foundNodes.put(nodeWithSmallestLikeliness, new Memory(segmentWithSmallestLikeliness, Conversion.union(foundNodes.get(segmentWithSmallestLikeliness.getNode1()).predecessorNodes, foundNodes.get(segmentWithSmallestLikeliness.getNode2()).predecessorNodes), smallestLikeliness, newSegment1, newSegment2));
			} else {
				System.out.println("No");
				Segment newSegment1 = new Segment(nodeWithSmallestLikeliness, segmentWithSmallestLikeliness.getNode1());
				Segment newSegment2 = new Segment(nodeWithSmallestLikeliness, segmentWithSmallestLikeliness.getNode2());
				newSegments.add(newSegment1);
				newSegments.add(newSegment2);
				oldSegments.add(segmentWithSmallestLikeliness);
				foundNodes.put(nodeWithSmallestLikeliness, new Memory(segmentWithSmallestLikeliness, Conversion.union(foundNodes.get(segmentWithSmallestLikeliness.getNode1()).predecessorNodes, foundNodes.get(segmentWithSmallestLikeliness.getNode2()).predecessorNodes), smallestLikeliness, newSegment1, newSegment2));
			}
			for (Segment segment : oldSegments) {
				System.out.println("OLD " + segment.getNode1Id() + " " + segment.getNode2Id());
				likelinesses.remove(segment);
				foundSegments.remove(segment);
			}
			for (Segment segment : newSegments) {
				System.out.println("NEW " + segment.getNode1Id() + " " + segment.getNode2Id());
				likelinesses.put(segment, buildNodeLikelinesses(segment, nodes));
				foundSegments.add(segment);
			}
		}
		
		//System.out.println("Run Mozes 1...");
		
		if (mozesBeforeTryOpenEnabled) {
			runMozes(input);
		}
		
		//System.out.println("Try to make open...");
		
		if (tryOpenEnabled) {
			tryMakeOpen(input);
		}
		
		//System.out.println("Run Mozes 2...");
		
		if (mozesAfterTryOpenEnabled) {
			runMozes(input);
		}
		
		//System.out.println("Conversion to array...");
		
		return Conversion.toArray(foundSegments, new Segment[foundSegments.size()]);
	}
	
	public void tryMakeOpen(Node[] input) {
		if (type == CurveType.CLOSED) {
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
				if (makeOpenCondition(longestLength, oneToLongestLength)) {
					foundSegments.remove(longestSegment);
					type = CurveType.OPEN;
					HashMap<Node, HashSet<Segment>> segmentsByNodes = new HashMap<Node, HashSet<Segment>>();
					for (Node n : input) {
						segmentsByNodes.put(n, new HashSet<Segment>());
					}
					for (Segment segment : foundSegments) {
						segmentsByNodes.get(segment.getNode1()).add(segment);
						segmentsByNodes.get(segment.getNode2()).add(segment);
					}
					/*System.out.println("N:");
					for (Entry<Node, HashSet<Segment>> entry : segmentsByNodes.entrySet()) {
						System.out.println(entry.getValue().size());
					}
					System.out.println("-M");*/
				}
			}
		}
	}
	
	public void runMozes(Node[] input) {
		if (mozesEnabled) {
			mozesRedSeaEnabledRightNow = mozesRedSeaEnabled;
			mozesSwitchEnabledRightNow = mozesSwitchEnabled;
			// should not change size variable afterwards, so made it final to prevent mistakes
			final int size = foundSegments.size();
			Segment[] segmentsInOrder;
			Node[] nodesInOrder;
			{
				Pair<Segment[], Node[]> constructedSingleCurve = constructSingleCurve(foundSegments, size, input);
				segmentsInOrder = constructedSingleCurve.first();
				nodesInOrder = constructedSingleCurve.second();
			}
			boolean busyTryingMozes = true;
			while (busyTryingMozes) {
				HashSet<Pair<Float, Runnable>> foundMozesChanges = new HashSet<Pair<Float, Runnable>>();
				if (mozesRedSeaEnabledRightNow) {
					for (int i = ((type==CurveType.CLOSED)?0:1); i < ((type==CurveType.CLOSED)?size:(size-2)); i++) {
						Node n = nodesInOrder[i];
						final Segment oldEgyptSegment1 = segmentsInOrder[wrap(i-1, size)];
						final Segment oldEgyptSegment2 = segmentsInOrder[i];
						final Segment oldEgyptSegment3 = segmentsInOrder[wrap(i+1, size)];
						final Segment oldEgyptSegment4 = segmentsInOrder[wrap(i+2, size)];
						try {
							final float oldEgyptLikelihood = getLikelihood(oldEgyptSegment1, oldEgyptSegment2, oldEgyptSegment3)+getLikelihood(oldEgyptSegment2, oldEgyptSegment3, oldEgyptSegment4);
							final Segment newEgyptSegment = new Segment(nodesInOrder[wrap(i-1, size)], nodesInOrder[wrap(i+1, size)]);
							final float newEgyptLikelihood = getLikelihood(oldEgyptSegment1, newEgyptSegment, oldEgyptSegment4);
							for (int j = ((type==CurveType.CLOSED)?0:1); j < ((type==CurveType.CLOSED)?size:(size-1)); j++) {
								//Debug.log("size="+size+" i="+i+" j="+j);
								final Segment oldIsraelSegment1 = segmentsInOrder[wrap(j-1, size)];
								final Segment oldIsraelSegment2 = segmentsInOrder[wrap(j, size)];
								final Segment oldIsraelSegment3 = segmentsInOrder[wrap(j+1, size)];
								//Debug.log("oldEgypt: "+oldEgyptSegment1.getNode1Id()+"->"+oldEgyptSegment1.getNode2Id()+" , "+oldEgyptSegment2.getNode1Id()+"->"+oldEgyptSegment2.getNode2Id()+" , "+oldEgyptSegment3.getNode1Id()+"->"+oldEgyptSegment3.getNode2Id()+" , "+oldEgyptSegment4.getNode1Id()+"->"+oldEgyptSegment4.getNode2Id()+" , ");
								//Debug.log("oldIsrael: "+oldIsraelSegment1.getNode1Id()+"->"+oldIsraelSegment1.getNode2Id()+" , "+oldIsraelSegment2.getNode1Id()+"->"+oldIsraelSegment2.getNode2Id()+" , "+oldIsraelSegment3.getNode1Id()+"->"+oldIsraelSegment3.getNode2Id());
								if (noneEquals(oldEgyptSegment1, oldEgyptSegment2, oldEgyptSegment3, oldEgyptSegment4, oldIsraelSegment1, oldIsraelSegment2, oldIsraelSegment3)) {
									try {
										final float oldIsraelLikelihood = getLikelihood(oldIsraelSegment1, oldIsraelSegment2, oldIsraelSegment3);
										Node israelNode2 = null;
										Node israelNode3 = null;
										{
											// find the good israel nodes
											israelNode2 = getNodeThatIsInBoth(oldIsraelSegment1, oldIsraelSegment2);
											israelNode3 = getNodeThatIsInBoth(oldIsraelSegment2, oldIsraelSegment3);
										}
										final Segment newIsraelSegment1 = new Segment(israelNode2, n);
										final Segment newIsraelSegment2 = new Segment(n, israelNode3);
										final float newIsraelLikelihood = getLikelihood(oldIsraelSegment1, newIsraelSegment1, newIsraelSegment2)+getLikelihood(newIsraelSegment1, newIsraelSegment2, oldIsraelSegment3);
										final float oldLikelihood = oldEgyptLikelihood+oldIsraelLikelihood;
										final float newLikelihood = newEgyptLikelihood+newIsraelLikelihood;
										if (likelihoodCondition(newLikelihood, oldLikelihood)) { // wel heel extreem drastisch dit: 'fixt' praktisch ALLES! TODO
											Runnable runnable = new Runnable() {
												
												@Override
												public void run() {
													Debug.log("Aight! Likelihoods: " + newLikelihood + " < " + oldLikelihood);
													Debug.log("Old Egypt likelihood 1:");
													getLikelihood(oldEgyptSegment1, oldEgyptSegment2, oldEgyptSegment3);
													Debug.release("likelihood");
													Debug.log("Old Egypt likelihood 2:");
													getLikelihood(oldEgyptSegment2, oldEgyptSegment3, oldEgyptSegment4);
													Debug.release("likelihood");
													Debug.log("Old Israel likelihood:");
													getLikelihood(oldIsraelSegment1, oldIsraelSegment2, oldIsraelSegment3);
													Debug.release("likelihood");
													Debug.log("New Egypt likelihood:");
													getLikelihood(oldEgyptSegment1, newEgyptSegment, oldEgyptSegment4);
													Debug.release("likelihood");
													Debug.log("New Israel likelihood 1:");
													getLikelihood(oldIsraelSegment1, newIsraelSegment1, newIsraelSegment2);
													Debug.release("likelihood");
													Debug.log("New Israel likelihood 2:");
													getLikelihood(newIsraelSegment1, newIsraelSegment2, oldIsraelSegment3);
													Debug.release("likelihood");
													foundSegmentsRemove(oldEgyptSegment2);
													foundSegmentsRemove(oldEgyptSegment3);
													foundSegmentsAdd(newEgyptSegment);
													foundSegmentsRemove(oldIsraelSegment2);
													foundSegmentsAdd(newIsraelSegment1);
													foundSegmentsAdd(newIsraelSegment2);
												}
											};
											foundMozesChanges.add(new Pair<Float, Runnable>(howLikelyFormula(newLikelihood, oldLikelihood), runnable));
										}
									} catch (IllegalStateException e) {}
								}
							}
						} catch (IllegalStateException e) {}
					}
				}
				if (mozesSwitchEnabledRightNow) {
					if (foundMozesChanges.size() == 0) {
						mozesRedSeaEnabledRightNow = false;
						for (int i = ((type==CurveType.CLOSED)?0:1); i < ((type==CurveType.CLOSED)?size:(size-1)); i++) {
							Node n = nodesInOrder[i];
							final Segment switchTopLeftSegment = segmentsInOrder[wrap(i-1, size)];
							final Segment switchLeftSegment = segmentsInOrder[i];
							final Segment switchBottomLeftSegment = segmentsInOrder[wrap(i+1, size)];
							try {
								final float oldSwitchLeftLikelihood = getLikelihood(switchTopLeftSegment, switchLeftSegment, switchBottomLeftSegment);
								for (int j = ((type==CurveType.CLOSED)?0:1); j < ((type==CurveType.CLOSED)?size:(size-1)); j++) {
									final Segment switchTopRightSegment = segmentsInOrder[wrap(j-1, size)];
									final Segment switchRightSegment = segmentsInOrder[wrap(j, size)];
									final Segment switchBottomRightSegment = segmentsInOrder[wrap(j+1, size)];
									if (noneEquals(switchRightSegment, switchLeftSegment, switchTopLeftSegment, switchBottomLeftSegment, switchTopRightSegment, switchBottomRightSegment)) {
										try {
											final float oldSwitchRightLikelihood = getLikelihood(switchTopRightSegment, switchRightSegment, switchBottomRightSegment);
											Node switchTopLeftNode = null;
											Node switchBottomLeftNode = null;
											Node switchTopRightNode = null;
											Node switchBottomRightNode = null;
											{
												switchTopLeftNode = getNodeThatIsInBoth(switchTopLeftSegment, switchLeftSegment);
												switchBottomLeftNode = getNodeThatIsInBoth(switchBottomLeftSegment, switchLeftSegment);
												switchTopRightNode = getNodeThatIsInBoth(switchTopRightSegment, switchRightSegment);
												switchBottomRightNode = getNodeThatIsInBoth(switchBottomRightSegment, switchRightSegment);
											}
											final Segment switchTopSegment = new Segment(switchTopLeftNode, switchTopRightNode);
											final Segment switchBottomSegment = new Segment(switchBottomLeftNode, switchBottomRightNode);
											final float newSwitchTopLikelihood = getLikelihood(switchTopLeftSegment, switchTopSegment, switchTopRightSegment);
											final float newSwitchBottomLikelihood = getLikelihood(switchBottomLeftSegment, switchBottomSegment, switchBottomRightSegment);
											final float oldLikelihood = oldSwitchLeftLikelihood+oldSwitchRightLikelihood;
											final float newLikelihood = newSwitchTopLikelihood+newSwitchBottomLikelihood;
											if (likelihoodCondition(newLikelihood, oldLikelihood)) { // wel heel extreem drastisch dit: 'fixt' praktisch ALLES! TODO
												Runnable runnable = new Runnable() {
													
													@Override
													public void run() {
														Debug.log("Aight (Switch)! Likelihoods: " + newLikelihood + " < " + oldLikelihood);
														foundSegmentsRemove(switchLeftSegment);
														foundSegmentsRemove(switchRightSegment);
														foundSegmentsAdd(switchTopSegment);
														foundSegmentsAdd(switchBottomSegment);
													}
												};
												foundMozesChanges.add(new Pair<Float, Runnable>(howLikelyFormula(newLikelihood, oldLikelihood), runnable));
											}
										} catch (IllegalStateException e) {}
									}
								}
							} catch (IllegalStateException e) {}
						}
					}
				}
				if (foundMozesChanges.size() > 0) {
					float bestHowLikely = Integer.MAX_VALUE;
					Runnable bestRunnable = null;
					for (Pair<Float, Runnable> foundMozesChange : foundMozesChanges) {
						float howLikely = foundMozesChange.first();
						if (howLikely < bestHowLikely) {
							bestHowLikely = howLikely;
							bestRunnable = foundMozesChange.second();
						}
					}
					if (bestRunnable != null) {
						bestRunnable.run();
						// TODO verander dit want om dit opnieuw te constructen is echt idioot
						// en hier is het vreselijke stuk: 'reconstruct de hele shit maar weer' zei Martijn!
						{
							Pair<Segment[], Node[]> constructedSingleCurve = constructSingleCurve(foundSegments, size, input);
							segmentsInOrder = constructedSingleCurve.first();
							nodesInOrder = constructedSingleCurve.second();
						}
					} else {
						busyTryingMozes = false;
					}
				} else {
					busyTryingMozes = false;
				}
			}
		}
	}

	private float likelinessFormula(float segmentLength, float distance1, float distance2) {
		return (distance1*distance1+distance2*distance2)/(segmentLength*segmentLength);
	}
	
	private ArrayList<Pair<Node, Float>> buildNodeLikelinesses(Segment segment, HashSet<Node> nodesToDo) {
		ArrayList<Pair<Node, Float>> nodeLikelinesses = new ArrayList<Pair<Node, Float>>();
		for (Node n : nodesToDo) {
			float segmentLength = segment.length();
			float distance1 = segment.getNode1().getDistanceTo(n);
			float distance2 = segment.getNode2().getDistanceTo(n);
			float likeliness = likelinessFormula(segmentLength, distance1, distance2);
			nodeLikelinesses.add(new Pair<Node, Float>(n, likeliness));
		}
		sortNodeLikelinesses(nodeLikelinesses);
		return nodeLikelinesses;
	}
	
	private void sortNodeLikelinesses(ArrayList<Pair<Node, Float>> nodeLikelinesses) {
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
	}
	
	/** Makes sure an integer is in the range (by applying (inefficient) mathematical modulo) [0, range) **/
	public int wrap(int value, int range) {
		while (value < 0) {
			value += range;
		}
		while (value >= range) {
			value -= range;
		}
		return value;
	}
	
	public Node getNodeThatIsInBoth(Segment segment1, Segment segment2) {
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
	
	// TODO make it so it also works for open curve (whereafter wrapping is impossible)
	public Pair<Segment[], Node[]> constructSingleCurve(HashSet<Segment> foundSegments, final int size, Node[] input) {
		//System.out.println("Call to constructSingleCurve");
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
		if (type == CurveType.CLOSED) {
			segmentsInOrder[0] = foundSegments.iterator().next();
			nodesInOrder[0] = segmentsInOrder[0].getNode2();
		} else if (type == CurveType.OPEN) {
			Node leaf = null;
			Segment segment = null;
			for (Entry<Node, HashSet<Segment>> entry : segmentsByNodes.entrySet()) {
				if (entry.getValue().size() == 1) {
					leaf = entry.getKey();
					segment = entry.getValue().iterator().next();
				}
			}
			segmentsInOrder[0] = segment;
			if (leaf == null || segment == null) {
				System.out.println(type);
				for (Entry<Node, HashSet<Segment>> entry : segmentsByNodes.entrySet()) {
					System.out.println(entry.getValue().size());
				}
			}
			if (leaf.equals(segment.getNode1())) {
				nodesInOrder[0] = segment.getNode2();
			} else {
				nodesInOrder[0] = segment.getNode1();
			}
		}
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
			if (newSegment == null) {
				System.out.println(type);
				for (Entry<Node, HashSet<Segment>> entry : segmentsByNodes.entrySet()) {
					System.out.println(entry.getValue().size());
				}
			}
			Node newNode1 = newSegment.getNode1();
			Node newNode2 = newSegment.getNode2();
			nodesInOrder[i] = (newNode1.equals(nodeToStartFrom))?(newNode2):(newNode1);
		}
		return new Pair<Segment[], Node[]>(segmentsInOrder, nodesInOrder);
	}
	
	public float getLikelihood(Segment line1, Segment line2, Segment line3) throws IllegalStateException {
		Debug.startHold("likelihood");
        if (line1.node2 != line2.node1) {
            if (line2.node2 != line3.node1) {
                line2 = line2.invertDirection();
            } else {
                line1 = line1.invertDirection();
            }   
        }   
        Node goingTo = line3.node2;
        Node standingAt = line3.node1;
        Node comingFrom = line2.node1;
        Node lookingBack = line1.node1;
        float totalDistance = goingTo.getDistanceTo(standingAt) + standingAt.getDistanceTo(comingFrom) + comingFrom.getDistanceTo(lookingBack);
		double angleBetweenComingFromAndStandingAt = rewrapAngle(comingFrom.getAngleTo(standingAt));
		double angleBetweenLookingBackAndComingFrom = rewrapAngle(lookingBack.getAngleTo(comingFrom));
		double angularDifferenceSum = abs(centerAngle(angleBetweenComingFromAndStandingAt))+abs(centerAngle(angleBetweenLookingBackAndComingFrom));
		double angularDifferenceBetweenThoseTwo = getAngularDifference(angleBetweenComingFromAndStandingAt, angleBetweenLookingBackAndComingFrom);
		double expectedAngle;
		Debug.log("anglesSortOfEqual: " + rewrapAngle(angleBetweenComingFromAndStandingAt-angularDifferenceBetweenThoseTwo) + " and " + rewrapAngle(angleBetweenLookingBackAndComingFrom));
		if (anglesSortOfEqual(rewrapAngle(angleBetweenLookingBackAndComingFrom-angularDifferenceBetweenThoseTwo), rewrapAngle(angleBetweenComingFromAndStandingAt))) {
			expectedAngle = rewrapAngle(angleBetweenComingFromAndStandingAt-angularDifferenceBetweenThoseTwo);
		} else {
			expectedAngle = rewrapAngle(angleBetweenComingFromAndStandingAt+angularDifferenceBetweenThoseTwo);
		}
		float distanceBetweenComingFromAndStandingAt = comingFrom.getDistanceTo(standingAt);
		float distanceBetweenLookingBackAndComingFrom = lookingBack.getDistanceTo(comingFrom);
		float distanceDifference = distanceBetweenLookingBackAndComingFrom-distanceBetweenComingFromAndStandingAt;
		if (distanceDifference < 0) {
			distanceDifference *= -1;
		}
		float expectedDistance;
		if (distanceBetweenComingFromAndStandingAt > distanceBetweenLookingBackAndComingFrom) {
			expectedDistance = distanceBetweenComingFromAndStandingAt+distanceDifference;
		} else {
			expectedDistance = distanceBetweenComingFromAndStandingAt*distanceBetweenComingFromAndStandingAt/distanceBetweenLookingBackAndComingFrom;
		}
		double actualNewAngle = rewrapAngle(standingAt.getAngleTo(goingTo));
		float actualNewDistance = goingTo.getDistanceTo(standingAt);
		if (distanceBetweenComingFromAndStandingAt == 0 || distanceBetweenLookingBackAndComingFrom == 0 || actualNewDistance == 0) {
			throw new IllegalStateException("Nope, some of the distances are 0.");
		}
		Debug.log("getLikelihood: " + distanceBetweenLookingBackAndComingFrom + " -> " + distanceBetweenComingFromAndStandingAt + " = " + expectedDistance + " / " + actualNewDistance + " , " + angleBetweenLookingBackAndComingFrom + " -> " + angleBetweenComingFromAndStandingAt + " (" + angularDifferenceBetweenThoseTwo + ") = " + expectedAngle + " / " + actualNewAngle);
		float distanceImpact = distanceImpactOnLikelihoodFormula(invertIfSmallerThanOne(actualNewDistance/expectedDistance));
		float angularDifferenceToExpectedImpact = angularDifferenceToExpectedImpactOnLikelihoodFormula(((float) (getAngularDifference(expectedAngle, actualNewAngle)/Math.PI)));
		float angularDifferenceToEachOtherImpact = angularDifferenceToEachOtherImpactOnLikelihoodFormula(((float) (angularDifferenceSum/Math.PI)));
		float totalDistanceImpact = totalDistanceImpactOnLikelihoodFormula(totalDistance);
		Debug.log("Likelihoods: " + distanceImpact + " - " + angularDifferenceToExpectedImpact + " - " + angularDifferenceToEachOtherImpact + " - " + totalDistanceImpact);
		float likelihood = likelihoodFormula(distanceImpact, angularDifferenceToExpectedImpact, angularDifferenceToEachOtherImpact, totalDistanceImpact);
		Debug.log("Final likelihood: " + likelihood);
		Debug.stopHold();
		return likelihood;
	}
	
	public float pow(float a, float b) {
		return (float) Math.pow(a, b);
	}
	
	/** CHANGED SINCE SingleSpider.java !!! **/
	public double getAngularDifference(double angle1, double angle2) {
		angle1 = rewrapAngle(angle1);
		angle2 = rewrapAngle(angle2);
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
		return dif;
	}
	
	public boolean anglesSortOfEqual(double a1, double a2) {
		a1 = rewrapAngle(a1);
		a2 = rewrapAngle(a2);
		double dif = a1-a2;
		if (Math.abs(dif) < 0.00000001) {
			return true;
		}
		if (Math.abs(dif-pi2) < 0.00000001) {
			return true;
		}
		if (Math.abs(dif+pi2) < 0.00000001) {
			return true;
		}
		return false;
	}
	
	public float invertIfSmallerThanOne(float x) {
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
	
	public double abs(double x) {
		return (x<0)?(-x):x;
	}
	
	public double rewrapAngle(double angle) {
		while (angle < 0) {
			angle += pi2;
		}
		while (angle >= pi2) {
			angle -= pi2;
		}
		return angle;
	}
	
	public double centerAngle(double angle) {
		while (angle < -pi) {
			angle += pi2;
		}
		while (angle >= pi) {
			angle -= pi2;
		}
		return angle;
	}
	
}
