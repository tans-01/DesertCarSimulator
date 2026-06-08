package comp3170.ass3.sceneobjects;

import comp3170.*;
import comp3170.ass3.Light;
import comp3170.ass3.TextureUtils;
import comp3170.ass3.models.Mesh;
import comp3170.ass3.models.ObjData;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import comp3170.ass3.Light;
import comp3170.ass3.sceneobjects.Scene;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.Math;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.glBindBuffer;

public class Wheel extends SceneObject {

    private static final String VERT_SHADER = "simple.vert";
    private static final String FRAG_SHADER = "simple.frag";
    private static final String TEXTURE = "car.png";   
    private static final String OBJ_FILE = "src/comp3170/ass3/models/wheel.obj";
    private static final String SUBMESH = "Wheel";    

    final Shader shader;
    final int texture;
    final int vertexBuffer;
    final int normalBuffer;
    final int uvBuffer;
    final int indexBuffer;
    final int indexCount;
    final boolean isFront;
    final boolean flipped;
    final Vector3f position;
    private float spinAngle = 0;
    
    public Wheel(Vector3f position, boolean isFront, boolean flipped) throws IOException, OpenGLException {
    	this.position = position;
        this.isFront = isFront;
        this.flipped = flipped;

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
        normalBuffer = GLBuffers.createBuffer(mesh.normals);
        uvBuffer = GLBuffers.createBuffer(mesh.uvs);
        indexBuffer = GLBuffers.createIndexBuffer(mesh.indices);
        indexCount = mesh.indices.length;
    }
    
    public void update(float spinDelta, float steerAngle) {
        spinAngle += spinDelta;             // spin accumulates over time
        
        Matrix4f matrix = getMatrix();
        matrix.identity();                  // rebuild fresh each frame
        matrix.translate(position);         // place at corner
        var spinAngleToRotateBy = spinAngle;
        if (flipped) {
            matrix.rotateY((float) Math.PI); // hubcap faces out
            // Need to flip the angle as we've rotated 180 degrees
            spinAngleToRotateBy *= -1;
        }
        if (isFront) {
            matrix.rotateY(steerAngle);     // front wheels steer
        }
        matrix.rotateX(spinAngleToRotateBy);          // all wheels spin around axle
    }
    
    @Override
    protected void drawSelf(Matrix4f mvpMatrix) {
        shader.enable();
        shader.setUniform("u_mvpMatrix", mvpMatrix);
        shader.setUniform("u_modelMatrix", getModelToWorldMatrix(new Matrix4f()));
        shader.setUniform("u_debugNormals", Scene.theScene.debugNormals);
        
        shader.setUniform("u_daytime", Scene.theScene.daytime);
        shader.setUniform("u_headlightposition", Scene.theScene.getHeadlightPosition());
        
        //light
        Light light = Scene.theScene.light;
        shader.setUniform("u_lightDirection", light.getDirection());
        shader.setUniform("u_lightColour", light.getColour());
        shader.setUniform("u_ambientColour", light.getAmbient());
        
        //spec
        Vector3f camPos = Scene.theScene.getCamera().getPosition(new Vector3f());
        shader.setUniform("u_cameraposition", camPos);
        shader.setUniform("u_shiny", false);
        
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, texture);
        shader.setUniform("u_texture", 0);
        shader.setUniform("u_alpha", 1.0f); 

        shader.setAttribute("a_position", vertexBuffer);
        shader.setAttribute("a_normal", normalBuffer);
        shader.setAttribute("a_uv", uvBuffer);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexBuffer);
        glDrawElements(GL_TRIANGLES, indexCount, GL_UNSIGNED_INT, 0);
    }
}