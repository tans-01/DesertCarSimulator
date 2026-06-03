#version 410

in vec2 v_uv;

uniform sampler2D u_texture;
uniform float u_alpha;

out vec4 o_colour;

void main() {
    vec4 colour = texture(u_texture, v_uv);
    o_colour = vec4(colour.rgb, colour.a * u_alpha);
    
}