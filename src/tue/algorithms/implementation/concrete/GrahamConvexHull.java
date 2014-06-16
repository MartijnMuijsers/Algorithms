package tue.algorithms.implementation.concrete;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Stack;

import tue.algorithms.utility.Node;
import tue.algorithms.utility.Point;
import tue.algorithms.utility.Segment;

public class GrahamConvexHull {
	
	public static HashSet<Segment> getConvexHull(final Node[] input) {
		Node[] inputClone = new Node[input.length];
		for (int i = 0; i < input.length; i++) {
			inputClone[i] = input[i];
		}
		
		int lowestI = -1;
		Node lowest = null;
		for (int i = 0; i < inputClone.length; i++) {
			if (lowest == null) {
				lowestI = i;
				lowest = inputClone[i];
			} else {
				boolean change = false;
				Node node = inputClone[i];
				float y0 = node.getY();
				float y1 = lowest.getY();
				if (y0 < y1) {
					change = true;
				} else if (y0 == y1) {
					float x0 = node.getX();
					float x1 = lowest.getX();
					if (x0 < x1) {
						change = true;
					}
				}
				if (change) {
					lowestI = i;
					lowest = node;
				}
			}
		}
		
		Node safeFirst = inputClone[0];
		inputClone[0] = lowest;
		inputClone[lowestI] = safeFirst;
		final Node finalLowest = lowest;
		
		Arrays.sort(inputClone, 1, inputClone.length, new Comparator<Node>() {
			
			@SuppressWarnings("null")
			@Override
			public int compare(Node arg0, Node arg1) {
				double theta0 = Math.atan2(arg0.getY()-finalLowest.getY(), arg0.getX()-finalLowest.getX());
				double theta1 = Math.atan2(arg1.getY()-finalLowest.getY(), arg1.getX()-finalLowest.getX());
				if (theta0 < theta1) {
					return -1;
				} else if (theta0 == theta1) {
					double distance0 = Math.sqrt(sqr(arg0.getY()-finalLowest.getY())+sqr(arg0.getX()-finalLowest.getX()));
					double distance1 = Math.sqrt(sqr(arg1.getY()-finalLowest.getY())+sqr(arg1.getX()-finalLowest.getX()));
					if (distance0 < distance1) {
						return -1;
					} else if (distance0 == distance1) {
						return 0;
					} else {
						return 1;
					}
				} else {
					return 1;
				}
			}
			
		});
		
		Stack<Node> hull = new Stack<Node>();
		
		hull.push(inputClone[0]);
		
		int k1;
        for (k1 = 1; k1 < inputClone.length; k1++) {
        	if (!inputClone[0].equals(inputClone[k1])) {
        		break;
        	}
        }
        
        int k2;
        for (k2 = k1 + 1; k2 < inputClone.length; k2++) {
            if (ccw(inputClone[0], inputClone[k1], inputClone[k2]) != 0){
            	break;
            }
        }
        hull.push(inputClone[k2-1]);
        
        for (int i = k2; i < inputClone.length; i++) {
            Node top = hull.pop();
            while (ccw(hull.peek(), top, inputClone[i]) <= 0) {
                top = hull.pop();
            }
            hull.push(top);
            hull.push(inputClone[i]);
            //System.out.println("Graham convex hull loop progress: " + i + " / " + (inputClone.length-1));//DEBUG
        }
        
        Node[] resultNodes = new Node[hull.size()];
        int i = 0;
        while (hull.size() > 0) {
        	resultNodes[i] = hull.pop();
        	i++;
        }
        HashSet<Segment> resultSegments = new HashSet<Segment>();
        for (int j = 0; j < resultNodes.length-1; j++) {
        	resultSegments.add(new Segment(resultNodes[j], resultNodes[j+1]));
        }
        resultSegments.add(new Segment(resultNodes[resultNodes.length-1], resultNodes[0]));
        
        return resultSegments;
	}
	
	public static float ccw(Point p1, Point p2, Point p3) {
	    return (p2.getX() - p1.getX())*(p3.getY() - p1.getY()) - (p2.getY() - p1.getY())*(p3.getX() - p1.getX());
	}
	
	public static float sqr(float x) {
		return x*x;
	}
	
}
