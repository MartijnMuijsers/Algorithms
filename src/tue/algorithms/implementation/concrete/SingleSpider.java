package tue.algorithms.implementation.concrete;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;

import tue.algorithms.implementation.concrete.SingleSpider.Spider.Viewpoint.Holiday;
import tue.algorithms.implementation.general.SingleImplementation;
import tue.algorithms.utility.Node;
import tue.algorithms.utility.Segment;

public class SingleSpider extends SingleImplementation {
	
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
		public boolean failed = false;
		public boolean checkLastIntersection = false;
		
		public void execute(boolean checkLastIntersection) {
			this.checkLastIntersection = checkLastIntersection;
			execute();
		}
		
		public void execute() {
			for (int secondNodeI = 0; secondNodeI < nodes.size()-1; secondNodeI++) {
				HashSet<Node> nodesToStartWith = new HashSet<Node>();
				for (Node node : nodes) {
					nodesToStartWith.add(node);
				}
				int firstNodeTime = 0;
				for (Node firstNode : nodesToStartWith) {
					failed = false;
					firstNodeTime++;
					//////System.out.println("First node with firstNodeTime="+firstNodeTime + " and secondNodeI="+secondNodeI);
					nodesToDo = new HashSet<Node>();
					for (Node node : nodes) {
						nodesToDo.add(node);
					}
					foundSegments = new ArrayList<Segment>();
					viewpoints = new ArrayList<Viewpoint>();
					nodesToDo.remove(firstNode);
					final Node finalFirstNode = firstNode;
					// choose the closest node
					float minDistanceToFirst = Float.MAX_VALUE;
					ArrayList<Node> nodesToSortByDistanceToFirstNode = new ArrayList<Node>();
					for (Node node : nodesToDo) {
						nodesToSortByDistanceToFirstNode.add(node);
					}
					Collections.sort(nodesToSortByDistanceToFirstNode, new Comparator<Node>() {
						@Override
						public int compare(Node arg0, Node arg1) {
							float dist0 = arg0.getDistanceTo(finalFirstNode);
							float dist1 = arg1.getDistanceTo(finalFirstNode);
							if (dist0 < dist1) {
								return -1;
							} else if (dist0 > dist1) {
								return 1;
							}
							return 0;
						}
					});
					Node closestNodeToFirst = nodesToSortByDistanceToFirstNode.get(secondNodeI);
					foundSegments.add(new Segment(firstNode, closestNodeToFirst));
					nodesToDo.remove(closestNodeToFirst);
					// assuming there is already one segment present now
					while (true) {
						//System.out.println("Main loop with nodesToDo.size()="+nodesToDo.size()+" and foundSegments.size()="+foundSegments.size());
						if (nodesToDo.size() == 0) {
							// we're done
							////System.out.println("We're done!");
							break;
						}
						Segment lastSegment = foundSegments.get(foundSegments.size()-1);
						Viewpoint viewpoint = new Viewpoint(lastSegment.getNode2(), lastSegment.getNode1());
						Segment newSegment = viewpoint.getCurrentSegment();
						if (newSegment == null) {
							//System.out.println("New segment is null");
							int viewpointToRewindTo = -1;
							float minLikelihoodDifferenceToNextSegment = Float.MAX_VALUE;
							{
								int i = 0;
								for (Viewpoint v : viewpoints) {
									float likelihoodDifferenceToNextSegment = v.getLikelihoodDifferenceToNextSegment();
									if (likelihoodDifferenceToNextSegment < minLikelihoodDifferenceToNextSegment) {
										minLikelihoodDifferenceToNextSegment = likelihoodDifferenceToNextSegment;
										viewpointToRewindTo = i;
									}
									i++;
								}
							}
							if (viewpointToRewindTo == -1) {
								// algorithm has completely absolutely failed! Woohoo! TODO
								// meaning: every Viewpoint has run out of Holidays to try
								////System.out.println("Algorithm has absolutely failed!");
								failed = true;
								// perhaps restart?
								break;
							}
							for (int i = viewpoints.size()-1; i > viewpointToRewindTo; i--) {
								Viewpoint v = viewpoints.get(i);
								viewpoints.remove(i);
							}
							for (int i = foundSegments.size()-1; i > viewpointToRewindTo; i--) {
								Segment segmentToRemove = foundSegments.get(i);
								//System.out.println("Added node when rewinding ("+segmentToRemove.getNode2().getId()+")");
								nodesToDo.add(segmentToRemove.getNode2());
								foundSegments.remove(i);
							}
							Viewpoint actualViewpointToRewindTo = viewpoints.get(viewpointToRewindTo);
							actualViewpointToRewindTo.goToNextSegment();
							foundSegments.add(actualViewpointToRewindTo.getCurrentSegment());
							continue;
						}
						//System.out.println("New segment is not null");
						foundSegments.add(newSegment);
						nodesToDo.remove(newSegment.getNode2());
						//System.out.println("Removed a node ("+newSegment.getNode2().getId()+")");
						viewpoints.add(viewpoint);
					}
					Segment lastSegment = new Segment(foundSegments.get(foundSegments.size()-1).getNode2(), firstNode);
					if (checkLastIntersection) {
						for (Segment segment : foundSegments) {
							if (segment.getNode1Id() == lastSegment.getNode1Id() || segment.getNode2Id() == lastSegment.getNode1Id() || segment.getNode1Id() == lastSegment.getNode2Id() || segment.getNode2Id() == lastSegment.getNode2Id()) {
								continue;
							}
							if (lastSegment.intersectsWith(segment)) {
								////System.out.println("Failed by intersection");
								failed = true;
								break;
							}
						}
					}
					foundSegments.add(lastSegment);
					////System.out.println("Done! (result size " + foundSegments.size() + ")");
					if (!failed) {
						break;
					}
				}
			}
		}
		
		public class Viewpoint {
			
			public Viewpoint(Node standingAt, Node comingFrom) {
				holidays = new ArrayList<Holiday>();
				this.standingAt = standingAt;
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
			
			public void goToNextSegment() {
				currentHolidayIndex++;
			}
			
			public float getLikelihoodDifferenceToNextSegment() {
				if (currentHolidayIndex >= holidays.size()-1) {
					//TODO wat hier te doen? (als geen volgend segment)
					// voor nu maar return heel veel opdat deze niet veranderd wordt, en zodat als er absoluut geen meer is, viewpoinToRewindTo -1 blijft
					return Float.MAX_VALUE;
				}
				return holidays.get(currentHolidayIndex+1).likelihood-holidays.get(currentHolidayIndex).likelihood;
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
		System.out.println("Running Spider (random id = " + ((int) (Math.random()*100000)) + ")");
		HashSet<Node> inputSet = new HashSet<Node>();
		for (Node n : input) {
			inputSet.add(n);
		}
		Spider spider = null;
		spider = new Spider(inputSet);
		spider.execute();
		if (spider.failed) {
			System.out.println("Oh no!");
			spider = new Spider(inputSet);
			spider.execute(true);
		}
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
