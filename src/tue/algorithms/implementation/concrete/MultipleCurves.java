package tue.algorithms.implementation.concrete;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;

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

    /**
     * If an angle is smaller than this value, it will be considered too sharp.
     */
    final static double TOO_SHARP_ANGLE_TRESHOLD = 0.25 * Math.PI;
    /**
     * If an angle is greater than this value, then the segments don't have to be disconnected.
     */
    final static double SUFFICIENTLY_LARGE_ANGLE = 0.5 * Math.PI;

    @Override
    public Segment[] getOutput(Node[] input) {
        AdjacentNodes adjNodes = new AdjacentNodes(input);
        ConnectedNodes cn = new ConnectedNodes();
        Segment[] segments = MinimumSpanningTree.getSegmentsPermutation(input);

        // Shortest node first.
        Arrays.sort(segments, new Comparator<Segment>() {
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
        });

        for (Segment segment : segments) {
            if (cn.getSegments(segment.getNode1()).length < 2 &&
                cn.getSegments(segment.getNode2()).length < 2 &&
                // TODO: Use an intersection algo that accounts for unconnected nodes.
                !cn.intersectsGraph(segment)) {
                cn.addSegment(segment);
            }
        }

        // If sharp corner (i.e. node with two segments in a folded setting), try
        // 1. remove longest segment
        // 2. take their endpoints and try to find a nearby segment that can be removed,
        //    and their endpoints reconnected to the freed endpoints from the previous steps
        // 3. If not possible, restore the longest segment (1) and retry step (2) with the
        //    shortest segment
        for (Node node : input) {
            Segment[] connectedSegments = cn.getSegments(node);
            if (connectedSegments.length == 2) {
                Segment segment1 = connectedSegments[0];
                Segment segment2 = connectedSegments[1];
                double angle = getAbsoluteAngle(node, segment1, segment2);
                if (angle > SUFFICIENTLY_LARGE_ANGLE) {
                    // Ignore nodes if their angle is already large enough.
                    continue;
                }
                Segment longestSegment, shortestSegment;
                if (segment1.length() > segment2.length()) {
                    longestSegment = segment1;
                    shortestSegment = segment2;
                } else {
                    longestSegment = segment2;
                    shortestSegment = segment1;
                }
                if (!tryReplaceSegment(cn, adjNodes, node, shortestSegment, longestSegment)) {
                    tryReplaceSegment(cn, adjNodes, node, longestSegment, shortestSegment);
                }
            }
        }


        return cn.getAllSegments();
    }

    /**
     * Get the smallest angle between the two segments of the node.
     *
     * @pre node is an endpoint of segment1 and segment2.
     * @return Smallest angle between the two segments, in range [0, PI] (in radians)
     */
    private double getAbsoluteAngle(Node node, Segment segment1, Segment segment2) {
        return Math.abs(segment1.originAt(node).getAngleOf(segment2.getOtherEndpoint(node)));
    }

    /**
     * Reconnect the node of a segment to another segment if the other segment is a better fit.
     * @param node An endpoint with exactly two connected segments.
     * @param keptSegment The to-be-kept segment connected to {@code node}.
     * @param oldSegment The to-be-removed segment connected to {@code node}.
     * @return Whether the segment has been reconnected.
     * @pre All nodes in the segment are connected to at most two other segments.
     */
    private boolean tryReplaceSegment(ConnectedNodes cn, AdjacentNodes adjNodes, Node node, Segment keptSegment, Segment oldSegment) {
        cn.removeSegment(oldSegment);
        // The segment that is going to be used for angle calculations.
        Segment keptSegmentOrigin = keptSegment.originAt(node);

        // After removing oldSegment, the circular structure is open, so we can now follow the path
        // and remember all segments along it. This will be used to avoid splitting a shape.
        HashSet<Segment> oldShape = new HashSet<Segment>();
        {
            Node endpoint = node;
            Segment partOfPath = keptSegment;
            assert cn.getSegments(node).length == 1;
            assert cn.getSegments(node)[0] == keptSegment;
            do {
                oldShape.add(partOfPath);
                endpoint = partOfPath.getOtherEndpoint(endpoint);
                partOfPath = cn.getOtherSegment(endpoint, partOfPath);
            } while (partOfPath != null);

            // If the graph is not closed, then it is possible that we have only selected nodes at one
            // tail only. In this case, we need to find the other nodes starting from the other endpoint.
            endpoint = oldSegment.getOtherEndpoint(node);
            if (cn.isNodeInGraph(endpoint)) {
                partOfPath = cn.getSegments(endpoint)[0];
                // If the figure was open, then .add() will return false on the first iteration because
                // it returns false on inserting a duplicate item.
                while (partOfPath != null && oldShape.add(partOfPath)) {
                    endpoint = partOfPath.getOtherEndpoint(endpoint);
                    partOfPath = cn.getOtherSegment(endpoint, partOfPath);
                }
            }
        }

        // Try to find a segment that results in a bigger angle and a shorter total segment length.
        Node node2 = oldSegment.getOtherEndpoint(node);
        float oldSegmentLength = oldSegment.length();
        // We Higher is better, we want to reduce the total length of all segments as much as possible.
        // (many unconnected shapes will rightfully be joined because of this.)
        // TODO: What if the length of the segment is very short, but there is still a triangle?
        // TODO: If we choose a too high value, then two shapes that are far apart could be connected
        //       (this could be solved by enforcing an upper bound for this value)
        float bestLengthDelta = -0.2f;
        Segment segmentToRemove = null;
        Segment segmentToAdd1 = null;
        Segment segmentToAdd2 = null;
        for (NodeDistancePair ndp : adjNodes.getAdjacentNodes(node)) {
            // TODO: Think about this magic number, and not at 4am.
            if (ndp.distance > oldSegmentLength * 2) {
                break;
            }
            if (Math.abs(keptSegmentOrigin.getAngleOf(ndp.node)) < TOO_SHARP_ANGLE_TRESHOLD) {
                // The angle is too small.
                continue;
            }

            for (Segment otherSegment : cn.getSegments(ndp.node)) {
                Node otherEndPoint = otherSegment.getOtherEndpoint(ndp.node);
                float distanceToNode2 = otherEndPoint.getDistanceTo(node2);
                float otherSegmentLength = otherSegment.length();
                // TODO: Multiply the following variable with a magic number to allow shapes to be reconnected
                // over greater distances. Pick e.g. 1.5f to allow shapes to be connected even if both of
                // the new segment are 1.5x longer than the removed segment.
                float reqMinSegmentLength = otherSegmentLength;
                if (distanceToNode2 > reqMinSegmentLength && ndp.distance > reqMinSegmentLength) {
                    // Never replace a segment if the new segments are going to be longer
                    // than the removed segment.
                    continue;
                }
                float deltaDistance = otherSegmentLength - distanceToNode2 + oldSegmentLength - ndp.distance;
                if (deltaDistance > bestLengthDelta && !oldShape.contains(otherSegment)) {
                    Segment newSegment1 = new Segment(node, ndp.node);
                    Segment newSegment2 = new Segment(node2, otherEndPoint);
                    if (!newSegment1.intersectsWith(newSegment2) &&
                        !cn.intersectsGraph(newSegment1) &&
                        !cn.intersectsGraph(newSegment2)) {
                        bestLengthDelta = deltaDistance;
                        segmentToRemove = otherSegment;
                        segmentToAdd1 = newSegment1;
                        segmentToAdd2 = newSegment2;
                    }
                }
            }
        }
        if (segmentToRemove == null) {
            // Nothing to be replaced, restore old segment.
            cn.addSegment(oldSegment);
            return false;
        } else {
            // Two segments were replaced.
            cn.addSegment(segmentToAdd1);
            cn.addSegment(segmentToAdd2);
            cn.removeSegment(segmentToRemove);
            return true;
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
        float weight = segment.length();
        weight *= 1 + (weight1 + weight2) / 2;
        return weight;
    }

    // TODO: Remove if unused
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
        // weight(angle) = (1 - angle/PI)^2
        float weight;
        weight = 1f - (float)(angle / Math.PI);
        weight = weight * weight;
        return weight;
    }

    // TODO: Remove if unused
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
            if (segment.intersectsWith(ndp.node)) {
                return true;
            }
        }
        return false;
    }
}
