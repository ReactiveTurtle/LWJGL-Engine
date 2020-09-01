#version 330 core

in vec3 vertex;
in vec2 texCoord;

out vec2 f_TexCoord;

uniform mat4 perspectiveProjection;
uniform mat4 viewMatrix;
uniform mat4 modelMatrix;

void main() {
    gl_Position = perspectiveProjection * viewMatrix * modelMatrix * vec4(vertex, 1);
    f_TexCoord = texCoord;
}
