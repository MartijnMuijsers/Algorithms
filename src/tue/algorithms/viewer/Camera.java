package tue.algorithms.viewer;

import org.lwjgl.opengl.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_MULTISAMPLE;

public class Camera {

    public static int width;
    public static int heigth;
    public static boolean flipped;
    public final static float SCALINGFACTOR = 0.95f;
    public final static float OFFSETFACTOR = 0.025f;

    public Camera(int w, int h) {
        width = w;
        heigth = h;
        flipped = false;
    }

    public void setDimension(int w, int h) {
        width = w;
        heigth = h;
    }

    public void setProjection(int width, int heigth) {
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(0, width, heigth, 0, 1, -1);
        glTranslatef(width * OFFSETFACTOR, heigth * OFFSETFACTOR, 0);
        glScalef(width * SCALINGFACTOR, heigth * SCALINGFACTOR * -1, 1f);
        glTranslatef(0, -1, 0);
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
    }

    public void setViewport(int width, int heigth) {
        glViewport(0, 0, width, heigth);
    }

    public void updateResolution() {
        setDimension(Display.getWidth(), Display.getHeight());
        setViewport(width, heigth);
        setProjection(width, heigth);
    }

    public void initialize() {
        setViewport(width, heigth);
        setProjection(width, heigth);

        glDisable(GL_DEPTH_TEST);
        glEnable(GL_STENCIL_TEST);
        glEnable(GL_BLEND);
        glEnable(GL_POLYGON_SMOOTH);
        glEnable(GL_MULTISAMPLE);
        glClearColor(0, 0, 0, 0);
    }

    public void flip(){
        glTranslatef(0.5f, 0.5f, 0);
        glScalef(1, -1, 1);
        glTranslatef(-0.5f, -0.5f, 0);
        flipped = !flipped;
    }
}
