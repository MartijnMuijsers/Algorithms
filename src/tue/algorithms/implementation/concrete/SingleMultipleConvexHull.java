package tue.algorithms.implementation.concrete;

import java.util.HashSet;

import tue.algorithms.implementation.general.MultipleImplementation;
import tue.algorithms.implementation.general.SingleImplementation;
import tue.algorithms.other.Conversion;
import tue.algorithms.other.Debug;
import tue.algorithms.utility.Node;
import tue.algorithms.utility.Segment;

/**
 * Single implementation which just returns the convex hull.
 * @author Martijn
 */
public class SingleMultipleConvexHull implements SingleImplementation, MultipleImplementation {
	
	@Override
	public Segment[] getOutput(Node[] input) {
		HashSet<Segment> convexHull = ConvexHull.getConvexHull(input);
		Debug.log("Convex hull size is " + convexHull.size());
		return Conversion.toArray(convexHull, new Segment[convexHull.size()]);
	}
	
}
