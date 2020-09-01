#version 330 core

in vec3 vertex;

uniform mat4 projection;
uniform mat4 view;
uniform mat4 model;

void main() {
    gl_Position = projection * view * model * vec4(vertex, 1);
}
