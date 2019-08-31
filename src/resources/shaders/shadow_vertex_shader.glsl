#version 150

in vec3 position;
in vec2 inTextureCoordinates;
in vec3 inVertexNormal;

uniform mat4 lightViewMatrix;
uniform mat4 projectionMatrix;
uniform mat4 modelMatrix;

void main() {
    gl_Position = projectionMatrix * lightViewMatrix * modelMatrix * vec4(position, 1.0f);
}
