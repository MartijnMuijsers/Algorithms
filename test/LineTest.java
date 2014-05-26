import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import tue.algorithms.utility.Line;
import tue.algorithms.utility.Point;

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

    // Unit tests for angles
    private void assertRelativeAngle(Line line, Point point, double expectedAngle) {
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
    private void assertRelativeAngleWithExpectedRotation(Line line, Point t, double rotation) {
        float sqrt3div2 = (float) Math.sqrt(3) / 2;
        // Points listed in counter-clockwise order.
        Point onposx = new Point(2, 0).add(t);
        Point quad1_closetox = new Point(sqrt3div2, 0.5f).add(t);
        Point quad1_diagonal = new Point(2, 2).add(t);
        Point quad1_closetoy = new Point(0.5f, sqrt3div2).add(t);
        Point onposy = new Point(0, 2).add(t);
        Point quad2_closetoy = new Point(-0.5f, sqrt3div2).add(t);
        Point quad2_diagonal = new Point(-2, 2).add(t);
        Point quad2_closetox = new Point(-sqrt3div2, 0.5f).add(t);
        Point onnegx = new Point(-2, 0).add(t);
        Point quad3_closetox = new Point(-sqrt3div2, -0.5f).add(t);
        Point quad3_diagonal = new Point(-2, -2).add(t);
        Point quad3_closetoy = new Point(-0.5f, -sqrt3div2).add(t);
        Point onnegy = new Point(0, -2).add(t);
        Point quad4_closetoy = new Point(0.5f, -sqrt3div2).add(t);
        Point quad4_diagonal = new Point(2, -2).add(t);
        Point quad4_closetox = new Point(sqrt3div2, -0.5f).add(t);

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
        Point t = new Point(translate_x, translate_y);
        Line xaxisPositive = new Line(0, 0, 10, 0).add(t);
        Line xaxisNegative = new Line(0, 0, -10, 0).add(t);
        Line yaxisPositive = new Line(0, 0, 0, 10).add(t);
        Line yaxisNegative = new Line(0, 0, 0, -10).add(t);

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
