#version 410

uniform mat4 u_mvpMatrix;
uniform mat4 u_modelMatrix;

in vec4 a_position;
in vec4 a_normal;
in vec2 a_uv;

out vec3 v_normal;
out vec2 v_uv;
out vec3 v_worldpos;

void main() {
    gl_Position = u_mvpMatrix * a_position;
    v_normal = normalize(mat3(u_modelMatrix) * a_normal.xyz);
     v_worldpos = (u_modelMatrix * a_position).xyz;   //vertex position in world space for specular
    v_uv = a_uv;
}