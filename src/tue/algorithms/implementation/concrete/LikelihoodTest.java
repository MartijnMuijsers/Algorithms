package tue.algorithms.implementation.concrete;

import tue.algorithms.other.Debug;
import tue.algorithms.utility.Line;
import tue.algorithms.utility.Point;

public class LikelihoodTest {
	
	public static void main(String[] args) {
		test(
				3,6,
				4,8,
				5,10,
				6,12
				);
		test(
				1,2,
				2,3,
				3,5,
				4,8
				);
	}
	
	public static void test(float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4) {
		Point point1 = new Point(x1, y1);
		Point point2 = new Point(x2, y2);
		Point point3 = new Point(x3, y3);
		Point point4 = new Point(x4, y4);
		Line segment1 = new Line(point1, point2);
		Line segment2 = new Line(point2, point3);
		Line segment3 = new Line(point3, point4);
		Debug.log(SingleImploding.getInstance().getLikelihood(segment1, segment2, segment3));
	}
	
}
