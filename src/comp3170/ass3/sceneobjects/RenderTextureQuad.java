package comp3170.ass3.sceneobjects;

import comp3170.*;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL30.*;

public class RenderTextureQuad extends SceneObject {

	final private String VERTEX_SHADER = "renderTextureVertex.glsl";
	final private String FRAGMENT_SHADER = "renderTextureFragment.glsl";

	private Shader shader;

	private Vector4f[] vertices;
	private int vertexBuffer;
	private Vector2f[] uvs;
	private int uvBuffer;

	private int renderTexture;
	private int frameBuffer;
	private float time;

	public RenderTextureQuad(int width, int height) {
		this.shader = ShaderLibrary.instance.compileShader(VERTEX_SHADER, FRAGMENT_SHADER);
		createQuad();
		// NB: This doesn't work with MSAA
		// Making it work with MSAA requires changing/duplicating createRenderTexture & createFrameBuffer to use GL_TEXTURE_2D_MULTISAMPLE instead of GL_TEXTURE_2D
		renderTexture = TextureLibrary.instance.createRenderTexture(width, height, GL_RGBA);
		try {
			frameBuffer = GLBuffers.createFrameBuffer(renderTexture);
		} catch (OpenGLException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	public int getRenderTexture() {
		return renderTexture;
	}

	public int getFrameBuffer() {
		return frameBuffer;
	}

	private void createQuad() {

		// Create a quad that fills the sceen in NDC space

		// 2---3
		// |\ | y
		// | * | |
		// | \| +--x
		// 0---1 /
		// z (out)

		// @formatter: off
		vertices = new Vector4f[] {
			new Vector4f(-1, -1, 0, 1), // 0
			new Vector4f(1, -1, 0, 1), // 1
			new Vector4f(-1, 1, 0, 1), // 2

			new Vector4f(1, 1, 0, 1), // 3
			new Vector4f(-1, 1, 0, 1), // 2
			new Vector4f(1, -1, 0, 1), // 1
		};

		vertexBuffer = GLBuffers.createBuffer(vertices);

		uvs = new Vector2f[] {
			new Vector2f(0, 0),
			new Vector2f(1, 0),
			new Vector2f(0, 1),

			new Vector2f(1, 1),
			new Vector2f(0, 1),
			new Vector2f(1, 0),
		};

		uvBuffer = GLBuffers.createBuffer(uvs);
		// @formatter: on
	}

	@Override
	public void drawSelf(Matrix4f mvpMatrix) {
		shader.enable();

		// no MVP matrix, as the quad is draw directly in NDC space

		// vertex attributes
		shader.setAttribute("a_position", vertexBuffer);
		shader.setAttribute("a_texcoord", uvBuffer);

		// textures
		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D, renderTexture);
		shader.setUniform("u_texture", 0);
		shader.setUniform("u_time", time);

		glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
		glDrawArrays(GL_TRIANGLES, 0, vertices.length);
	}

	public void update(float deltaTime) {
		this.time += deltaTime;
	}
}
