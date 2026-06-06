#version 410

uniform sampler2D u_texture;
uniform float u_alpha;
uniform bool u_debugNormals;

in vec3 v_normal;
in vec2 v_uv;

out vec4 o_colour;

void main() {
    if (u_debugNormals) {
        o_colour = vec4(v_normal, 1.0);
    } else {
        vec4 colour = texture(u_texture, v_uv);
        o_colour = vec4(colour.rgb, colour.a * u_alpha);
    }
}