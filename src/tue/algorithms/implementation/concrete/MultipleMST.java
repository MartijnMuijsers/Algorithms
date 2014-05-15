package tue.algorithms.implementation.concrete;

import java.util.Comparator;
import tue.algorithms.implementation.general.MultipleImplementation;
import tue.algorithms.utility.MinimumSpanningTree;
import tue.algorithms.utility.Node;
import tue.algorithms.utility.Point;
import tue.algorithms.utility.Segment;

/**
 * A solution to the multiple curve reconstruction problem using a MST with custom weight.
 *
 * @author Rob
 */
public class MultipleMST extends MultipleImplementation {

    @Override
    public Segment[] getOutput(Node[] input) {
        Segment[] segments = MinimumSpanningTree.getSegmentsPermutation(input);
        Comparator<Segment> comparator = new Comparator<Segment>() {
            @Override
            public int compare(Segment s1, Segment s2) {
                // TODO: Implement a weight.
                return 1;
            }
        };

        Segment[] result = MinimumSpanningTree.applyMST(segments, input, comparator);
        return result;

    }

}
