package comp3170.ass3.models;

import org.joml.Vector2f;
import org.joml.Vector4f;

public class Mesh {
	public Vector4f[] vertices;
	public Vector4f[] normals;
	public Vector2f[] uvs;
	public int[] indices;
	public Material material;
}

