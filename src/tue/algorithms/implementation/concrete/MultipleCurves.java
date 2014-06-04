package tue.algorithms.implementation.concrete;

import java.util.ArrayList;
import java.util.Arrays;
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

        removeSegmentIfAnyEndpointHasDegreeTwoPlus(cn);
        connectEndpointsWithDegreeOne(input, cn, adjNodes);
        // TODO: Fix nodes with degree 0
        // TODO: Find all corners in the curve and reconnect the corners to
        // get a bigger curve (test case = multiCloseCurves).
        // TODO: Walk over the final curves and remove segments if they deviate
        // too much from the shape.

        Segment[] result = cn.getAllSegments();
        return result;
    }

    /**
     * Remove segments if any of the endpoints have degree two or more.
     */
    private static void removeSegmentIfAnyEndpointHasDegreeTwoPlus(ConnectedNodes cn) {
        Segment[] segments = cn.getAllSegments();
        // Sort by segment length, longest first.
        Arrays.sort(segments, new Comparator<Segment>() {
            @Override
            public int compare(Segment s1, Segment s2) {
                float diff = s1.length() - s2.length();
                if (diff < 0) {
                    return 1;
                } else if (diff > 0) {
                    return -1;
                }
                return 0;
            }
        });
        for (Segment segment : segments) {
            Node node1 = segment.getNode1();
            Node node2 = segment.getNode2();
            Segment[] neighbors1 = cn.getSegments(node1);
            Segment[] neighbors2 = cn.getSegments(node2);
            if (neighbors1.length > 2 || neighbors2.length > 2) {
                cn.removeSegment(segment);
            }
        }
    }

    /**
     * Find all nodes with degree one and connect them if possible.
     *
     * @pre Every node in nodes has a degree of at most 2.
     * @post Pre-condition still holds.
     */
    private static void connectEndpointsWithDegreeOne(Node[] nodes, ConnectedNodes cn, AdjacentNodes adjNodes) {
        Node[] nodesTodo = getNodesWithDegreeInRange(nodes, cn, 0, 1);

        // When a new segment is added, one of the segments might suddenly get degree one.
        // Then we have to re-run this step on this new node. These nodes are tracked in the next list.
        ArrayList<Node> newNodesWithDegreeOne = new ArrayList<Node>(nodes.length);

        for (Node node : nodesTodo) {
            Segment[] neighborSegments = cn.getSegments(node);
            if (neighborSegments.length != 1) {
                continue;
            }
            // We have found a segment that ends in a node (endpoint). Try to find
            // another node so that we connect the endpoint to this other node.
            Segment existingSegment = neighborSegments[0];

            // Lower weight = better.
            float bestWeight = Integer.MAX_VALUE;
            Segment bestSegment = null;
            Segment segmentToRemove = null;
            NodeDistancePair[] ndps = adjNodes.getAdjacentNodes(node);
            for (NodeDistancePair ndp : ndps) {
                Segment newSegment = new Segment(node, ndp.node);
                float newSegmentWeight;
                Segment removedSegment = null;

                Segment[] ndpSegments = cn.getSegments(ndp.node);
                if (ndpSegments.length == 0) {
                    newSegmentWeight = getSegmentWeight(newSegment, existingSegment, null);
                } else if (ndpSegments.length == 1) {
                    newSegmentWeight = getSegmentWeight(newSegment, existingSegment, ndpSegments[0]);
                } else if (ndpSegments.length == 2) {
                    // When the other node has two connected segments, check whether it makes sense to
                    // replace one of these segments with the new segment.
                    Segment seg1 = ndpSegments[0];
                    Segment seg2 = ndpSegments[1];
                    float weight1 = getSegmentWeight(seg1, cn.getOtherSegment(seg1), cn.getOtherSegment(seg1.invertDirection()));
                    float weight2 = getSegmentWeight(seg2, cn.getOtherSegment(seg2), cn.getOtherSegment(seg2.invertDirection()));

                    float removedSegmentWeight;
                    if (weight1 > weight2) {
                        removedSegmentWeight = weight1;
                        removedSegment = seg1;
                    } else {
                        removedSegmentWeight = weight2;
                        removedSegment = seg2;
                    }
                    newSegmentWeight = getSegmentWeight(newSegment, existingSegment, removedSegment);
                    if (removedSegmentWeight < newSegmentWeight) {
                        continue;
                    }
                } else {
                    throw new RuntimeException("Precondition failed: All nodes must have a degree of at most two");
                }

                if (newSegmentWeight < bestWeight && !intersectsGraph(cn, ndps, ndp, newSegment)) {
                    bestWeight = newSegmentWeight;
                    bestSegment = newSegment;
                    segmentToRemove = removedSegment;
                }
            } // end for NodeDistancePair ndp : ndps

            if (bestSegment != null) {
                if (segmentToRemove != null) {
                    // The new segment is a better fit than an existing one, so replace the
                    // existing one. After removing the existing segment, one of the free nodes
                    // will immediately be used by the new segment (=stay at degree two), and
                    // the other one will have degree one.
                    cn.removeSegment(segmentToRemove);
                    if (cn.getSegments(segmentToRemove.getNode1()).length == 1) {
                        newNodesWithDegreeOne.add(segmentToRemove.getNode1());
                    } else if (cn.getSegments(segmentToRemove.getNode2()).length == 1) {
                        newNodesWithDegreeOne.add(segmentToRemove.getNode2());
                    }
                } else if (cn.getSegments(bestSegment.getNode2()).length == 0) {
                    // The to-ne-added node will have degree one.
                    newNodesWithDegreeOne.add(bestSegment.getNode2());
                }
                cn.addSegment(bestSegment);
            }
        }

        // TODO: Change recursion to iteration.
        if (newNodesWithDegreeOne.size() > 0) {
            nodesTodo = newNodesWithDegreeOne.toArray(new Node[0]);
            connectEndpointsWithDegreeOne(nodesTodo, cn, adjNodes);
        }
    }

    /**
     * Get the weight of a segment.
     *
     * @param segment
     * @param otherSegmentAtNode1 Optional, if set it must be connected to segment.getNode1()
     * @param otherSegmentAtNode2 Optional, if set it must be connected to segment.getNode2()
     *
     * @return The weight of the segment (positive floating point number). Lower = better.
     */
    private static float getSegmentWeight(Segment segment, Segment otherSegmentAtNode1, Segment otherSegmentAtNode2) {
        Node node1 = segment.getNode1();
        Node node2 = segment.getNode2();
        float weight1 = otherSegmentAtNode1 == null ? 1 : angleToWeight(segment.getAngleOf(otherSegmentAtNode1.getOtherEndpoint(node1)));
        float weight2 = otherSegmentAtNode2 == null ? 1 : angleToWeight(segment.getAngleOf(otherSegmentAtNode2.getOtherEndpoint(node2)));
        // TODO: Ignore angle differences below a certain treshold? E.g. angle1 = Math.min(angle1, Math.PI / 2);
        float weight = segment.length();
        weight *= 1 + (weight1 + weight2) / 2;
        return weight;
    }

    /**
     * Helper function for getSegmentWeight: Converts an angle to a weight.
     * @param angle Angle in radian, [-PI, PI]. |angle| close to 0 is worst, close to PI is best.
     * @return relative weight in range [0, 1]. 0 = best, 1 = worst
     */
    private static float angleToWeight(double angle) {
        // |angle| is within the range [0, PI]
        // In the best case, all segments form a straight line, so |angle| = PI.
        // In the worst case, the segments are almost folded (think of a Z-shape), so the angle is close to 0.
        angle = Math.abs(angle);

        // The following function maps [0, PI] to [1, 0]
        float weight;
        weight = 1f - (float)(angle / Math.PI);
        return weight;
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
