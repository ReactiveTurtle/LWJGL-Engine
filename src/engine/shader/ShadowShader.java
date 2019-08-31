package engine.shader;

import engine.Base;
import engine.model.Model;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

public class ShadowShader extends Shader {
    private static final String SHADOW_VERTEX_SHADER = "src/resources/shaders/shadow_vertex_shader.glsl";
    private static final String SHADOW_FRAGMENT_SHADER = "src/resources/shaders/shadow_fragment_shader.glsl";

    private int projectionMatrixLocation;
    private int modelLightViewMatrixLocation;

    private List<Model> modelsList = new ArrayList<>();

    public ShadowShader() {
        super(SHADOW_VERTEX_SHADER, SHADOW_FRAGMENT_SHADER);
    }

    @Override
    public void bindAllAttributes() {
        super.bindAttribute(0, "position");
    }

    @Override
    public void getAllUniforms() {
        projectionMatrixLocation = super.getUniform("projectionMatrix");
        modelLightViewMatrixLocation = super.getUniform("modelLightViewMatrix");
    }

    @Override
    public void load(Matrix4f lightViewMatrix, Material material) {
        if (Base.shadowMap.getProjectionMatrix() != null) {
            super.loadMatrix4fUniform(projectionMatrixLocation, Base.shadowMap.getProjectionMatrix());
            for (Model model : modelsList) {
                super.loadMatrix4fUniform(modelLightViewMatrixLocation, new Matrix4f().set(lightViewMatrix).mul(model.getModelMatrix()));
            }
        }
    }

    public void addModel(Model model) {
        modelsList.add(model);
    }

    public Model getModel(int index) {
        if (index >= modelsList.size() || index < 0) {
            throw new IndexOutOfBoundsException();
        }
        return modelsList.get(index);
    }
}
