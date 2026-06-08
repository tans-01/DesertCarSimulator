package comp3170.ass3.sceneobjects;

import comp3170.*;
import comp3170.ass3.Light;
import comp3170.ass3.TextureUtils;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.io.IOException;
import java.lang.Math;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.glBindBuffer;

public class Road extends SceneObject {

	private static final String VERT_SHADER = "simple.vert";
	private static final String FRAG_SHADER = "simple.frag";
	private static final String TEXTURE = "road.jpg";
	private static final Vector4f[] CONTROL_POINTS = {
		new Vector4f(-25, 0, -50, 1),
		new Vector4f(-25, 0, -25, 1),
		new Vector4f(25, 0, 0, 1),
		new Vector4f(25, 0, 50, 1),
	};
	private static final float WIDTH = 8;
	private static final float HEIGHT = 0.1F;
	private static final float SLOPE = (float) Math.toRadians(45);

	final Shader shader;
	final int vertexBuffer;
	final int normalBuffer;
	final int indexBuffer;
	final int indexCount;
	final int uvBuffer;
	final int texture;

	public Road() throws IOException, OpenGLException {
		shader = ShaderLibrary.instance.compileShader(VERT_SHADER, FRAG_SHADER);
		texture = TextureLibrary.instance.loadTexture(TEXTURE);
		TextureUtils.setupTexture(texture);


		var segmentCount = CONTROL_POINTS.length - 1;
		var vertices = new Vector4f[segmentCount * 4];
		var normals = new Vector4f[vertices.length];
		var uvs = new Vector2f[vertices.length];
		var indices = new int[vertices.length];
		var h = WIDTH / 2;
		for (var i = 0; i < segmentCount; i++) {
			var c1 = CONTROL_POINTS[i];
			var c2 = CONTROL_POINTS[i + 1];
			var j = i * 4;
			var start =
			vertices[j + 0] = new Vector4f(c1.x - h, c1.y + HEIGHT, c1.z, 1);
			vertices[j + 1] = new Vector4f(c1.x + h, c1.y + HEIGHT, c1.z, 1);
			vertices[j + 2] = new Vector4f(c2.x - h, c2.y + HEIGHT, c2.z, 1);
			vertices[j + 3] = new Vector4f(c2.x + h, c2.y + HEIGHT, c2.z, 1);
			normals[j + 0] = new Vector4f(0, 1, 0, 1);
			normals[j + 1] = new Vector4f(0, 1, 0, 1);
			normals[j + 2] = new Vector4f(0, 1, 0, 1);
			normals[j + 3] = new Vector4f(0, 1, 0, 1);
			uvs[j + 0] = new Vector2f(0, 0);
			uvs[j + 1] = new Vector2f(h, 0);
			uvs[j + 2] =  new Vector2f(h, h);
			uvs[j + 3] = new Vector2f(0, h);
		}
		for (var i = 0; i < vertices.length; i++) {
			indices[i] = i;
		}

		vertexBuffer = GLBuffers.createBuffer(vertices);
		normalBuffer = GLBuffers.createBuffer(normals);
		uvBuffer = GLBuffers.createBuffer(uvs);
		indexBuffer = GLBuffers.createIndexBuffer(indices);
		indexCount = indices.length;
	}

	@Override
	protected void drawSelf(Matrix4f mvpMatrix) {
		shader.enable();
		shader.setUniform("u_mvpMatrix", mvpMatrix);
		shader.setUniform("u_modelMatrix", getModelToWorldMatrix(new Matrix4f()));
		shader.setUniform("u_debugNormals", Scene.theScene.debugNormals);

		shader.setUniform("u_daytime", Scene.theScene.daytime);
		shader.setUniform("u_headlightposition", Scene.theScene.getHeadlightPosition());
		shader.setUniform("u_spotdirection", Scene.theScene.getHeadlightDirection());

		//light
		Light light = Scene.theScene.light;
		shader.setUniform("u_lightDirection", light.getDirection());
		shader.setUniform("u_lightColour", light.getColour());
		shader.setUniform("u_ambientColour", light.getAmbient());

		//spec
		Vector3f camPos = Scene.theScene.getCamera().getPosition(new Vector3f());
		shader.setUniform("u_cameraposition", camPos);
		shader.setUniform("u_shiny", false);

		// bind the texture to texture unit 0
		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D, texture);
		shader.setUniform("u_texture", 0);
		shader.setUniform("u_alpha", 1.0f);

		shader.setAttribute("a_position", vertexBuffer);
		shader.setAttribute("a_normal", normalBuffer);
		shader.setAttribute("a_uv", uvBuffer);

		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexBuffer);
		glDrawElements(GL_TRIANGLES, indexCount, GL_UNSIGNED_INT, 0);
	}
}