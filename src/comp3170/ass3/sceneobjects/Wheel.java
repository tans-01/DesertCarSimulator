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

public class Wheel extends SceneObject {

    private static final String VERT_SHADER = "simple.vert";
    private static final String FRAG_SHADER = "simple.frag";
    private static final String TEXTURE = "car.png";   
    private static final String OBJ_FILE = "src/comp3170/ass3/models/wheel.obj";
    private static final String SUBMESH = "Wheel";    

    private Shader shader;
    private int texture;
    private int vertexBuffer;
    private int uvBuffer;
    private int indexBuffer;
    private int indexCount;

    public Wheel() throws IOException, OpenGLException {
        shader = ShaderLibrary.instance.compileShader(VERT_SHADER, FRAG_SHADER);

        texture = TextureLibrary.instance.loadTexture(TEXTURE);
        TextureUtils.setupTexture(texture);

        ObjData data;
        try {
            data = new ObjData(OBJ_FILE);
        } catch (FileNotFoundException e) {
            throw new IOException(e);
        }

        Mesh mesh = data.getMesh(SUBMESH);
        vertexBuffer = GLBuffers.createBuffer(mesh.vertices);
        uvBuffer = GLBuffers.createBuffer(mesh.uvs);
        indexBuffer = GLBuffers.createIndexBuffer(mesh.indices);
        indexCount = mesh.indices.length;
    }

    @Override
    protected void drawSelf(Matrix4f mvpMatrix) {
        shader.enable();
        shader.setUniform("u_mvpMatrix", mvpMatrix);

        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, texture);
        shader.setUniform("u_texture", 0);

        shader.setAttribute("a_position", vertexBuffer);
        shader.setAttribute("a_uv", uvBuffer);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexBuffer);
        glDrawElements(GL_TRIANGLES, indexCount, GL_UNSIGNED_INT, 0);
    }
}