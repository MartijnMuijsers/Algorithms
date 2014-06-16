import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import tue.algorithms.utility.Node;
import tue.algorithms.utility.Segment;

public class LineTest {
    @Test
    public void testDisjointLinesDontIntersect() {
        Segment line1 = new Segment(new Node(Node.FAKE_NODE_ID, 0, 0), new Node(Node.FAKE_NODE_ID, 1, 1));
        Segment line2 = new Segment(new Node(Node.FAKE_NODE_ID, 2, 2), new Node(Node.FAKE_NODE_ID, 3, 3));
        assertFalse("Two disjoint lines should not intersect", line1.intersectsWith(line2));
    }

    @Test
    public void testIntersectingLine() {
        Segment line1 = new Segment(new Node(Node.FAKE_NODE_ID, 0, 1), new Node(Node.FAKE_NODE_ID, 1, -1));
        Segment line2 = new Segment(new Node(Node.FAKE_NODE_ID, 0, -1), new Node(Node.FAKE_NODE_ID, 1, 1));
        assertTrue("Two crossing lines should intersect.", line1.intersectsWith(line2));
    }

    @Test
    public void testConnectedLinesDontIntersect() {
        Segment line1 = new Segment(new Node(Node.FAKE_NODE_ID, 0, 0), new Node(Node.FAKE_NODE_ID, 1, 1));
        Segment line2 = new Segment(new Node(Node.FAKE_NODE_ID, 1, 1), new Node(Node.FAKE_NODE_ID, 2, 2));
        assertFalse("Two lines with a common extreme point should not intersect.", line1.intersectsWith(line2));
    }

    @Test
    public void testConnectedLinesDifferentSlopeDontIntersect() {
        Segment line1 = new Segment(new Node(Node.FAKE_NODE_ID, 0, 0), new Node(Node.FAKE_NODE_ID, 1, 1));
        Segment line2 = new Segment(new Node(Node.FAKE_NODE_ID, 1, 1), new Node(Node.FAKE_NODE_ID, 2, 3));
        assertFalse("Two lines with a common extreme point should not intersect.", line1.intersectsWith(line2));
    }

    @Test
    public void testLineTouchingOtherLine() {
        Segment vertical = new Segment(new Node(Node.FAKE_NODE_ID, 0, 0), new Node(Node.FAKE_NODE_ID, 0, 2));
        Segment horizontal = new Segment(new Node(Node.FAKE_NODE_ID, 0, 1), new Node(Node.FAKE_NODE_ID, 1, 1));
        assertTrue("A line that touches another line should intersect.", vertical.intersectsWith(horizontal));
    }

    @Test
    public void testIdenticalLinesIntersect() {
        Segment line = new Segment(new Node(Node.FAKE_NODE_ID, 0, 0), new Node(Node.FAKE_NODE_ID, 1, 1));
        assertTrue("A line intersects itself.", line.intersectsWith(line));
    }

    @Test
    public void testIdenticalLinesInvertedIntersect() {
        Segment line = new Segment(new Node(Node.FAKE_NODE_ID, 0, 0), new Node(Node.FAKE_NODE_ID, 1, 1));
        assertTrue("A line intersects itself.", line.intersectsWith(line.invertDirection()));
    }

    @Test
    public void testLineSameSlopeIntersect() {
        Segment line1 = new Segment(new Node(Node.FAKE_NODE_ID, 0, 0), new Node(Node.FAKE_NODE_ID, 1, 1));
        Segment line2 = new Segment(new Node(Node.FAKE_NODE_ID, -1, -1), new Node(Node.FAKE_NODE_ID, 2, 2));
        assertTrue("A line contained in another line should intersect.", line1.intersectsWith(line2));
        assertTrue("A line contained in another line should intersect.", line2.intersectsWith(line1));
    }

    @Test
    public void testLineSameSlopeAndOriginIntersect() {
        Segment line1 = new Segment(new Node(Node.FAKE_NODE_ID, 0, 0), new Node(Node.FAKE_NODE_ID, 1, 1));
        Segment line2 = new Segment(new Node(Node.FAKE_NODE_ID, 0, 0), new Node(Node.FAKE_NODE_ID, 2, 2));
        assertTrue("Two lines with the same slope and a common origin intersect.", line1.intersectsWith(line2));
    }

    // Unit tests for angles
    private void assertRelativeAngle(Segment line, Node point, double expectedAngle) {
        double epsilon = 0.0000001;
        double actualAngle = line.getAngleOf(point);

        assertTrue("Post condition lower bound: " + actualAngle/Math.PI + "PI >= -PI", actualAngle >= -Math.PI);
        assertTrue("Post condition upper bound: " + actualAngle/Math.PI + "PI <= +PI", actualAngle <= Math.PI);

        if (actualAngle < -Math.PI + epsilon) {
            // For testing purposes, we just want to know whether a line is vertical.
            // Math.PI = -Math.PI in this regard.
            actualAngle += 2 * Math.PI;
        }

        expectedAngle %= 2 * Math.PI;
        assertEquals("Line(" + line.getX1() + ", " + line.getY1() + " ; " + line.getX2() + ", " + line.getY2() +
                ").getAngleOf(Point(" + point.getX() + " ; " + point.getY() + "))/Math.PI",
                expectedAngle / Math.PI, actualAngle / Math.PI, epsilon);
    }

    /**
     * Tests whether the angle for a pre-determined set of points makes sense after
     * applying a translation by {@code t}, relative to @code line}.
     */
    private void assertRelativeAngleWithExpectedRotation(Segment line, Node t, double rotation) {
        float sqrt3div2 = (float) Math.sqrt(3) / 2;
        // Points listed in counter-clockwise order.
        Node onposx = new Node(2 + t.x, 0 + t.y);
        Node quad1_closetox = new Node(sqrt3div2 + t.x, 0.5f + t.y);
        Node quad1_diagonal = new Node(Node.FAKE_NODE_ID, 2 + t.x, 2 + t.y);
        Node quad1_closetoy = new Node(Node.FAKE_NODE_ID, 0.5f + t.x, sqrt3div2 + t.y);
        Node onposy = new Node(Node.FAKE_NODE_ID, 0 + t.x, 2 + t.y);
        Node quad2_closetoy = new Node(Node.FAKE_NODE_ID, -0.5f + t.x, sqrt3div2 + t.y);
        Node quad2_diagonal = new Node(Node.FAKE_NODE_ID, -2 + t.x, 2 + t.y);
        Node quad2_closetox = new Node(Node.FAKE_NODE_ID, -sqrt3div2 + t.x, 0.5f + t.y);
        Node onnegx = new Node(Node.FAKE_NODE_ID, -2 + t.x, 0 + t.y);
        Node quad3_closetox = new Node(Node.FAKE_NODE_ID, -sqrt3div2 + t.x, -0.5f + t.y);
        Node quad3_diagonal = new Node(Node.FAKE_NODE_ID, -2 + t.x, -2 + t.y);
        Node quad3_closetoy = new Node(Node.FAKE_NODE_ID, -0.5f + t.x, -sqrt3div2 + t.y);
        Node onnegy = new Node(Node.FAKE_NODE_ID, 0 + t.x, -2 + t.y);
        Node quad4_closetoy = new Node(Node.FAKE_NODE_ID, 0.5f + t.x, -sqrt3div2 + t.y);
        Node quad4_diagonal = new Node(Node.FAKE_NODE_ID, 2 + t.x, -2 + t.y);
        Node quad4_closetox = new Node(Node.FAKE_NODE_ID, sqrt3div2 + t.x, -0.5f + t.y);

        // Tests the angle at every possibly relevant location
        assertRelativeAngle(line, onposx, clampPi(rotation + 0));
        assertRelativeAngle(line, quad1_closetox, clampPi(rotation + Math.PI / 6));
        assertRelativeAngle(line, quad1_diagonal, clampPi(rotation + Math.PI / 4));
        assertRelativeAngle(line, quad1_closetoy, clampPi(rotation + Math.PI / 3));
        assertRelativeAngle(line, onposy, clampPi(rotation + Math.PI / 2));
        assertRelativeAngle(line, quad2_closetoy, clampPi(rotation + Math.PI / 2 + Math.PI / 6));
        assertRelativeAngle(line, quad2_diagonal, clampPi(rotation + Math.PI / 2 + Math.PI / 4));
        assertRelativeAngle(line, quad2_closetox, clampPi(rotation + Math.PI / 2 + Math.PI / 3));
        assertRelativeAngle(line, onnegx, clampPi(rotation + Math.PI));
        assertRelativeAngle(line, quad3_closetox, clampPi(rotation + Math.PI + Math.PI / 6));
        assertRelativeAngle(line, quad3_diagonal, clampPi(rotation + Math.PI + Math.PI / 4));
        assertRelativeAngle(line, quad3_closetoy, clampPi(rotation + Math.PI + Math.PI / 3));
        assertRelativeAngle(line, onnegy, clampPi(rotation - Math.PI / 2));
        assertRelativeAngle(line, quad4_closetoy, clampPi(rotation - Math.PI / 2 + Math.PI / 6));
        assertRelativeAngle(line, quad4_diagonal, clampPi(rotation - Math.PI / 2 + Math.PI / 4));
        assertRelativeAngle(line, quad4_closetox, clampPi(rotation - Math.PI / 2 + Math.PI / 3));
    }

    /**
     * Normalizes angles.
     * Output: -1 Math.PI < n <= Math.PI
     */
    private double clampPi(double n) {
        n %= 2 * Math.PI;
        // Normalize expected angles
        if (n > Math.PI) {
            return n - 2 * Math.PI;
        } else if (n <= -Math.PI) {
            return n + 2 * Math.PI;
        }
        return n;
    }

    /**
     * Test some values of the angle method, translated over (translate_x, translate_y).
     */
    private void doTestRotationsAndTranslations(float translate_x, float translate_y) {
        Node t = new Node(Node.FAKE_NODE_ID, translate_x, translate_y);
        Segment xaxisPositive = new Segment(new Node(Node.FAKE_NODE_ID, 0 + t.x, 0 + t.y), new Node(Node.FAKE_NODE_ID, 10, 0));
        Segment xaxisNegative = new Segment(new Node(Node.FAKE_NODE_ID, 0 + t.x, 0 + t.y), new Node(Node.FAKE_NODE_ID, -10 , 0));
        Segment yaxisPositive = new Segment(new Node(Node.FAKE_NODE_ID, 0 + t.x, 0 + t.y), new Node(Node.FAKE_NODE_ID, 0, 10));
        Segment yaxisNegative = new Segment(new Node(Node.FAKE_NODE_ID, 0 + t.x, 0 + t.y), new Node(Node.FAKE_NODE_ID, 0, -10));

        // Standard axes (relative to the positive X axis)
        assertRelativeAngleWithExpectedRotation(xaxisPositive, t, 0);
        // Rotated the coordinate system by 90 degrees (a quarter, counter-clockwise).
        assertRelativeAngleWithExpectedRotation(yaxisPositive, t, -Math.PI / 2);
        // Flipped the coordinate system by 180 degrees.
        assertRelativeAngleWithExpectedRotation(xaxisNegative, t, -Math.PI);
        // Rotated the coordinate system by 270 degrees (counter-clockwise).
        assertRelativeAngleWithExpectedRotation(yaxisNegative, t, Math.PI / 2);
    }

    // Basic test: Rotate the coordinate system around the origin
    @Test
    public void testAngle_0_0() {
        doTestRotationsAndTranslations(0, 0);
    }

    // Tests for all quadrants and axes
    @Test
    public void testAngle_x_axis_positive() {
        doTestRotationsAndTranslations(2, 0);
    }

    @Test
    public void testAngle_quadrant_I() {
        doTestRotationsAndTranslations(2, 2);
    }

    @Test
    public void testAngle_y_axis_positive() {
        doTestRotationsAndTranslations(0, 2);
    }

    @Test
    public void testAngle_quadrant_II() {
        doTestRotationsAndTranslations(-2, 2);
    }

    @Test
    public void testAngle_x_axis_negative() {
        doTestRotationsAndTranslations(-2, 0);
    }

    @Test
    public void testAngle_quadrant_III() {
        doTestRotationsAndTranslations(-2, -2);
    }

    @Test
    public void testAngle_y_axis_negative() {
        doTestRotationsAndTranslations(0, -2);
    }

    @Test
    public void testAngle_quadrant_IV() {
        doTestRotationsAndTranslations(2, -2);
    }
}
