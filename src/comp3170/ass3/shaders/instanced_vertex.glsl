#version 410

uniform mat4 u_mvpMatrix;

in mat4 a_modelMatrix;
in vec4 a_position;
in vec4 a_normal;
in vec2 a_uv;

out vec3 v_normal;
out vec2 v_uv;

void main() {
    gl_Position = u_mvpMatrix * a_modelMatrix * a_position;
    v_normal = normalize(mat3(a_modelMatrix) * a_normal.xyz);
    v_uv = a_uv;
}

