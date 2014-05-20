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
        Segment[] segments = MinimumSpanningTree.getSegmentsPermutation(input);
        Comparator<Segment> comparator = new Comparator<Segment>() {
            @Override
            public int compare(Segment s1, Segment s2) {
                float diff = s1.length() - s2.length();
                if (diff < 0) {
                    return -1;
                } else if (diff > 0) {
                    return 1;
                }
                return 0;
            }
        };

        segments = MinimumSpanningTree.applyMST(segments, input, comparator);
        AdjacentNodes adjNodes = new AdjacentNodes(input);
        ConnectedNodes cn = new ConnectedNodes();
        for (Segment segment : segments) {
            cn.addSegment(segment);
        }

        addSegmentsToNodesWithDegreeOne(input, cn, adjNodes);

        // TODO: Identify cycles.
        removeSegmentsFromNodesWithDegreeTwoPlus(cn);
        // TODO: Split cycles.

        Segment[] result = cn.getAllSegments();
        return result;
    }

    /**
     * Add a segment to a node that has a degree of at most one.
     */
    private static void addSegmentsToNodesWithDegreeOne(Node[] nodes, ConnectedNodes cn, AdjacentNodes adjNodes) {
        for (Node node : nodes) {
            Segment[] neighbors = cn.getSegments(node);
            if (neighbors.length > 1) {
                continue;
            }
            NodeDistancePair[] ndps = adjNodes.getAdjacentNodes(node);
            for (NodeDistancePair ndp : ndps) {
                // In this loop body, we check whether connecting node to ndp.node
                // is a good choice.
                if (!cn.isConnected(node, ndp.node)) {
                    Segment newSegment = new Segment(node, ndp.node);
                    if (cn.intersectsGraph(newSegment) || containsOtherNode(ndps, ndp, newSegment)) {
                        // Intersects some other line or node - skip it.
                        continue;
                    }
                    cn.addSegment(newSegment);
                    break;
                } // end if !cn.isConnected
            } // end for ndp : ndps
        } // end for node : nodes
    }

    /**
     * Remove segments if both endpoints have degree two or more.
     */
    private static void removeSegmentsFromNodesWithDegreeTwoPlus(ConnectedNodes cn) {
        for (Segment segment : cn.getAllSegments()) {
            Node node1 = segment.getNode1();
            Node node2 = segment.getNode2();
            Segment[] neighbors1 = cn.getSegments(node1);
            Segment[] neighbors2 = cn.getSegments(node2);
            if (neighbors1.length > 2 && neighbors2.length > 2) {
                // TODO: Check whether it breaks a cycle.
                cn.removeSegment(segment);
            }
        } // end for segment.getAllSegments()
    }

    /**
     * Checks whether the segment intersects an existing node.
     * This method relies on the fact that the neighbors for a given node are known,
     * and that if there is a node that is intersected by the segment, then it must
     * be one of these neighbors (all other nodes are too far away).
     * Time complexity: O(k) where k is the number of nodes with a smaller distance
     * than {@code neighbor}.
     */
    private static boolean containsOtherNode(NodeDistancePair[] neighbors, NodeDistancePair neighbor, Segment segment) {
        for (NodeDistancePair ndp : neighbors) {
            if (ndp.distance >= neighbor.distance) {
                return false;
            }
            // TODO: Check if the neighbor is on the segment and if so return true
        }
        return false;
    }
}
