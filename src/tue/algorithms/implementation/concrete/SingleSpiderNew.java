package tue.algorithms.implementation.concrete;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;

import tue.algorithms.implementation.concrete.SingleSpiderNew.Spider.Viewpoint.Holiday;
import tue.algorithms.implementation.general.SingleImplementation;
import tue.algorithms.utility.Node;
import tue.algorithms.utility.Segment;

public class SingleSpiderNew extends SingleImplementation {
	
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
			// assuming there is already one segment present now
			while (true) {
				if (nodesToDo.size() == 0) {
					break;
				}
				Viewpoint viewpoint = new Viewpoint(viewpoints.size());
				Segment newSegment = viewpoint.getCurrentSegment();
				if (newSegment == null) {
					return;
				}
				//System.out.println("New segment is not null");
				foundSegments.add(newSegment);
				nodesToDo.remove(newSegment.getNode2());
				viewpoints.add(viewpoint);
			}
			Segment lastSegment = new Segment(foundSegments.get(foundSegments.size()-1).getNode2(), firstNode);
			foundSegments.add(lastSegment);
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
					for (int u = 0; u < foundSegments.size()-1; u++) {
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
					float likelihood = invertIfSmallerThanOne(n.getDistanceTo(standingAt)/minimalDistance.get(standingAt))+((float) (getAngularDifference(expectedAngle, standingAt.getAngleTo(n))*5/Math.PI));
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
			
			public Segment getCurrentSegment() {
				if (currentHolidayIndex >= holidays.size()) {
					return null;
				}
				return new Segment(standingAt, holidays.get(currentHolidayIndex).goingTo);
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
			
		}
		
	}
	
	@Override
	public Segment[] getOutput(Node[] input) {
		System.out.println("Running SpiderNew:");
		System.out.println("- random id = " + ((int) (Math.random()*100000)));
		System.out.println("- input size = " + input.length);
		long startTime = System.nanoTime();
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
		System.out.println("Done:");
		long time = (System.nanoTime()-startTime)/1000000;
		System.out.println("- time taken (millis) = " + time);
		return output;
	}
	
}
