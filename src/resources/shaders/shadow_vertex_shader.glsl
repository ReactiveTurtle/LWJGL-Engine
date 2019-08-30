#version 150

in vec3 position;
in vec2 inTextureCoordinates;
in vec3 inVertexNormal;

uniform mat4 modelLightViewProjectionMatrix;

void main() {
    gl_Position = modelLightViewProjectionMatrix * vec4(position, 1.0f);
}
