package tue.algorithms.implementation.concrete;

import java.util.ArrayList;
import java.util.Arrays;
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
        Optimize(cn, adjNodes, input);
        //Improve(cn, input);
        //Correct(cn, adjNodes, input);
        Better(cn, adjNodes, input);
        Node[] addedNodes = addedNodesList.toArray(new Node[addedNodesList.size()]);
        return new Pair(cn.getAllSegments(), addedNodes);
    }

    public void Optimize(ConnectedNodes cn, AdjacentNodes adjNodes, Node[] nodes) {

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

    public void Improve(ConnectedNodes cn, Node[] nodes) {

        for (Node node : nodes) {
            Segment[] neighbors = cn.getSegments(node);
            if (neighbors.length == 3) {
                for (Segment neighbor : neighbors) {
                    int angle = 0;
                    for (Segment other : neighbors) {
                        if (neighbor == other) {
                            continue;
                        }
                        if (other.getNode1() == node) {
                            angle += Math.abs(neighbor.originAt(node).getAngleOf(other.getNode2()) * (180 / Math.PI));
                        } else {
                            angle += Math.abs(neighbor.originAt(node).getAngleOf(other.getNode1()) * (180 / Math.PI));
                        }
                    }
                    if (angle < 190 && angle > 170) {
                        cn.removeSegment(neighbor);
                    }
                }
            }
            if (neighbors.length == 4) {
                for (Segment neighbor : neighbors) {
                    float angle;
                    for (Segment other : neighbors) {
                        if (neighbor == other) {
                            continue;
                        }
                        if (other.getNode1() == node) {
                            angle = (float) Math.abs(neighbor.originAt(node).getAngleOf(other.getNode2()) * (180 / Math.PI));
                        } else {
                            angle = (float) Math.abs(neighbor.originAt(node).getAngleOf(other.getNode1()) * (180 / Math.PI));
                        }

                        if (angle < 190 && angle > 170) {
                            cn.removeSegment(neighbor);
                            break;
                        }
                    }
                }
            }
        }
    }

    public void Correct(ConnectedNodes cn, AdjacentNodes adjNodes, Node[] nodes) {
        ArrayList<Segment> removedSegments = new ArrayList();
        float z = 0.04f;
        int i = 0;
        for (Node node : nodes) {
            Segment[] neighbors = cn.getSegments(node);
            if (neighbors.length == 1) {
                i++;
                System.out.println(i);

                for (Segment segment : cn.getAllSegments()) {
                    Node d = new Node(1, (float) Math.cos(neighbors[0].endAt(node).getAngle()) * z + node.getX(), (float) Math.sin(neighbors[0].endAt(node).getAngle()) * z + node.getY());
                    if (new Segment(node, d).intersectsWith(segment)) {
                        removedSegments.add(segment);
                    }
                }
            }
        }
        for (Segment segment : removedSegments) {
            cn.removeSegment(segment);
        }
    }

    public void Better(ConnectedNodes cn, AdjacentNodes adjNodes, Node[] nodes){
        
          for (Node node : nodes) {
            Segment[] neighbors = cn.getSegments(node);
              if (neighbors.length ==2) {
                  if (Math.abs(neighbors[0].originAt(node).getAngleOf(neighbors[1].originAt(node).getNode2())*(180/Math.PI)) < 160) {
                      addedNodesList.add(node);
                      Segment[] neighbors1 = cn.getSegments(neighbors[0].originAt(node).getNode2());
                      Segment[] neighbors2 = cn.getSegments(neighbors[1].originAt(node).getNode2());
                      if (neighbors1.length > 2) {
                          cn.removeSegment(neighbors[0]);
                      }
                      if (neighbors2.length > 2) {
                          cn.removeSegment(neighbors[1]);
                      }
                  }
                  
              }
        }
          
 
      
        
    
    }
}
