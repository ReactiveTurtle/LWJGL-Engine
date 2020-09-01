#version 330 core

in vec2 f_TexCoord;

uniform sampler2D textureSampler;
uniform float alpha;

out vec4 fragColor;

void main() {
    fragColor = vec4(texture(textureSampler, f_TexCoord).xyz, alpha);
}
