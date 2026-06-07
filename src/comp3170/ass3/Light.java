package comp3170.ass3;

import org.joml.Vector3f;

public class Light {

    // direction TO the sun (world space).
    private Vector3f direction = new Vector3f(0.3f, 1.0f, 0.3f).normalize(); //sun

    private Vector3f colour = new Vector3f(1.0f, 1.0f, 1.0f);    // white sun
    private Vector3f ambient = new Vector3f(0.25f, 0.25f, 0.25f); // dim light

    public Vector3f getDirection() {
        return direction;
    }

    public Vector3f getColour() {
        return colour;
    }

    public Vector3f getAmbient() {
        return ambient;
    }
    public void sunRotaion(float angle) {
    	direction.rotateZ(angle);
    }
}