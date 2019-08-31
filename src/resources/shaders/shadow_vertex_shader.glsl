#version 150

in vec3 position;
in vec2 inTextureCoordinates;
in vec3 inVertexNormal;

uniform mat4 modelLightViewMatrix;
uniform mat4 projectionMatrix;

void main() {
    gl_Position = projectionMatrix * modelLightViewMatrix * vec4(position, 1.0f);
}
