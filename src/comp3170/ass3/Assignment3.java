package comp3170.ass3;

import comp3170.IWindowListener;
import comp3170.OpenGLException;
import comp3170.Window;

/**
 * COMP3170 Assignment 3 - 3D desert car simulator
 * Created by Cadigal (47100192) and Tanish (?)
 *
 * Features to implement
 * 1. Scene - Car, ground & trees
 * 2. Car movement
 * 3. Lighting - Sun (direct), road & trees (diffuse & ambient), car (diffuse, ambient & specular)
 * 4. Cameras - top down orthographic; 3rd person - follow, zoom, dolly, angle/pitch; window resizing (no distortion)
 * 5. Debug modes (normals, wireframe)
 * 6. Night mode (headlights point light & skybox)
 * 7. Heat shimmer (screen space effect)
 *
 * Functional features to implement:
 * 1. Anti-aliasing using 4x multisampling.
 * 2. Backface culling
 * 3. Mipmaps for all textures (with trilinear filtering)
 * 4. Gamma correction (with a default gamma of 2.2)
 *
 * A high-quality submission is:
 * 1. Correctly implemented, making appropriate use of the OpenGL and JOML libraries in Java, and GLSL functions in shaders.
 * 2. Clearly implemented, with code that well structured and easy to follow.
 * 3. Clearly documented, with appropriate use of diagrams and equations to convey the geometrical and mathematical details of the code.
 *
 * Rubric:
 * 1. Correctness:
 * Code relevant to feature is free from any apparent errors.
 * Problems are solved in a suitable fashion.
 * Contains no irrelevant code.
 * 2. Clarity:
 * Good consistent style.
 * Well structured & commented code relevant to feature.
 * Appropriate division into classes and methods, to make implementation clear.
 * 3. Documentation (markdown document):
 * Illustrations are neat, clear and well annotated.
 * Relevant equations are provided and clearly annotated.
 * No discrepancies between explanation and code (except as noted).
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

	@Override
	public void init() {
		// Initialise OpenGL and create the scene
		// run once when the window is created to initialise everything
	}

	@Override
	public void draw() {
		// Redraw the scene.
		// run every frame, to update & render the scene
	}

	@Override
	public void resize(int width, int height) {
		// The window has been resized.
		// This is always called between init() and the first call to draw()
		// run when the window is resized (e.g. to resize the camera aspect)
	}

	@Override
	public void close() {
		// We generally don't do anything here
		// run when the window is closed (we don't tend to use this)
	}

}
