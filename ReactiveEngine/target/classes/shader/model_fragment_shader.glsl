#version 330 core

#define MAX_LIGHTS_COUNT 16

struct Material {
    int textureExists;
    sampler2D textureSampler;
    int normalMapTextureExists;
    sampler2D normalMapTextureSampler;

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
    mat4 mvpMatrix;
    int shadowMapTextureExists;
    sampler2D shadowMapTextureSampler;
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

in vec3 fVertex;
vec4 fVertex4;

in vec2 fTextureCoordinate;
in vec3 fVertexNormal;

out vec4 fragColor;

uniform int materialExists;
uniform Material material;

uniform int directionalLightExists;
uniform DirectionalLight directionalLight;

uniform int pointLightsCount;
uniform PointLight pointLights[MAX_LIGHTS_COUNT];

uniform int spotLightsCount;
uniform SpotLight spotLights[MAX_LIGHTS_COUNT];

uniform mat4 fProjectionMatrix;
uniform mat4 fViewMatrix;
uniform mat4 fModelMatrix;

vec3 normal;
vec3 cameraDirection;

vec4 modelVertexPosition;

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
    vec3 lightDirection = normalize(directionalLight.direction.xyz);

    vec4 color = material.emission;

    color = max(material.ambient * directionalLight.ambient, color);

    float nDotL = max(dot(normal, lightDirection), 0.0f);
    color = max(color, material.diffuse * directionalLight.diffuse * nDotL);

    float rDotVPow = max(pow(dot(reflect(-lightDirection, normal), cameraDirection), material.reflectance), 0.0f);
    color = max(color, material.specular * directionalLight.specular * rDotVPow);

    color = clamp(color * calcShadow(directionalLight.shadowMapTextureSampler, directionalLight.mvpMatrix * fVertex4), vec4(0), vec4(1));
    return color;
}

vec4 getPointLightColor() {
    vec4 result;
    for (int i = 0; i < pointLightsCount; i++) {
        vec4 lightDir = pointLights[i].position - modelVertexPosition;

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
    return clamp(result, vec4(0), vec4(1));
}

vec4 getSpotLightColor()
{
    vec4 result;
    for (int i = 0; i < spotLightsCount; i++)
    {
        vec4 lightDir = spotLights[i].position - modelVertexPosition;

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
    return clamp(result, vec4(0), vec4(1));
}

vec3 calcNormal(Material material, vec3 normal, vec2 textureCoordinate, mat4 modelViewMatrix)
{
    if (material.normalMapTextureExists == 0) {
        return normal;
    }
    vec3 newNormal = texture2D(material.normalMapTextureSampler, textureCoordinate).rgb;
    newNormal = normalize(newNormal * 2.0 - 1);
    newNormal = normalize(modelViewMatrix * vec4(newNormal, 0.0)).xyz;
    return newNormal;
}

void main() {
    if (materialExists == 0) {
        fragColor = vec4(0, 0.8, 0, 1);
    } else if (materialExists == 1) {
        fVertex4 = vec4(fVertex, 1);
        mat4 viewModelMatrix = fViewMatrix * fModelMatrix;
        mat3 normalModelMatrix = transpose(mat3(inverse(fModelMatrix)));
        modelVertexPosition = fModelMatrix * vec4(fVertex4);
        vec3 viewPosition = vec3(fViewMatrix[3][0], fViewMatrix[3][1], fViewMatrix[3][2]);

        cameraDirection = normalize(viewPosition - vec3(modelVertexPosition));
        normal = calcNormal(material, normalize(normalModelMatrix * fVertexNormal), fTextureCoordinate, viewModelMatrix);
        fragColor = getDirectionalLightColor();
        fragColor = max(fragColor, getPointLightColor());
        fragColor = max(fragColor, getSpotLightColor());
        fragColor *= texture(material.textureSampler, fTextureCoordinate);
    }
}
