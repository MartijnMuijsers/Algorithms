package tue.algorithms.implementation.concrete;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;

import tue.algorithms.implementation.general.NetworkImplementation;
import tue.algorithms.other.Pair;
import tue.algorithms.utility.Node;
import tue.algorithms.utility.Segment;

/**
 * I copied this class to make this version in time available for later use and reference.
 * Please do not modify!! I may use this! Make your own class if you want to.
 * - Martijn (14-5-2014)
 * 
 * @author Chris
 */
public class RMST implements NetworkImplementation {

    @Override
    public Pair<Segment[], Node[]> getOutput(Node[] input) {
        HashSet<Segment> segments = new HashSet<Segment>();

        for (Node node : input) {
            for (Node node2 : input) {

                if (!node.equals(node2)) {
                    segments.add(new Segment(node, node2));
                }
            }
        }

        Segment[] resultSegments = new Segment[segments.size()];
        int i = 0;
        for (Segment segment : segments) {
            resultSegments[i] = segment;
            i++;
        }

        return new Pair<Segment[], Node[]>(MSTsegments(resultSegments, input), new Node[0]);

    }

    public Segment[] MSTsegments(Segment[] segments, Node[] nodes) {
        HashSet<Segment> mst = new HashSet<>();
        Sort(segments);
        HashMap<Node, Integer> A = new HashMap<Node, Integer>();
        HashMap<Integer, HashSet<Node>> B = new HashMap<Integer, HashSet<Node>>();
        for (Node node : nodes){
            A.put(node, node.getId());
            HashSet<Node> set = new HashSet<Node>();
            set.add(node);
            B.put(node.getId(), set);
        }
        for (Segment segment : segments){
            int u = A.get(segment.getNode1());
            int v = A.get(segment.getNode2());
            if (u != v) {
                mst.add(segment);
                HashSet<Node> U = B.get(u);
                HashSet<Node> V = B.get(v);
                for (Node node : U) {
                    A.put(node, v);
                    V.add(node);
                }
                U.clear();
            }
        }
        
        Segment[] resultSegments = new Segment[mst.size()];
        int i = 0;
        for (Segment segment : mst) {
            resultSegments[i] = segment;
            i++;
        }
        return resultSegments;
    }

    private void Sort(Segment[] segments) {
        
        ArrayList<Segment> list = toArrayList(segments);
        
        Collections.sort(list, segmentManhattanComparator);
        
        for (int i = 0; i < segments.length; i++) {
            segments[i] = list.get(i);
        }
        
    }
    
    private Comparator<Segment> segmentManhattanComparator = new Comparator<Segment>() {

        @Override
        public int compare(Segment t, Segment t1) {
            if (t.manhattanDistance() < t1.manhattanDistance()) {
                return -1;
            } else if (t.manhattanDistance() > t1.manhattanDistance()) {
                return 1;
            }
            return 0;
        }
        
    };
    
    private <T> ArrayList<T> toArrayList(T[] array) {
        ArrayList<T> list = new ArrayList<T>();
        for (T t : array) {
            list.add(t);
        }
        return list;
    }

}
