package tue.algorithms.implementation.concrete;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;

import tue.algorithms.implementation.concrete.SingleSpiderNew.Spider.Viewpoint.Holiday;
import tue.algorithms.implementation.general.SingleImplementation;
import tue.algorithms.other.Debug;
import tue.algorithms.utility.Node;
import tue.algorithms.utility.Segment;

/**
 * Just a backup, don't change please, this is purely for reference.
 * 
 * @author Martijn
 */
public class SingleSpiderNew extends SingleImplementation {
	
	/* -- START Parameters as formulas -- */
	
	/** Where x = distance **/
	public static float distanceImpactOnLikelihoodFormula(float x) {
		return pow(x, 3);
	}
	
	/** WHere x = angular difference **/
	public static float angleImpactOnLikelihoodFormula(float x) {
		return 2*pow(x, 3);
	}
	
	/** Where x = distance impact, and y = angle impact **/
	public static float likelihoodFormula(float x, float y) {
		return x+y;
	}
	
	/** Where x = initial likelihood (likelihood of the first holiday from that viewpoint) **/
	public static float maxLikelihoodFormula(float x) {
		return maxLikelihoodFactor*x;
	}
	
	/* -- END Parameters as formulas -- */
	
	public static final float initialMaxLikelihoodFactor = 1.5f;
	public static final float maxLikelihoodFactorIncreaseFactor = 1.2f;
	
	public static float maxLikelihoodFactor;
	
	public static final double pi = Math.PI;
	public static final double pi2 = Math.PI*2;
	
	public static Comparator<Holiday> holidayComparator = new Comparator<Holiday>() {
		
		@Override
		public int compare(Holiday arg0, Holiday arg1) {
			if (arg0.likelihood < arg1.likelihood) {
				return -1;
			} else if (arg0.likelihood > arg1.likelihood) {
				return 1;
			}
			return 0;
		}
		
	};
	
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
	
	public static class Spider {
		
		public Spider(HashSet<Node> nodes) {
			this.nodes = new HashSet<Node>();
			for (Node node : nodes) {
				this.nodes.add(node);
			}
			this.nodesToDo = new HashSet<Node>();
			for (Node node : nodes) {
				this.nodesToDo.add(node);
			}
			this.foundSegments = new ArrayList<Segment>();
			this.viewpoints = new ArrayList<Viewpoint>();
			minimalDistance = new HashMap<Node, Float>();
			for (Node n : nodes) {
				float minDistance = Float.MAX_VALUE;
				for (Node m : nodes) {
					if (!n.equals(m)) {
						float distance = n.getDistanceTo(m);
						if (distance < minDistance) {
							minDistance = distance;
						}
					}
				}
				minimalDistance.put(n, minDistance);
			}
		}
		
		public ArrayList<Segment> foundSegments;
		public ArrayList<Viewpoint> viewpoints;
		public HashSet<Node> nodes;
		public HashSet<Node> nodesToDo;
		public HashMap<Node, Float> minimalDistance;
		
		public void execute() {
			maxLikelihoodFactor = initialMaxLikelihoodFactor;
			Node firstNode = null;
			{
				float smallestY = 1000000000;
				for (Node n : nodesToDo) {
					float y = n.getY();
					if (y < smallestY) {
						firstNode = n;
						smallestY = y;
					}
				}
			}
			nodesToDo.remove(firstNode);
			Node secondNode = null;
			{
				float smallestDistance = 1000000000;
				for (Node n : nodesToDo) {
					float distance = n.getDistanceTo(firstNode);
					if (distance < smallestDistance) {
						secondNode = n;
						smallestDistance = distance;
					}
				}
			}
			nodesToDo.remove(secondNode);
			Node thirdNode = null;
			{
				float smallestDistance = 1000000000;
				for (Node n : nodesToDo) {
					float distance = n.getDistanceTo(secondNode);
					if (distance < smallestDistance) {
						thirdNode = n;
						smallestDistance = distance;
					}
				}
			}
			nodesToDo.remove(thirdNode);
			foundSegments.add(new Segment(firstNode, secondNode));
			foundSegments.add(new Segment(secondNode, thirdNode));
			// assuming here that at least two segments are present
			while (true) {
				boolean doRewind = false;
				if (nodesToDo.size() == 0) {
					// try to make last segment, if fails, do a rewind TODO, else break
					Segment lastSegment = new Segment(foundSegments.get(foundSegments.size()-1).getNode2(), firstNode);
					foundSegments.add(lastSegment);
					// ^ TODO check intersections: not allowed!
					break;
				}
				Viewpoint viewpoint = new Viewpoint(viewpoints.size());
				Segment newSegment = viewpoint.getCurrentSegment();
				if (newSegment == null) {
					doRewind = true;
				} else {
					foundSegments.add(newSegment);
					nodesToDo.remove(newSegment.getNode2());
					viewpoints.add(viewpoint);
				}
				if (doRewind) {
					///Debug.log("Doing a rewind at viewpoints.size() = " + viewpoints.size());
					boolean successfulRewind = false;
					for (int u = viewpoints.size()-1; u >= 0; u--) {
						Viewpoint v = viewpoints.get(u);
						v.goToNextSegment();
						Segment newRewindSegment = v.getCurrentSegment();
						if (newRewindSegment != null) {
							///Debug.log("Rewind successful at u = " + u);
							// remove all stuffs
							for (int z = viewpoints.size()-1; z > u; z--) {
								viewpoints.remove(z);
								Segment segmentToRemove = foundSegments.get(z+2);
								Node nodeToAddToNodesToDo = segmentToRemove.getNode2();
								nodesToDo.add(nodeToAddToNodesToDo);
								foundSegments.remove(z+2);
							}
							Segment segmentToRemove = foundSegments.get(u+2);
							Node nodeToAddToNodesToDo = segmentToRemove.getNode2();
							nodesToDo.add(nodeToAddToNodesToDo);
							foundSegments.remove(u+2);
							// add new segment
							foundSegments.add(newRewindSegment);
							nodesToDo.add(newRewindSegment.getNode2());
							// finish rewind
							successfulRewind = true;
							break;
						}
					}
					if (!successfulRewind) {
						maxLikelihoodFactor *= maxLikelihoodFactorIncreaseFactor;
						Debug.log("Max likelihood factor is now " + maxLikelihoodFactor);
						///Scanner scanner = new Scanner(System.in);
						///scanner.nextLine();
						// remove ALL the shit :D
						viewpoints.clear();
						while (foundSegments.size() > 2) {
							foundSegments.remove(2);
						}
						for (Node node : nodes) {
							nodesToDo.add(node);
						}
						nodesToDo.remove(firstNode);
						nodesToDo.remove(secondNode);
						nodesToDo.remove(thirdNode);
					}
				}
			}
		}
		
		public class Viewpoint {
			
			private int id;
			
			public Viewpoint(int id) {
				this.id = id;
				holidays = new ArrayList<Holiday>();
				this.standingAt = foundSegments.get(id+1).getNode2();
				this.comingFrom = foundSegments.get(id+1).getNode1();
				this.lookingBack = foundSegments.get(id).getNode1();
				double angleBetweenComingFromAndStandingAt = comingFrom.getAngleTo(standingAt);
				double angleBetweenLookingBackAndComingFrom = lookingBack.getAngleTo(comingFrom);
				double angularDifferenceBetweenThoseTwo = getAngularDifference(angleBetweenComingFromAndStandingAt, angleBetweenLookingBackAndComingFrom);
				if (anglesSortOfEqual(angleBetweenComingFromAndStandingAt-angularDifferenceBetweenThoseTwo, angleBetweenLookingBackAndComingFrom)) {
					expectedAngle = angleBetweenLookingBackAndComingFrom-angularDifferenceBetweenThoseTwo;
				} else {
					expectedAngle = angleBetweenLookingBackAndComingFrom+angularDifferenceBetweenThoseTwo;
				}
				for (Node n : nodesToDo) {
					// check for intersections with existing segments
					Segment segmentToBe = new Segment(n, standingAt);
					boolean intersects = false;
					for (int u = 0; u < id+1; u++) {
						Segment s = foundSegments.get(u);
						if (s.intersectsWith(segmentToBe)) {
							intersects = true;
							break;
						}
					}
					if (intersects) {
						continue;
					}
					// get likelihood
					float likelihood = likelihoodFormula(distanceImpactOnLikelihoodFormula(invertIfSmallerThanOne(n.getDistanceTo(standingAt)/minimalDistance.get(standingAt))), angleImpactOnLikelihoodFormula(((float) (getAngularDifference(expectedAngle, standingAt.getAngleTo(n))/Math.PI))));
					// add to holidays
					holidays.add(new Holiday(n, likelihood));
				}
				Collections.sort(holidays, holidayComparator);
				currentHolidayIndex = 0;
			}
			
			public ArrayList<Holiday> holidays;
			public Node standingAt;
			public Node comingFrom;
			public Node lookingBack;
			public double expectedAngle;
			public int currentHolidayIndex;
			
			public float initialLikelihood() {
				if (holidays.size() == 0) {
					return 0;
				}
				return holidays.get(0).likelihood;
			}
			
			public Segment getCurrentSegment() {
				if (currentHolidayIndex >= holidays.size()) {
					return null;
				}
				Holiday holiday = holidays.get(currentHolidayIndex);
				if (holiday.likelihood > maxLikelihoodFormula(initialLikelihood())) {
					return null;
				}
				return new Segment(standingAt, holiday.goingTo);
			}
			
			public void goToNextSegment() {
				currentHolidayIndex++;
			}
			
			public class Holiday {
				
				public Holiday(Node goingTo, float likelihood) {
					this.goingTo = goingTo;
					this.likelihood = likelihood;
				}
				
				public Node goingTo;
				public float likelihood;
				
			}
			
			public Segment getFoundSegment() {
				return foundSegments.get(id+2);
			}
			
			public float getLikelihoodForANode(Node n) {
				for (Holiday holiday : holidays) {
					
				}
				return 0; // Added by Rob to fix compiler error.
			}
			
		}
		
	}
	
	@Override
	public Segment[] getOutput(Node[] input) {
		/*Debug.log("Running SpiderNew:");
		Debug.log("- random id = " + ((int) (Math.random()*100000)));
		Debug.log("- input size = " + input.length);*/
		HashSet<Node> inputSet = new HashSet<Node>();
		for (Node n : input) {
			inputSet.add(n);
		}
		Spider spider = null;
		spider = new Spider(inputSet);
		spider.execute();
		ArrayList<Segment> outputList = spider.foundSegments;
		Segment[] output = new Segment[outputList.size()];
		int i = 0;
		for (Segment s : outputList) {
			output[i] = s;
			i++;
		}
		/*Debug.log("Done:");*/
		return output;
	}
	
}
