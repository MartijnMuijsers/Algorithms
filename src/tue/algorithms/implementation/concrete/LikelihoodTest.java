package tue.algorithms.implementation.concrete;

import tue.algorithms.other.Debug;
import tue.algorithms.utility.Node;
import tue.algorithms.utility.Segment;

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
		Node point1 = new Node(Node.FAKE_NODE_ID, x1, y1);
		Node point2 = new Node(Node.FAKE_NODE_ID, x2, y2);
		Node point3 = new Node(Node.FAKE_NODE_ID, x3, y3);
		Node point4 = new Node(Node.FAKE_NODE_ID, x4, y4);
		Segment segment1 = new Segment(point1, point2);
		Segment segment2 = new Segment(point2, point3);
		Segment segment3 = new Segment(point3, point4);
		Debug.log(SingleImploding.getInstance().getLikelihood(segment1, segment2, segment3));
	}
	
}
