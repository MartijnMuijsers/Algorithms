package tue.algorithms.implementation.concrete;

import java.util.HashSet;

import tue.algorithms.implementation.general.SingleImplementation;
import tue.algorithms.other.Conversion;
import tue.algorithms.utility.Node;
import tue.algorithms.utility.Segment;

/**
 * Under development. Testing ground here.
 * @author Martijn
 */
public class SingleImploding implements SingleImplementation {
	
	@Override
	public Segment[] getOutput(Node[] input) {
		HashSet<Segment> convexHull = ConvexHull.getConvexHull(input);
		return Conversion.toArray(convexHull, new Segment[convexHull.size()]);
	}
	
}
