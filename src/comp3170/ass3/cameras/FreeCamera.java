package comp3170.ass3.cameras;

import comp3170.InputManager;
import comp3170.SceneObject;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import static comp3170.Math.TAU;
import static org.lwjgl.glfw.GLFW.*;

public class FreeCamera extends SceneObject implements Camera  {

	private static final float ASPECT = 1;
	private static final float FOVY = TAU / 4;
	private static final float HEIGHT = 2f;

	private Matrix4f cameraMatrix = new Matrix4f();

	public FreeCamera() {
		cameraMatrix.translate(0, HEIGHT, 0);
	}

	@Override
	public Matrix4f getViewMatrix(Matrix4f dest) {
		return cameraMatrix.invert(dest);
	}

	@Override
	public Matrix4f getProjectionMatrix(Matrix4f dest, int width, int height) {
		return dest.setPerspective(FOVY, ASPECT, NEAR, FAR);
	}

	@Override
	public Vector3f getPosition(Vector3f dest) {
		// for a perspective camera
		// the view vector is the origin point of the cameraMatrix
		return cameraMatrix.getColumn(3, dest);
	}

	private static final float ROTATION_SPEED = TAU / 6;
	private static final float MOVEMENT_SPEED = 4f;

	public void update(InputManager input, float deltaTime) {
		if (input.isKeyDown(GLFW_KEY_LEFT)) {
			cameraMatrix.rotateY(ROTATION_SPEED * deltaTime);
		}
		if (input.isKeyDown(GLFW_KEY_RIGHT)) {
			cameraMatrix.rotateY(-ROTATION_SPEED * deltaTime);
		}
		if (input.isKeyDown(GLFW_KEY_UP)) {
			cameraMatrix.rotateX(ROTATION_SPEED * deltaTime);
		}
		if (input.isKeyDown(GLFW_KEY_DOWN)) {
			cameraMatrix.rotateX(-ROTATION_SPEED * deltaTime);
		}
		if (input.isKeyDown(GLFW_KEY_W)) {
			cameraMatrix.translate(0, 0, -MOVEMENT_SPEED * deltaTime);
		}
		if (input.isKeyDown(GLFW_KEY_S)) {
			cameraMatrix.translate(0, 0, MOVEMENT_SPEED * deltaTime);
		}
	}


}
