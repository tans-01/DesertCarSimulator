package comp3170.ass3.sceneobjects;

import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.glBindBuffer;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.joml.Matrix4f;

import comp3170.GLBuffers;
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

    // the three submeshes in the car OBJ
    private static final String[] SUBMESHES = { "Body", "Interior", "Windows" };

    private Shader shader;
    private int texture;

    // one set of buffers per submesh
    private int[] vertexBuffers;
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
        uvBuffers = new int[n];
        indexBuffers = new int[n];
        indexCounts = new int[n];

        // create buffers for each submesh
        for (int i = 0; i < n; i++) {
            Mesh mesh = data.getMesh(SUBMESHES[i]);    // The array will hold each submesh like body, interior
            vertexBuffers[i] = GLBuffers.createBuffer(mesh.vertices); // window.
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
     // Flip from US (left-hand drive) to Australian (right-hand drive) layout
        getMatrix().scale(-1, 1, 1);

     // four wheels attached to the car as its children
        float[][] wheelPositions = {
            {  0.62f, 0.35f,  1.3f  },  // front left
            { -0.62f, 0.35f,  1.3f  },  // front right
            {  0.62f, 0.35f, -1.15f },  // back left
            { -0.62f, 0.35f, -1.15f },  // back right
        };

        for (float[] pos : wheelPositions) {
            Wheel wheel = new Wheel();
            wheel.setParent(this);
            wheel.getMatrix().translate(pos[0], pos[1], pos[2]);
           
            if (pos[0] > 0) {
                wheel.getMatrix().rotateY((float) Math.PI);   // 180 degrees
            }
        }
        
    }

    @Override
    protected void drawSelf(Matrix4f mvpMatrix) {
        shader.enable();
        shader.setUniform("u_mvpMatrix", mvpMatrix);

        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, texture);
        shader.setUniform("u_texture", 0);

        // draw each submesh
        for (int i = 0; i < SUBMESHES.length; i++) {
            shader.setAttribute("a_position", vertexBuffers[i]);
            shader.setAttribute("a_uv", uvBuffers[i]);
            
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexBuffers[i]);
            glDrawElements(GL_TRIANGLES, indexCounts[i], GL_UNSIGNED_INT, 0);
        }
    }
}