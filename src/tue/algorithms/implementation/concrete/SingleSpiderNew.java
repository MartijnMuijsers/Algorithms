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
	
public static double pi2 = Math.PI*2;
	
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
	
	public static double getAngularDifference(double angle1, double angle2) {
		double dif = angle1-angle2;
		while (dif < 0) {
			dif += pi2;
		}
		while (dif > pi2) {
			dif -= pi2;
		}
		return Math.min(dif, pi2-dif);
	}
	
public static class Spider {
		
		public Spider(HashSet<Node> nodes) {
			this.nodes = nodes;
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
			Node firstNode = deNodeDieDeKleineYHeeft();
			Node secondNode = closestToFirst();
			Node thirdNode = inIederGevalNietDeEersteMaarClosestToSecond();
			foundSegments.add(new Segment(firstNode, closestNodeToFirst));
			foundSegments.add(new Segment(secondNode, closestNodeToSecond));
			nodesToDo.remove(firstNode);
			nodesToDo.remove(secondNode);
			nodesToDo.remove(thirdNode);
			// assuming there is already one segment present now
			while (true) {
				if (nodesToDo.size() == 0) {
					break;
				}
				Segment lastSegment = foundSegments.get(foundSegments.size()-1);
				Viewpoint viewpoint = new Viewpoint(lastSegment.getNode2(), lastSegment.getNode1());
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
			
			public Viewpoint(Node standingAt, Node comingFrom) {
				holidays = new ArrayList<Holiday>();
				this.standingAt = viewpoints.get(id-1).getFoundSegment().getNode2();
				this.comingFrom = viewpoints.get(id-1).getFoundSegment().getNode1();
				this.comingFrom = viewpoints.get(id-2).getFoundSegment().getNode1();
				this.comingFrom = comingFrom;
				angleBetweenStandingAtAndComingFrom = standingAt.getAngleTo(comingFrom);
				// calculate holidays
				//System.out.println("Calculating holidays for viewpoint...");
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
						//System.out.println("Intersects, so nope");
						continue;
					}
					// get likelihood
					float likelihood = n.getDistanceTo(standingAt)/minimalDistance.get(standingAt)+((float) (getAngularDifference(angleBetweenStandingAtAndComingFrom, standingAt.getAngleTo(n))*5/Math.PI));
					// add to holidays
					//System.out.println("Added node with n="+n+" and likelihood="+likelihood);
					holidays.add(new Holiday(n, likelihood));
				}
				Collections.sort(holidays, holidayComparator);
				currentHolidayIndex = 0;
			}
			
			public ArrayList<Holiday> holidays;
			public Node standingAt;
			public Node comingFrom;
			public double angleBetweenStandingAtAndComingFrom;
			public int currentHolidayIndex;
			
			public Segment getCurrentSegment() {
				//System.out.println("Getting current segment from a Viewpoint...");
				//System.out.println("That Viewpoint has currentHolidayIndex="+currentHolidayIndex+" and holidays.size()="+holidays.size());
				if (currentHolidayIndex >= holidays.size()) {
					//System.out.println("^ Ouch, returning null");
					return null;
				}
				//System.out.println("^ Not returning null");
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
			
		}
		
	}
	
	@Override
	public Segment[] getOutput(Node[] input) {
		System.out.println("Running SpiderNew (random id = " + ((int) (Math.random()*100000)) + ")");
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
		System.out.println("Done.");
		return output;
	}
	
}
