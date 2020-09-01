package ru.reactiveturtle.game.engine.shadow;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import ru.reactiveturtle.game.engine.base.GameContext;
import ru.reactiveturtle.game.engine.base.Shader;
import ru.reactiveturtle.game.engine.camera.Camera;
import ru.reactiveturtle.game.engine.light.DirectionalLight;
import ru.reactiveturtle.game.engine.light.Light;
import ru.reactiveturtle.game.engine.model.mesh.Mesh;

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
    public void load(Matrix4f modelMatrix, Mesh mesh) {
        DirectionalLight directionalLight = Light.getDirectionalLight(GameContext.lights);
        if (GameContext.camera != null && directionalLight != null) {
            Matrix4f lightViewMatrix = new Matrix4f().identity()
                    .lookAt(directionalLight.getDirection(), new Vector3f(0, 0, 0), new Vector3f(0, 1, 0));
            super.loadMatrix4fUniform(projectionMatrixLocation,
                    Camera.getOrtho());
            super.loadMatrix4fUniform(viewMatrixLocation, lightViewMatrix
                    .mul(GameContext.camera.getFlatTranslationMatrix()));
            super.loadMatrix4fUniform(modelMatrixLocation, modelMatrix);
        }
    }
}
