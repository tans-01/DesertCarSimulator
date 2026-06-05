package comp3170.ass3.cameras;

import comp3170.InputManager;
import comp3170.SceneObject;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import static comp3170.Math.TAU;
import static org.lwjgl.glfw.GLFW.*;

public class OrthographicCamera extends SceneObject implements Camera {

	private static final float WIDTH = 100;
	private static final float HEIGHT = 100;
	private static final float ELEVATION = 5;
	private static final float DISTANCE = 5;

	final static float ROTATION_SPEED = TAU / 4;
	final Vector3f angle = new Vector3f(-TAU/4, 0, 0);

	final Matrix4f cameraMatrix = new Matrix4f();

	public OrthographicCamera() {
	}

	@Override
	public Matrix4f getCameraMatrix(Matrix4f dest) {
		return cameraMatrix;
	}

	@Override
	public Matrix4f getViewMatrix(Matrix4f dest) {
		return cameraMatrix.invert(dest);
	}

	@Override
	public Matrix4f getProjectionMatrix(Matrix4f dest) {
		return dest.setOrthoSymmetric(WIDTH, HEIGHT, NEAR, FAR);
	}

	@Override
	public Vector4f getViewVector(Vector4f dest) {

		// the viewDirection is the Z axis (i.e. the k vector)
		// because the view direction points towards the camera
		// and the view volume is normally in negative view space.

		// Note: this assumes the camera is orthographic
		// the view direction for a perspective camera will
		// depend on the target's position

		return cameraMatrix.getColumn(2, dest);
	}

	public void update(InputManager input, float deltaTime) {
		if (input.isKeyDown(GLFW_KEY_UP)) {
			angle.x -= ROTATION_SPEED * deltaTime;
		}
		if (input.isKeyDown(GLFW_KEY_DOWN)) {
			angle.x += ROTATION_SPEED * deltaTime;
		}
		if (input.isKeyDown(GLFW_KEY_LEFT)) {
			angle.y -= ROTATION_SPEED * deltaTime;
		}
		if (input.isKeyDown(GLFW_KEY_RIGHT)) {
			angle.y += ROTATION_SPEED * deltaTime;
		}

		cameraMatrix.identity();
		cameraMatrix.rotateY(angle.y);    // heading
		cameraMatrix.rotateX(angle.x);    // pitch
		cameraMatrix.translate(0.0f, ELEVATION, DISTANCE);
	}
}
