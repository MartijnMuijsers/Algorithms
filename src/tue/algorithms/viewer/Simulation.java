package tue.algorithms.viewer;

import java.lang.UnsupportedOperationException;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_LINES;
import static org.lwjgl.opengl.GL11.GL_POLYGON;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glVertex2f;
import static org.lwjgl.opengl.GL11.glVertex3f;

import java.util.ArrayList;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.Dimension;

import tue.algorithms.implementation.general.MultipleImplementation;
import tue.algorithms.implementation.general.NetworkImplementation;
import tue.algorithms.implementation.general.ProblemType;
import tue.algorithms.implementation.general.SingleImplementation;
import tue.algorithms.other.Pair;
import tue.algorithms.test.FakeInputReader;
import tue.algorithms.utility.Node;
import tue.algorithms.utility.Segment;

public class Simulation {

    /**
     * Get an instance of the class that is chosen to provide input.
     *
     * @return An instance of a class that extends FakeInputReader.
     */
    public static FakeInputReader getFakeInputReader() {
        /* TODO Choose an implementation */
        //throw new UnsupportedOperationException("getFakeInputReader() not implemented.");
        return new tue.algorithms.test.CaseWilcoViewerTest();
    }

    /**
     * Get an instance of the class that is chosen to solve single-curve problem
     * cases.
     *
     * @return An instance of a class that extends SingleImplementation.
     */
    public static SingleImplementation getSingleImplementation() {
        /* TODO Choose an implementation */
        //throw new UnsupportedOperationException("getSingleImplementation() not implemented.");
        return new tue.algorithms.implementation.concrete.SingleRandomSegments();
    }

    /**
     * Get an instance of the class that is chosen to solve multiple-curve
     * problem cases.
     *
     * @return An instance of a class that extends MultipleImplementation.
     */
    public static MultipleImplementation getMultipleImplementation() {
        /* TODO Choose an implementation */
        throw new UnsupportedOperationException("getMultipleImplementation() not implemented.");
    }

    /**
     * Get an instance of the class that is chosen to solve network problem
     * cases.
     *
     * @return An instance of a class that extends NetworkImplementation.
     */
    public static NetworkImplementation getNetworkImplementation() {
        /* TODO Choose an implementation */
        throw new UnsupportedOperationException("getNetworkImplementation() not implemented.");
    }

    // Problem type
    public ProblemType problemType;

    // Input
    private Node[] inputNodes;
    private Segment[] calculatedSegments;
    private Node[] newNetworkNodes;
    private FakeInputReader fakeInputReader;
    private Pair<ProblemType, Node[]> input;

    // Nodes
    private ArrayList<Node> nodes;
    private ArrayList<Segment> segments;

    // Edit boolean
    public boolean editMode;
    private boolean editModeToggleKeyDown;
    private boolean editModeMouseButtonDown;

    // Recalculate
    private boolean recalculateKeyDown;

    // Constructor
    public Simulation() {
        editMode = false;
        editModeToggleKeyDown = false;
        editModeMouseButtonDown = false;
        recalculateKeyDown = false;
    }

    public void initialize() {
        // Read the input
        fakeInputReader = getFakeInputReader();
        input = fakeInputReader.readInput();
        problemType = /*input.first();*/ ProblemType.SINGLE;
        inputNodes = input.second();
        newNetworkNodes = new Node[0];
        
        // Convert to arraylists
        this.nodes = new ArrayList<>(inputNodes.length + newNetworkNodes.length);
        for (Node node : inputNodes) {
            this.nodes.add(node);
        }
        for (Node node : newNetworkNodes) {
            this.nodes.add(node);
        }

        // Output
        calculatedSegments = new Segment[0];
        calculateSegments();
    }

    private void calculateSegments() {
        Node[] allNodes = nodes.toArray(new Node[nodes.size()]);
        
        if (problemType == ProblemType.SINGLE) {
            calculatedSegments = getSingleImplementation().getOutput(allNodes);
        } else if (problemType == ProblemType.MULTIPLE) {
            calculatedSegments = getMultipleImplementation().getOutput(allNodes);
        } else if (problemType == ProblemType.NETWORK) {
            Pair<Segment[], Node[]> output = getNetworkImplementation().getOutput(allNodes);
            calculatedSegments = output.first();
            newNetworkNodes = output.second();
        }

        this.segments = new ArrayList<Segment>(calculatedSegments.length);
        for (Segment segment : calculatedSegments) {
            this.segments.add(segment);
        }
    }

    private void addNode() {
        float clickX = (float) Mouse.getX() / Engine.resolution.getWidth() * 1.05263157895f - 0.025f;
        float clickY = 1 - ((float) Mouse.getY() / Engine.resolution.getHeight() * 1.05263157895f - 0.025f);
        int clickID = nodes.size() + 1;
        nodes.add(new Node(clickID, clickX, clickY));
    }

    public boolean getInput() {
        if (Mouse.isButtonDown(0) && !editModeMouseButtonDown) {
            if (editMode) {
                addNode();
            }
        }
        editModeMouseButtonDown = Mouse.isButtonDown(0);

        if (Keyboard.isKeyDown(Keyboard.KEY_E) && !editModeToggleKeyDown) {
            editMode = !editMode;
        }
        editModeToggleKeyDown = Keyboard.isKeyDown(Keyboard.KEY_E);

        if (Keyboard.isKeyDown(Keyboard.KEY_R) && !recalculateKeyDown) {
            calculateSegments();
        }
        recalculateKeyDown = Keyboard.isKeyDown(Keyboard.KEY_R);

        //if ESC is pressed, close program
        return Keyboard.isKeyDown(Keyboard.KEY_ESCAPE);
    }

    public void render() {
        glClear(GL_COLOR_BUFFER_BIT);
        glColor3f(1f, 1f, 1f);

        for (Node node : nodes) {
            drawNode(node);
        }

        for (Segment segment : segments) {
            drawSegment(segment);
        }
    }

    private void drawSegment(Segment segment) {
        glBegin(GL_LINES);
        glVertex3f(segment.getX1(), segment.getY1(), 0);
        glVertex3f(segment.getX2(), segment.getY2(), 0);
        glEnd();
    }

    private void drawNode(Node node) {
        drawCircle(node.getX(), node.getY(), 0.005f, 32);
    }

    private void drawCircle(float cx, float cy, float r, int num_segments) {
        final float theta = 2f * 3.1415926f / (float) num_segments;
        final float c = (float) cos(theta);
        final float s = (float) sin(theta);
        float t;

        float x = r;// we start at angle = 0 
        float y = 0;

        glBegin(GL_POLYGON);
        for (int ii = 0; ii < num_segments; ii++) {
            glVertex2f(x + cx, y + cy);// output vertex 

            // apply the rotation matrix
            t = x;
            x = c * x - s * y;
            y = s * t + c * y;
        }
        glEnd();
    }
}
