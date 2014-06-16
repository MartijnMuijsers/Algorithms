package tue.algorithms.utility;

import java.awt.geom.Line2D;

public class OpLine {
	
	public final static float LINE_WIDTH = 0.0001f;
	
	public final float x1;
	public final float y1;
	public final float x2;
	public final float y2;
	
	public final float xLeft;
	public final float yLeft;
	public final float xRight;
	public final float yRight;
	
	public final int hashCode;
	
	public OpLine(OpPoint point1, OpPoint point2) {
		x1 = point1.x;
		y1 = point1.y;
		x2 = point2.x;
		y2 = point2.y;
		float y1k = y1*10000;
		float x2k = x2*10000;
		float y2k = y2*10000;
		hashCode = (int) (x1*10000+y1k*y1k+x2k*x2k*x2k+y2k*y2k*y2k*y2k);
		if (x1 < x2 || (x1 == x2 && y1 < y2)) {
			this.xLeft = x1;
			this.yLeft = y1;
			this.xRight = x2;
			this.yRight = y2;
		} else {
			this.xLeft = x2;
			this.yLeft = y2;
			this.xRight = x1;
			this.yRight = y1;
		}
	}
	
	public OpLine(float x1, float y1, float x2, float y2) {
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
		float y1k = y1*10000;
		float x2k = x2*10000;
		float y2k = y2*10000;
		hashCode = (int) (x1*10000+y1k*y1k+x2k*x2k*x2k+y2k*y2k*y2k*y2k);
		if (x1 < x2 || (x1 == x2 && y1 < y2)) {
			this.xLeft = x1;
			this.yLeft = y1;
			this.xRight = x2;
			this.yRight = y2;
		} else {
			this.xLeft = x2;
			this.yLeft = y2;
			this.xRight = x1;
			this.yRight = y1;
		}
	}

	public float getMinX() {
		return x1 < x2 ? x1 : x2;
	}
	
	public float getMaxX() {
		return x1 < x2 ? x2 : x1;
	}
	
	public float getMinY() {
		return y1 < y2 ? y1 : y2;
	}
	
	public float getMaxY() {
		return y1 < y2 ? y2 : y1;
	}
	
	public float length() {
		float dx = x2-x1;
		float dy = y2-y1;
		return (float) Math.sqrt(dx*dx+dy*dy);
	}
    
	public float manhattanDistance() {
		return Math.abs(x1-x2)+Math.abs(y1-y2);
	}
	
	public double getAngle() {
		float dx = x2-x1;
		float dy = y2-y1;
		return Math.atan2(dy, dx);
	}
	
	public float getSlope() {
		float dx = x2-x1;
		float dy = y2-y1;
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
	
	public float getNormalizedSlope() {
		float dx = xRight-xLeft;
		float dy = yRight-yLeft;
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
	
	public boolean intersectsWith(OpLine other) {
		Line2D line1 = new Line2D.Float(x1, y1, x2, y2);
		Line2D line2 = new Line2D.Float(other.x1, other.y1, other.x2, other.y2);
		if (line2.intersectsLine(line1)) {
			if (xLeft == other.xLeft && yLeft == other.yLeft || xRight == other.xRight && yRight == other.yRight) {
				return getNormalizedSlope() == other.getNormalizedSlope();
			}
			if (xLeft == other.xRight && yLeft == other.yRight || xRight == other.xLeft && yRight == other.yLeft) {
				return false;
			}
			return true;
		}
		return false;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof OpLine) {
			OpLine other = (OpLine) obj;
			return (other.x1 == x1 &&
					other.y1 == y1 &&
					other.x2 == x2 &&
					other.y2 == y2);
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return hashCode;
	}
	
	/**
	 * Use minimally!!!
	 */
	public Line toLine() {
		return new Line(x1, y1, x2, y2);
	}
	
}
