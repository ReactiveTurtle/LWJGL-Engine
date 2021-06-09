package ru.reactiveturtle.engine.shadow;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import ru.reactiveturtle.engine.base3d.Stage3D;
import ru.reactiveturtle.engine.light.DirectionalLight;
import ru.reactiveturtle.engine.model.mesh.Mesh;
import ru.reactiveturtle.engine.base.Shader;
import ru.reactiveturtle.engine.camera.CameraExtensions;

public class ShadowShader extends Shader {
    private int projectionMatrixLocation;
    private int viewMatrixLocation;
    private int modelMatrixLocation;

    public ShadowShader() {
        super(SHADOW_VERTEX_SHADER, SHADOW_FRAGMENT_SHADER);
        create();
    }

    @Override
    public void bindAllAttributes() {
        super.bindAttribute(0, "vertex");
    }

    @Override
    public void getAllUniforms() {
        projectionMatrixLocation = super.getUniform("projection");
        viewMatrixLocation = super.getUniform("view");
        modelMatrixLocation = super.getUniform("model");
    }

    @Override
    public void load(Stage3D stage, Matrix4f modelMatrix, Mesh mesh) {
        DirectionalLight directionalLight = stage.getDirectionalLight();
        if (stage.getCamera() != null && directionalLight != null) {
            Matrix4f lightViewMatrix = new Matrix4f().identity()
                    .lookAt(directionalLight.getDirection(), new Vector3f(0, 0, 0), new Vector3f(0, 1, 0));
            super.loadMatrix4fUniform(projectionMatrixLocation,
                    CameraExtensions.getOrtho());
            super.loadMatrix4fUniform(viewMatrixLocation, lightViewMatrix
                    .mul(stage.getCamera().getFlatTranslationMatrix())
            );
            super.loadMatrix4fUniform(modelMatrixLocation, modelMatrix);
        }
    }
}
