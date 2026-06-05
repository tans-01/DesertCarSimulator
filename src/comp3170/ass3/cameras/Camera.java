package comp3170.ass3.cameras;

import comp3170.InputManager;
import org.joml.Matrix4f;
import org.joml.Vector4f;

public interface Camera {
	Matrix4f getCameraMatrix(Matrix4f dest);
	Matrix4f getViewMatrix(Matrix4f dest);
	Matrix4f getProjectionMatrix(Matrix4f dest);
	Vector4f getViewVector(Vector4f dest);
	void update(InputManager input, float deltaTime);
}
