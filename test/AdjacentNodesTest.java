import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import tue.algorithms.utility.AdjacentNodes;
import tue.algorithms.utility.AdjacentNodes.NodeDistancePair;
import tue.algorithms.utility.Node;

public class AdjacentNodesTest {
    private Node[] nodes = {
        new Node(1, -1.0f,  0.0f),
        new Node(2,  0.0f, -1.0f),
        new Node(3,  2.0f,  0.0f),
        new Node(4, -3.0f,  2.0f),
        new Node(5,  4.0f, -1.0f)
    };

    /**
     * Verifies that the returned list from getAdjacentNodes is sorted.
     */
    @Test
    public void testAdjacentNodes() {
        AdjacentNodes adjNodes = new AdjacentNodes(nodes);

        for (Node node : nodes) {
            System.out.println("Testing node " + node.getId());

            NodeDistancePair[] ndPairs = adjNodes.getAdjacentNodes(node);
            // Exclude the input node
            assertEquals(nodes.length - 1, ndPairs.length);

            float previousDistance = 0;
            for (NodeDistancePair ndp : ndPairs) {
                System.out.println(ndp);

                assertTrue(ndp.distance > 0);
                assertTrue(ndp.distance >= previousDistance);
                previousDistance = ndp.distance;
            }
        }
    }
}
