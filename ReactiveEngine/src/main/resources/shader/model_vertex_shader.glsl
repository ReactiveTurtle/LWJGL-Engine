#version 330 core

in vec3 position;
in vec2 inTextureCoordinates;
in vec3 inVertexNormals;

out vec2 textureCoordinates;
out vec3 vertexNormal;

out vec4 vertex;
out vec4 modelWorldPosition;
out mat4 f_modelViewMatrix;

uniform mat4 perspectiveProjection;
uniform mat4 viewMatrix;
uniform mat4 modelMatrix;

void main() {
    gl_Position = perspectiveProjection * viewMatrix * modelMatrix * vec4(position, 1);

    modelWorldPosition = modelMatrix * vec4(position, 1);

    textureCoordinates = inTextureCoordinates;

    f_modelViewMatrix = viewMatrix * modelMatrix;

    vertex = vec4(position, 1);
    vertexNormal = inVertexNormals;
}
