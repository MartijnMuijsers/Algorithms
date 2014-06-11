package tue.algorithms.implementation.concrete;

import java.util.ArrayList;
import java.util.Comparator;
import tue.algorithms.implementation.general.NetworkImplementation;
import tue.algorithms.other.Pair;
import tue.algorithms.utility.AdjacentNodes;
import tue.algorithms.utility.AdjacentNodes.NodeDistancePair;
import tue.algorithms.utility.ConnectedNodes;
import tue.algorithms.utility.Line;
import tue.algorithms.utility.MinimumSpanningTree;
import tue.algorithms.utility.Node;
import tue.algorithms.utility.Point;
import tue.algorithms.utility.Segment;

/**
 * A solution to the Network problem using rectilinear minimum spanning trees.
 *
 * @author Chris
 */
public class NetworkRMST implements NetworkImplementation {

    ArrayList<Node> addedNodesList = new ArrayList<>();
    float MAXDISTANCE = 0.1f;
    double MINANGLE = 160;
    double STRAIGHTANGLE = 165;
    
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
        addNewSegments(cn, adjNodes, input);

        Node[] addedNodes = addedNodesList.toArray(new Node[0]);
        return new Pair(cn.getAllSegments(), addedNodes);
    }

    public void connectEndPoints(ConnectedNodes cn, AdjacentNodes adjNodes, Node[] nodes) {

        for (Node node : nodes) {
            Segment[] neighbors = cn.getSegments(node);
            if (neighbors.length == 1) {

                NodeDistancePair[] ndps = adjNodes.getAdjacentNodes(node);
                for (int i = 0; i < ndps.length && i < 5; i++) {
                    if (Math.abs(neighbors[0].originAt(node).getAngleOf(ndps[i].node)) > Math.PI / 2 && node.getDistanceTo(ndps[i].node) < MAXDISTANCE) {
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
                if (angle > STRAIGHTANGLE) {
                    for (Segment neighbor : neighbors) {
                        Segment[] secondNeighbors = cn.getSegments(neighbor.getOtherEndpoint(node));
                        for (Segment secondNeighbor : secondNeighbors) {
                            if (secondNeighbor != neighbor) {
                                double secondAngle = Math.abs(neighbor.originAt(neighbor.getOtherEndpoint(node)).getAngleOf(secondNeighbor.getOtherEndpoint(neighbor.getOtherEndpoint(node))) * (180 / Math.PI));
                                if (secondAngle < MINANGLE) {
                                    NodeDistancePair[] ndps = adjNodes.getAdjacentNodes(neighbor.getOtherEndpoint(node));
                                    for (int i = 0; i < ndps.length && i < 5; i++) {
                                        double thirdAngle = Math.abs(neighbor.originAt(neighbor.getOtherEndpoint(node)).getAngleOf(ndps[i].node)) * (180 / Math.PI);
                                        float distance = neighbor.getOtherEndpoint(node).getDistanceTo(ndps[i].node);
                                        if (thirdAngle > MINANGLE && distance < MAXDISTANCE) {
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
                float distance = neighbors[0].getOtherEndpoint(node).getDistanceTo(connectNode);

                if (angle > MINANGLE && connectNode != null && distance < MAXDISTANCE) {
                    Segment segment = new Segment(node, connectNode);
                    boolean intersects = false;
                    for (int i = 0; i < ndps.length && i < 5; i++) {
                        Segment[] intersections = cn.getSegments(ndps[i].node);
                        for (Segment intersection : intersections) {
                            if (segment.intersectsWith(intersection)) {
                                intersects= true;
                                float x = (segment.getY1()-segment.getSlope()*segment.getX1()-intersection.getY1()+intersection.getSlope()*intersection.getX1())/(intersection.getSlope()-segment.getSlope());
                                float y = segment.getSlope()*(x-segment.getX1())+segment.getY1();
                                Node intersectionNode = new Node(nodes.length+1, x, y);
                                addedNodesList.add(intersectionNode);
                                cn.addSegment(new Segment(node, intersectionNode));
                                cn.addSegment(new Segment(segment.getOtherEndpoint(node), intersectionNode));
                                cn.addSegment(new Segment(intersection.getNode1(), intersectionNode));
                                cn.addSegment(new Segment(intersection.getNode2(), intersectionNode));
                                cn.removeSegment(intersection);
                            }
                        }
                    }
                    
                    if (!intersects) {
                        cn.addSegment(segment);
                    } else{
                        
                    }
                } else {
                    tJunction(neighbors[0], node, adjNodes, cn, nodes);
                }
            }
        }
    }

    public void tJunction(Segment segment, Node node, AdjacentNodes adjNodes, ConnectedNodes cn, Node[] nodes) {
        NodeDistancePair[] ndps = adjNodes.getAdjacentNodes(node);
        for (int i = 0; i < ndps.length && i < 10; i++) {
            Segment[] neighbors = cn.getSegments(ndps[i].node);
            for (Segment neighbor : neighbors) {
                Point p = new Point(node.getX() + (float) (Math.cos(segment.endAt(node).getAngle()) * MAXDISTANCE), node.getY() + (float) (Math.sin(segment.endAt(node).getAngle()) * MAXDISTANCE));
                Line line = new Line(node.getX(), node.getY(), p.getX(), p.getY());
                if (line.intersectsWith(neighbor) && segment != neighbor) {
                    float x = (line.getY1()-line.getSlope()*line.getX1()-neighbor.getY1()+neighbor.getSlope()*neighbor.getX1())/(neighbor.getSlope()-line.getSlope());
                    float y = line.getSlope()*(x-line.getX1())+line.getY1();
                    Node intersectionNode = new Node(nodes.length+1, x, y);
                    addedNodesList.add(intersectionNode);
                    cn.addSegment(new Segment(node,intersectionNode));
                    cn.addSegment(new Segment(neighbor.getNode1(), intersectionNode));
                    cn.addSegment(new Segment(neighbor.getNode2(), intersectionNode));
                    cn.removeSegment(neighbor);
                    return;
                }
            }
        }
    }
    
    
}
