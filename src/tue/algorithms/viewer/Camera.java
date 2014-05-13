package tue.algorithms.viewer;

import org.lwjgl.opengl.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.util.glu.GLU.gluOrtho2D;

public class Camera {
	public static int width;
	public static int heigth;
    public final static float OFFSETFACTOR = 0.025f;
    public final static float SCALINGFACTOR = 0.95f;

	public Camera(int w, int h) {
		width = w;
		heigth = h;
	}

	public void setDimension(int w, int h) {
		width = w;
		heigth = h;
	}

	public void setProjection(int width, int heigth) {
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        gluOrtho2D(0, width, 0, heigth);
        glTranslatef(width*OFFSETFACTOR, heigth*OFFSETFACTOR, 0);
		glScalef(width*SCALINGFACTOR, heigth*SCALINGFACTOR, 1f);
		glMatrixMode(GL_MODELVIEW);
		glLoadIdentity();
	}

	public void setViewport(int width, int heigth) {
		glViewport(0, 0, width, heigth);
	}

	public void updateResolution() {
		setDimension(Display.getWidth(),Display.getHeight());
        setProjection(width, heigth);
        setViewport(width, heigth);
	}

	public void initialize() {
		setProjection(width, heigth);
        setViewport(width, heigth);
        
		glDisable(GL_DEPTH_TEST);
		glEnable(GL_STENCIL_TEST);
		glClearColor(0, 0, 0, 0);
	}

}
