package comp3170.ass3;

import comp3170.IWindowListener;
import comp3170.OpenGLException;
import comp3170.Window;

/**
 * Template main class for Assignment 3
 * 
 * This class implements the IWindowListener interface which includes the event handling methods:
 * 
 * - init() - run once when the window is created to initialise everything
 * - draw() - run every frame, to update & render the scene
 * - resize() - run when the window is resized (e.g. to resize the camera aspect)
 * - close() - run when the window is closed (we don't tend to use this)
 * 
 */

public class Assignment3 implements IWindowListener {

	private Window window;
	private int screenWidth = 1000;
	private int screenHeight = 1000;

	public Assignment3() throws OpenGLException {
		window = new Window("Assignment 3", screenWidth, screenHeight, this);
		window.run();
	}

	public static void main(String[] args) throws OpenGLException {
		new Assignment3();
	}

	/**
	 * Initialise OpenGL and create the scene
	 */
	@Override
	public void init() {
		
	}

	/**
	 * Redraw the scene.
	 */

	@Override
	public void draw() {

	}

	/**
	 * The window has been resized.
	 * This is always called between init() and the first call to draw()
	 */
	
	@Override
	public void resize(int width, int height) {

	}

	@Override
	public void close() {
		// We generally don't do anything here
	}

}
