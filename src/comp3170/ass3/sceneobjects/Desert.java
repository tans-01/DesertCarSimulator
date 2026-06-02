package comp3170.ass3.sceneobjects;

import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL20.GL_FLOAT_VEC3;

import java.io.File;
import java.io.IOException;

import org.joml.Matrix4f;

import comp3170.GLBuffers;
import comp3170.OpenGLException;
import comp3170.SceneObject;
import comp3170.Shader;

public class Desert extends SceneObject {

    private static final String VERT_SHADER = "src/comp3170/ass3/shaders/simple.vert";
    private static final String FRAG_SHADER = "src/comp3170/ass3/shaders/simple.frag";

    private static final float SIZE = 100f; // 100x100 metres

    private Shader shader;
    private int vertexBuffer;
    private int indexBuffer;
    private int indexCount;

    public Desert() throws IOException, OpenGLException {
        shader = new Shader(new File(VERT_SHADER), new File(FRAG_SHADER));

        float h = SIZE / 2f; // 50
        float[] vertices = {
            -h, 0, -h,   // back left
             h, 0, -h,   // back right
             h, 0,  h,   // front right
            -h, 0,  h,   // front left
        };

        int[] indices = {
            0, 1, 2,
            0, 2, 3,
        };

        vertexBuffer = GLBuffers.createBuffer(vertices, GL_FLOAT_VEC3);
        indexBuffer = GLBuffers.createIndexBuffer(indices);
        indexCount = indices.length;
    }

    @Override
    protected void drawSelf(Matrix4f mvpMatrix) {
        shader.enable();
        shader.setAttribute("a_position", vertexBuffer);
        shader.setUniform("u_mvpMatrix", mvpMatrix);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexBuffer);
        glDrawElements(GL_TRIANGLES, indexCount, GL_UNSIGNED_INT, 0);
    }
}