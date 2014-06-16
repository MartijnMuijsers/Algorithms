import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import tue.algorithms.utility.ConnectedNodes;
import tue.algorithms.utility.Node;
import tue.algorithms.utility.Segment;

public class ConnectedNodesTest {
    // Nodes in a square
    private Node topleft     = new Node(1, -1.0f,  1.0f);
    private Node topright    = new Node(2,  1.0f,  1.0f);
    private Node bottomright = new Node(3,  1.0f, -1.0f);
    private Node bottomleft  = new Node(4, -1.0f, -1.0f);
    // All possible segments in the square
    private Segment topleft_topright       = new Segment(topleft, topright);
    private Segment topright_bottomright   = new Segment(topright, bottomright);
    private Segment bottomright_bottomleft = new Segment(bottomright, bottomleft);
    private Segment bottomleft_topleft     = new Segment(bottomleft, topleft);
    private Segment topleft_bottomright    = new Segment(topleft, bottomright);
    private Segment topright_bottomleft    = new Segment(topright, bottomleft);

    // The ConnectedNodes instance used for each test.
    private ConnectedNodes cn;

    @Before
    public void setUp() {
        Node.clearNodeCache();
        cn = new ConnectedNodes();
    }

    // Create a square and one diagonal
    @Test
    public void testSquareNoIntersections() {
        assertAddSegment(topleft_topright);
        assertAddSegment(topright_bottomright);
        assertAddSegment(bottomright_bottomleft);
        assertAddSegment(bottomleft_topleft);

        assertAddSegment(topleft_bottomright);

        assertEquals(5, cn.getAllSegments().length);
    }

    // Two perpendicular lines intersect each other
    @Test
    public void testCrossIntersection() {
        assertAddSegment(topleft_bottomright);
        assertTrue("Segment should intersect graph", cn.intersectsGraph(topright_bottomleft));
    }

    @Test
    public void testAddNoDuplicates() {
        assertAddSegment(topleft_topright);
        cn.addSegment(topleft_topright);

        assertEquals(1, cn.getAllSegments().length);
    }

    @Test
    public void testAddNoDuplicatesInverted() {
        assertAddSegment(topleft_topright);
        cn.addSegment(topleft_topright.invertDirection());

        assertEquals(1, cn.getAllSegments().length);
    }

    @Test
    public void testRemoveSegmentByInvertedSegment() {
        assertAddSegment(topleft_topright);
        cn.removeSegment(topleft_topright.invertDirection());

        assertEquals(0, cn.getAllSegments().length);
    }

    @Test
    public void testGetOtherSegment() {
        assertAddSegment(bottomright_bottomleft);
        assertAddSegment(topleft_bottomright);
        assertTrue("getOtherSegment(node, segment) should return the other segment connected to node",
                cn.getOtherSegment(bottomright, bottomright_bottomleft) == topleft_bottomright);
    }

    @Test
    public void testGetOtherSegmentInverted() {
        assertAddSegment(bottomright_bottomleft);
        assertAddSegment(topleft_bottomright);
        assertTrue("getOtherSegment(node, segment) should return the other segment connected to node",
                cn.getOtherSegment(bottomright, bottomright_bottomleft.invertDirection()) == topleft_bottomright);
    }

    @Test
    public void testGetOtherSegmentNonExistent() {
        assertAddSegment(bottomright_bottomleft);
        assertAddSegment(topleft_bottomright);
        assertTrue("getOtherSegment(sole endpoint, segment) should return null",
                cn.getOtherSegment(bottomleft, bottomright_bottomleft) == null);
    }

    private void assertAddSegment(Segment s) {
        assertFalse("Segment should not intersect graph", cn.intersectsGraph(s));
        cn.addSegment(s);
    }
}
