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
				if (a.getId() < b.getId()) {
					allSegments.add(new Segment(a, b));
				}
			}
		}
		HashSet<Segment> boundarySegments = new HashSet<Segment>();
		for (Segment segment : allSegments) {
			boolean success = true;
			Side foundSide = null;
			for (Node n : input) {
				if (!n.equals(segment.getNode1())) {
					if (!n.equals(segment.getNode2())) {
						Side side = getSide(n, segment);
						if (side == Side.ON) {
							success = false;
							break;
						}
						if (side == Side.ON_EXTENDED) {
							continue;
						}
						if (foundSide == null) {
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
	
	public static Side getSide(Point point, Line line) {
		if (line.getX1() == line.getX2()) {
			if (point.getX() < line.getX1()) {
				return Side.TOP_LEFT;
			} else if (point.getX() > line.getX1()) {
				return Side.BOTTOM_RIGHT;
			}
			float minLineY = Math.min(line.getY1(), line.getY2());
			float maxLineY = Math.max(line.getY1(), line.getY2());
			if (point.getY() >= minLineY && point.getY() <= maxLineY) {
				return Side.ON;
			}
			return Side.ON_EXTENDED;
		}
		float supposedY = line.getSlope()*(point.getX()-line.getX1())+line.getY1();
		if (point.getY() < supposedY) {
			return Side.TOP_LEFT;
		}
		if (point.getY() > supposedY) {
			return Side.BOTTOM_RIGHT;
		}
		float minLineY = Math.min(line.getY1(), line.getY2());
		float maxLineY = Math.max(line.getY1(), line.getY2());
		float minLineX = Math.min(line.getX1(), line.getX2());
		float maxLineX = Math.max(line.getX1(), line.getX2());
		if (point.getY() >= minLineY && point.getY() <= maxLineY && point.getX() >= minLineX && point.getX() <= maxLineX) {
			return Side.ON;
		}
		return Side.ON_EXTENDED;
	}
	
	public static enum Side {
		
		TOP_LEFT,
		BOTTOM_RIGHT,
		ON,
		ON_EXTENDED
		
	}
	
}
