package comp3170.ass3.sceneobjects;

import java.io.FileNotFoundException;

import org.joml.Matrix4f;

import comp3170.GLBuffers;
import comp3170.SceneObject;
import comp3170.ass3.models.Mesh;
import comp3170.ass3.models.ObjData;

public class Tree extends SceneObject {

	private static final String TEXTURE = "tree.png";
	private static final String OBJ_FILE = "src/comp3170/ass3/models/tree.obj";

	private int vertexBuffer;
	private int normalBuffer;
	private int uvBuffer;
	private int indexBuffer;
	
	public Tree()
	{
		// Example of how to load a mesh from a Wavefront OBJ file 
		ObjData data = null;

		try {
			data = new ObjData(OBJ_FILE);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		// Get the submesh
		Mesh mesh = data.getMesh("Tree");

		// Create the buffers
		vertexBuffer = GLBuffers.createBuffer(mesh.vertices);
		normalBuffer = GLBuffers.createBuffer(mesh.normals);
		uvBuffer = GLBuffers.createBuffer(mesh.uvs);
		indexBuffer = GLBuffers.createIndexBuffer(mesh.indices);
	}

	protected void drawSelf(Matrix4f mvpMatrix) {
		// To be completed
	}

}
