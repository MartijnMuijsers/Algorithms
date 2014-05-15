package tue.algorithms.implementation.concrete;

import java.util.HashSet;

import tue.algorithms.utility.Line;
import tue.algorithms.utility.Node;
import tue.algorithms.utility.Point;
import tue.algorithms.utility.Segment;

/**
 * Under development. Testing ground here.
 * @author Martijn
 */
public abstract class ConvexHull {
	
	public static HashSet<Segment> getConvexHull(Node[] input) {
		HashSet<Segment> allSegments = new HashSet<Segment>();
		for (Node a : input) {
			for (Node b : input) {
				if (!a.equals(b)) {
					allSegments.add(new Segment(a, b));
				}
			}
		}
		HashSet<Segment> boundarySegments = new HashSet<Segment>();
		for (Segment segment : allSegments) {
			boolean success = true;
			int foundSide = 0;
			for (Node n : input) {
				if (!n.equals(segment.getNode1())) {
					if (!n.equals(segment.getNode2())) {
						int side = getSide(n, segment);
						if (side == 0) {
							success = false;
							break;
						}
						if (foundSide == 0) {
							foundSide = side;
						} else {
							if (side != foundSide) {
								success = false;
								break;
							}
						}
					}
				}
			}
			if (success) {
				boundarySegments.add(segment);
			}
		}
		return boundarySegments;
	}
	
	public static int getSide(Point point, Line line) {
		if (line.getX1() == line.getX2()) {
			if (point.getX() < line.getX1()) {
				return -1;
			} else if (point.getX() > line.getX1()) {
				return 1;
			}
			return 0;
		}
		float supposedY = line.getSlope()*(point.getX()-line.getX1())+line.getY1();
		if (point.getY() < supposedY) {
			return -1;
		}
		if (point.getY() > supposedY) {
			return 1;
		}
		return 0;
	}
	
}
