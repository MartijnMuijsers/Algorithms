package tue.algorithms.viewer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
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
import tue.algorithms.implementation.general.MultipleImplementation;
import tue.algorithms.implementation.general.NetworkImplementation;
import tue.algorithms.implementation.general.ProblemType;
import tue.algorithms.implementation.general.SingleImplementation;
import tue.algorithms.other.Debug;
import tue.algorithms.other.Pair;
import tue.algorithms.test.FakeInputReader;
import tue.algorithms.utility.Node;
import tue.algorithms.utility.Point;
import tue.algorithms.utility.Segment;

public class Simulation {

    final private static float ERASER_RADIUS = 0.025f;
    final private static float ERASER_RADIUS_NETWORK = 0.008f;
    final private static float NODE_RADIUS = 0.0054f;

	/**
     * Get an instance of the class that is chosen to provide input.
     *
     * @return An instance of a class that extends FakeInputReader.
     */
    public static FakeInputReader getFakeInputReader() {
        return SimulationSettings.getFakeInputReader();
    }

    /**
     * Get an instance of the class that is chosen to solve single-curve problem
     * cases.
     *
     * @return An instance of a class that extends SingleImplementation.
     */
    public static SingleImplementation getSingleImplementation() {
    	return SimulationSettings.getSingleImplementation();
    }

    /**
     * Get an instance of the class that is chosen to solve multiple-curve
     * problem cases.
     *
     * @return An instance of a class that extends MultipleImplementation.
     */
    public static MultipleImplementation getMultipleImplementation() {
    	return SimulationSettings.getMultipleImplementation();
    }

    /**
     * Get an instance of the class that is chosen to solve network problem
     * cases.
     *
     * @return An instance of a class that extends NetworkImplementation.
     */
    public static NetworkImplementation getNetworkImplementation() {
        return SimulationSettings.getNetworkImplementation();
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
    private ArrayList<Node> networkNodes;
    private ArrayList<Segment> segments;

    // Keypress memory
    private boolean addButtonDown;
    private boolean clearKeyDown;
    private boolean runKeyDown;
    private boolean oneKeyDown;
    private boolean twoKeyDown;
    private boolean threeKeyDown;
    private boolean saveKeyDown;
    private boolean openKeyDown;
    private boolean escKeyDown;
    private boolean flipKeyDown;
    private boolean helpKeyDown;
    
    
    // File open
    public Preferences prefs;
    
    // Clear
    private boolean showSegments;
    private boolean brushMode;
    private float eraserRadius;
    
    // Constructor
    public Simulation() {
        addButtonDown = false;
        runKeyDown = false;
        clearKeyDown = false;
        showSegments = true;
        saveKeyDown = false;
        openKeyDown = false;
        oneKeyDown = false;
        twoKeyDown = false;
        threeKeyDown = false;
        brushMode = false;
        escKeyDown = false;
        flipKeyDown = false;
        helpKeyDown = false;
        prefs = Preferences.userRoot().node(this.getClass().getName());
        prefs.put("file", "none");
        eraserRadius = ERASER_RADIUS;
        networkNodes = new ArrayList<>();
        segments = new ArrayList<>();
    }

    public void initialize() {
        // Read the input
        fakeInputReader = getFakeInputReader();
        input = fakeInputReader.readInput();
        problemType = /*input.first();*/ SimulationSettings.getInitialProblemType();
        inputNodes = input.second();

        // Convert to arraylists
        this.nodes = new ArrayList<>();

        for (Node node : inputNodes) {
            this.nodes.add(node);
        }        
    }
   
    public Point getMousePosition() {
        float clickX = (float) Mouse.getX() / Camera.width * 1.0f / Camera.SCALINGFACTOR - Camera.OFFSETFACTOR;
        float clickY = (float) Mouse.getY() / Camera.heigth * 1.0f / Camera.SCALINGFACTOR - Camera.OFFSETFACTOR;
        if (Camera.flipped) {
            clickY = 1f - clickY;
        }
        return new Point(clickX, clickY);
    }

    public void getInput() throws IOException {
        while (Mouse.next()) {
            if (Mouse.getEventButton() > -1) {
                // Add
                if (Mouse.getEventButton() == 0) {
                    addButtonDown = Mouse.getEventButtonState() && !addButtonDown;
                }

                // Erase
                if (Mouse.getEventButton() == 1) {
                    brushMode = Mouse.getEventButtonState();
                }
            }
        }
        
        while (Keyboard.next()) {
            // Run
            if (Keyboard.getEventKey() == Keyboard.KEY_R) {
                runKeyDown = !runKeyDown && Keyboard.getEventKeyState();
            }              
            
            // Clear
            if (Keyboard.getEventKey() == Keyboard.KEY_C) {
                clearKeyDown = !clearKeyDown && Keyboard.getEventKeyState();
            }             
            
            // Flip
            if (Keyboard.getEventKey() == Keyboard.KEY_F) {
                flipKeyDown = !flipKeyDown && Keyboard.getEventKeyState();
            }    
            
            // Help
            if (Keyboard.getEventKey() == Keyboard.KEY_F1) {
                helpKeyDown = !helpKeyDown && Keyboard.getEventKeyState();
            }              
            
            // Save
            if (Keyboard.getEventKey() == Keyboard.KEY_S) {
                saveKeyDown = !saveKeyDown && Keyboard.getEventKeyState();
            } 
            
            // Open
            if (Keyboard.getEventKey() == Keyboard.KEY_O) {
                openKeyDown = !openKeyDown && Keyboard.getEventKeyState();
            }            
            
            // Type
            if (Keyboard.getEventKey() == Keyboard.KEY_1) {
                oneKeyDown = Keyboard.getEventKeyState();
            }
            if (Keyboard.getEventKey() == Keyboard.KEY_2) {
                twoKeyDown = Keyboard.getEventKeyState();
            }
            if (Keyboard.getEventKey() == Keyboard.KEY_3) {
                threeKeyDown = Keyboard.getEventKeyState();
            }            
            
            // Close
            if(Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)){
                escKeyDown = true;
            }
        } 
    }
    
    public KeyboardValue processInput() throws FileNotFoundException{
        if (addButtonDown){
            addNode();
            addButtonDown = false;
        }
        
        if (brushMode) {
            deleteNodes();
        }
        
        if (runKeyDown) {
            calculateSegments();
            showSegments = true;
            runKeyDown = false;
        }
        
        if (clearKeyDown){
            showSegments = false;
            clearKeyDown = false;
        }
        
        if (saveKeyDown){
            save();
            saveKeyDown = false;
        }
        
        if (openKeyDown){
            open();
            openKeyDown = false;
        }        
        
        if(oneKeyDown){
            problemType = ProblemType.SINGLE;
            oneKeyDown = false;
        }
        
        if(twoKeyDown){
            problemType = ProblemType.MULTIPLE;
            twoKeyDown = false;
        }        
        
        if(threeKeyDown){
            problemType = ProblemType.NETWORK;
            threeKeyDown = false;
        }      
        
        // Returning values
        if(escKeyDown){
            return KeyboardValue.CLOSE;
        } 
        
        if(flipKeyDown){
            flipKeyDown = false;
            return KeyboardValue.FLIPSCREEN;
        }
        
        if(helpKeyDown){
            helpKeyDown = false;
            return KeyboardValue.HELP;
        }
        
        return KeyboardValue.CONTINUE;
    }
    
    private void calculateSegments() {
        Node[] allNodes = nodes.toArray(new Node[nodes.size()]);

        if (problemType == ProblemType.SINGLE) {
        	SingleImplementation singleImplementation = getSingleImplementation();
        	Debug.log("Running single implementation...");
        	Debug.log("Name: '" + singleImplementation.getClass().getCanonicalName() + "'");
        	Debug.log("Input size: " + allNodes.length);
        	long startTime = System.nanoTime();
            calculatedSegments = singleImplementation.getOutput(allNodes);
            Debug.log("Time taken (millis): " + (System.nanoTime()-startTime)/1000000);
            System.out.println("-----");
        } else if (problemType == ProblemType.MULTIPLE) {
        	MultipleImplementation multipleImplementation = getMultipleImplementation();
        	Debug.log("Running multiple implementation...");
        	Debug.log("Name: '" + multipleImplementation.getClass().getCanonicalName() + "'");
        	Debug.log("Input size: " + allNodes.length);
        	long startTime = System.nanoTime();
            calculatedSegments = multipleImplementation.getOutput(allNodes);
            Debug.log("Time taken (millis): " + (System.nanoTime()-startTime)/1000000);
            System.out.println("-----");
        } else if (problemType == ProblemType.NETWORK) {
        	NetworkImplementation networkImplementation = getNetworkImplementation();
        	Debug.log("Running network implementation...");
        	Debug.log("Name: '" + networkImplementation.getClass().getCanonicalName() + "'");
        	Debug.log("Input size: " + allNodes.length);
        	long startTime = System.nanoTime();
            Pair<Segment[], Node[]> output = networkImplementation.getOutput(allNodes);
            Debug.log("Time taken (millis): " + (System.nanoTime()-startTime)/1000000);
            System.out.println("-----");
            calculatedSegments = output.first();
            newNetworkNodes = output.second();
            for (Node node : newNetworkNodes) {
                networkNodes.add(node);
            }
        }

        this.segments = new ArrayList<>(calculatedSegments.length);
        for (Segment segment : calculatedSegments) {
            this.segments.add(segment);
        }
    }
    
    private void deleteNodes() {
        if (problemType.equals(ProblemType.NETWORK)) {
            eraserRadius = ERASER_RADIUS_NETWORK;
        } else{
            eraserRadius = ERASER_RADIUS;
        }
        
        float clickX = getMousePosition().getX();
        float clickY = getMousePosition().getY();
        Point mouseNode = new Point(clickX, clickY);

        for (Node node : nodes) {
            if (node.subtract(mouseNode).length() < eraserRadius) {
                // Found a node under the eraser, reconstruct the whole set of nodes.
                ArrayList<Node> tempNodes = new ArrayList<Node>(nodes.size());
                int i = Node.MINIMAL_NODE_ID;
                for (Node n : nodes) {
                    if (n.subtract(mouseNode).length() >= eraserRadius) {
                        tempNodes.add(new Node(i++, n.getX(), n.getY()));
                    }
                }
                nodes = tempNodes;
                break;
            }
        }
    }

    private void addNode() {
        float clickX = getMousePosition().getX();
        float clickY = getMousePosition().getY();
        if (clickX >= 0 && clickX <= 1 && clickY >= 0 && clickY <= 1) {
            boolean exists = false;
            for (Node node : nodes) {
                if (node.getX()==clickX && node.getY()==clickY){
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                ArrayList<Node> tempNodes = new ArrayList<Node>();
                int i = Node.MINIMAL_NODE_ID;
                for (Node node : nodes) {
                    tempNodes.add(new Node(i++, node.getX(), node.getY()));
                }
                tempNodes.add(new Node(i++, clickX, clickY));

                nodes = tempNodes;
            }
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
        JFileChooser saveFile = new JFileChooser(prefs.get("loc", null));
        saveFile.showSaveDialog(null);
        if (saveFile.getSelectedFile() != null) {
            File file = saveFile.getSelectedFile();
            try {
                file.createNewFile();
                buildFile(file);
                prefs.put("loc", file.getParent());
            } catch (IOException ex) {
                Logger.getLogger(Simulation.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void open() throws FileNotFoundException {
        JFileChooser openFile = new JFileChooser(prefs.get("loc", null));
        boolean done = false;
        while (!done) {
            done = true;
            if (openFile.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                File file = openFile.getSelectedFile();
                try {
                    prefs.put("loc", file.getParent());
                    prefs.put("file", file.getName());
                    Scanner scanner = new Scanner(file);
                    String line = scanner.nextLine();
                    ProblemType pType = ProblemType.valueOf(line.substring(12).toUpperCase());
                    line = scanner.nextLine();
                    int numberOfNodes = Integer.parseInt(line.substring(0, line.indexOf(' ')));
                    nodes.clear();
                    for (int i = 0; i < numberOfNodes; i++) {
                        String a = scanner.next();
                        String b = scanner.next();
                        String c = scanner.next();
                        nodes.add(new Node(Integer.parseInt(a),
                                Float.parseFloat(b),
                                Float.parseFloat(c)));
                    }
                    problemType = pType;
                    showSegments = false;
                } catch (Exception e) {
                    done = false;
                }
            }
        }
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
        for (Node node : nodes) {
            drawPoint(NODE_RADIUS, node, true);
        }
        
        if (problemType.equals(ProblemType.NETWORK)) {
            glColor3f(1f, 0f, 0f);
            for (Node node : networkNodes) {
                drawPoint(NODE_RADIUS, node, true);
            }
        }
        
        glColor3f(1f, 0f, 0f);
        if (brushMode){
            drawPoint(eraserRadius, new Point(getMousePosition().getX(), getMousePosition().getY()), false);
        }
    }
    
    private void drawSegment(Segment segment) {
        glBegin(GL_LINES);
        glVertex3f(segment.getX1(), segment.getY1(), 0);
        glVertex3f(segment.getX2(), segment.getY2(), 0);
        glEnd();
    }

    private void drawPoint(float radius, Point n, boolean scale) {
        float ratio = ((float) Camera.width) / Camera.heigth;
        glPushMatrix();
        glTranslatef(n.getX(), n.getY(), 0);
        float pointSize = Math.min(Camera.width, Camera.heigth);
        glScalef(640 / pointSize, 640 / pointSize, 1);
        if(scale){
        if (ratio > 1f) {
            glScalef(1f / ratio, 1f, 1);
        } else if (ratio < 1f) {
            glScalef(1f, 1f / ratio, 1);
        }
        }
        drawCircle(radius, 18);
        glPopMatrix();
    }
    

    // source: http://slabode.exofire.net/circle_draw.shtml
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
