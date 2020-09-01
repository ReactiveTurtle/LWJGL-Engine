#version 330

in vec3 vertex;
in vec2 texCoord;

out vec2 f_TexCoord;

uniform mat4 perspectiveProjection;
uniform mat4 modelMatrix;

void main() {
    gl_Position = perspectiveProjection * modelMatrix * vec4(vertex, 1);
    f_TexCoord = texCoord;
}
