package ru.reactiveturtle.game.engine.shader;

import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import ru.reactiveturtle.game.engine.base.GameContext;
import ru.reactiveturtle.game.engine.base.Shader;
import ru.reactiveturtle.game.engine.camera.Camera;
import ru.reactiveturtle.game.engine.light.DirectionalLight;
import ru.reactiveturtle.game.engine.light.Light;
import ru.reactiveturtle.game.engine.light.PointLight;
import ru.reactiveturtle.game.engine.light.SpotLight;
import ru.reactiveturtle.game.engine.material.Material;
import ru.reactiveturtle.game.engine.model.mesh.Mesh;

public class TextureShader extends Shader {
    private int textureSamplerLocation;
    private int normalMapLocation;
    private int shadowMapLocation;

    private int normalMatrixLocation;

    private int projectionLocation;
    private int viewMatrixLocation;
    private int modelMatrixLocation;

    private int materialExistsLocation;
    private int materialHasNormalMapLocation;
    private int materialAmbientLocation;
    private int materialDiffuseLocation;
    private int materialSpecularLocation;
    private int materialEmissionLocation;
    private int materialReflectanceLocation;

    private int directionalLightExistsLocation;
    private int directionalLightDirectionLocation;
    private int directionalLightAmbientLocation;
    private int directionalLightDiffuseLocation;
    private int directionalLightSpecularLocation;

    private int pointLightsCountLocation;
    private int[] pointLightPositionLocations = new int[Light.MAX_LIGHTS_COUNT];
    private int[] pointLightAmbientLocations = new int[Light.MAX_LIGHTS_COUNT];
    private int[] pointLightDiffuseLocations = new int[Light.MAX_LIGHTS_COUNT];
    private int[] pointLightSpecularLocations = new int[Light.MAX_LIGHTS_COUNT];
    private int[] pointLightAttenuationConstantLocations = new int[Light.MAX_LIGHTS_COUNT];
    private int[] pointLightAttenuationLinearLocations = new int[Light.MAX_LIGHTS_COUNT];
    private int[] pointLightAttenuationExponentLocations = new int[Light.MAX_LIGHTS_COUNT];

    private int spotLightsCountLocation;
    private int[] spotLightPositionLocations = new int[Light.MAX_LIGHTS_COUNT];
    private int[] spotLightAmbientLocations = new int[Light.MAX_LIGHTS_COUNT];
    private int[] spotLightDiffuseLocations = new int[Light.MAX_LIGHTS_COUNT];
    private int[] spotLightSpecularLocations = new int[Light.MAX_LIGHTS_COUNT];
    private int[] spotLightAttenuationConstantLocations = new int[Light.MAX_LIGHTS_COUNT];
    private int[] spotLightAttenuationLinearLocations = new int[Light.MAX_LIGHTS_COUNT];
    private int[] spotLightAttenuationExponentLocations = new int[Light.MAX_LIGHTS_COUNT];
    private int[] spotLightDirectionLocations = new int[Light.MAX_LIGHTS_COUNT];
    private int[] spotLightCutoffLocations = new int[Light.MAX_LIGHTS_COUNT];
    private int[] spotLightExponentLocations = new int[Light.MAX_LIGHTS_COUNT];

    private int cameraPositionLocation;

    private int modelLightViewProjectionMatrixLocation;

    public TextureShader() {
        super(MODEL_VERTEX_SHADER, MODEL_FRAGMENT_SHADER);
        create();
    }

    @Override
    public void bindAllAttributes() {
        super.bindAttribute(0, "position");
        super.bindAttribute(2, "inTextureCoordinates");
        super.bindAttribute(3, "inVertexNormals");
    }

    @Override
    public void getAllUniforms() {
        textureSamplerLocation = super.getUniform("textureSampler");
        normalMapLocation = super.getUniform("normalMap");
        shadowMapLocation = super.getUniform("shadowMap");

        normalMatrixLocation = super.getUniform("normalMatrix");
        modelLightViewProjectionMatrixLocation = super.getUniform("modelLightViewProjectionMatrix");

        projectionLocation = super.getUniform("perspectiveProjection");
        viewMatrixLocation = super.getUniform("viewMatrix");
        modelMatrixLocation = super.getUniform("modelMatrix");

        materialExistsLocation = super.getUniform("materialExists");
        materialHasNormalMapLocation = super.getUniform("material.hasNormalMap");
        materialAmbientLocation = super.getUniform("material.ambient");
        materialDiffuseLocation = super.getUniform("material.diffuse");
        materialSpecularLocation = super.getUniform("material.specular");
        materialEmissionLocation = super.getUniform("material.emission");
        materialReflectanceLocation = super.getUniform("material.reflectance");

        directionalLightExistsLocation = super.getUniform("directionalLightExists");
        directionalLightDirectionLocation = super.getUniform("directionalLight.direction");
        directionalLightAmbientLocation = super.getUniform("directionalLight.ambient");
        directionalLightDiffuseLocation = super.getUniform("directionalLight.diffuse");
        directionalLightSpecularLocation = super.getUniform("directionalLight.specular");

        pointLightsCountLocation = super.getUniform("pointLightsCount");
        for (int i = 0; i < Light.MAX_LIGHTS_COUNT; i++) {
            pointLightPositionLocations[i] = super.getUniform("pointLights[" + i + "].position");
            pointLightAmbientLocations[i] = super.getUniform("pointLights[" + i + "].ambient");
            pointLightDiffuseLocations[i] = super.getUniform("pointLights[" + i + "].diffuse");
            pointLightSpecularLocations[i] = super.getUniform("pointLights[" + i + "].specular");
            pointLightAttenuationConstantLocations[i] = super.getUniform("pointLights[" + i + "].attenuation.constant");
            pointLightAttenuationLinearLocations[i] = super.getUniform("pointLights[" + i + "].attenuation.linear");
            pointLightAttenuationExponentLocations[i] = super.getUniform("pointLights[" + i + "].attenuation.exponent");
        }

        spotLightsCountLocation = super.getUniform("spotLightsCount");
        for (int i = 0; i < Light.MAX_LIGHTS_COUNT; i++) {
            spotLightPositionLocations[i] = super.getUniform("spotLights[" + i + "].position");
            spotLightAmbientLocations[i] = super.getUniform("spotLights[" + i + "].ambient");
            spotLightDiffuseLocations[i] = super.getUniform("spotLights[" + i + "].diffuse");
            spotLightSpecularLocations[i] = super.getUniform("spotLights[" + i + "].specular");
            spotLightAttenuationConstantLocations[i] = super.getUniform("spotLights[" + i + "].attenuation.constant");
            spotLightAttenuationLinearLocations[i] = super.getUniform("spotLights[" + i + "].attenuation.linear");
            spotLightAttenuationExponentLocations[i] = super.getUniform("spotLights[" + i + "].attenuation.exponent");
            spotLightDirectionLocations[i] = super.getUniform("spotLights[" + i + "].direction");
            spotLightCutoffLocations[i] = super.getUniform("spotLights[" + i + "].cutoff");
            spotLightExponentLocations[i] = super.getUniform("spotLights[" + i + "].exponent");
        }

        cameraPositionLocation = super.getUniform("cameraPosition");
    }

    @Override
    public void load(Matrix4f modelMatrix, Mesh mesh) {
        Material material = mesh.getMaterial();

        super.loadIntUniform(textureSamplerLocation, 0);
        super.loadIntUniform(normalMapLocation, 1);

        if (GameContext.camera != null) {
            super.loadMatrix4fUniform(projectionLocation, GameContext.camera.getProjectionMatrix());
            super.loadMatrix4fUniform(viewMatrixLocation, GameContext.camera.getViewMatrix());
        }

        super.loadMatrix4fUniform(modelMatrixLocation, modelMatrix);

        super.loadIntUniform(materialExistsLocation, material == null ? 0 : 1);

        if (material != null) {

            Vector3f translation = new Vector3f();
            modelMatrix.getTranslation(translation);

            DirectionalLight directionalLight = Light.getDirectionalLight(GameContext.lights);

            PointLight[] nearestPointLights = Light.getNearestPointLights(GameContext.lights, translation);

            SpotLight[] nearestSpotLights = Light.getNearestSpotLights(GameContext.lights, translation);

            if (directionalLight != null || nearestPointLights.length > 0 || nearestSpotLights.length > 0) {
                setupMaterial(material);

                super.loadIntUniform(directionalLightExistsLocation, directionalLight == null ? 0 : 1);
                if (directionalLight != null) {
                    if (directionalLight.getShadowMap() != null) {
                        super.loadIntUniform(shadowMapLocation, 2);
                    }
                    setupDirectionalLight(modelMatrix, directionalLight);
                }

                super.loadIntUniform(pointLightsCountLocation, nearestPointLights.length);
                if (nearestPointLights.length > 0) {
                    for (int i = 0; i < nearestPointLights.length; i++) {
                        setupPointLights(nearestPointLights[i], i);
                    }
                }

                super.loadIntUniform(spotLightsCountLocation, nearestSpotLights.length);
                if (nearestSpotLights.length > 0) {
                    for (int i = 0; i < nearestSpotLights.length; i++) {
                        setupSpotLights(nearestSpotLights[i], i);
                    }
                }
                super.loadMatrix3fUniform(normalMatrixLocation, new Matrix3f(new Matrix4f().set(modelMatrix).invert()).transpose());
                super.loadVector3fUniform(cameraPositionLocation, GameContext.camera.getPosition());
            } else {
                super.loadIntUniform(materialExistsLocation, 0);
            }
        }
    }

    private void setupMaterial(Material material) {
        super.loadIntUniform(materialHasNormalMapLocation, material.getNormalMap() == null ? 0 : 1);
        super.loadVector4fUniform(materialAmbientLocation, new Vector4f(material.getAmbient(), 1));
        super.loadVector4fUniform(materialDiffuseLocation, new Vector4f(material.getDiffuse(), 1));
        super.loadVector4fUniform(materialSpecularLocation, new Vector4f(material.getSpecular(), 1));
        super.loadVector4fUniform(materialEmissionLocation, new Vector4f(material.getEmission(), 1));
        super.loadFloatUniform(materialReflectanceLocation, material.getReflectance());
    }

    private void setupDirectionalLight(Matrix4f modelMatrix, DirectionalLight directionalLight) {
        super.loadVector4fUniform(directionalLightDirectionLocation, new Vector4f(directionalLight.getDirection(), 0));
        super.loadVector4fUniform(directionalLightAmbientLocation, new Vector4f(directionalLight.getAmbient(), 1));
        super.loadVector4fUniform(directionalLightDiffuseLocation, new Vector4f(directionalLight.getDiffuse(), 1));
        super.loadVector4fUniform(directionalLightSpecularLocation, new Vector4f(directionalLight.getSpecular(), 1));

        Matrix4f lightViewMatrix = new Matrix4f().identity().lookAt(directionalLight.getDirection(), new Vector3f(0, 0, 0), new Vector3f(0, 1, 0));
        super.loadMatrix4fUniform(modelLightViewProjectionMatrixLocation,
                Camera.getOrtho()
                        .mul(lightViewMatrix)
                        .mul(GameContext.camera.getFlatTranslationMatrix())
                        .mul(modelMatrix));
    }

    private void setupPointLights(PointLight pointLight, int index) {
        super.loadVector4fUniform(pointLightPositionLocations[index], new Vector4f(pointLight.getPosition(), 1));
        super.loadVector4fUniform(pointLightAmbientLocations[index], new Vector4f(pointLight.getAmbient(), 1));
        super.loadVector4fUniform(pointLightDiffuseLocations[index], new Vector4f(pointLight.getDiffuse(), 1));
        super.loadVector4fUniform(pointLightSpecularLocations[index], new Vector4f(pointLight.getSpecular(), 1));

        PointLight.Attenuation attenuation = pointLight.getAttenuation();
        super.loadFloatUniform(pointLightAttenuationConstantLocations[index], attenuation.constant);
        super.loadFloatUniform(pointLightAttenuationLinearLocations[index], attenuation.linear);
        super.loadFloatUniform(pointLightAttenuationExponentLocations[index], attenuation.exponent);
    }

    private void setupSpotLights(SpotLight spotLight, int index) {
        super.loadVector4fUniform(spotLightPositionLocations[index], new Vector4f(spotLight.getPosition(), 1));
        super.loadVector4fUniform(spotLightAmbientLocations[index], new Vector4f(spotLight.getAmbient(), 1));
        super.loadVector4fUniform(spotLightDiffuseLocations[index], new Vector4f(spotLight.getDiffuse(), 1));
        super.loadVector4fUniform(spotLightSpecularLocations[index], new Vector4f(spotLight.getSpecular(), 1));

        PointLight.Attenuation attenuation = spotLight.getAttenuation();
        super.loadFloatUniform(spotLightAttenuationConstantLocations[index], attenuation.constant);
        super.loadFloatUniform(spotLightAttenuationLinearLocations[index], attenuation.linear);
        super.loadFloatUniform(spotLightAttenuationExponentLocations[index], attenuation.exponent);

        super.loadVector3fUniform(spotLightDirectionLocations[index], spotLight.getDirection());
        super.loadFloatUniform(spotLightCutoffLocations[index], (float) Math.cos(Math.toRadians(spotLight.getCutoff())));
        super.loadFloatUniform(spotLightExponentLocations[index], spotLight.getExponent());
    }
}
