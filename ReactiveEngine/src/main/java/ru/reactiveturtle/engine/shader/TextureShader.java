package ru.reactiveturtle.engine.shader;

import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;
import ru.reactiveturtle.engine.base3d.Stage3D;
import ru.reactiveturtle.engine.camera.Camera;
import ru.reactiveturtle.engine.camera.PerspectiveCamera;
import ru.reactiveturtle.engine.light.DirectionalLight;
import ru.reactiveturtle.engine.light.Light;
import ru.reactiveturtle.engine.light.PointLight;
import ru.reactiveturtle.engine.light.SpotLight;
import ru.reactiveturtle.engine.model.mesh.Mesh;
import ru.reactiveturtle.engine.base.Shader;
import ru.reactiveturtle.engine.material.Material;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL13.GL_TEXTURE2;
import static org.lwjgl.opengl.GL13.glActiveTexture;

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

    private int directionalLightsCountLocation;
    private int[] directionalLightDirectionLocations = new int[2];
    private int[] directionalLightAmbientLocations = new int[2];
    private int[] directionalLightDiffuseLocations = new int[2];
    private int[] directionalLightSpecularLocations = new int[2];
    private int[] directionalLightShadowMapLocations = new int[2];
    private int[] modelLightViewProjectionMatrixLocation = new int[2];

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

        directionalLightsCountLocation = super.getUniform("directionalLightsCount");
        for (int i = 0; i < 2; i++) {
            directionalLightDirectionLocations[i] = super.getUniform("directionalLights[" + i + "].direction");
            directionalLightAmbientLocations[i] = super.getUniform("directionalLights[" + i + "].ambient");
            directionalLightDiffuseLocations[i] = super.getUniform("directionalLights[" + i + "].diffuse");
            directionalLightSpecularLocations[i] = super.getUniform("directionalLights[" + i + "].specular");
            directionalLightShadowMapLocations[i] = super.getUniform("directionalLights[" + i + "].shadowMap");
            modelLightViewProjectionMatrixLocation[i] = super.getUniform("directionalLights[" + i + "].modelLightViewProjectionMatrix");
        }

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
    public void load(Stage3D stage, Matrix4f model, Mesh mesh) {
        Material material = mesh.getMaterial();

        super.loadIntUniform(textureSamplerLocation, 0);
        super.loadIntUniform(normalMapLocation, 1);

        if (stage.getCamera() != null) {
            super.loadMatrix4fUniform(projectionLocation, stage.getCamera().getPerspectiveMatrix());
            super.loadMatrix4fUniform(viewMatrixLocation, stage.getCamera().getViewMatrix());
        }

        super.loadMatrix4fUniform(modelMatrixLocation, model);

        super.loadIntUniform(materialExistsLocation, material == null ? 0 : 1);

        if (material != null) {

            Vector3f translation = new Vector3f();
            model.getTranslation(translation);

            DirectionalLight[] directionalLights = Light.getDirectionalLights(stage.getLights());

            PointLight[] nearestPointLights = Light.getNearestPointLights(stage.getLights(), translation);

            SpotLight[] nearestSpotLights = Light.getNearestSpotLights(stage.getLights(), translation);

            if (directionalLights.length > 0 || nearestPointLights.length > 0 || nearestSpotLights.length > 0) {
                setupMaterial(material);

                super.loadIntUniform(directionalLightsCountLocation, directionalLights.length);
                if (directionalLights.length > 0) {
                    for (int i = 0; i < directionalLights.length; i++) {
                        setupDirectionalLight(stage.getCamera(), model, directionalLights[i], i);
                    }
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
                super.loadMatrix3fUniform(normalMatrixLocation, new Matrix3f(new Matrix4f().set(model).invert()).transpose());
                super.loadVector3fUniform(cameraPositionLocation, stage.getCamera().getPosition());
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

    private void setupDirectionalLight(PerspectiveCamera camera, Matrix4f modelMatrix, DirectionalLight directionalLight, int index) {
        super.loadVector4fUniform(directionalLightDirectionLocations[index], new Vector4f(directionalLight.getDirection(), 0));
        super.loadVector4fUniform(directionalLightAmbientLocations[index], new Vector4f(directionalLight.getAmbient(), 1));
        super.loadVector4fUniform(directionalLightDiffuseLocations[index], new Vector4f(directionalLight.getDiffuse(), 1));
        super.loadVector4fUniform(directionalLightSpecularLocations[index], new Vector4f(directionalLight.getSpecular(), 1));

        super.loadIntUniform(directionalLightShadowMapLocations[index], 2 + index);
        glActiveTexture(GL_TEXTURE2 + index);
        GL11.glBindTexture(GL_TEXTURE_2D, directionalLight.getShadowMap().getShadowTexture().getTextureId());

        Matrix4f lightViewMatrix = new Matrix4f().identity().lookAt(directionalLight.getDirection(), new Vector3f(0, 0, 0), new Vector3f(0, 1, 0));
        super.loadMatrix4fUniform(modelLightViewProjectionMatrixLocation[index],
                Camera.getOrtho()
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
