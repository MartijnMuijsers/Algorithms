package tue.algorithms.implementation.concrete;

import java.util.ArrayList;
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

    ArrayList<Node> addedNodesList = new ArrayList<>();

    @Override
    public Pair<Segment[], Node[]> getOutput(Node[] input) {
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

        Segment[] mst = MinimumSpanningTree.applyMST(segments, input, comparator);
        ConnectedNodes cn = new ConnectedNodes();
        for (Segment segment : mst) {
            cn.addSegment(segment);
        }
        AdjacentNodes adjNodes = new AdjacentNodes(input);

        connectEndPoints(cn, adjNodes, input);
        removeObliqueSegments(cn, adjNodes, input);
        addNewSegments(cn, adjNodes, input);
        Node[] addedNodes = addedNodesList.toArray(new Node[addedNodesList.size()]);
        return new Pair(cn.getAllSegments(), addedNodes);
    }

    public void connectEndPoints(ConnectedNodes cn, AdjacentNodes adjNodes, Node[] nodes) {

        for (Node node : nodes) {
            Segment[] neighbors = cn.getSegments(node);
            if (neighbors.length == 1) {

                NodeDistancePair[] ndps = adjNodes.getAdjacentNodes(node);
                for (int i = 0; i < ndps.length && i < 5; i++) {
                    if (Math.abs(neighbors[0].originAt(node).getAngleOf(ndps[i].node)) > Math.PI / 2 && node.getDistanceTo(ndps[i].node) < 0.09f) {
                        cn.addSegment(new Segment(node, ndps[i].node));
                        break;
                    }
                }
            }
        }
    }

    public void removeObliqueSegments(ConnectedNodes cn, AdjacentNodes adjNodes, Node[] nodes) {
        for (Node node : nodes) {
            Segment[] neighbors = cn.getSegments(node);
            if (neighbors.length == 2) {
                double angle = Math.abs(neighbors[0].originAt(node).getAngleOf(neighbors[1].getOtherEndpoint(node)) * (180 / Math.PI));
                System.out.println(angle);
                if (angle > 170) {
                    for (Segment neighbor : neighbors) {
                        Segment[] secondNeighbors = cn.getSegments(neighbor.getOtherEndpoint(node));
                        for (Segment secondNeighbor : secondNeighbors) {
                            if (secondNeighbor != neighbor) {
                                double secondAngle = Math.abs(neighbor.originAt(neighbor.getOtherEndpoint(node)).getAngleOf(secondNeighbor.getOtherEndpoint(neighbor.getOtherEndpoint(node))) * (180 / Math.PI));
                                if (secondAngle < 165) {
                                    NodeDistancePair[] ndps = adjNodes.getAdjacentNodes(neighbor.getOtherEndpoint(node));
                                    for (int i = 0; i < ndps.length && i < 5; i++) {
                                        double thirdAngle = Math.abs(neighbor.originAt(neighbor.getOtherEndpoint(node)).getAngleOf(ndps[i].node)) * (180 / Math.PI);
                                        float distance = neighbor.getOtherEndpoint(node).getDistanceTo(ndps[i].node);
                                        if (thirdAngle > 170 && distance < 0.07f) {
                                            cn.removeSegment(secondNeighbor);
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void addNewSegments(ConnectedNodes cn, AdjacentNodes adjNodes, Node[] nodes) {
        for (Node node : nodes) {
            Segment[] neighbors = cn.getSegments(node);
            NodeDistancePair[] ndps = adjNodes.getAdjacentNodes(node);
            if (neighbors.length == 1) {
                Node connectNode = null;
                double angle = 0;
                double newAngle;
                for (int i = 0; i < ndps.length && i < 5; i++) {
                    newAngle = Math.abs(neighbors[0].originAt(node).getAngleOf(ndps[i].node) * (180 / Math.PI));
                    if (angle < newAngle) {
                        angle = newAngle;
                        connectNode = ndps[i].node;
                    }
                }
                if (angle > 165 && connectNode != null) {
                    cn.addSegment(new Segment(node, connectNode));
                }
            }
        }
    }
}
