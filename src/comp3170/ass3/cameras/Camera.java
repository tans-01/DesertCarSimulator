package comp3170.ass3.cameras;

import comp3170.InputManager;
import org.joml.Matrix4f;
import org.joml.Vector4f;

public interface Camera {
	float NEAR = 0.1f;
	float FAR = 500f;
	Matrix4f getViewMatrix(Matrix4f dest);
	Matrix4f getProjectionMatrix(Matrix4f dest, int windowWidth, int windowHeight);
	void update(InputManager input, float deltaTime);
}
