#version 410

in vec4 a_position;			// MODEL
uniform mat4 u_mvpMatrix;	// MODEL -> NDC

out vec4 v_view;		// MODEL == WORLD

void main() {
	v_view = vec4(a_position.xyz, 0);
    gl_Position = u_mvpMatrix * a_position;
}

