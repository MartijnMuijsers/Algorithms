package tue.algorithms.viewer;

import org.lwjgl.opengl.Display;

public class Timer {
	private int frames;
	private long lastTime;
	private long totalTime;
	private long now;
	private long passed;

	public Timer() {
		frames = 0;
		lastTime = System.nanoTime();
		totalTime = 0;
	}

	public void calculateFPS() {
		now = System.nanoTime();
		passed = now - lastTime;
		lastTime = now;
		totalTime += passed;

		if (totalTime >= 1000000000) {
			Display.setTitle("DBL Algorithms" + " FPS: " + frames);
			frames = 0;
			totalTime = 0;
		}

		frames++;
	}

}
