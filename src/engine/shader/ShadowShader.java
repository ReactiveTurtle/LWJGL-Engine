package engine.shader;

import engine.Base;
import engine.Camera;
import engine.model.Model;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class ShadowShader extends Shader {
    private static final String SHADOW_VERTEX_SHADER = "src/resources/shaders/shadow_vertex_shader.glsl";
    private static final String SHADOW_FRAGMENT_SHADER = "src/resources/shaders/shadow_fragment_shader.glsl";

    private int modelLightViewProjectionwMatrixLocation;

    private List<Model> modelsList = new ArrayList<>();

    public ShadowShader() {
        super(SHADOW_VERTEX_SHADER, SHADOW_FRAGMENT_SHADER);
    }

    @Override
    public void bindAllAttributes() {
        super.bindAttribute(0, "position");
        super.bindAttribute(2, "inTextureCoordinates");
        super.bindAttribute(3, "inVertexNormal");
    }

    @Override
    public void getAllUniforms() {
        modelLightViewProjectionwMatrixLocation = super.getUniform("modelLightViewProjectionMatrix");
    }

    @Override
    public void load(Matrix4f matrix, Material material) {
        super.loadMatrix4fUniform(modelLightViewProjectionwMatrixLocation, matrix);
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
