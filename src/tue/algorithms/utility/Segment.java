package tue.algorithms.utility;

import java.awt.geom.Line2D;

/**
 * A segment that connects two nodes.
 * This class is immutable.
 */
public class Segment {
    /**
     * The width of the line. This is used to determine whether the line intersects another line or point.
     * TODO: Evaluate whether this arbitrarily chosen value makes sense.
     */
    final static float LINE_WIDTH = 0.0001f;

	public final Node node1;
	public final Node node2;

	/**
	 * Create a segment from node1 to node2.
	 * @param node1 The node the segment starts at.
	 * @param node2 The node the segments ends at.
	 */
	public Segment(Node node1, Node node2) {
		this.node1 = node1;
		this.node2 = node2;
	}

	/**
	 * Create a segment from the node with id node1id to the node with id node2id.
	 * @param node1id The id of the node the segment starts at.
	 * @param node2id The id of the node the segment ends at.
	 * @deprecated
	 */
	public Segment(int node1id, int node2id) {
		this(Node.getById(node1id), Node.getById(node2id));
	}

	/* -- START Deprecated methods -- */

	/**
	 * Use node1.id
	 * @deprecated
	 */
	@Deprecated
	public int getNode1Id() {
		return node1.id;
	}

	/**
	 * Use node2.id
	 * @deprecated
	 */
	@Deprecated
	public int getNode2Id() {
		return node2.id;
	}

	/**
	 * Use .node1
	 * @deprecated
	 */
	@Deprecated
	public Node getNode1() {
		return node1;
	}

	/**
	 * Use .node2
	 * @deprecated
	 */
	@Deprecated
	public Node getNode2() {
		return node2;
	}

    @Deprecated
    public float getX1() {
        return node1.x;
    }

    @Deprecated
    public float getY1() {
        return node1.y;
    }

    @Deprecated
    public float getX2() {
        return node2.x;
    }

    @Deprecated
    public float getY2() {
        return node2.y;
    }

	/* -- END Deprecated methods -- */

    /**
     * Get the length of the line.
     * @return The length as a float.
     */
    public float length() {
        return node1.getDistanceTo(node2);
    }

    /**
     * Get the angle of the line.
     * An angle of 0 means this line points to the right, and an angle of Math.PI/2 means this line points upwards.
     * @return The angle as a double.
     */
    public double getAngle() {
        return Math.atan2(node2.y - node1.y, node2.x - node1.x);
    }

    /**
     * Get the angle of {@code other} relative to this line, as if
     * this line were the positive X-axis of some coordinate system,
     * with (x1, y1) as origin.
     *
     * @return The angle in radians in the range [-Math.PI, Math.PI]
     */
    public double getAngleOf(Node other) {
		double otherAbsoluteAngle = Math.atan2(other.y - node1.y, other.x - node1.x);
        double thisAbsoluteAngle = getAngle();
        double relativeAngle = otherAbsoluteAngle - thisAbsoluteAngle;
        if (relativeAngle <= -Math.PI) relativeAngle += 2 * Math.PI;
        else if (relativeAngle >= Math.PI) relativeAngle -= 2 * Math.PI;
        return relativeAngle;
    }

    /**
     * Get the slope of the line.
     * @return The slope as a float.
     */
    public float getSlope() {
        return getSlope(node2.x - node1.x, node2.y - node1.y);
    }

    public boolean intersectsWith(Segment other) {
        Line2D line1 = new Line2D.Float(node1.x, node1.y, node2.x, node2.y);
        Line2D line2 = new Line2D.Float(other.node1.x, other.node1.y, other.node2.x, other.node2.y);
        if (line2.intersectsLine(line1)) {
            float xLeft, xRight, yLeft, yRight;
            float xLeftOther, xRightOther, yLeftOther, yRightOther;
            if (isNode1AtLeft()) {
                xLeft = node1.x;
                yLeft = node1.y;
                xRight = node2.x;
                yRight = node2.y;
            } else {
                xLeft = node2.x;
                yLeft = node2.y;
                xRight = node1.x;
                yRight = node1.y;
            }
            if (other.isNode1AtLeft()) {
                xLeftOther = other.node1.x;
                yLeftOther = other.node1.y;
                xRightOther = other.node2.x;
                yRightOther = other.node2.y;
            } else {
                xLeftOther = other.node2.x;
                yLeftOther = other.node2.y;
                xRightOther = other.node1.x;
                yRightOther = other.node1.y;
            }

            // Tests for touching lines
            if (xLeft == xLeftOther && yLeft == yLeftOther || xRight == xRightOther && yRight == yRightOther) {
                // Both lines start or end in the same point. If the slopes are equal, then they overlap.
				return getSlope(xRight - xLeft, yRight - yLeft) ==
					   getSlope(xRightOther - xLeftOther, yRightOther - yLeftOther);
            }
            if (xLeft == xRightOther && yLeft == yRightOther || xRight == xLeftOther && yRight == yLeftOther) {
                // One line ends in the start point of the other line. They will never intersect, because the
                // normalized coordinates already ensure that if they overlap, then the previous branch should
                // have been taken.
                return false;
            }
            return true;
        }
        return false;
    }

    /**
     * @return Whether a point lies somewhere on the line.
     */
    public boolean intersectsWith(Node point) {
        float x = point.x;
        float y = point.y;
        float dx = node2.x - node1.x;
        float dy = node2.y - node1.y;

        if (Math.abs(dx) < LINE_WIDTH) {
            // Current line is vertical
            return y > getMinY() && y < getMaxY() && LINE_WIDTH > Math.abs(x - node1.x);
        } else if (Math.abs(dy) < LINE_WIDTH) {
            // Current line is horizontal
            return x > getMinX() && x < getMaxX() && LINE_WIDTH > Math.abs(y - node1.y);
        } else {
            // Neither horizontal nor vertical.
            if (isNode1AtLeft()){
                return x > getMinX() && x < getMaxX() && LINE_WIDTH > (dy / dx) * (x - node1.x) - (y - node1.y);
            } else {
                return x > getMinX() && x < getMaxX() && LINE_WIDTH > (-dy / dx) * (x - node2.y) - (y - node2.y);
			}
        }
    }

    /**
     * @return The distance between {@code point} and this line segment viewed as an (infinite) line.
     */
    public float getDistanceOf(Node point) {
        // Get the angle in the range [0, 0.5PI]
        double angle = Math.abs(getAngleOf(point));
        if (angle >= 0.5 * Math.PI) angle = Math.PI - angle;
        float diagonalLength = point.getDistanceTo(node1);
        return diagonalLength * (float)Math.sin(angle);
    }

	/**
	 * @param node
	 * @return Whether the segment has {@code node} as one of its end points.
	 */
	public boolean isEndPoint(Node node) {
		return node1 == node || node2 == node;
	}

	/**
	 * @param node
	 * @return The other endpoint.
	 * @pre {@code node} is one of the endpoints.
	 */
	public Node getOtherEndpoint(Node node) {
		return node != node1 ? node1 : node2;
	}

	/* -- START Manipulation method to invert line -- */

	/**
	 * Get a view of this line such that the line "ends" at {@code node}
	 * @param node
	 * @return The segment
	 */
	public Segment endAt(Node node) {
		return originAt(getOtherEndpoint(node));
	}

	/**
	 * Get a view of this line such that the line "starts" at {@code node}.
	 *
	 * @param node The point that is used as start point
	 * @pre node must be one of the endpoints of this line, i.e.
	 *  {@code isEndPoint(node) == true}
	 */
	public Segment originAt(Node node) {
		if (node1 == node) {
			return this;
		} else {
			return invertDirection();
		}
	}

	/**
	 * Get a segment with the direction inverted: the created segment will start where this segment ends, and end where this segment starts.
	 * @return The segment with inverted direction.
	 */
	public Segment invertDirection() {
		return new Segment(node2, node1);
	}

	// Private helper methods
    /** Whether node1 is at the left (relative to node2) */
    private boolean isNode1AtLeft() {
        return node1.x < node2.x || (node1.x == node2.x && node1.y < node2.y);
    }

    /**
     * @return The minimal X-coordinate of this line.
     */
    private float getMinX() {
        return node1.x < node2.x ? node1.x : node2.x;
    }

    /**
     * @return The maximal X-coordinate of this line.
     */
    private float getMaxX() {
        return node1.x < node2.x ? node2.x : node1.x;
    }

    /**
     * @return The minimal Y-coordinate of this line.
     */
    private float getMinY() {
        return node1.y < node2.y ? node1.y : node2.y;
    }

    /**
     * @return The maximal Y-coordinate of this line.
     */
    private float getMaxY() {
        return node1.y < node2.y ? node2.y : node1.y;
    }

    private static float getSlope(float dx, float dy) {
        if (dx == 0) {
            if (dy > 0) {
                return Integer.MAX_VALUE;
            }
            if (dy < 0) {
                return Integer.MIN_VALUE;
            }
            return 0;
        }
        return dy / dx;
    }

	/* -- END Manipulation method to invert line -- */

	/* -- START Override equals(), hashCode() and toString() -- */

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Segment) {
			Segment other = (Segment) obj;
			return other.node1.id == node1.id && other.node2.id == node2.id;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return node1.id * 12000 + node2.id;
	}

	@Override
	public String toString() {
		return super.toString() + "["
			+ "id1=" + node1.id + ", "
			+ "x1=" + node1.x + ", "
			+ "y1=" + node1.y + ", "
			+ "id2=" + node2.id + ", "
			+ "x2=" + node2.x + ", "
			+ "y2=" + node2.y
			+ "]";
	}

	/* -- END Override equals(), hashCode() and toString() -- */
	/**
	 * Use minimally!!!
	 */
	public OpSegment toOpSegment() {
		return new OpSegment(node1.toOpNode(), node2.toOpNode());
	}

}
