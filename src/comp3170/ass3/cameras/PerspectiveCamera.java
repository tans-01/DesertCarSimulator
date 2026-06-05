package comp3170.ass3.cameras;

import comp3170.InputManager;
import comp3170.SceneObject;
import comp3170.ass3.sceneobjects.Scene;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;

import static comp3170.Math.TAU;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT;

public class PerspectiveCamera extends SceneObject implements Camera {

	private static final float ASPECT = 1;
	private static final float FOVY = TAU / 6;
	private static final float NEAR = 0.1f;
	private static final float FAR = 500f;
	private static final float HEIGHT = 2f;
	final static float ROTATION_SPEED = TAU / 4;
	Vector2f tiltYawAngleRadians = new Vector2f(
		// Look slightly down initially
		-(float)Math.PI / 10,
		0
	);

	public Matrix4f cameraMatrix = new Matrix4f();
	
	public PerspectiveCamera() {
		cameraMatrix.translate(0, HEIGHT, 0);
	}
		
	@Override
	public Matrix4f getCameraMatrix(Matrix4f dest) {
		return dest.set(cameraMatrix);
	}

	@Override
	public Matrix4f getViewMatrix(Matrix4f dest) {
		return cameraMatrix.invert(dest);
	}

	@Override
	public Matrix4f getProjectionMatrix(Matrix4f dest) {
		return dest.setPerspective(FOVY, ASPECT, NEAR, FAR);
	}
	
	@Override
	public Vector4f getViewVector(Vector4f dest) {
		// for a perspective camera
		// the view vector is the origin point of the cameraMatrix
		return cameraMatrix.getColumn(3, dest);
	}

	public void update(InputManager input, float deltaTime) {
		if (input.isKeyDown(GLFW_KEY_UP)) {
			tiltYawAngleRadians.x += ROTATION_SPEED * deltaTime;
		}
		if (input.isKeyDown(GLFW_KEY_DOWN)) {
			tiltYawAngleRadians.x -= ROTATION_SPEED * deltaTime;
		}
		if (input.isKeyDown(GLFW_KEY_LEFT)) {
			tiltYawAngleRadians.y += ROTATION_SPEED * deltaTime;
		}
		if (input.isKeyDown(GLFW_KEY_RIGHT)) {
			tiltYawAngleRadians.y -= ROTATION_SPEED * deltaTime;
		}

		cameraMatrix.set(Scene.theScene.getCar().getMatrix());
		cameraMatrix.translate(0, 5, 10); // up + back
		cameraMatrix.rotateY(tiltYawAngleRadians.y);
		cameraMatrix.rotateX(tiltYawAngleRadians.x);
	}
}
