package tue.algorithms.implementation.concrete;

import java.util.ArrayList;
import java.util.Comparator;
import tue.algorithms.implementation.general.NetworkImplementation;
import tue.algorithms.other.Pair;
import tue.algorithms.utility.AdjacentNodes;
import tue.algorithms.utility.NodeDistancePair;
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

    ArrayList<Node> addedNodesList = new ArrayList<Node>();
    float MAXDISTANCE = 0f;
    double MINANGLE = 167;
    double STRAIGHTANGLE = 170;
    
    @Override
    public Pair<Segment[], Node[]> getOutput(Node[] input) {
        ConnectedNodes cn = new ConnectedNodes();
        for (Segment segment : MinimumSpanningTree.getMST(input)) {
            cn.addSegment(segment);
            MAXDISTANCE += segment.length();
        }
        MAXDISTANCE = (MAXDISTANCE / (input.length - 1)) * 2.5f;
        AdjacentNodes adjNodes = new AdjacentNodes(input);
        
        connectEndPoints(cn, adjNodes, input);
        removeObliqueSegments(cn, adjNodes, input);
        addNewSegments(cn, adjNodes, input);
        addNewSegments(cn, adjNodes, input);
        connectEndPoints(cn, adjNodes, input);
       

        Node[] addedNodes = addedNodesList.toArray(new Node[0]);
        return new Pair(cn.getAllSegments(), addedNodes);
    }

    public void connectEndPoints(ConnectedNodes cn, AdjacentNodes adjNodes, Node[] nodes) {

        for (Node node : nodes) {
            Segment[] neighbors = cn.getSegments(node);
            if (neighbors.length == 1) {

                NodeDistancePair[] ndps = adjNodes.getAdjacentNodes(node);
                for (int i = 0; i < ndps.length && i < 10; i++) {
                    if (Math.abs(neighbors[0].originAt(node).getAngleOf(ndps[i].node)) > Math.PI / 2 && node.getDistanceTo(ndps[i].node) < MAXDISTANCE) {
                        cn.addSegment(new Segment(node, ndps[i].node));
                        break;
                    }
                }
            }
            if (neighbors.length == 0) {
                NodeDistancePair[] ndps = adjNodes.getAdjacentNodes(node);
                cn.addSegment(new Segment(node, ndps[0].node));
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
                                    for (int i = 0; i < ndps.length && i < 10; i++) {
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
            if (neighbors.length == 1) {
                NodeDistancePair[] ndps = adjNodes.getAdjacentNodes(node);
                Node connectNode = null;
                double angle = 0;
                double newAngle;
                float distance;
                for (int i = 0; i < ndps.length && i < 10; i++) {
                    newAngle = Math.abs(neighbors[0].originAt(node).getAngleOf(ndps[i].node) * (180 / Math.PI));
                    distance = neighbors[0].getOtherEndpoint(node).getDistanceTo(ndps[i].node);

                    if (angle < newAngle && distance < MAXDISTANCE) {
                        angle = newAngle;
                        connectNode = ndps[i].node;
                    }
                }
                if (angle > MINANGLE) {
                    Segment segment = new Segment(node, connectNode);
                    boolean intersects = false;
                    for (int i = 0; i < ndps.length && i < 10; i++) {
                        Segment[] intersections = cn.getSegments(ndps[i].node);
                        for (Segment intersection : intersections) {
                            if (segment.intersectsWith(intersection) && segment !=intersection) {
                                intersects= true;
                                float x = (segment.getY1()-segment.getSlope()*segment.getX1()-intersection.getY1()+intersection.getSlope()*intersection.getX1())/(intersection.getSlope()-segment.getSlope());
                                float y = segment.getSlope()*(x-segment.getX1())+segment.getY1();
                                Node intersectionNode = new Node(x, y);
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
                Node p = new Node(Node.FAKE_NODE_ID,
                        node.x + (float) (Math.cos(segment.endAt(node).getAngle()) * MAXDISTANCE),
                        node.y + (float) (Math.sin(segment.endAt(node).getAngle()) * MAXDISTANCE));
                Segment line = new Segment(node, p);
                if (line.intersectsWith(neighbor) && segment != neighbor) {
                    float x = (line.getY1()-line.getSlope()*line.getX1()-neighbor.getY1()+neighbor.getSlope()*neighbor.getX1())/(neighbor.getSlope()-line.getSlope());
                    float y = line.getSlope()*(x-line.getX1())+line.getY1();
                    Node intersectionNode = new Node(x, y);
                    addedNodesList.add(intersectionNode);
                    cn.addSegment(new Segment(node, intersectionNode));
                    cn.addSegment(new Segment(neighbor.getNode1(), intersectionNode));
                    cn.addSegment(new Segment(neighbor.getNode2(), intersectionNode));
                    cn.removeSegment(neighbor);
                    return;
                }
            }
        }
    }
    
    
}
