package comp3170.ass3.sceneobjects;

import comp3170.InputManager;
import comp3170.OpenGLException;
import comp3170.SceneObject;

import java.io.IOException;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_3;

public class Scene extends SceneObject {

	public static Scene theScene = null;
	public boolean daytime = true;
	private Skybox skybox;
	private Desert desert;
	private Car car;

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
		
	}

	public void update(InputManager input, float deltaTime) {
//		if (input.wasKeyPressed(GLFW_KEY_1)) {
//			activeCamera = mapCamera;
//		}
//		if (input.wasKeyPressed(GLFW_KEY_2)) {
//			activeCamera = thirdPersonCamera;
//		}
		if (input.wasKeyPressed(GLFW_KEY_3)) {
			daytime = !daytime;
		}
	    car.update(input, deltaTime);
	}
}
