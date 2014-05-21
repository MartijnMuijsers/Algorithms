package tue.algorithms.implementation.concrete;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import tue.algorithms.implementation.general.NetworkImplementation;
import tue.algorithms.other.Pair;
import tue.algorithms.utility.AdjacentNodes;
import tue.algorithms.utility.AdjacentNodes.NodeDistancePair;
import tue.algorithms.utility.ConnectedNodes;
import tue.algorithms.utility.MinimumSpanningTree;
import tue.algorithms.utility.Node;
import tue.algorithms.utility.Segment;

/**
 * A solution to the Network problem using rectilinear minimum spanning trees.
 *
 * @author Chris
 */
public class NetworkRMST implements NetworkImplementation {

    @Override
    public Pair<Segment[], Node[]> getOutput(Node[] input) {
        Segment[] segments = MinimumSpanningTree.getSegmentsPermutation(input);
        Comparator<Segment> comparator = new Comparator<Segment>() {
            @Override
            public int compare(Segment s1, Segment s2) {
                float diff = s1.manhattanDistance() - s2.manhattanDistance();
                if (diff < 0) {
                    return -1;
                } else if (diff > 0) {
                    return 1;
                }
                return 0;
            }
        };

        Segment[] result = MinimumSpanningTree.applyMST(segments, input, comparator);
        result = Optimize(result, input);
        return new Pair(result, null);

    }

    private Segment[] Optimize(Segment[] segments, Node[] nodes) {
        AdjacentNodes adjNodes = new AdjacentNodes(nodes);
        ConnectedNodes cn = new ConnectedNodes();
        for (Segment segment : segments ) {
            cn.addSegment(segment);
        }
        for (Node node : nodes){
            Segment[] neighbors = cn.getSegments(node);
            if (neighbors.length == 1) {
                boolean nodeFound=false;
                int i=0;
                NodeDistancePair[] ndps = adjNodes.getAdjacentNodes(node);
                while(!nodeFound){
                    if (Math.abs(neighbors[0].originAt(node).getAngleOf(ndps[i].node)) > Math.PI/2) {
                        nodeFound =true;
                        cn.addSegment(new Segment(node, ndps[i].node));
                    }
                    else{
                        if (i<5) {
                            i++;
                        }
                        else{
                            nodeFound =true;
                        }
                        
                    }
                }
            }
        }
      
        
     return cn.getAllSegments();
    }
    
    private float getSlope(Node n1, Node n2) {
        
        if (n2.getX() - n1.getX() == 0) {
         return   0f; 
        }
        return (n2.getY()-n1.getY())/(n2.getX()-n1.getX());
    }

}
