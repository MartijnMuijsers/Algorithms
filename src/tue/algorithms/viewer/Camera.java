package tue.algorithms.viewer;

import org.lwjgl.opengl.*;
import static org.lwjgl.opengl.GL11.*;

public class Camera {
	private int width;
	private int heigth;

	public Camera(int w, int h) {
		this.width = w;
		this.heigth = h;
	}

	public void setDimension(int width, int heigth) {
		this.width = width;
		this.heigth = heigth;
	}

	public void setProjection(int width, int heigth) {
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glOrtho(0, width, heigth, 0, 1, -1);
		glTranslatef(width*0.025f, heigth*0.025f, 0);
		glScalef(width*0.95f, heigth*0.95f, 1f);
		glMatrixMode(GL_MODELVIEW);
		glLoadIdentity();
	}

	public void setViewport(int width, int heigth) {
		glViewport(0, 0, width, heigth);
	}

	public void updateResolution() {
		setDimension(Display.getWidth(),Display.getHeight());
		setViewport(width, heigth);
		setProjection(width, heigth);
	}

	public void initialize() {
		setViewport(width, heigth);
		setProjection(width, heigth);

		glDisable(GL_DEPTH_TEST);
		glEnable(GL_STENCIL_TEST);
		glClearColor(0, 0, 0, 0);

	}

}
