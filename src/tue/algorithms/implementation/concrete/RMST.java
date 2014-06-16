package tue.algorithms.implementation.concrete;

import tue.algorithms.implementation.general.NetworkImplementation;
import tue.algorithms.other.Pair;
import tue.algorithms.utility.MinimumSpanningTree;
import tue.algorithms.utility.Node;
import tue.algorithms.utility.Segment;

// TODO: Rename to EMST
public class RMST implements NetworkImplementation {
    @Override
    public Pair<Segment[], Node[]> getOutput(Node[] input) {
        return new Pair<Segment[], Node[]>(MinimumSpanningTree.getMST(input), new Node[0]);
    }
}
