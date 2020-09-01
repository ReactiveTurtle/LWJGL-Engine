#version 330 core

#define MAX_LIGHTS_COUNT 16

struct Material {
    int hasNormalMap;
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
    mat4 modelLightViewProjectionMatrix;
    sampler2D shadowMap;
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

in vec2 textureCoordinates;
in vec4 vertex;
in vec3 vertexNormal;
in vec4 modelWorldPosition;

in mat4 f_modelViewMatrix;

out vec4 fragColor;

uniform sampler2D textureSampler;
uniform sampler2D normalMap;

uniform mat3 normalMatrix;

uniform int materialExists;
uniform Material material;

uniform int directionalLightsCount;
uniform DirectionalLight directionalLights[2];

uniform int pointLightsCount;
uniform PointLight pointLights[MAX_LIGHTS_COUNT];

uniform int spotLightsCount;
uniform SpotLight spotLights[MAX_LIGHTS_COUNT];

uniform vec3 cameraPosition;

vec3 normal;
vec3 cameraDirection;

float calcShadow(sampler2D shadowMap, vec4 position) {
    vec3 projCoords = position.xyz * 0.5 + 0.5;

    if (projCoords.z > 1.0) {
        return 1;
    }
    float bias = 0.0025;
    float shadowFactor = 0.0;
    vec2 inc = 1.0f / textureSize(shadowMap, 0);
    for (int row = -1; row <= 1; ++row) {
        for (int col = -1; col <= 1; ++col) {
            float textDepth = texture(shadowMap, projCoords.xy + vec2(row, col) * inc).r;
            shadowFactor += projCoords.z - bias > textDepth ? 0.5 : 0.0;
        }
    }
    shadowFactor /= 9.0;

    if (projCoords.z - bias < texture(shadowMap, projCoords.xy).r) {
        shadowFactor = 0;
    }

    return 1 - shadowFactor;
}

vec4 getDirectionalLightColor() {
    vec4 result = vec4(0);
    for (int i = 0; i < directionalLightsCount; i++) {

        vec3 lightDirection = normalize(vec3(directionalLights[i].direction));

        vec4 color = material.emission;

        color += material.ambient * directionalLights[i].ambient;

        float nDotL = max(dot(normal, lightDirection), 0.0f);
        color += material.diffuse * directionalLights[i].diffuse * nDotL;

        float rDotVPow = max(pow(dot(reflect(-lightDirection, normal), cameraDirection), material.reflectance), 0.0f);
        color += material.specular * directionalLights[i].specular * rDotVPow;

        color += clamp(color * calcShadow(directionalLights[i].shadowMap, directionalLights[i].modelLightViewProjectionMatrix * vertex), 0, 1);
        result = max(result, color);
    }
    return result;
}

vec4 getPointLightColor() {
    vec4 result = vec4(0);
    for (int i = 0; i < pointLightsCount; i++) {
        vec4 lightDir = pointLights[i].position - modelWorldPosition;

        float distance = length(lightDir);

        vec3 lightDirection = normalize(vec3(lightDir));

        float att = 1.0f / (pointLights[i].attenuation.constant + pointLights[i].attenuation.linear * distance +
        pointLights[i].attenuation.exponent * distance * distance);

        vec4 color = material.emission;

        color += material.ambient * pointLights[i].ambient * att;

        float nDotL = max(dot(normal, lightDirection), 0.0f);
        color += material.diffuse * pointLights[i].diffuse * nDotL * att;

        float rDotVPow = max(pow(dot(reflect(-lightDirection, normal), cameraDirection), material.reflectance), 0.0f);
        color += material.specular * pointLights[i].specular * rDotVPow * att;
        result = max(result, color);
    }
    return max(result, 0);
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

        vec4 color = material.emission;

        color += material.ambient * spotLights[i].ambient * att;

        float nDotL = max(dot(normal, lightDirection), 0.0f);
        color += material.diffuse * spotLights[i].diffuse * nDotL * att;

        float rDotVPow = max(pow(dot(reflect(-lightDirection, normal), cameraDirection), material.reflectance), 0.0f);
        color += material.specular * spotLights[i].specular * rDotVPow * att;
        result = max(result, color);
    }
    return result;
}

vec3 calcNormal(Material material, vec3 normal, vec2 text_coord, mat4 modelViewMatrix)
{
    vec3 newNormal = normal;
    if (material.hasNormalMap == 1) {
        newNormal = texture2D(normalMap, text_coord).rgb;
        newNormal = normalize(newNormal * 2 - 1);
        newNormal = normalize(modelViewMatrix * vec4(newNormal, 0.0)).xyz;
    }
    return newNormal;
}

void main() {
    if (materialExists == 0) {
        fragColor = texture(textureSampler, textureCoordinates);
    } else if (materialExists == 1) {
        normal = calcNormal(material, normalize(normalMatrix * vertexNormal), textureCoordinates, f_modelViewMatrix);
        cameraDirection = normalize(cameraPosition - vec3(modelWorldPosition));
        if (directionalLightsCount > 0) {
            fragColor = max(fragColor, getDirectionalLightColor());
        }
        if (pointLightsCount > 0) {
            fragColor = max(fragColor, getPointLightColor());
        }
        if (spotLightsCount > 0) {
            fragColor = max(fragColor, getSpotLightColor());
        }
        fragColor *= texture(textureSampler, textureCoordinates);
    }
}
