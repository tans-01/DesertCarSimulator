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

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL20.GL_FLOAT_VEC2;   // for the UV buffer
import comp3170.TextureLibrary;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T; // this is for the repeating texture of the sand.
import static org.lwjgl.opengl.GL11.GL_REPEAT;
import static org.lwjgl.opengl.GL11.glTexParameteri;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER; //mipmaps
import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL30.GL_LINEAR_MIPMAP_LINEAR;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;

import comp3170.ShaderLibrary;

public class Desert extends SceneObject {

    private static final String VERT_SHADER = "simple.vert";
    private static final String FRAG_SHADER = "simple.frag";
    private static final String TEXTURE = "sand.jpg";
    
    private static final float SIZE = 100f; // 100x100 meters

    private Shader shader;
    private int vertexBuffer;
    private int indexBuffer;
    private int indexCount;
    private int uvBuffer;
    private int texture;

    public Desert() throws IOException, OpenGLException {
    	shader = ShaderLibrary.instance.compileShader(VERT_SHADER, FRAG_SHADER);
        texture = TextureLibrary.instance.loadTexture(TEXTURE);
        
      //binding the texture first.
        glBindTexture(GL_TEXTURE_2D, texture);
      // wrap modes (tiling)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT); 
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
      // trilinear filtering
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        
     // build mipmaps
        glGenerateMipmap(GL_TEXTURE_2D);
        
        float h = SIZE / 2f; // 50
        float[] vertices = {
            -h, 0, -h,   // back left
             h, 0, -h,   // back right
             h, 0,  h,   // front right
            -h, 0,  h,   // front left
        };
        
        float[] uvs = {    // i have set this a float you can also keep this a vector but
                0,    0,   // dont forget to change the uvBuffer = GLBuffers.createBuffer(uvs, GL_FLOAT_VEC2);
                SIZE, 0,   // right not it specifies the type of the uvs.
                SIZE, SIZE,
                0,    SIZE,
            };

        int[] indices = {
            0, 1, 2,
            0, 2, 3,
        };

        vertexBuffer = GLBuffers.createBuffer(vertices, GL_FLOAT_VEC3);
        uvBuffer = GLBuffers.createBuffer(uvs, GL_FLOAT_VEC2);
        indexBuffer = GLBuffers.createIndexBuffer(indices);
        indexCount = indices.length;
    }

    @Override
    protected void drawSelf(Matrix4f mvpMatrix) {
        shader.enable();
        
        shader.setAttribute("a_position", vertexBuffer);
        shader.setAttribute("a_uv", uvBuffer);
        shader.setUniform("u_mvpMatrix", mvpMatrix);
        
     // bind the texture to texture unit 0
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, texture);
        shader.setUniform("u_texture", 0);
        
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexBuffer);
        glDrawElements(GL_TRIANGLES, indexCount, GL_UNSIGNED_INT, 0);
    }
}