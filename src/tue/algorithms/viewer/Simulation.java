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
	 * @return An instance of a class that extends FakeInputReader.
	 */
	public static FakeInputReader getFakeInputReader() {
		/* TODO Choose an implementation */
		throw new UnsupportedOperationException("getFakeInputReader() not implemented.");
	}
	
	/**
	 * Get an instance of the class that is chosen to solve single-curve problem cases.
	 * @return An instance of a class that extends SingleImplementation.
	 */
	public static SingleImplementation getSingleImplementation() {
		/* TODO Choose an implementation */
		throw new UnsupportedOperationException("getSingleImplementation() not implemented.");
	}
	
	/**
	 * Get an instance of the class that is chosen to solve multiple-curve problem cases.
	 * @return An instance of a class that extends MultipleImplementation.
	 */
	public static MultipleImplementation getMultipleImplementation() {
		/* TODO Choose an implementation */
		throw new UnsupportedOperationException("getMultipleImplementation() not implemented.");
	}
	
	/**
	 * Get an instance of the class that is chosen to solve network problem cases.
	 * @return An instance of a class that extends NetworkImplementation.
	 */
	public static NetworkImplementation getNetworkImplementation() {
		/* TODO Choose an implementation */
		throw new UnsupportedOperationException("getNetworkImplementation() not implemented.");
	}
	
	// Nodes
	private ArrayList<Node> nodes;
	private ArrayList<Segment> segments;

	// Constructor
	public Simulation() {

	}

	public void initialize() {
		// Read the input
		FakeInputReader fakeInputReader = getFakeInputReader();
		Pair<ProblemType, Node[]> input = fakeInputReader.readInput();
		ProblemType problemType = input.first();
		Node[] nodes = input.second();
		// Solve for the output
		Segment[] segments = new Segment[0];
		Node[] newNodes = new Node[0];
		if (problemType == ProblemType.SINGLE) {
			segments = getSingleImplementation().getOutput(nodes);
		} else if (problemType == ProblemType.MULTIPLE) {
			segments = getMultipleImplementation().getOutput(nodes);
		} else if (problemType == ProblemType.NETWORK) {
			Pair<Segment[], Node[]> output = getNetworkImplementation().getOutput(nodes);
			segments = output.first();
			newNodes = output.second();
		}
		// Convert to arraylists
		this.nodes = new ArrayList<Node>(nodes.length+newNodes.length);
		for (Node node : nodes) {
			this.nodes.add(node);
		}
		for (Node node : newNodes) {
			this.nodes.add(node);
		}
		this.segments = new ArrayList<Segment>(segments.length);
		for (Segment segment : segments) {
			this.segments.add(segment);
		}
	}

	public boolean getInput() {
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
