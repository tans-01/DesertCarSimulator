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


		
		
		
		var h = WIDTH / 2;
		
		int SMOOTH = 20;
		int pointcount = SMOOTH + 1;
		var segmentCount = SMOOTH;
		var vertices = new Vector4f[segmentCount * 4];
		var normals = new Vector4f[vertices.length];
		var uvs = new Vector2f[vertices.length];
		
		
		Vector3f[] curvy = new Vector3f[pointcount];
		for (int i = 0; i < pointcount; i++) {
		    float t = (float) i / SMOOTH;
		    curvy[i] = bezier(t);
		}

		// compute a perpendicular AT EACH POINT (so edges line up)
		Vector3f[] perp = new Vector3f[pointcount];
		for (int i = 0; i < pointcount; i++) {
		    Vector3f prev = curvy[Math.max(i - 1, 0)];
		    Vector3f next = curvy[Math.min(i + 1, pointcount - 1)];
		    Vector3f dir = new Vector3f(next.x - prev.x, 0, next.z - prev.z).normalize();
		    perp[i] = new Vector3f(-dir.z, 0, dir.x).mul(h);
		}

		// distance for tiling
		float[] dist = new float[pointcount];
		dist[0] = 0;
		for (int i = 1; i < pointcount; i++) {
		    dist[i] = dist[i - 1] + curvy[i].distance(curvy[i - 1]);
		}
		float TILE = 0.1f;

		// each point uses ITS OWN perp, so shared edges match
		for (int i = 0; i < segmentCount; i++) {
		    Vector3f c1 = curvy[i];
		    Vector3f c2 = curvy[i + 1];
		    Vector3f p1 = perp[i];       // perpendicular at start point
		    Vector3f p2 = perp[i + 1];   // perpendicular at end point
		    int j = i * 4;

		    vertices[j + 0] = new Vector4f(c1.x - p1.x, c1.y + HEIGHT, c1.z - p1.z, 1);
		    vertices[j + 1] = new Vector4f(c1.x + p1.x, c1.y + HEIGHT, c1.z + p1.z, 1);
		    vertices[j + 2] = new Vector4f(c2.x - p2.x, c2.y + HEIGHT, c2.z - p2.z, 1);
		    vertices[j + 3] = new Vector4f(c2.x + p2.x, c2.y + HEIGHT, c2.z + p2.z, 1);

		    normals[j + 0] = new Vector4f(0, 1, 0, 0);
		    normals[j + 1] = new Vector4f(0, 1, 0, 0);
		    normals[j + 2] = new Vector4f(0, 1, 0, 0);
		    normals[j + 3] = new Vector4f(0, 1, 0, 0);

		    float v1 = dist[i] * TILE;
		    float v2 = dist[i + 1] * TILE;
		    uvs[j + 0] = new Vector2f(v1, 0);   
		    uvs[j + 1] = new Vector2f(v1, 1);
		    uvs[j + 2] = new Vector2f(v2, 0);
		    uvs[j + 3] = new Vector2f(v2, 1);
		}
		var indicess = new int[segmentCount * 6];
		for (var i = 0; i < segmentCount; i++) {
		    var j = i * 4;
		    var k = i * 6;
		    indicess[k + 0] = j + 0;
		    indicess[k + 1] = j + 1;
		    indicess[k + 2] = j + 2;
		    indicess[k + 3] = j + 1;
		    indicess[k + 4] = j + 3;
		    indicess[k + 5] = j + 2;
		}

		vertexBuffer = GLBuffers.createBuffer(vertices);
		normalBuffer = GLBuffers.createBuffer(normals);
		uvBuffer = GLBuffers.createBuffer(uvs);
		indexBuffer = GLBuffers.createIndexBuffer(indicess);
		indexCount = indicess.length;
	}
	//bezier formula B(t) = (1-t)³·P0 + 3(1-t)²·t·P1 + 3(1-t)·t²·P2 + t³·P3
	private Vector3f bezier(float t) {
	    float u = 1 - t;
	    float b0 = u * u * u;          // (1-t)^3
	    float b1 = 3 * u * u * t;      // 3(1-t)^2 t
	    float b2 = 3 * u * t * t;      // 3(1-t) t^2
	    float b3 = t * t * t;          // t^3

	    Vector3f p = new Vector3f();
	    p.x = b0*CONTROL_POINTS[0].x + b1*CONTROL_POINTS[1].x + b2*CONTROL_POINTS[2].x + b3*CONTROL_POINTS[3].x;
	    p.y = b0*CONTROL_POINTS[0].y + b1*CONTROL_POINTS[1].y + b2*CONTROL_POINTS[2].y + b3*CONTROL_POINTS[3].y;
	    p.z = b0*CONTROL_POINTS[0].z + b1*CONTROL_POINTS[1].z + b2*CONTROL_POINTS[2].z + b3*CONTROL_POINTS[3].z;
	    return p;
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