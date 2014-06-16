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

        // Shortest segment first.
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
        segments = null;

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
        // The following variable is used to determine whether a new segment pair is better.
        // It is <sum of old segment lengths> - <sum of new segment length>
        // We try to get the highest possible value.
        // The initial value sets the absolute treshold:
        //  - stricter: A high (positive) value means that the new segments must be absolutely
        //    shorter than the current one.
        //  - laxer: A low (negative) value means that the sum of the new segments is allowed to
        //    be bigger than the current segments.
        float bestLengthDelta = -0.2f;
        Segment segmentToRemove = null;
        Segment segmentToAdd1 = null;
        Segment segmentToAdd2 = null;
        for (NodeDistancePair ndp : adjNodes.getAdjacentNodes(node)) {
            // TODO: Think about this magic number, and not at 4am.
            // TODO: Add an extra condition, to limit the max length of the segment?
            //       Perhaps use statistics and the oldShape subroutine to determine
            //       a sensible max length? Not sure what to do at 6 am.
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
                    // TODO: Put 5f in a constant.
                    // This magic number is used to determine whether the new segments do NOT
                    // get "too close" to one of the endpoints of the other segments.
                    if (newSegment1.getDistanceOf(node2) * 5f > newSegment1.length() &&
                        newSegment1.getDistanceOf(otherEndPoint) * 5f > newSegment1.length() &&
                        newSegment2.getDistanceOf(ndp.node) * 5f > newSegment2.length() &&
                        newSegment2.getDistanceOf(node) * 5f > newSegment2.length() &&
                        !newSegment1.intersectsWith(newSegment2) &&
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
}
