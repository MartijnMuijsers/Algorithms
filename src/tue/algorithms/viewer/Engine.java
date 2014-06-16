package tue.algorithms.viewer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.PixelFormat;
import org.lwjgl.util.Dimension;

public class Engine {
    /*---------------- vars ----------------*/

    private Simulation simulation;
    private Dimension resolution;
    private Camera camera;

    /*---------------- main ----------------*/
    public static void main(String[] args) throws IOException, LWJGLException {
        Engine engine = new Engine();

        // Initialize
        engine.initDisplay();
        engine.initProjection();
        engine.initInput();
        engine.initSimulation();

        // Run simulation
        engine.runSimulation();

        // Close 
        engine.cleanUp();
    }
    /*-------------- methods ---------------*/

    private void initDisplay() {
        resolution = new Dimension(640, 640);
        try {
            Display.setDisplayMode(new DisplayMode(resolution.getWidth(), resolution.getHeight()));
            Display.setTitle("DBL Algorithms");
            Display.setResizable(true);
            Display.create(new PixelFormat(0, 16, 1));
        } catch (LWJGLException e) {
        }
    }
    
    private void initProjection() {
        camera = new Camera(resolution.getWidth(), resolution.getHeight());
        camera.initialize();
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

    private void runSimulation() throws IOException {
        while (!Display.isCloseRequested()) {
            getInput();
            if (processInput()) {
                break; //if ESC pressed close simulation
            }
            setTitle();
            resize();
            render();
        }
    }
    
    private void cleanUp() {
        Display.destroy();
        Keyboard.destroy();
        Mouse.destroy();
    }

    private void resize() {
        if (Display.wasResized()) {
            camera.updateResolution();
        }
    }

    private void getInput() throws IOException {
        simulation.getInput();
    }

    private boolean processInput() throws FileNotFoundException {
        switch (simulation.processInput()) {
            case CLOSE:
                return true;
            case FLIPSCREEN:
                camera.flip();
                break;
            case HELP:
                showHelpDialog();
                break;
        }
        return false;
    }

    private void showHelpDialog() {
        JOptionPane.showMessageDialog(null,
                "Available commandos: \n"
                + "1: Set problem type to SINGLE \n"
                + "2: Set problem type to MULTIPLE \n"
                + "3: Set problem type to NETWORK \n\n"
                + "R: Run simulation \n"
                + "C: Clear the segments \n"
                + "F: Flip the screen \n"
                + "O: Open input file \n"
                + "S: Save input file \n\n"
                + "Left mouse button: Add node \n"
                + "Right mouse button: Delete nodes \n\n"
                + "ESC: Close Simulation \n"
                + "RETURN: Close this dialog",
                "Commandos",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void setTitle() {
        String title = String.format("file: %s type: %s mouse: (%.5f, %.5f)  [press F1 for help]",
                simulation.prefs.get("file", ""),
                simulation.problemType.name(),
                simulation.getMousePosition().getX(),
                simulation.getMousePosition().getY());
        Display.setTitle(title);
    }

    private void render() {
        simulation.render();
        Display.update();
        Display.sync(60);
    }

}
