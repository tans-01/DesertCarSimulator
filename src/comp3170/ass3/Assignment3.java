package comp3170.ass3;
import comp3170.ShaderLibrary;
import comp3170.IWindowListener;
import comp3170.OpenGLException;
import comp3170.Window;
import comp3170.ass3.sceneobjects.Desert;
import static org.lwjgl.opengl.GL11.*;

import java.io.IOException;

import org.joml.Matrix4f;
import java.io.File;
import comp3170.TextureLibrary;
/**
 * COMP3170 Assignment 3 - 3D desert car simulator
 * Created by Cadigal (47100192) and Tanish (47896345)
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

	 private Desert desert;

	    // Camera
	    private Matrix4f viewMatrix = new Matrix4f();
	    private Matrix4f projMatrix = new Matrix4f();
	    private Matrix4f mvpMatrix = new Matrix4f();

	
	public Assignment3() throws OpenGLException {
		window = new Window("Assignment 3", screenWidth, screenHeight, this);
		window.run();
	}

	public static void main(String[] args) throws OpenGLException {
		new Assignment3();
	}

	@Override
	public void init() {
		new TextureLibrary(new File("src/comp3170/ass3/textures")); //finding texture library for sand
		new ShaderLibrary(new File("src/comp3170/ass3/shaders"));   //finding shaders for objects

		 try {
	            desert = new Desert();
	        } catch (IOException | OpenGLException e) {
	            e.printStackTrace();
	        }

	        // Enable depth testing so closer objects appear in front
	        glEnable(GL_DEPTH_TEST);
	    }
	

	@Override
	public void draw() {
		// Clear screen
        glClearColor(0.5f, 0.8f, 1.0f, 1.0f); // light blue sky
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        // Simple camera looking down at the desert from above and to the side
        viewMatrix.setLookAt(
            0, 50, 80,   // camera position (x, y, z)
            0, 0, 0,     // look at origin
            0, 1, 0      // up direction
        );

        projMatrix.setPerspective(
            (float) Math.toRadians(60),          // field of view
            (float) screenWidth / screenHeight,  // aspect ratio
            0.1f,                                // near plane
            500f                                 // far plane
        );

        // MVP = projection * view * model
        // Desert has no model transform so model = identity
        mvpMatrix.set(projMatrix).mul(viewMatrix);

        desert.draw(mvpMatrix);
	}

	@Override
	public void resize(int width, int height) {
		 screenWidth = width;
	        screenHeight = height;
	}

	@Override
	public void close() {
		// We generally don't do anything here
		// run when the window is closed (we don't tend to use this)
	}

}
