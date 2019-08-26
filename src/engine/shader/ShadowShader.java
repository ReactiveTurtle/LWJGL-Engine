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

    private ShadowMap shadowMap;

    private int modelLightViewMatrixLocation;
    private int modelViewProjectionMatrixLocation;

    private List<Model> modelsList = new ArrayList<>();

    public ShadowShader(ShadowMap shadowMap) {
        super(SHADOW_VERTEX_SHADER, SHADOW_FRAGMENT_SHADER);
        this.shadowMap = shadowMap;
    }

    @Override
    public void bindAllAttributes() {
        super.bindAttribute(0, "position");
        super.bindAttribute(2, "inTextureCoordinates");
        super.bindAttribute(3, "inVertexNormal");
    }

    @Override
    public void getAllUniforms() {
        modelViewProjectionMatrixLocation = super.getUniform("modelViewProjectionMatrix");
    }

    @Override
    public void load(Matrix4f modelMatrix, Material material) {
        super.loadMatrix4fUniform(modelViewProjectionMatrixLocation,
                modelMatrix.mul(Base.camera.getViewMatrix()
                        .mul(Base.camera.getOrtho(-10f, 10f, -10f, 10f, 0.1f, 100f))));
    }

    public void setShadowMap(ShadowMap shadowMap) {
        this.shadowMap = shadowMap;
    }

    public ShadowMap getShadowMap() {
        return shadowMap;
    }

    public void removeShadowMap() {
        if (shadowMap != null) {
            shadowMap.clear();
            shadowMap = null;
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

    private Matrix4f buildLightViewMatrix(Vector3f position, Vector3f rotation) {
        Camera camera = Base.camera;
        return new Matrix4f().identity()
                .rotate((float)Math.toRadians(camera.getRotationX()), new Vector3f(1, 0, 0))
                .rotate((float)Math.toRadians(camera.getRotationY()), new Vector3f(0, 1, 0))
                .translate(-camera.getX(), -camera.getY(), -camera.getZ());
    }
}
