package comp3170.ass3.sceneobjects;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_S;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_W;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.glBindBuffer;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import comp3170.GLBuffers;
import comp3170.InputManager;
import comp3170.OpenGLException;
import comp3170.SceneObject;
import comp3170.Shader;
import comp3170.ShaderLibrary;
import comp3170.TextureLibrary;
import comp3170.ass3.TextureUtils;
import comp3170.ass3.models.Mesh;
import comp3170.ass3.models.ObjData;
public class Car extends SceneObject {

    private static final String VERT_SHADER = "simple.vert";
    private static final String FRAG_SHADER = "simple.frag";
    private static final String TEXTURE = "car.png";
    private static final String OBJ_FILE = "src/comp3170/ass3/models/car.obj";
    private static final float TURN_SPEED = (float) Math.toRadians(120); // radians per second
    private static final float ACCELERATION = 4f;    // m/s^2 - how fast it speeds up
    private static final float MAX_SPEED = 20f;       // m/s - top speed
    private static final float FRICTION = 6f;         // m/s^2 - how fast it coasts to stop
    private static final float BRAKE = 20f;   // much stronger than friction
    
    private float velocity = 0;   // current speed, remembered between frames
    private Wheel[] wheels;
    private static final float WHEEL_RADIUS = 0.35f;                 // wheel radius in metres
    private static final float MAX_STEER = (float) Math.toRadians(30); // how far front wheels turn
    // the three submeshes in the car OBJ
    private static final String[] SUBMESHES = { "Body", "Interior", "Windows" };

    private Shader shader;
    private int texture;

    // one set of buffers per submesh
    private int[] vertexBuffers;
    private int[] normalBuffers;
    private int[] uvBuffers;
    private int[] indexBuffers;
    private int[] indexCounts;

    public Car() throws IOException, OpenGLException {
        shader = ShaderLibrary.instance.compileShader(VERT_SHADER, FRAG_SHADER);

        texture = TextureLibrary.instance.loadTexture(TEXTURE);
        TextureUtils.setupTexture(texture);

        // load the OBJ file once
        ObjData data;
        try {
            data = new ObjData(OBJ_FILE);
        } catch (FileNotFoundException e) {
            throw new IOException(e);
        }

        int n = SUBMESHES.length;
        vertexBuffers = new int[n];
        normalBuffers = new int[n];
        uvBuffers = new int[n];
        indexBuffers = new int[n];
        indexCounts = new int[n];

        // create buffers for each submesh
        for (int i = 0; i < n; i++) {
            Mesh mesh = data.getMesh(SUBMESHES[i]);    // The array will hold each submesh like body, interior
            vertexBuffers[i] = GLBuffers.createBuffer(mesh.vertices); // window.
            normalBuffers[i] = GLBuffers.createBuffer(mesh.normals);
            uvBuffers[i] = GLBuffers.createBuffer(mesh.uvs);
            indexBuffers[i] = GLBuffers.createIndexBuffer(mesh.indices);
            indexCounts[i] = mesh.indices.length;
        } /* like this 
        
        *vertexBuffers = [Body's ID, Interior's ID, Windows' ID]
		uvBuffers     = [Body's ID, Interior's ID, Windows' ID]
		indexBuffers  = [Body's ID, Interior's ID, Windows' ID]
		indexCounts   = [Body count, Interior count, Windows count]
        *
        */

        // four wheels attached to the car as its children
        float[][] wheelPositions = {
            {  0.62f, 0.35f,  1.3f  },  // front left
            { -0.62f, 0.35f,  1.3f  },  // front right
            {  0.62f, 0.35f, -1.15f },  // back left
            { -0.62f, 0.35f, -1.15f },  // back right
        };
        wheels = new Wheel[wheelPositions.length];
        for (int i = 0; i < wheelPositions.length; i++) {
        	float[] pos = wheelPositions[i];
            boolean isFront = pos[2] > 0;
            boolean flipped = pos[0] > 0;
            Wheel wheel = new Wheel(new Vector3f(pos[0], pos[1], pos[2]), isFront, flipped);
            wheel.setParent(this);
            wheels[i] = wheel; 
        }
    }
    
    public void update(InputManager input, float deltaTime) {
        float steerAngle = 0;

        // Acceleration
        if (input.isKeyDown(GLFW_KEY_W)) {
            velocity += ACCELERATION * deltaTime;
        } else if (input.isKeyDown(GLFW_KEY_S)) {
            velocity -= ACCELERATION * deltaTime;
        } else {
            velocity = applyDecel(velocity, FRICTION * deltaTime);   // coasting
        }

        // Brake (spacebar): strong deceleration
        if (input.isKeyDown(GLFW_KEY_SPACE)) {
            velocity = applyDecel(velocity, BRAKE * deltaTime);
        }

        // cap speed cap
        if (velocity > MAX_SPEED)  velocity = MAX_SPEED;
        if (velocity < -MAX_SPEED) velocity = -MAX_SPEED;

        float moveDistance = velocity * deltaTime;
        getMatrix().translate(0, 0, -moveDistance);

        // Steering:
        // turn RATE scales with speed (no car rotation when stationary)
        // steer ANGLE is full so wheels visually turn even when parked
        float speedFactor = velocity / MAX_SPEED;   // -1..1, 0 when stopped

        if (input.isKeyDown(GLFW_KEY_A)) {
            getMatrix().rotateY(TURN_SPEED * deltaTime * speedFactor);
            steerAngle = -MAX_STEER;
        }
        if (input.isKeyDown(GLFW_KEY_D)) {
            getMatrix().rotateY(-TURN_SPEED * deltaTime * speedFactor);
            steerAngle = MAX_STEER;
        }

        // --- Wheels ---
        float spinDelta = moveDistance / WHEEL_RADIUS;
        for (Wheel wheel : wheels) {
            wheel.update(spinDelta, steerAngle);
        }
    }

    //reduces the velocity 
    private float applyDecel(float v, float amount) {
        if (v > 0) {
            v -= amount;
            if (v < 0) v = 0;
        } else if (v < 0) {
            v += amount;
            if (v > 0) v = 0;
        }
        return v;
    }

    @Override
    protected void drawSelf(Matrix4f mvpMatrix, int pass) {
        // Flip from US (left-hand drive) to Australian (right-hand drive) layout
        mvpMatrix.scale(-1, 1, 1);
        // Fix model pointing backwards
        mvpMatrix.rotateY((float) Math.TAU / 2);

        shader.enable();
        shader.setUniform("u_mvpMatrix", mvpMatrix);
        shader.setUniform("u_modelMatrix", getMatrix());

        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, texture);
        shader.setUniform("u_texture", 0);

        if (pass == 0) {
            // OPAQUE PASS: Body (0) and Interior (1)
            shader.setUniform("u_alpha", 1.0f);
            drawSubmesh(0);
            drawSubmesh(1);
        } else if (pass == 1) {
            // TRANSPARENT PASS: Windows (2)
            shader.setUniform("u_alpha", 0.2f);
            drawSubmesh(2);
        }
    }

    private void drawSubmesh(int i) {
        shader.setAttribute("a_position", vertexBuffers[i]);
        shader.setAttribute("a_uv", uvBuffers[i]);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexBuffers[i]);
        glDrawElements(GL_TRIANGLES, indexCounts[i], GL_UNSIGNED_INT, 0);
    }
}