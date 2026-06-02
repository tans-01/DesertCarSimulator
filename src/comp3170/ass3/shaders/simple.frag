#version 410

in vec2 v_uv;

uniform sampler2D u_texture;

out vec4 o_colour;

void main() {
    o_colour = texture(u_texture, v_uv);
}