#version 330

in vec3 position;
in vec2 inTextureCoordinates;
in vec3 inVertexNormals;

out vec2 textureCoordinates;
out vec3 vertexNormal;

out vec4 vertex;
out vec4 modelWorldPosition;
out vec4 lightViewPosition;
out mat4 f_modelViewMatrix;

uniform mat4 perspectiveProjection;
uniform mat4 viewMatrix;
uniform mat4 modelMatrix;
uniform mat4 modelLightViewProjectionMatrix;

void main() {
    gl_Position = perspectiveProjection * viewMatrix * modelMatrix * vec4(position, 1);

    modelWorldPosition = modelMatrix * vec4(position, 1);

    textureCoordinates = inTextureCoordinates;

    lightViewPosition = modelLightViewProjectionMatrix * vec4(position, 1);

    f_modelViewMatrix = viewMatrix * modelMatrix;

    vertex = vec4(position, 1);
    vertexNormal = inVertexNormals;
}
