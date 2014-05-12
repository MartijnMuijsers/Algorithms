package tue.algorithms.implementation.concrete;

import java.util.HashSet;
import tue.algorithms.implementation.general.NetworkImplementation;
import tue.algorithms.other.Pair;
import tue.algorithms.utility.Node;
import tue.algorithms.utility.Segment;

/**
 * A solution to the Network problem using rectilinear minimum spanning trees.
 * @author Chris
 */
public class NetworkRMST extends NetworkImplementation {
    
    @Override
    public Pair<Segment[], Node[]> getOutput(Node[] input) {
        HashSet<Segment> segments = new HashSet<Segment>();
        
        
		
		for (Node node : input) {
			for (Node node2 : input){
                            
                            if (!node.equals(node2)){
                                segments.add(new Segment(node, node2));
                            }
                        }
		}
                Segment[] resultSegments = new Segment[segments.size()];
		int i = 0;
		for (Segment segment : segments) {
			resultSegments[i] = segment;
			i++;
		}
		
               return new Pair(resultSegments, null);
         }
    
}
