package comp3170.ass3.sceneobjects;

import comp3170.InputManager;
import comp3170.OpenGLException;
import comp3170.SceneObject;
import comp3170.ass3.cameras.Camera;
import comp3170.ass3.cameras.OrthographicCamera;
import comp3170.ass3.cameras.PerspectiveCamera;
import org.joml.Matrix4f;

import java.io.IOException;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class Scene extends SceneObject {

	public static Scene theScene = null;
	public boolean daytime = true;
	public boolean wireframe = false;
	final Skybox skybox;
	final Desert desert;
	final Car car;
	final PerspectiveCamera thirdPersonCamera;
	final OrthographicCamera mapCamera;
	Camera activeCamera;

	public Scene() {
		theScene = this;

		skybox = new Skybox();
		skybox.setParent(theScene);
		try {
			desert = new Desert();
			desert.setParent(theScene);
			car = new Car();
			car.setParent(desert);
		} catch (IOException | OpenGLException e) {
			throw new RuntimeException(e);
		}

		thirdPersonCamera = new PerspectiveCamera();
		thirdPersonCamera.setParent(theScene);
		mapCamera = new OrthographicCamera();
		mapCamera.setParent(theScene);

		activeCamera = thirdPersonCamera;
	}

	public void update(int windowWidth, int windowHeight, InputManager input, float deltaTime) {
		if (input.wasKeyPressed(GLFW_KEY_1)) {
			activeCamera = mapCamera;
		}
		if (input.wasKeyPressed(GLFW_KEY_2)) {
			activeCamera = thirdPersonCamera;
		}
		if (input.wasKeyPressed(GLFW_KEY_3)) {
			daytime = !daytime;
		}
		if (input.wasKeyPressed(GLFW_KEY_4)) {
			wireframe = !wireframe;
		}
	    car.update(input, deltaTime);
		activeCamera.update(input, deltaTime);
		skybox.update(windowWidth, windowHeight);
	}

	public Camera getCamera() {
		return activeCamera;
	}

	@Override
	public void draw(Matrix4f parentMatrix, int pass) {
		if (wireframe) {
			glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
		} else {
			glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
		}
		super.draw(parentMatrix, pass);
	}

	public Car getCar() {
		return car;
	}
}
