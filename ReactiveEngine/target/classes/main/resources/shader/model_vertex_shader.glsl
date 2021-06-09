#version 330 core

in vec3 vVertex;
in vec2 vTextureCoordinate;
in vec3 vVertexNormal;

out vec3 fVertex;
out vec2 fTextureCoordinate;
out vec3 fVertexNormal;

uniform mat4 vProjectionMatrix;
uniform mat4 vViewMatrix;
uniform mat4 vModelMatrix;

void main() {
    gl_Position = vProjectionMatrix * vViewMatrix * vModelMatrix * vec4(vVertex, 1);

    fVertex = vVertex;
    fTextureCoordinate = vTextureCoordinate;
    fVertexNormal = vVertexNormal;
}
