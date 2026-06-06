#version 410

uniform mat4 u_mvpMatrix;	// MODEL -> NDC

in vec4 a_position;			// MODEL

out vec4 v_view;		// MODEL == WORLD

void main() {
	v_view = vec4(a_position.xyz, 0);
    gl_Position = u_mvpMatrix * a_position;
}

