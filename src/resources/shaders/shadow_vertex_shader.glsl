#version 150

in vec3 position;
in vec2 inTextureCoordinates;
in vec3 inVertexNormal;

uniform mat4 modelViewProjectionMatrix;

void main() {
    gl_Position = modelViewProjectionMatrix * vec4(position, 1.0f);
}
