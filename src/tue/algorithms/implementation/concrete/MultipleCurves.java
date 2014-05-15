package tue.algorithms.implementation.concrete;

import java.util.ArrayList;
import java.util.Comparator;

import tue.algorithms.implementation.general.MultipleImplementation;
import tue.algorithms.utility.AdjacentNodes;
import tue.algorithms.utility.AdjacentNodes.NodeDistancePair;
import tue.algorithms.utility.ConnectedNodes;
import tue.algorithms.utility.MinimumSpanningTree;
import tue.algorithms.utility.Node;
import tue.algorithms.utility.Segment;

/**
 * A solution to the multiple curve reconstruction problem using a MST with custom weight.
 *
 * @author Rob
 */
public class MultipleCurves implements MultipleImplementation {

    @Override
    public Segment[] getOutput(Node[] input) {
        AdjacentNodes adjNodes = new AdjacentNodes(input);
        ConnectedNodes cn = new ConnectedNodes();

        for (Node node : input) {
            NodeDistancePair[] ndps = adjNodes.getAdjacentNodes(node);
            for (NodeDistancePair ndp : ndps) {
                if (!cn.isConnected(node, ndp.node)) {
                    // TODO: Avoid intersections.
                    // TODO: Take the distance into account.
                    // TODO: Prefer paths that closely follow the direction
                    //  (e.g. try to avoid 350 degree rotations).
                    cn.addSegment(new Segment(node, ndp.node));
                    break;
                }
            }
        }
        Segment[] result = cn.getAllSegments();
        return result;
    }
}
