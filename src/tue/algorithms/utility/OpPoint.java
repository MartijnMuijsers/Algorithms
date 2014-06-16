package tue.algorithms.utility;

public class OpPoint {
	
	public final float x;
	public final float y;
	private final int hashCode;
	
	public OpPoint(float x, float y) {
		this.x = x;
		this.y = y;
		float y2 = y*10000;
		this.hashCode = (int) (x*10000+y2*y2);
	}
	
	public float length() {
		return (float) Math.sqrt(x * x + y * y);
	}
	
	public double getAngle() {
		return Math.atan2(y, x);
	}
	
	public float getDistanceTo(OpPoint point) {
		float dy = point.y-y;
		float dx = point.x-x;
		return (float) Math.sqrt(dx*dx+dy*dy);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof OpPoint) {
			OpPoint other = (OpPoint) obj;
			return (other.x == x && other.y == y);
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return hashCode;
	}
	
	public Point toPoint() {
		return new Point(x, y);
	}
	
}
