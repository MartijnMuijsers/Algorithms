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

        removeSegmentIfAnyNodeHasDegreeOnePlus(input, cn);
        connectEndpointsWithDegreeOne(input, cn, adjNodes);

        //addSegmentsToNodesWithDegreeOne(input, cn, adjNodes);

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
                    if (intersectsGraph(cn, ndps, ndp, newSegment)) {
                        // Intersects some other line or node - skip it.
                        continue;
                    }
                    if (cn.getSegments(ndp.node).length > 1) {
                        // TODO: Whether this branch is taken depends on the order of the iteration
                        // over the nodes. Check whether this dependency on the relative ordering of
                        // the nodes is undesired, and if so, fix it.
                        continue;
                    }
                    cn.addSegment(newSegment);
                    break;
                } // end if !cn.isConnected
            } // end for ndp : ndps
        } // end for node : nodes
    }

    private static void removeSegmentIfAnyNodeHasDegreeOnePlus(Node[] nodes, ConnectedNodes cn) {
        ArrayList<Node> nodesToDisconnect = new ArrayList<Node>();
        for (Node node : nodes) {
            if (cn.getSegments(node).length > 2) {
                nodesToDisconnect.add(node);
            }
        }
        for (Node node : nodesToDisconnect) {
            for (Segment segment : cn.getSegments(node)) {
                cn.removeSegment(segment);
            }
        }
    }

    private static void connectEndpointsWithDegreeOne(Node[] nodes, ConnectedNodes cn, AdjacentNodes adjNodes) {
        Node[] nodesTodo = getNodesWithDegreeInRange(nodes, cn, 0, 1);

        // TODO: For all nodes with degree one, try to follow the path
        //  - Prefer nodes that follow the path
        //  - Prefer nodes with deg 1
        //  - Deprioritize nodes if that node has an angle with degree <= 90
        for (Node node : nodesTodo) {
            Segment[] neighborSegments = cn.getSegments(node);
            if (neighborSegments.length != 1) {
                continue;
            }
            // We have found a segment that ends in a node (endpoint). Try to find
            // another node so that we connect the endpoint to this other node.
            Segment existingSegment = neighborSegments[0].originAt(node);
            double previousAngle = getAngleRelativeToPreviousSegment(cn, existingSegment);

            // Lower weight = better.
            float bestWeight = Integer.MAX_VALUE;
            Segment bestSegment = null;
            NodeDistancePair[] ndps = adjNodes.getAdjacentNodes(node);
            for (NodeDistancePair ndp : ndps) {
                if (cn.getSegments(ndp.node).length >= 2) {
                    continue;
                }
                // Example:
                //        existingSegment (=positive X-axis with origin at node)
                //                     |   @#####
                //                     |   #####%
                //                     V   #####@
                //             _*---------* <-- node (=origin of existingSegment)
                //          _-- _,     ( /
                //       _--    ^  angle/  <-- segment does not yet exist.
                //      *       | 2/3PI/
                //   previousAngle    * <-- ndp.node
                //       4/3PI
                //
                // Rules:
                // - Strongly prefer straight lines over folded lines.
                // - Slightly bent lines (within a margin) get no penalty.
                // - The margin can be relaxed depending on the previous line.
                //
                // |angle| close to PI means that the line is almost straight.
                // |angle| close to 0 means that the line is almost folded.
                double angle = existingSegment.getAngleOf(ndp.node);
                double absAngle = Math.abs(angle);

                float weight = ndp.distance;
                // TODO: Consider a different (smaller?) margin.
                double margin = Math.PI / 2;
                if (absAngle < margin) {
                    // TODO: Get rid of this magic number (0.5 = half of the field width/height)
                    // TODO: Tweak the weight depending on the angle, such that angles
                    //  of PI/2 are preferred over 0)
                    weight += 0.5;
                }

                if (weight < bestWeight) {
                    Segment newSegment = new Segment(node, ndp.node);
                    if (!intersectsGraph(cn, ndps, ndp, newSegment)) {
                        bestWeight = weight;
                        bestSegment = newSegment;
                    }
                }
            }
            if (bestSegment != null) {
                cn.addSegment(bestSegment);
            }
        }
    }

    /**
     * Get the angle between the current segment and the previous segment.
     * @pre segment.getNode1() is part of at most two segments.
     */
    private static double getAngleRelativeToPreviousSegment(ConnectedNodes cn, Segment segment) {
        Node commonEndpoint = segment.getNode1();
        Segment[] connectedSegments = cn.getSegments(commonEndpoint);
        assert connectedSegments.length <= 2;
        for (Segment previousSegment : connectedSegments) {
            if (!previousSegment.equals(segment)) {
                //              *
                // negative  _ / <-- segment (node2)
                // angle-> (  /
                // *---------* <-- commonEndpoint (node1)
                //   ^
                //   Segment. For the angle calculation, it's considered to be the positive X-axis
                //   originating from [commonEndpoint], so this coordinate system can be viewed as
                //   a standard carthesian coordinate system, flipped over [commonEndpoint].
                return previousSegment.originAt(commonEndpoint).getAngleOf(segment.getNode2());
            }
        }
        // There's no other segment. Use angle 0, i.e. the path containing the segment did not bend.
        return 0;
    }

    /**
     * Construct a new set from the array of nodes containing all nodes whose
     * degree is within the range [lower, upper]
     */
    private static Node[] getNodesWithDegreeInRange(Node[] nodes, ConnectedNodes cn, int lower, int upper) {
        ArrayList<Node> result = new ArrayList<Node>(nodes.length);
        for (Node node : nodes) {
            int degree = cn.getSegments(node).length;
            if (upper >= degree && degree >= lower) {
                result.add(node);
            }
        }
        return result.toArray(new Node[0]);
    }

    /**
     * Checks whether the segment intersects any existing node or segment.
     *
     * This method relies on the fact that the neighbors for a given node are known,
     * and that if there is a node that is intersected by the segment, then it must
     * be one of these neighbors (all other nodes are too far away).
     * Time complexity: O(k) where k is the number of nodes with a smaller distance
     * than {@code neighbor}.
     */
    private static boolean intersectsGraph(ConnectedNodes cn, NodeDistancePair[] neighbors, NodeDistancePair neighbor, Segment segment) {
        if (cn.intersectsGraph(segment)) {
            return true;
        }
        for (NodeDistancePair ndp : neighbors) {
            if (ndp.distance >= neighbor.distance) {
                return false;
            }
            // TODO: Check if the neighbor is on the segment and if so return true
        }
        return false;
    }
}
