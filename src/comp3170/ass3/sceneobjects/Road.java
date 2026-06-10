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

		int segmentCount = 20;
		int curvePointCount = segmentCount + 1;
		var verticesPerQuad = 6;
		var uniqueVerticesPerQuad = 4;
		// 1 quad for road, 1 quad for each side slope (2)
		var quadCount = 3;
		var uniqueVerticesPerSegment = quadCount * uniqueVerticesPerQuad;
		var vertices = new Vector4f[segmentCount * uniqueVerticesPerSegment];
		var uvs = new Vector2f[vertices.length];

		Vector3f[] curvy = new Vector3f[curvePointCount];
		for (int i = 0; i < curvePointCount; i++) {
			float t = (float) i / segmentCount;
			Vector3f p = new Vector3f();
			curvy[i] = bezier(t, p);
		}

		// compute a perpendicular AT EACH POINT (so edges line up)
		Vector3f[] curvePointPerpendicularNormals = new Vector3f[curvePointCount];
		for (int i = 0; i < curvePointCount; i++) {
			Vector3f prev = curvy[Math.max(i - 1, 0)];
			Vector3f next = curvy[Math.min(i + 1, curvePointCount - 1)];
			Vector3f dir = new Vector3f(next.x - prev.x, next.y - prev.y, next.z - prev.z).normalize();
			curvePointPerpendicularNormals[i] = dir.set(-dir.z, dir.y, dir.x);
		}

		// distance for tiling
		float[] dist = new float[curvePointCount];
		dist[0] = 0;
		for (int i = 1; i < curvePointCount; i++) {
			dist[i] = dist[i - 1] + curvy[i].distance(curvy[i - 1]);
		}
		// How many times it tiles inside a single quad, 1/10 looks good
		float tile = 0.1f;

		var roadWidth = WIDTH;
		// Horizontal extent of the angled slope
		// By having the sides extend out by HEIGHT, both edges of the side angle are HEIGHT and we get a 45 degree angle
		var slopeWidth = HEIGHT;
		// Length of the slope of the slope (actual distance, used for texturing)
		var slopeHypotenuseLength = slopeWidth * (float) Math.sqrt(2);
		// Full width is the width including the slope sections
		var totalWidth = roadWidth + slopeWidth * 2F;
		var halfRoadWidth = roadWidth / 2F;
		var halfTotalWidth = totalWidth / 2F;

		// UV proportions
		// fraction of texture the slope occupies
		var slopeProportion = slopeHypotenuseLength / totalWidth;
		var roadTexStart = 0F + slopeProportion;
		var roadTexEnd = 001F - slopeProportion;

		// each point uses ITS OWN perpendicular, so shared edges match
		for (int segmentIndex = 0; segmentIndex < segmentCount; segmentIndex++) {
			Vector3f c1 = curvy[segmentIndex];
			Vector3f c2 = curvy[segmentIndex + 1];
			Vector3f p1 = curvePointPerpendicularNormals[segmentIndex];       // perpendicular at start point
			Vector3f p2 = curvePointPerpendicularNormals[segmentIndex + 1];   // perpendicular at end point
			int j = segmentIndex * uniqueVerticesPerSegment;

			float texV1 = dist[segmentIndex] * tile;
			float texV2 = dist[segmentIndex + 1] * tile;

			// Road quad
			{
				var quadIndex = 0;
				var indexOfFirstVertex = j + quadIndex * uniqueVerticesPerQuad;

				vertices[indexOfFirstVertex + 0] = new Vector4f(c1.x - p1.x * halfRoadWidth, c1.y + HEIGHT, c1.z - p1.z * halfRoadWidth, 1);
				vertices[indexOfFirstVertex + 1] = new Vector4f(c1.x + p1.x * halfRoadWidth, c1.y + HEIGHT, c1.z + p1.z * halfRoadWidth, 1);
				vertices[indexOfFirstVertex + 2] = new Vector4f(c2.x - p2.x * halfRoadWidth, c2.y + HEIGHT, c2.z - p2.z * halfRoadWidth, 1);
				vertices[indexOfFirstVertex + 3] = new Vector4f(c2.x + p2.x * halfRoadWidth, c2.y + HEIGHT, c2.z + p2.z * halfRoadWidth, 1);

				uvs[indexOfFirstVertex + 0] = new Vector2f(texV1, roadTexStart);
				uvs[indexOfFirstVertex + 1] = new Vector2f(texV1, roadTexEnd);
				uvs[indexOfFirstVertex + 2] = new Vector2f(texV2, roadTexStart);
				uvs[indexOfFirstVertex + 3] = new Vector2f(texV2, roadTexEnd);
			}

			// Slope quad 1
			{
				var quadIndex = 1;
				var indexOfFirstVertex = j + quadIndex * uniqueVerticesPerQuad;

				vertices[indexOfFirstVertex + 0] = new Vector4f(c1.x + p1.x * halfRoadWidth, c1.y + HEIGHT, c1.z + p1.z * halfRoadWidth, 1);
				vertices[indexOfFirstVertex + 1] = new Vector4f(c1.x + p1.x * halfTotalWidth, c1.y, c1.z + p1.z * halfTotalWidth, 1);
				vertices[indexOfFirstVertex + 2] = new Vector4f(c2.x + p2.x * halfRoadWidth, c1.y + HEIGHT, c2.z + p2.z * halfRoadWidth, 1);
				vertices[indexOfFirstVertex + 3] = new Vector4f(c2.x + p2.x * halfTotalWidth, c1.y, c2.z + p2.z * halfTotalWidth, 1);

				uvs[indexOfFirstVertex + 0] = new Vector2f(texV1, roadTexEnd);
				uvs[indexOfFirstVertex + 1] = new Vector2f(texV1, 1);
				uvs[indexOfFirstVertex + 2] = new Vector2f(texV2, roadTexEnd);
				uvs[indexOfFirstVertex + 3] = new Vector2f(texV2, 1);
			}

			// Slope quad 2
			{
				var quadIndex = 2;
				var indexOfFirstVertex = j + quadIndex * uniqueVerticesPerQuad;

				vertices[indexOfFirstVertex + 1] = new Vector4f(c1.x - p1.x * halfRoadWidth, c1.y + HEIGHT, c1.z - p1.z * halfRoadWidth, 1);
				vertices[indexOfFirstVertex + 0] = new Vector4f(c1.x - p1.x * halfTotalWidth, c1.y, c1.z - p1.z * halfTotalWidth, 1);
				vertices[indexOfFirstVertex + 3] = new Vector4f(c2.x - p2.x * halfRoadWidth, c1.y + HEIGHT, c2.z - p2.z * halfRoadWidth, 1);
				vertices[indexOfFirstVertex + 2] = new Vector4f(c2.x - p2.x * halfTotalWidth, c1.y, c2.z - p2.z * halfTotalWidth, 1);

				uvs[indexOfFirstVertex + 0] = new Vector2f(texV1, 0);
				uvs[indexOfFirstVertex + 1] = new Vector2f(texV1, roadTexStart);
				uvs[indexOfFirstVertex + 2] = new Vector2f(texV2, 0);
				uvs[indexOfFirstVertex + 3] = new Vector2f(texV2, roadTexStart);
			}
		}

		// For each quad (4 unique vertices) make normals
		var normals = new Vector4f[vertices.length];
		var e0 = new Vector3f();
		var e1 = new Vector3f();
		var e2 = new Vector3f();
		var e3 = new Vector3f();
		var triangle1Normal = new Vector3f();
		var triangle2Normal = new Vector3f();
		var n_v0 = new Vector3f();
		var n_v1 = new Vector3f();
		var n_v2 = new Vector3f();
		var n_v3 = new Vector3f();
		for (var segmentIndex = 0; segmentIndex < segmentCount; segmentIndex++) {
			for (var quadIndex = 0; quadIndex < quadCount; quadIndex++) {
				var indexOfFirstVertexOfQuad = (segmentIndex + (quadIndex * segmentCount)) * uniqueVerticesPerQuad;

				var v0 = vertices[indexOfFirstVertexOfQuad + 0];
				var v1 = vertices[indexOfFirstVertexOfQuad + 1];
				var v2 = vertices[indexOfFirstVertexOfQuad + 2];
				var v3 = vertices[indexOfFirstVertexOfQuad + 3];

				// Triangle 1: v0, v1, v2, compute edges
				e0.set(v1.x - v0.x, v1.y - v0.y, v1.z - v0.z);
				e1.set(v2.x - v0.x, v2.y - v0.y, v2.z - v0.z);
				triangle1Normal.set(e0).cross(e1).normalize();

				// Triangle 2: v1, v3, v2, compute edges
				e2.set(v3.x - v1.x, v3.y - v1.y, v3.z - v1.z);
				e3.set(v2.x - v1.x, v2.y - v1.y, v2.z - v1.z);
				triangle2Normal.set(e2).cross(e3).normalize();

				n_v0.set(triangle1Normal).normalize();
				n_v1.set(triangle1Normal).add(triangle2Normal).normalize();
				n_v2.set(triangle1Normal).add(triangle2Normal).normalize();
				n_v3.set(triangle2Normal).normalize();

				normals[indexOfFirstVertexOfQuad + 0] = new Vector4f(n_v0, 0);
				normals[indexOfFirstVertexOfQuad + 1] = new Vector4f(n_v1, 0);
				normals[indexOfFirstVertexOfQuad + 2] = new Vector4f(n_v2, 0);
				normals[indexOfFirstVertexOfQuad + 3] = new Vector4f(n_v3, 0);
			}
		}

		// For each quad (4 unique vertices) make 2 triangles (6 vertices, 2 shared)
		var indices = new int[segmentCount * quadCount * verticesPerQuad];
		for (var segmentIndex = 0; segmentIndex < segmentCount; segmentIndex++) {
			for (var quadIndex = 0; quadIndex < quadCount; quadIndex++) {
				// [segment][quad] * (4 | 6)
				var indexOfFirstVertexOfQuad = (segmentIndex + (quadIndex * segmentCount)) * uniqueVerticesPerQuad;
				var indexOfFirstVertexOfTriangles = (segmentIndex + (quadIndex * segmentCount)) * verticesPerQuad;
				indices[indexOfFirstVertexOfTriangles + 0] = indexOfFirstVertexOfQuad + 0;
				indices[indexOfFirstVertexOfTriangles + 1] = indexOfFirstVertexOfQuad + 1;
				indices[indexOfFirstVertexOfTriangles + 2] = indexOfFirstVertexOfQuad + 2;
				indices[indexOfFirstVertexOfTriangles + 3] = indexOfFirstVertexOfQuad + 1;
				indices[indexOfFirstVertexOfTriangles + 4] = indexOfFirstVertexOfQuad + 3;
				indices[indexOfFirstVertexOfTriangles + 5] = indexOfFirstVertexOfQuad + 2;
			}
		}

		vertexBuffer = GLBuffers.createBuffer(vertices);
		normalBuffer = GLBuffers.createBuffer(normals);
		uvBuffer = GLBuffers.createBuffer(uvs);
		indexBuffer = GLBuffers.createIndexBuffer(indices);
		indexCount = indices.length;
	}

	//bezier formula B(t) = (1-t)³·P0 + 3(1-t)²·t·P1 + 3(1-t)·t²·P2 + t³·P3
	private Vector3f bezier(float t, Vector3f p) {
		float u = 1 - t;
		float b0 = u * u * u;          // (1-t)^3
		float b1 = 3 * u * u * t;      // 3(1-t)^2 t
		float b2 = 3 * u * t * t;      // 3(1-t) t^2
		float b3 = t * t * t;          // t^3

		p.x = b0 * CONTROL_POINTS[0].x + b1 * CONTROL_POINTS[1].x + b2 * CONTROL_POINTS[2].x + b3 * CONTROL_POINTS[3].x;
		p.y = b0 * CONTROL_POINTS[0].y + b1 * CONTROL_POINTS[1].y + b2 * CONTROL_POINTS[2].y + b3 * CONTROL_POINTS[3].y;
		p.z = b0 * CONTROL_POINTS[0].z + b1 * CONTROL_POINTS[1].z + b2 * CONTROL_POINTS[2].z + b3 * CONTROL_POINTS[3].z;
		return p;
	}

	private final Matrix4f modelMatrix = new Matrix4f();
	private final Vector3f camPos = new Vector3f();
	private final Vector3f headlightDirection = new Vector3f();
	private final Vector3f headlightPosition = new Vector3f();

	@Override
	protected void drawSelf(Matrix4f mvpMatrix) {
		shader.enable();
		shader.setUniform("u_mvpMatrix", mvpMatrix);
		shader.setUniform("u_modelMatrix", getModelToWorldMatrix(modelMatrix));
		shader.setUniform("u_debugNormals", Scene.theScene.debugNormals);

		shader.setUniform("u_daytime", Scene.theScene.daytime);
		shader.setUniform("u_headlightposition", Scene.theScene.getHeadlightPosition(headlightPosition));
		shader.setUniform("u_spotdirection", Scene.theScene.getHeadlightDirection(headlightDirection));

		//light
		Light light = Scene.theScene.light;
		shader.setUniform("u_lightDirection", light.getDirection());
		shader.setUniform("u_lightColour", light.getColour());
		shader.setUniform("u_ambientColour", light.getAmbient());

		//spec
		Scene.theScene.getCamera().getPosition(camPos);
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