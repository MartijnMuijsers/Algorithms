package tue.algorithms.viewer;

import java.util.ArrayList;
import org.lwjgl.input.Keyboard;
import static org.lwjgl.opengl.GL11.*;
import static java.lang.Math.*;

public class Simulation {
    // Nodes
    ArrayList<Node> nodes = new ArrayList<>();
    ArrayList<Segment> segments = new ArrayList<>();
   
    // Constructor
    public Simulation() {

    }

    public void initialize(){
		// TODO(wilco): use input reader instead of hard-coded values.
        nodes.add(new Node(0, 0.5f, 0.5f));
        nodes.add(new Node(1, 0f, 0f));
        nodes.add(new Node(2, 0f, 1f));
        nodes.add(new Node(3, 1f, 1f));
        nodes.add(new Node(4, 1f, 0f));
        
        segments.add(new Segment(nodes.get(0), nodes.get(1)));
    }
    
    public boolean getInput() {
        //if ESC is pressed, close program
        return Keyboard.isKeyDown(Keyboard.KEY_ESCAPE);
    }

    public void render(){
        glClear(GL_COLOR_BUFFER_BIT);
        glColor3f(1f, 1f, 1f);
        
        for (Node node : nodes) {
            drawNode(node);
        }
        
        for (Segment segment : segments) {
            drawSegment(segment);
        }         
    }
    
    private void drawSegment(Segment segment){
        glBegin(GL_LINES);
        glVertex3f(segment.getX1(),segment.getY1(),0);
        glVertex3f(segment.getX2(),segment.getY2(),0);
        glEnd();
    }
    
    private void drawNode(Node node){
        drawCircle(node.x, node.y, 0.005f, 32);
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
