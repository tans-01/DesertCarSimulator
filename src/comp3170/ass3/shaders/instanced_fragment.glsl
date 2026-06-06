#version 410

uniform sampler2D u_texture;
uniform float u_alpha;

in vec3 v_normal;
in vec2 v_uv;

out vec4 o_colour;

void main() {
    vec4 c = texture(u_texture, v_uv);
    o_colour = vec4(c.rgb, c.a * u_alpha);
}

