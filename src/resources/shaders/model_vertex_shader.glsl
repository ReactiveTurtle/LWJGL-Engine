#version 150

in vec3 position;
in vec3 inColorPalette;
in vec2 inTextureCoordinates;
in vec3 inVertexNormal;

out vec3 colorPalette;
out vec2 textureCoordinates;
out vec3 vertexNormal;

out vec4 modelWorldPosition;
out vec4 lightViewPosition;

uniform mat4 perspectiveProjection;
uniform mat4 viewMatrix;
uniform mat4 modelMatrix;
uniform mat4 modelLightViewProjectionMatrix;

void main() {
    gl_Position = perspectiveProjection * viewMatrix * modelMatrix * vec4(position, 1);

    modelWorldPosition = modelMatrix * vec4(position, 1);

    textureCoordinates = inTextureCoordinates;
    colorPalette = inColorPalette;

    lightViewPosition = modelLightViewProjectionMatrix * vec4(position, 1);

    vertexNormal = inVertexNormal;
}
