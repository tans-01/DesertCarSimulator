package comp3170.ass3.cameras;

import comp3170.InputManager;
import comp3170.SceneObject;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import static comp3170.Math.TAU;

public class OrthographicCamera extends SceneObject implements Camera {
	private static final float WIDTH = 100;
	private static final float HEIGHT = 100;
	private static final float ELEVATION = 5;

	final Matrix4f cameraMatrix = new Matrix4f();

	public OrthographicCamera() {
	}

	@Override
	public Matrix4f getViewMatrix(Matrix4f dest) {
		return cameraMatrix.invert(dest);
	}

	@Override
	public Matrix4f getProjectionMatrix(Matrix4f dest, int windowWidth, int windowHeight) {
		// The vertical/horizontal extents aren't fixed
		// The map is always as big as possible
		// I.e. always touching either the vertical or horizontal extents, or both if they're the same
		float windowAspect = (float) windowWidth / windowHeight;
		float mapAspect = WIDTH / HEIGHT;
		float orthoWidth, orthoHeight;
		if (windowAspect > mapAspect) {
			orthoHeight = HEIGHT;
			orthoWidth = HEIGHT * windowAspect;
		} else {
			orthoWidth = WIDTH;
			orthoHeight = WIDTH / windowAspect;
		}
		return dest.setOrthoSymmetric(orthoWidth, orthoHeight, NEAR, FAR);
	}

	public void update(InputManager input, float deltaTime) {
		cameraMatrix.identity();
		cameraMatrix.translate(0.0f, ELEVATION, 0);
		// Rotate so we are looking down
		cameraMatrix.rotateX(-TAU / 4);
	}
	@Override
	public Vector3f getPosition(Vector3f dest) {
	    return cameraMatrix.getTranslation(dest);
	}
}
