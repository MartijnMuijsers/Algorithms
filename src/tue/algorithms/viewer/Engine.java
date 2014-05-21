package tue.algorithms.viewer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.*;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.util.Dimension;

public class Engine {
    /*---------------- vars ----------------*/

    private Simulation simulation;
    private Dimension resolution;
    public static Camera camera;

    /*---------------- main ----------------*/
    public static void main(String[] args) throws IOException, LWJGLException {
        Engine engine = new Engine();

        // Initialize
        engine.resolution = new Dimension(640, 640);
        engine.initDisplay();
        engine.initProjection();
        engine.initInput();
        engine.initSimulation();

        // Run simulation
        engine.doSimulation();

        // Close 
        engine.cleanUp();
    }
    /*-------------- methods ---------------*/

    private void initProjection() {
        camera = new Camera(resolution.getWidth(), resolution.getHeight());
        camera.initialize();
    }

    private void initDisplay() {
        try {
            Display.setDisplayMode(new DisplayMode(resolution.getWidth(), resolution.getHeight()));
            Display.setTitle("DBL Algorithms");
            Display.setResizable(true);
            Display.create(new PixelFormat(0, 16, 1));
        } catch (LWJGLException e) {
        }
    }

    private void initInput() {
        try {
            Keyboard.create();
            Mouse.create();
        } catch (LWJGLException e) {
            Logger.getLogger(Engine.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    private void initSimulation() {
        simulation = new Simulation();
        simulation.initialize();
    }

    private void doSimulation() throws IOException {
        while (!Display.isCloseRequested()) {
            if (Display.wasResized()) {
                camera.updateResolution();
            }
            if (getInput()) {
                break; // ESC is pressed
            }
            processInput();
            String title = "";
            title += " problemType: " + simulation.problemType.name();
            title += "  [R = run | C = clear | S = save | O = open | (1, 2, 3) = type] ";
            Display.setTitle(title);
            render();
        }
    }

    private boolean getInput() throws IOException {
        return simulation.getInput();
    }
    
    private void processInput() throws FileNotFoundException{
        simulation.processInput();
    }

    private void render() {
        simulation.render();
        Display.update();
        Display.sync(60);
    }

    private void cleanUp() {
        Display.destroy();
        Keyboard.destroy();
        Mouse.destroy();
    }

}
