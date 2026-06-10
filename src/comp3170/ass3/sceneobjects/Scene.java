package comp3170.ass3.sceneobjects;

import comp3170.InputManager;
import comp3170.OpenGLException;
import comp3170.SceneObject;
import comp3170.ass3.Light;
import comp3170.ass3.cameras.Camera;
import comp3170.ass3.cameras.FreeCamera;
import comp3170.ass3.cameras.OrthographicCamera;
import comp3170.ass3.cameras.PerspectiveCamera;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.io.IOException;
import java.util.Random;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class Scene extends SceneObject {

	public static Scene theScene = null;
	public boolean daytime = true;
	public boolean wireframe = false;
	public boolean debugNormals = false;
	final Skybox skybox;
	final Desert desert;
	final Road road;
	final Car car;
	final PerspectiveCamera thirdPersonCamera;
	final OrthographicCamera mapCamera;
	final FreeCamera freeCamera;
	Camera activeCamera;
	public Light light;
	public float sunspeed = 1f;

	public Scene() {
		theScene = this;

		skybox = new Skybox();
		skybox.setParent(theScene);
		try {
			desert = new Desert();
			desert.setParent(theScene);
			road = new Road();
			road.setParent(desert);
			car = new Car();
			car.setParent(desert);
			light = new Light();
			{
				final var seed = 2026;
				final var treeCount = 20;
				final var spawnBound = Desert.SIZE / 2;
				final var minSize = 0.75F;
				final var maxSize = 1.5F;
				var rescale = 1 / 20F; // Trees are way too big by default
				var random = new Random(seed);
				var treeTransforms = new Matrix4f[treeCount];
				for (int i = 0; i < treeCount; i++) {
					var treeTransform = new Matrix4f().identity();
					treeTransform.translate(random.nextFloat(-spawnBound, spawnBound), 0, random.nextFloat(-spawnBound, spawnBound));
					treeTransform.rotateY(random.nextFloat() * (float) Math.TAU);
					treeTransform.scale(random.nextFloat(minSize, maxSize));
					treeTransform.scale(rescale);
					treeTransforms[i] = treeTransform;
				}
				var tree = new Tree(treeTransforms);
				tree.setParent(desert);
			}
		} catch (IOException | OpenGLException e) {
			throw new RuntimeException(e);
		}

		thirdPersonCamera = new PerspectiveCamera();
		thirdPersonCamera.setParent(theScene);
		mapCamera = new OrthographicCamera();
		mapCamera.setParent(theScene);
		freeCamera = new FreeCamera();
		freeCamera.setParent(theScene);

		activeCamera = thirdPersonCamera;
	}

	private final Matrix4f carMatrix = new Matrix4f();

	// direction of the headlight
	public Vector3f getHeadlightDirection(Vector3f headlightDirection) {
	    car.getModelToWorldMatrix(carMatrix);
	    //fliping becaue of the flipped car
	    //carMatrix.scale(-1, 1, 1); 
	    carMatrix.rotateY((float) Math.TAU / 2);

	    // transform the forward direction (w=0 so translation is ignored)
		headlightDirection.set(0, -0.3f, 1);
	    return carMatrix.transformDirection(headlightDirection).normalize();
	}
	// position of the headlight on the car
	public Vector3f getHeadlightPosition(Vector3f headlightPosition) {
		// headlight on the car space
		headlightPosition.set(0, 0.93f, 2.1f);
	    car.getModelToWorldMatrix(carMatrix);
	    //carMatrix.scale(-1, 1, 1);
	    carMatrix.rotateY((float) Math.TAU / 2);
	    return carMatrix.transformPosition(headlightPosition); // position in the world space
	}
	
	public void update(int windowWidth, int windowHeight, InputManager input, float deltaTime) {
		if (input.wasKeyPressed(GLFW_KEY_0)) {
			activeCamera = freeCamera;
		}
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
		if (input.wasKeyPressed(GLFW_KEY_5)) {
			debugNormals = !debugNormals;
		}
		car.update(input, deltaTime);
		activeCamera.update(input, deltaTime);
		skybox.update(windowWidth, windowHeight);
		
		if (input.isKeyDown(GLFW_KEY_LEFT_BRACKET)) {
			light.sunRotation(-sunspeed * deltaTime);
		}
		if (input.isKeyDown(GLFW_KEY_RIGHT_BRACKET)) {
		    light.sunRotation(sunspeed * deltaTime);
		}
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
