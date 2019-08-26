#version 150

in vec3 position;
in vec3 inColorPalette;
in vec2 inTextureCoordinates;
in vec3 inVertexNormal;

out vec3 colorPalette;
out vec2 textureCoordinates;
out vec3 vertexNormal;
//For lightDirection
out vec4 modelWorldPosition;

uniform mat4 projection;
uniform mat4 viewMatrix;
uniform mat4 modelMatrix;


void main() {
    gl_Position = projection * viewMatrix * modelMatrix * vec4(position, 1);

    modelWorldPosition = modelMatrix * vec4(position, 1);

    textureCoordinates = inTextureCoordinates;
    colorPalette = inColorPalette;

    vertexNormal = inVertexNormal;
}
