package tue.algorithms.viewer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_LINES;
import static org.lwjgl.opengl.GL11.GL_POLYGON;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glLineWidth;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glScalef;
import static org.lwjgl.opengl.GL11.glTranslatef;
import static org.lwjgl.opengl.GL11.glVertex2f;
import static org.lwjgl.opengl.GL11.glVertex3f;
import org.lwjgl.util.Dimension;

import tue.algorithms.implementation.concrete.NetworkRMST;
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
        return new tue.algorithms.test.CaseEmpty();
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
        
        return new NetworkRMST();
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
    private ArrayList<Node> tempNodes;
    private ArrayList<Segment> segments;

    // Keypress memory
    private boolean editModeMouseButtonDown;
    private boolean clearKeyDown;
    private boolean recalculateKeyDown;
    private boolean typeKeyDown;
    private boolean saveKeyDown;
    private boolean openKeyDown;
    
    // Clear
    private boolean showSegments;
    private boolean brushMode;
    
    // Constructor
    public Simulation() {
        editModeMouseButtonDown = false;
        recalculateKeyDown = false;
        clearKeyDown = false;
        showSegments = true;
        saveKeyDown = false;
        openKeyDown = false;
        brushMode = false;
        typeKeyDown = false;
    }

    public void initialize() {
        // Read the input
        fakeInputReader = getFakeInputReader();
        input = fakeInputReader.readInput();
        problemType = /*input.first();*/ ProblemType.NETWORK;
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
    }
 
    public void render() {
        glClear(GL_COLOR_BUFFER_BIT);
        
        glColor3f(1f, 1f, 1f);
        glLineWidth(2f);
        if (showSegments) {
            for (Segment segment : segments) {
                drawSegment(segment);
            }
        }
    
        glColor3f(1f, 1f, 0f);
        float ratio = ((float)Camera.width) / Camera.heigth;
        for (Node node : nodes) {
            glPushMatrix();
            glTranslatef(node.getX(), node.getY(), 0);
            float pointSize = Math.min(Camera.width, Camera.heigth);
            glScalef(600/pointSize, 600/pointSize, 1);
            if (ratio > 1f) {
                glScalef(1f/ratio, 1f, 1);
            } else if (ratio < 1f) {
                glScalef(1f, 1f/ratio, 1);
            }
            drawNode();
            glPopMatrix();
        }

        glColor3f(1f, 0f, 0f);
        if (brushMode){
            glPushMatrix();
            glTranslatef((float) Mouse.getX() / Camera.width * 1.0f / Camera.SCALINGFACTOR - Camera.OFFSETFACTOR, (float) Mouse.getY() / Camera.heigth * 1.0f / Camera.SCALINGFACTOR - Camera.OFFSETFACTOR, 0);
                        float pointSize = Math.min(Camera.width, Camera.heigth);
            glScalef(600/pointSize, 600/pointSize, 1);
            if (ratio > 1f) {
                glScalef(1f/ratio, 1f, 1);
            } else if (ratio < 1f) {
                glScalef(1f, 1f/ratio, 1);
            }
            drawCircle( Camera.OFFSETFACTOR, 16);
            glPopMatrix();
        }
    }
    
    public boolean getInput() throws IOException {
        // Add
        if (Mouse.isButtonDown(0) && !editModeMouseButtonDown) {
                addNode();
        }
        editModeMouseButtonDown = Mouse.isButtonDown(0);
        
        // Erase
        if (!brushMode && Mouse.isButtonDown(1)) {
                brushMode = true;
        }
        if (brushMode && !Mouse.isButtonDown(1)){
            brushMode = false;
        }
        if (brushMode){
            deleteNodes();
        }

        // Recalculate
        if (Keyboard.isKeyDown(Keyboard.KEY_R) && !recalculateKeyDown) {
            calculateSegments();
            showSegments = true;
        }
        recalculateKeyDown = Keyboard.isKeyDown(Keyboard.KEY_R);

        // Clear
        if (Keyboard.isKeyDown(Keyboard.KEY_C) && !clearKeyDown) {
            showSegments = false;
        }
        clearKeyDown = Keyboard.isKeyDown(Keyboard.KEY_C);        
        
        // Save
        if (Keyboard.isKeyDown(Keyboard.KEY_S) && !saveKeyDown) {
            save();
        }
        saveKeyDown = Keyboard.isKeyDown(Keyboard.KEY_S);  
        
        // Open
        if (Keyboard.isKeyDown(Keyboard.KEY_O) && !openKeyDown) {
            open();
        }
        openKeyDown = Keyboard.isKeyDown(Keyboard.KEY_O);  
 
        // Type
        if (Keyboard.isKeyDown(Keyboard.KEY_T) && !typeKeyDown) {
            toggleType();
        }
        typeKeyDown = Keyboard.isKeyDown(Keyboard.KEY_T);          
        
        //if ESC is pressed, close program
        return Keyboard.isKeyDown(Keyboard.KEY_ESCAPE);
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

        this.segments = new ArrayList<>(calculatedSegments.length);
        for (Segment segment : calculatedSegments) {
            this.segments.add(segment);
        }
    }

    private void toggleType(){
        if (problemType == ProblemType.SINGLE){
            problemType = ProblemType.MULTIPLE;
        } else if (problemType == ProblemType.MULTIPLE){
            problemType = ProblemType.NETWORK;
        } else if (problemType == ProblemType.NETWORK){
            problemType = ProblemType.SINGLE;
        } 
    }
    
    private void deleteNodes() {
        float clickX = (float) Mouse.getX() / Camera.width * 1.0f / Camera.SCALINGFACTOR - Camera.OFFSETFACTOR;
        float clickY = (float) Mouse.getY() / Camera.heigth * 1.0f / Camera.SCALINGFACTOR - Camera.OFFSETFACTOR;
        Node mouseNode = new Node(0, clickX, clickY);

        boolean modified = false;
        ArrayList<Node> nodesToDelete = new ArrayList<>();
        for (Node node : nodes) {
            if (node.subtract(mouseNode).length() < 0.025) {
                nodesToDelete.add(node);
                modified = true;
            }
        }
        if (modified) {
            tempNodes = new ArrayList<>(nodes);
            for (Node n : nodesToDelete) {
                tempNodes.remove(n);
            }            
            int i = 0;
            for (Node n : tempNodes) {
                n = new Node(i + 1, n.getX(), n.getY());
                ++i;
            }
            nodes = tempNodes;
        }
    }

    private void addNode() {
        float clickX = (float) Mouse.getX() / Camera.width * 1.0f / Camera.SCALINGFACTOR - Camera.OFFSETFACTOR;
        float clickY = ((float) Mouse.getY() / Camera.heigth * 1.0f / Camera.SCALINGFACTOR - Camera.OFFSETFACTOR);
        if (clickX >= 0 && clickX <= 1 && clickY >= 0 && clickY <= 1) {
            tempNodes = new ArrayList<>();
            int i = 1;
            for (Node node : nodes) {
                tempNodes.add(new Node(i, node.getX(), node.getY()));
                ++i;
            }
            tempNodes.add(new Node(tempNodes.size() + 1, clickX, clickY));

            nodes = tempNodes;
        }
    }

    private void buildFile(File file){
        try (PrintStream fileStream = new PrintStream(file);) {
            fileStream.print("reconstruct ");
            if (problemType.equals(ProblemType.SINGLE)){
                fileStream.println("single");
            } else if (problemType.equals(ProblemType.MULTIPLE)){
                fileStream.println("multiple");
            } else {
                fileStream.println("network");
            } 
            fileStream.println(nodes.size() + " number of sample points");         
            for (Node node : nodes) {
			fileStream.println(node.getId() + " " + node.getX() + " " + node.getY());
            }
            fileStream.flush();
        } catch (IOException ex) {
            Logger.getLogger(Simulation.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void save() {
        JFileChooser saveFile = new JFileChooser();
        saveFile.showSaveDialog(null);
        if (saveFile.getSelectedFile() != null) {
            File file = saveFile.getSelectedFile();
            try {
                file.createNewFile();
                buildFile(file);
            } catch (IOException ex) {
                Logger.getLogger(Simulation.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void open() throws FileNotFoundException{
        JFileChooser openFile = new JFileChooser();
        if (openFile.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            File file = openFile.getSelectedFile();
            ProblemType pType;
            try (Scanner scanner = new Scanner(file)) {
                String line = scanner.nextLine();
                pType = ProblemType.valueOf(line.substring(12).toUpperCase());
                line = scanner.nextLine();
                int numberOfNodes = Integer.parseInt(line.substring(0, line.indexOf(' ')));
                nodes.clear();
                for (int i = 0; i < numberOfNodes; i++) {
                    nodes.add(new Node(scanner.nextInt(), scanner.nextFloat(), scanner.nextFloat()));
                }
            }
            problemType = pType;
            showSegments = false;
        }
    }
    
    private void drawSegment(Segment segment) {
        glBegin(GL_LINES);
        glVertex3f(segment.getX1(), segment.getY1(), 0);
        glVertex3f(segment.getX2(), segment.getY2(), 0);
        glEnd();
    }

    private void drawNode() {
        drawCircle(0.005f, 32);
    }

    private void drawCircle(float r, int num_segments) {
        final float theta = 2f * 3.1415926f / (float) num_segments;
        final float c = (float) cos(theta);
        final float s = (float) sin(theta);
        float t;

        float x = r;// we start at angle = 0 
        float y = 0;

        glBegin(GL_POLYGON);
        for (int ii = 0; ii < num_segments; ii++) {
            glVertex2f(x, y);// output vertex 

            // apply the rotation matrix
            t = x;
            x = c * x - s * y;
            y = s * t + c * y;
        }
        glEnd();
    }
}
