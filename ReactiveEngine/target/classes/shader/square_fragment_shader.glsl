#version 330 core

in vec2 f_TexCoord;

uniform sampler2D textureSampler;

void main() {
    gl_FragColor = texture(textureSampler, f_TexCoord);
}
