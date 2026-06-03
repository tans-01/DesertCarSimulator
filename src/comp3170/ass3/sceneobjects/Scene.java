package comp3170.ass3.sceneobjects;

import comp3170.OpenGLException;
import comp3170.SceneObject;
import comp3170.ShaderLibrary;
import comp3170.TextureLibrary;

import java.io.IOException;

public class Scene extends SceneObject {

	public static Scene theScene = null;
	private Desert desert;
	private Car car;

	public Scene() {
		theScene = this;

		try {
			desert = new Desert();
			desert.setParent(theScene);
			car = new Car();
			car.setParent(theScene);
		} catch (IOException | OpenGLException e) {
			throw new RuntimeException(e);
		}
	}
	
}
