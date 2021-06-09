package ru.reactiveturtle.engine.shader;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import ru.reactiveturtle.engine.base.Shader;
import ru.reactiveturtle.engine.base3d.Stage3D;
import ru.reactiveturtle.engine.camera.Camera;
import ru.reactiveturtle.engine.camera.CameraExtensions;
import ru.reactiveturtle.engine.light.DirectionalLight;
import ru.reactiveturtle.engine.light.Light;
import ru.reactiveturtle.engine.light.PointLight;
import ru.reactiveturtle.engine.light.SpotLight;
import ru.reactiveturtle.engine.material.Material;
import ru.reactiveturtle.engine.model.mesh.Mesh;

import java.util.List;

public class ModelShader extends Shader {
    private int vProjectionMatrixLocation;
    private int vViewMatrixLocation;
    private int vModelMatrixLocation;

    private int fProjectionMatrixLocation;
    private int fViewMatrixLocation;
    private int fModelMatrixLocation;

    private int materialExistsLocation;
    private int materialTextureExistsLocation;
    private int materialTextureSamplerLocation;
    private int materialNormalMapTextureExistsLocation;
    private int materialNormalMapTextureSamplerLocation;
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
    private int directionalLightMVPMatrixLocation;
    private int directionalLightShadowMapTextureExistsLocation;
    private int directionalLightShadowMapTextureSamplerLocation;

    private int pointLightsCountLocation;
    private final int[] pointLightPositionLocations = new int[Light.MAX_LIGHTS_COUNT];
    private final int[] pointLightAmbientLocations = new int[Light.MAX_LIGHTS_COUNT];
    private final int[] pointLightDiffuseLocations = new int[Light.MAX_LIGHTS_COUNT];
    private final int[] pointLightSpecularLocations = new int[Light.MAX_LIGHTS_COUNT];
    private final int[] pointLightAttenuationConstantLocations = new int[Light.MAX_LIGHTS_COUNT];
    private final int[] pointLightAttenuationLinearLocations = new int[Light.MAX_LIGHTS_COUNT];
    private final int[] pointLightAttenuationExponentLocations = new int[Light.MAX_LIGHTS_COUNT];

    private int spotLightsCountLocation;
    private final int[] spotLightPositionLocations = new int[Light.MAX_LIGHTS_COUNT];
    private final int[] spotLightAmbientLocations = new int[Light.MAX_LIGHTS_COUNT];
    private final int[] spotLightDiffuseLocations = new int[Light.MAX_LIGHTS_COUNT];
    private final int[] spotLightSpecularLocations = new int[Light.MAX_LIGHTS_COUNT];
    private final int[] spotLightAttenuationConstantLocations = new int[Light.MAX_LIGHTS_COUNT];
    private final int[] spotLightAttenuationLinearLocations = new int[Light.MAX_LIGHTS_COUNT];
    private final int[] spotLightAttenuationExponentLocations = new int[Light.MAX_LIGHTS_COUNT];
    private final int[] spotLightDirectionLocations = new int[Light.MAX_LIGHTS_COUNT];
    private final int[] spotLightCutoffLocations = new int[Light.MAX_LIGHTS_COUNT];
    private final int[] spotLightExponentLocations = new int[Light.MAX_LIGHTS_COUNT];

    public ModelShader() {
        super(MODEL_VERTEX_SHADER, MODEL_FRAGMENT_SHADER);
        create();
    }

    @Override
    public void bindAllAttributes() {
        super.bindAttribute(0, "vVertex");
        super.bindAttribute(2, "vTextureCoordinate");
        super.bindAttribute(3, "vVertexNormal");
    }

    @Override
    public void getAllUniforms() {
        vProjectionMatrixLocation = super.getUniform("vProjectionMatrix");
        vViewMatrixLocation = super.getUniform("vViewMatrix");
        vModelMatrixLocation = super.getUniform("vModelMatrix");

        fProjectionMatrixLocation = super.getUniform("fProjectionMatrix");
        fViewMatrixLocation = super.getUniform("fViewMatrix");
        fModelMatrixLocation = super.getUniform("fModelMatrix");

        materialExistsLocation = super.getUniform("materialExists");
        materialTextureExistsLocation = super.getUniform("material.textureExists");
        materialTextureSamplerLocation = super.getUniform("material.textureSampler");
        materialNormalMapTextureExistsLocation = super.getUniform("material.normalMapTextureExists");
        materialNormalMapTextureSamplerLocation = super.getUniform("material.normalMapTextureSampler");
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
        directionalLightMVPMatrixLocation = super.getUniform("directionalLight.mvpMatrix");
        directionalLightShadowMapTextureExistsLocation = super.getUniform("directionalLight.shadowMapTextureExists");
        directionalLightShadowMapTextureSamplerLocation = super.getUniform("directionalLight.shadowMapTextureSampler");

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
    }

    @Override
    public void load(Stage3D stage, Matrix4f modelMatrix, Mesh mesh) {
        super.loadMatrix4fUniform(vProjectionMatrixLocation, stage.getCamera().getPerspectiveMatrix());
        super.loadMatrix4fUniform(vViewMatrixLocation, stage.getCamera().getViewMatrix());
        super.loadMatrix4fUniform(vModelMatrixLocation, modelMatrix);

        super.loadMatrix4fUniform(fProjectionMatrixLocation, stage.getCamera().getPerspectiveMatrix());
        super.loadMatrix4fUniform(fViewMatrixLocation, stage.getCamera().getViewMatrix());
        super.loadMatrix4fUniform(fModelMatrixLocation, modelMatrix);

        Material material = mesh.getMaterial();
        DirectionalLight directionalLight = stage.getDirectionalLight();
        List<PointLight> pointLights = stage.getPointLights();
        List<SpotLight> spotLights = stage.getSpotLights();

        super.loadIntUniform(materialExistsLocation, toBoolInt(material));
        if (material != null) {
            setupMaterial(material);
            if (directionalLight != null || pointLights.size() > 0 || spotLights.size() > 0) {

                setupDirectionalLight(stage.getCamera(), modelMatrix, directionalLight);

                super.loadIntUniform(pointLightsCountLocation, pointLights.size());
                if (pointLights.size() > 0) {
                    for (int i = 0; i < pointLights.size(); i++) {
                        setupPointLights(pointLights.get(i), i);
                    }
                }

                super.loadIntUniform(spotLightsCountLocation, spotLights.size());
                if (spotLights.size() > 0) {
                    for (int i = 0; i < spotLights.size(); i++) {
                        setupSpotLights(spotLights.get(i), i);
                    }
                }
            }
        }
    }

    private void setupMaterial(Material material) {
        super.loadIntUniform(materialTextureExistsLocation, toBoolInt(material.getTexture()));
        super.loadIntUniform(materialTextureSamplerLocation, 0);
        super.loadIntUniform(materialNormalMapTextureExistsLocation, toBoolInt(material.getNormalMapTexture()));
        super.loadIntUniform(materialNormalMapTextureSamplerLocation, 1);
        super.loadVector4fUniform(materialAmbientLocation, new Vector4f(material.getAmbient(), 1));
        super.loadVector4fUniform(materialDiffuseLocation, new Vector4f(material.getDiffuse(), 1));
        super.loadVector4fUniform(materialSpecularLocation, new Vector4f(material.getSpecular(), 1));
        super.loadVector4fUniform(materialEmissionLocation, new Vector4f(material.getEmission(), 1));
        super.loadFloatUniform(materialReflectanceLocation, material.getReflectance());
    }

    private void setupDirectionalLight(Camera camera,
                                       Matrix4f modelMatrix,
                                       DirectionalLight directionalLight) {
        super.loadIntUniform(directionalLightExistsLocation, toBoolInt(directionalLight));
        if (directionalLight == null) {
            return;
        }
        super.loadVector4fUniform(directionalLightDirectionLocation, new Vector4f(directionalLight.getDirection(), 0));
        super.loadVector4fUniform(directionalLightAmbientLocation, new Vector4f(directionalLight.getAmbient(), 1));
        super.loadVector4fUniform(directionalLightDiffuseLocation, new Vector4f(directionalLight.getDiffuse(), 1));
        super.loadVector4fUniform(directionalLightSpecularLocation, new Vector4f(directionalLight.getSpecular(), 1));
        super.loadIntUniform(directionalLightShadowMapTextureExistsLocation, toBoolInt(directionalLight.getShadowMap()));
        super.loadIntUniform(directionalLightShadowMapTextureSamplerLocation, 2);

        Matrix4f lightViewMatrix = new Matrix4f().identity()
                .lookAt(directionalLight.getDirection(),
                        new Vector3f(0, 0, 0),
                        new Vector3f(0, 1, 0));
        super.loadMatrix4fUniform(directionalLightMVPMatrixLocation,
                CameraExtensions.getOrtho()
                        .mul(lightViewMatrix)
                        .mul(camera.getFlatTranslationMatrix())
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
