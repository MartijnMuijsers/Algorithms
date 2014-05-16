import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import tue.algorithms.utility.Line;

public class LineTest {
    @Test
    public void testDisjointLinesDontIntersect() {
        Line line1 = new Line(0, 0, 1, 1);
        Line line2 = new Line(2, 2, 3, 3);
        assertFalse("Two disjoint lines should not intersect", line1.intersectsWith(line2));
    }

    @Test
    public void testIntersectingLine() {
        Line line1 = new Line(0, 1, 1, -1);
        Line line2 = new Line(0, -1, 1, 1);
        assertTrue("Two crossing lines should intersect.", line1.intersectsWith(line2));
    }

    @Test
    public void testConnectedLinesDontIntersect() {
        Line line1 = new Line(0, 0, 1, 1);
        Line line2 = new Line(1, 1, 2, 2);
        assertFalse("Two lines with a common extreme point should not intersect.", line1.intersectsWith(line2));
    }

    @Test
    public void testConnectedLinesDifferentSlopeDontIntersect() {
        Line line1 = new Line(0, 0, 1, 1);
        Line line2 = new Line(1, 1, 2, 3);
        assertFalse("Two lines with a common extreme point should not intersect.", line1.intersectsWith(line2));
    }

    @Test
    public void testLineTouchingOtherLine() {
        Line vertical = new Line(0, 0, 0, 2);
        Line horizontal = new Line(0, 1, 1, 1);
        assertTrue("A line that touches another line should intersect.", vertical.intersectsWith(horizontal));
    }

    @Test
    public void testIdenticalLinesIntersect() {
        Line line = new Line(0, 0, 1, 1);
        assertTrue("A line intersects itself.", line.intersectsWith(line));
    }

    @Test
    public void testIdenticalLinesInvertedIntersect() {
        Line line = new Line(0, 0, 1, 1);
        assertTrue("A line intersects itself.", line.intersectsWith(line.invertDirection()));
    }

    @Test
    public void testLineSameSlopeIntersect() {
        Line line1 = new Line(0, 0, 1, 1);
        Line line2 = new Line(-1, -1, 2, 2);
        assertTrue("A line contained in another line should intersect.", line1.intersectsWith(line2));
        assertTrue("A line contained in another line should intersect.", line2.intersectsWith(line1));
    }

    @Test
    public void testLineSameSlopeAndOriginIntersect() {
        Line line1 = new Line(0, 0, 1, 1);
        Line line2 = new Line(0, 0, 2, 2);
        assertTrue("Two lines with the same slope and a common origin intersect.", line1.intersectsWith(line2));
    }
}
