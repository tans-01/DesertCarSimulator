package comp3170.ass3.cameras;

import comp3170.InputManager;
import comp3170.ass3.sceneobjects.Scene;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import static comp3170.Math.TAU;
import static org.lwjgl.glfw.GLFW.*;

public class PerspectiveCamera implements Camera {

	private static final float HEIGHT = 2f;
	static final float PITCH_SPEED = TAU / 4;
	static final float ROTATION_SPEED = TAU / 4;
	static final float DOLLY_SPEED = 1f;
	static final float FOV_ZOOM_SPEED = 1f;
	static final float MIN_PITCH = -TAU / 4;
	static final float MAX_PITCH = TAU / 4;
	static final float MIN_DOLLY = 1;
	static final float MAX_DOLLY = 20;
	// 5º
	static final float MIN_FOV_ZOOM = TAU / 72;
	// A fov zoom bigger than TAU / 2, flips the image, TAU / 3 is a healthy margin away from this
	static final float MAX_FOV_ZOOM = TAU / 3;
	// Look slightly down initially
	float pitchRadians = -(float)TAU / 20;
	float rotationRadians = 0;
	// 10m away initially
	float dollyMeters = 10;
	// 90º initially
	float fovZoom = TAU / 6;

	public Matrix4f cameraMatrix = new Matrix4f();

	public PerspectiveCamera() {
		cameraMatrix.translate(0, HEIGHT, 0);
	}

	@Override
	public Matrix4f getViewMatrix(Matrix4f dest) {
		return cameraMatrix.invert(dest);
	}

	@Override
	public Matrix4f getProjectionMatrix(Matrix4f dest, int windowWidth, int windowHeight) {
		return dest.setPerspective(fovZoom, (float) windowWidth / windowHeight, NEAR, FAR);
	}
	
	@Override
	public Vector3f getPosition(Vector3f dest) {
	    return cameraMatrix.getTranslation(dest);
	}
	
	public void update(InputManager input, float deltaTime) {
		if (input.isKeyDown(GLFW_KEY_UP)) {
			pitchRadians = Math.min(MAX_PITCH, pitchRadians + PITCH_SPEED * deltaTime);
		}
		if (input.isKeyDown(GLFW_KEY_DOWN)) {
			pitchRadians = Math.max(MIN_PITCH, pitchRadians - PITCH_SPEED * deltaTime);
		}
		if (input.isKeyDown(GLFW_KEY_LEFT)) {
			rotationRadians += ROTATION_SPEED * deltaTime;
		}
		if (input.isKeyDown(GLFW_KEY_RIGHT)) {
			rotationRadians -= ROTATION_SPEED * deltaTime;
		}
		if (input.isKeyDown(GLFW_KEY_COMMA)) {
			dollyMeters = Math.min(MAX_DOLLY, dollyMeters + DOLLY_SPEED * deltaTime);
		}
		if (input.isKeyDown(GLFW_KEY_PERIOD)) {
			dollyMeters = Math.max(MIN_DOLLY, dollyMeters - DOLLY_SPEED * deltaTime);
		}
		if (input.isKeyDown(GLFW_KEY_PAGE_UP)) {
			fovZoom = Math.min(MAX_FOV_ZOOM, fovZoom + FOV_ZOOM_SPEED * deltaTime);
		}
		if (input.isKeyDown(GLFW_KEY_PAGE_DOWN)) {
			fovZoom = Math.max(MIN_FOV_ZOOM, fovZoom - FOV_ZOOM_SPEED * deltaTime);
		}

		cameraMatrix.set(Scene.theScene.getCar().getMatrix());
		cameraMatrix.rotateY(rotationRadians);
		cameraMatrix.translate(0, 5, dollyMeters); // up + back
		cameraMatrix.rotateX(pitchRadians);
	}
}
