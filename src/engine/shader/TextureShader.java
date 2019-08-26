package engine.shader;

import engine.Base;
import engine.environment.DirectionalLight;
import engine.environment.Environment;
import engine.environment.PointLight;
import engine.environment.SpotLight;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class TextureShader extends Shader {
    private int typeLocation;
    private int textureSamplerLocation;

    private int projectionLocation;
    private int viewMatrixLocation;
    private int modelMatrixLocation;

    private int materialExistsLocation;
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
    private int[] pointLightPositionLocations = new int[Environment.MAX_LIGHTS_COUNT];
    private int[] pointLightAmbientLocations = new int[Environment.MAX_LIGHTS_COUNT];
    private int[] pointLightDiffuseLocations = new int[Environment.MAX_LIGHTS_COUNT];
    private int[] pointLightSpecularLocations = new int[Environment.MAX_LIGHTS_COUNT];
    private int[] pointLightAttenuationConstantLocations = new int[Environment.MAX_LIGHTS_COUNT];
    private int[] pointLightAttenuationLinearLocations = new int[Environment.MAX_LIGHTS_COUNT];
    private int[] pointLightAttenuationExponentLocations = new int[Environment.MAX_LIGHTS_COUNT];

    private int spotLightsCountLocation;
    private int[] spotLightPositionLocations = new int[Environment.MAX_LIGHTS_COUNT];
    private int[] spotLightAmbientLocations = new int[Environment.MAX_LIGHTS_COUNT];
    private int[] spotLightDiffuseLocations = new int[Environment.MAX_LIGHTS_COUNT];
    private int[] spotLightSpecularLocations = new int[Environment.MAX_LIGHTS_COUNT];
    private int[] spotLightAttenuationConstantLocations = new int[Environment.MAX_LIGHTS_COUNT];
    private int[] spotLightAttenuationLinearLocations = new int[Environment.MAX_LIGHTS_COUNT];
    private int[] spotLightAttenuationExponentLocations = new int[Environment.MAX_LIGHTS_COUNT];
    private int[] spotLightDirectionLocations = new int[Environment.MAX_LIGHTS_COUNT];
    private int[] spotLightCutoffLocations = new int[Environment.MAX_LIGHTS_COUNT];
    private int[] spotLightExponentLocations = new int[Environment.MAX_LIGHTS_COUNT];

    private int transposedMatrixLocation;
    private int cameraPositionLocation;

    public TextureShader() {
        super(MODEL_VERTEX_SHADER, MODEL_FRAGMENT_SHADER);
    }

    @Override
    public void bindAllAttributes() {
        super.bindAttribute(0, "vertices");
        super.bindAttribute(2, "inTextureCoordinates");
        super.bindAttribute(3, "inVertexNormals");
    }

    @Override
    public void getAllUniforms() {
        typeLocation = super.getUniform("type");
        textureSamplerLocation = super.getUniform("textureSampler");

        projectionLocation = super.getUniform("perspectiveProjection");
        viewMatrixLocation = super.getUniform("viewMatrix");
        modelMatrixLocation = super.getUniform("modelMatrix");

        materialExistsLocation = super.getUniform("materialExists");
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
        for (int i = 0; i < Environment.MAX_LIGHTS_COUNT; i++) {
            pointLightPositionLocations[i] = super.getUniform("pointLights[" + i + "].position");
            pointLightAmbientLocations[i] = super.getUniform("pointLights[" + i + "].ambient");
            pointLightDiffuseLocations[i] = super.getUniform("pointLights[" + i + "].diffuse");
            pointLightSpecularLocations[i] = super.getUniform("pointLights[" + i + "].specular");
            pointLightAttenuationConstantLocations[i] = super.getUniform("pointLights[" + i + "].attenuation.constant");
            pointLightAttenuationLinearLocations[i] = super.getUniform("pointLights[" + i + "].attenuation.linear");
            pointLightAttenuationExponentLocations[i] = super.getUniform("pointLights[" + i + "].attenuation.exponent");
        }

        spotLightsCountLocation = super.getUniform("spotLightsCount");
        for (int i = 0; i < Environment.MAX_LIGHTS_COUNT; i++) {
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

        transposedMatrixLocation = super.getUniform("transposedModelMatrix");
        cameraPositionLocation = super.getUniform("cameraPosition");
    }

    @Override
    public void load(Matrix4f modelMatrix, Material material) {
        super.loadIntUniform(typeLocation, 1);

        super.loadIntUniform(textureSamplerLocation, 0);

        if (Base.camera != null) {
            super.loadMatrix4fUniform(projectionLocation, Base.camera.getProjectionMatrix());
            super.loadMatrix4fUniform(viewMatrixLocation, Base.camera.getViewMatrix());
        }

        super.loadMatrix4fUniform(modelMatrixLocation, modelMatrix);

        super.loadIntUniform(materialExistsLocation, material == null ? 0 : 1);

        if (material != null) {
            DirectionalLight directionalLight = Base.directionalLight;

            Vector3f translation = new Vector3f();
            modelMatrix.getTranslation(translation);

            PointLight[] nearestPointLights = Environment.getNearestPointLights(Base.pointLightsList, translation);

            SpotLight[] nearestSpotLights = Environment.getNearestSpotLights(Base.spotLightsList, translation);

            if (directionalLight != null || nearestPointLights.length > 0 || nearestSpotLights.length > 0) {
                setupMaterial(material);

                super.loadIntUniform(directionalLightExistsLocation, directionalLight == null ? 0 : 1);
                if (directionalLight != null) {
                    setupDirectionalLight(directionalLight);
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
                super.loadMatrix3fUniform(transposedMatrixLocation, new Matrix3f().set(new Matrix4f().set(modelMatrix).invert()).transpose());
                super.loadVector3fUniform(cameraPositionLocation, Base.camera.getPosition());
            } else {
                super.loadIntUniform(materialExistsLocation, 0);
            }
        }
    }

    private void setupMaterial(Material material) {
        super.loadVector4fUniform(materialAmbientLocation, new Vector4f(material.getAmbient(), 1));
        super.loadVector4fUniform(materialDiffuseLocation, new Vector4f(material.getDiffuse(), 1));
        super.loadVector4fUniform(materialSpecularLocation, new Vector4f(material.getSpecular(), 1));
        super.loadVector4fUniform(materialEmissionLocation, new Vector4f(material.getEmission(), 1));
        super.loadFloatUniform(materialReflectanceLocation, material.getReflectance());
    }

    private void setupDirectionalLight(DirectionalLight directionalLight) {
        super.loadVector4fUniform(directionalLightDirectionLocation, new Vector4f(directionalLight.getDirection(), 0));
        super.loadVector4fUniform(directionalLightAmbientLocation, new Vector4f(directionalLight.getAmbient(), 1));
        super.loadVector4fUniform(directionalLightDiffuseLocation, new Vector4f(directionalLight.getDiffuse(), 1));
        super.loadVector4fUniform(directionalLightSpecularLocation, new Vector4f(directionalLight.getSpecular(), 1));
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
