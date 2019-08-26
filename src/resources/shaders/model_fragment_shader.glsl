#version 150

#define MAX_LIGHTS_COUNT 16

struct Material {
    vec4 ambient;
    vec4 diffuse;
    vec4 specular;
    vec4 emission;
    float reflectance;
};

struct DirectionalLight {
    vec4 direction;
    vec4 ambient;
    vec4 diffuse;
    vec4 specular;
};

struct Attenuation {
    float constant;
    float linear;
    float exponent;
};

struct PointLight {
    vec4 position;
    vec4 ambient;
    vec4 diffuse;
    vec4 specular;
    Attenuation attenuation;
};

struct SpotLight {
    vec4 position;
    vec4 ambient;
    vec4 diffuse;
    vec4 specular;
    Attenuation attenuation;
    vec3 direction;
    float cutoff;
    float exponent;
};

in vec3 colorPalette;
in vec2 textureCoordinates;
in vec3 vertexNormal;
in vec4 modelWorldPosition;

out vec4 fragColor;

uniform int type;
uniform sampler2D textureSampler;

uniform int materialExists;
uniform Material material;

uniform int directionalLightExists;
uniform DirectionalLight directionalLight;

uniform int pointLightsCount;
uniform PointLight pointLights[MAX_LIGHTS_COUNT];

uniform int spotLightsCount;
uniform SpotLight spotLights[MAX_LIGHTS_COUNT];

uniform mat3 transposedModelMatrix;
uniform vec3 cameraPosition;

vec3 normal;
vec3 cameraDirection;

vec4 getDirectionalLightColor(DirectionalLight light) {
    vec4 result;

    vec3 lightDirection = normalize(vec3(light.direction));

    result += material.emission;

    result += material.ambient * light.ambient;

    float nDotL = max(dot(normal, lightDirection), 0.0f);
    result += material.diffuse * light.diffuse * nDotL;

    float rDotVPow = max(pow(dot(reflect(-lightDirection, normal), cameraDirection), material.reflectance), 0.0f);
    result += material.specular * light.specular * rDotVPow;
    return result;
}

vec4 getPointLightColor() {
    vec4 result;
    for (int i = 0; i < pointLightsCount; i++) {
        vec4 lightDir = pointLights[i].position - modelWorldPosition;

        float distance = length(lightDir);

        vec3 lightDirection = normalize(vec3(lightDir));

        float att = 1.0f / (pointLights[i].attenuation.constant + pointLights[i].attenuation.linear * distance +
        pointLights[i].attenuation.exponent * distance * distance);

        result += material.emission;

        result += material.ambient * pointLights[i].ambient * att;

        float nDotL = max(dot(normal, lightDirection), 0.0f);
        result += material.diffuse * pointLights[i].diffuse * nDotL * att;

        float rDotVPow = max(pow(dot(reflect(-lightDirection, normal), cameraDirection), material.reflectance), 0.0f);
        result += material.specular * pointLights[i].specular * rDotVPow * att;
    }
    return result;
}

vec4 getSpotLightColor() {
    vec4 result;
    for (int i = 0; i < spotLightsCount; i++) {
        vec4 lightDir = spotLights[i].position - modelWorldPosition;

        float distance = length(lightDir);

        vec3 lightDirection = normalize(vec3(lightDir));

        float spotEffect = dot(normalize(spotLights[i].direction.xyz), -lightDirection);
        float spot = float(spotEffect > spotLights[i].cutoff);
        spotEffect = max(pow(spotEffect, spotLights[i].exponent), 0.0f);

        float att = spot * spotEffect / (spotLights[i].attenuation.constant + spotLights[i].attenuation.linear * distance +
        spotLights[i].attenuation.exponent * distance * distance);

        result += material.emission;

        result += material.ambient * spotLights[i].ambient * att;

        float nDotL = max(dot(normal, lightDirection), 0.0f);
        result += material.diffuse * spotLights[i].diffuse * nDotL * att;

        float rDotVPow = max(pow(dot(reflect(-lightDirection, normal), cameraDirection), material.reflectance), 0.0f);
        result += material.specular * spotLights[i].specular * rDotVPow * att;
    }
    return result;
}

void main() {
    if (materialExists == 0) {
        if (type == 0) {
            fragColor = vec4(colorPalette, 1.0f);
        } else if (type == 1) {
            fragColor = texture(textureSampler, textureCoordinates);
        }
    } else if (materialExists == 1) {
        normal = normalize(transposedModelMatrix * vertexNormal);
        cameraDirection = normalize(cameraPosition - vec3(modelWorldPosition));
        if (directionalLightExists == 1) {
            fragColor += getDirectionalLightColor(directionalLight);
        }
        if (pointLightsCount > 0) {
            fragColor += getPointLightColor();
        }
        if (spotLightsCount > 0) {
            fragColor += getSpotLightColor();
        }
        if (type == 0) {
            fragColor *= vec4(colorPalette, 1.0f);
        } else if (type== 1) {
            fragColor *= texture(textureSampler, textureCoordinates);
        }
    }
}
