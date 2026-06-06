package comp3170.ass3.sceneobjects;

import comp3170.*;
import comp3170.ass3.TextureUtils;
import comp3170.ass3.models.ObjData;
import org.joml.Matrix4f;

import java.io.IOException;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL31.glDrawElementsInstanced;
import static org.lwjgl.opengl.GL33.glVertexAttribDivisor;

/**
 * Instances of Trees
 */
public class Tree extends SceneObject {
	private static final String VERT_SHADER = "instanced_vertex.glsl";
	private static final String FRAG_SHADER = "instanced_fragment.glsl";
	private static final String TEXTURE = "tree.png";
	private static final String OBJ_FILE = "src/comp3170/ass3/models/tree.obj";

	final Shader shader;
	final int texture;
	final int vertexBuffer;
	final int normalBuffer;
	final int uvBuffer;
	final int indexBuffer;
	final int indexCount;
	final Matrix4f[] matrices;
	final int matrixBuffer;

	public Tree(Matrix4f[] matrices) throws OpenGLException, IOException {
		shader = ShaderLibrary.instance.compileShader(VERT_SHADER, FRAG_SHADER);
		texture = TextureLibrary.instance.loadTexture(TEXTURE);
		TextureUtils.setupTexture(texture);

		var data = new ObjData(OBJ_FILE);

		var mesh = data.getMesh("Tree");

		// Create the buffers
		vertexBuffer = GLBuffers.createBuffer(mesh.vertices);
		normalBuffer = GLBuffers.createBuffer(mesh.normals);
		uvBuffer = GLBuffers.createBuffer(mesh.uvs);
		indexBuffer = GLBuffers.createIndexBuffer(mesh.indices);
		indexCount = mesh.indices.length;
		this.matrices = matrices;
		matrixBuffer = GLBuffers.createBuffer(matrices);
	}

	protected void drawSelf(Matrix4f mvpMatrix) {
		shader.enable();
		shader.setUniform("u_mvpMatrix", mvpMatrix);

		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D, texture);
		shader.setUniform("u_texture", 0);
		shader.setUniform("u_alpha", 1.0f);

		shader.setAttribute("a_position", vertexBuffer);
		shader.setAttribute("a_normal", normalBuffer);
		shader.setAttribute("a_uv", uvBuffer);

		var matricesLoc = shader.getAttribute("a_modelMatrix");
		// Matrix4f[] is passed to the GPU as 4x Vector4f[]s
		glBindBuffer(GL_ARRAY_BUFFER, matrixBuffer);
		for (int i = 0; i < 4; i++) {
			glEnableVertexAttribArray(matricesLoc + i);
			glVertexAttribPointer(matricesLoc + i, 4, GL_FLOAT, false, 16 * Float.BYTES, (long) i * 4 * Float.BYTES);
			glVertexAttribDivisor(matricesLoc + i, 1);
		}

		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexBuffer);
		try {
			glDrawElementsInstanced(GL_TRIANGLES, indexCount, GL_UNSIGNED_INT, 0, matrices.length);
		} finally {
			for (var i = 0; i < 4; i++) {
				glVertexAttribDivisor(matricesLoc + i, 0);
			}
		}
	}

}
