#version 410

uniform samplerCube u_cubemap;
uniform mat4 u_cameraMatrix;

in vec4 v_view;	// WORLD

layout(location = 0) out vec4 o_colour;

const vec3 GAMMA = vec3(2.2);
const float INFINITY = 1. / 0.;

void main() {
	vec4 p = u_cameraMatrix[3];	// camera position
	vec4 v = normalize(v_view);	// view vector

	vec3 tex = texture(u_cubemap, v.xyz).xyz;
    vec3 colour = pow(tex, GAMMA);

	colour = pow(colour,1./GAMMA);	// gamma correction
	o_colour = vec4(colour, 1);
}


