package tue.algorithms.implementation.concrete;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import tue.algorithms.implementation.general.NetworkImplementation;
import tue.algorithms.other.Pair;
import tue.algorithms.utility.MinimumSpanningTree;
import tue.algorithms.utility.Node;
import tue.algorithms.utility.Segment;

/**
 * A solution to the Network problem using rectilinear minimum spanning trees.
 *
 * @author Chris
 */
public class NetworkRMST implements NetworkImplementation {

    @Override
    public Pair<Segment[], Node[]> getOutput(Node[] input) {
        Segment[] segments = MinimumSpanningTree.getSegmentsPermutation(input);
        Comparator<Segment> comparator = new Comparator<Segment>() {
            @Override
            public int compare(Segment s1, Segment s2) {
                float diff = s1.manhattanDistance() - s2.manhattanDistance();
                if (diff < 0) {
                    return -1;
                } else if (diff > 0) {
                    return 1;
                }
                return 0;
            }
        };
                           
        Segment[] result = MinimumSpanningTree.applyMST(segments, input, comparator);
        result = Optimize(result, input);
        return new Pair(result, null);

    }

    private Segment[] Optimize(Segment[] segments, Node[] nodes) {
        ArrayList<Segment> mst = new ArrayList<>();
        int test = 1;
        for (Node node : nodes) {
            int count = 0;
            for (Segment segment : segments) {
                if (node == segment.getNode1() || node == segment.getNode2()) {
                    count++;
                }
            }
            if (count == 1) {
                for (Node node2 : nodes) {
                    if (true) {
                        for (Segment dir : segments) {
                            if ((node == dir.getNode1() && node2 == dir.getNode2()) || (node2 == dir.getNode1() && node == dir.getNode2())) {
                                mst.add(new Segment(new Node(-1,node.getX()-0.1f,(1/((node2.getY()-node.getY())/(node2.getX()-node.getX())))*(-0.1f)+node.getY()),new Node(-2,node.getX()+0.1f,(1/((node2.getY()-node.getY())/(node2.getX()-node.getX())))*(0.1f)+node.getY())));
                
                            }
                        }
                    }
                }
            }
        }
        mst.addAll(Arrays.asList(segments));
        Segment[] resultSegments = new Segment[mst.size()];
        int i = 0;
        for (Segment segment : mst) {
            resultSegments[i] = segment;
            i++;
        }

        return resultSegments;
    }
}
