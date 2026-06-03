#version 410

uniform sampler2D u_texture;
uniform float u_time;

in vec2 v_texcoord;	// UV 

layout(location = 0) out vec4 o_colour;

void main() {
	vec2 uv = v_texcoord;
	float wave1 = sin(uv.y * 10.0 + u_time * 2.0);
    float wave2 = sin(uv.x * 6.0 - u_time * 1.5);
	uv.x += wave1 * 0.003;
    uv.y += wave2 * 0.002;
    uv.y += sin(u_time * 0.5 + uv.x * 3.0) * 0.0015;
    o_colour = texture(u_texture, uv);
}

