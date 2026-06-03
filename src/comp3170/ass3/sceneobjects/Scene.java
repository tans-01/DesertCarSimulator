package comp3170.ass3.sceneobjects;

import comp3170.InputManager;
import comp3170.OpenGLException;
import comp3170.SceneObject;
import org.joml.Matrix4f;

import java.io.IOException;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_3;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_4;
import static org.lwjgl.opengl.GL11.*;

public class Scene extends SceneObject {

	public static Scene theScene = null;
	public boolean daytime = true;
	public boolean wireframe = false;
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
		if (input.wasKeyPressed(GLFW_KEY_4)) {
			wireframe = !wireframe;
		}
	    car.update(input, deltaTime);
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
}
