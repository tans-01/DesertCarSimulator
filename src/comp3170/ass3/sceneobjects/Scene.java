package comp3170.ass3.sceneobjects;

import comp3170.InputManager;
import comp3170.OpenGLException;
import comp3170.SceneObject;

import java.io.IOException;

public class Scene extends SceneObject {

	public static Scene theScene = null;
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
	    car.update(input, deltaTime);
	}
	
}
