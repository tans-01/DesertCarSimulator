package comp3170.ass3.sceneobjects;

import comp3170.*;
import comp3170.ass3.cameras.PerspectiveCamera;
import org.joml.Matrix4f;
import org.joml.Vector4f;

import java.io.File;
import java.io.IOException;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.glBindBuffer;

public class Skybox extends SceneObject {
	
	static final private String VERTEX_SHADER = "skybox_vertex.glsl";
	static final private String FRAGMENT_SHADER = "skybox_fragment.glsl";
	
	private static final String[] DAY_TEXTURE_FILES = new String[] {
		"jettelly_sunshine_LEFT.png",
		"jettelly_sunshine_RIGHT.png",
		"jettelly_sunshine_UP.png",
		"jettelly_sunshine_DOWN.png",
		"jettelly_sunshine_FRONT.png",
		"jettelly_sunshine_BACK.png",
	};

	private static final String[] NIGHT_TEXTURE_FILES = new String[] {
		"jettelly_no_moon_LEFT.png",
		"jettelly_no_moon_RIGHT.png",
		"jettelly_no_moon_UP.png",
		"jettelly_no_moon_DOWN.png",
		"jettelly_no_moon_FRONT.png",
		"jettelly_no_moon_BACK.png",
	};

	private Shader shader;
	private Vector4f[] vertices;
	private int vertexBuffer;
	private int[] indices;
	private int indexBuffer;
	private int dayTexture;
	private int nightTexture;

	
	public Skybox() {
		shader = ShaderLibrary.instance.compileShader(VERTEX_SHADER, FRAGMENT_SHADER);
		shader.setStrict(false);
		
		createCube();
		loadTextures();
	}
	
	private void createCube() {
		//          6-----7
		//         /|    /|
		//        / |   / |
		//       1-----0  |     y    RHS coords
		//       |  |  |  |     | 
		//       |  5--|--4     +--x
		//       | /   | /     /
		//       |/    |/     z
		//       2-----3
		
		vertices = new Vector4f[] {
			new Vector4f( 1, 1, 1, 1),
			new Vector4f(-1, 1, 1, 1),
			new Vector4f(-1,-1, 1, 1),
			new Vector4f( 1,-1, 1, 1),
			new Vector4f( 1,-1,-1, 1),
			new Vector4f(-1,-1,-1, 1),
			new Vector4f(-1, 1,-1, 1),
			new Vector4f( 1, 1,-1, 1),
		};
		
		vertexBuffer = GLBuffers.createBuffer(vertices);

		// indices for the lines forming each face
		// faces are facing inwards, as we will be viewing the
		// cube from the inside

		indices = new int[] {
			// front
			2, 1, 0,
			0, 3, 2,
			
			// back
			6, 5, 4,
			4, 7, 6,
			
			// top
			6, 7, 0,
			0, 1, 6,
			
			// bottom 
			4, 5, 2,
			2, 3, 4,
			
			// left
			6, 1, 2,
			2, 5, 6,
			
			// right
			3, 0, 7,
			7, 4, 3,			
		};
		
		indexBuffer = GLBuffers.createIndexBuffer(indices);
	}

	private void loadTextures() {
		try {
			dayTexture = TextureLibrary.instance.loadCubemap(DAY_TEXTURE_FILES);
			nightTexture = TextureLibrary.instance.loadCubemap(NIGHT_TEXTURE_FILES);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (OpenGLException e) {
			e.printStackTrace();
			System.exit(1);
		}		
	}

	final Matrix4f cameraMatrix = new Matrix4f();
	final Matrix4f viewMatrix = new Matrix4f();
	final Matrix4f projectionMatrix = new Matrix4f();
	final Matrix4f mvpMatrix = new Matrix4f();
	final Vector4f origin = new Vector4f(0,0,0,1);

	@Override
	protected void drawSelf(Matrix4f mvpMatrixIgnored) {
		if (!(Scene.theScene.getCamera() instanceof PerspectiveCamera)) {
			// No skybox in map view
			return;
		}
		// draw the skybox without view translation,
		// so it is always centred on the perspective camera
		Scene.theScene.getCamera().getViewMatrix(viewMatrix);
		viewMatrix.setTranslation(0, 0, 0);
		Scene.theScene.getCamera().getProjectionMatrix(projectionMatrix);
		projectionMatrix.mul(viewMatrix, mvpMatrix);

		shader.enable();
		shader.setAttribute("a_position", vertexBuffer);
		shader.setUniform("u_mvpMatrix", mvpMatrix);

		glDepthMask(false);

		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_CUBE_MAP, Scene.theScene.daytime ? dayTexture : nightTexture);
		shader.setUniform("u_cubemap", 0);

		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexBuffer);
		glDrawElements(GL_TRIANGLES, indices.length, GL_UNSIGNED_INT, 0);

		glDepthMask(true);
	}
}
