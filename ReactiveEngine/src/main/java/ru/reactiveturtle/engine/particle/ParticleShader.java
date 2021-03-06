package ru.reactiveturtle.engine.particle;

import org.joml.Matrix4f;
import ru.reactiveturtle.engine.base3d.Stage3D;
import ru.reactiveturtle.engine.model.mesh.Mesh;
import ru.reactiveturtle.engine.base.Shader;

public class ParticleShader extends Shader {
    private int projectionLocation;
    private int viewMatrixLocation;
    private int modelMatrixLocation;

    private int textureSamplerLocation;
    private int alphaLocation;

    public ParticleShader() {
        super(PARTICLE_VERTEX_SHADER, PARTICLE_FRAGMENT_SHADER);
        create();
    }

    @Override
    public void bindAllAttributes() {
        super.bindAttribute(0, "vertex");
        super.bindAttribute(2, "texCoord");
    }

    @Override
    public void getAllUniforms() {
        projectionLocation = super.getUniform("perspectiveProjection");
        viewMatrixLocation = super.getUniform("viewMatrix");
        modelMatrixLocation = super.getUniform("modelMatrix");

        textureSamplerLocation = super.getUniform("textureSampler");
        alphaLocation = super.getUniform("alpha");
    }

    @Override
    public void load(Stage3D stage, Matrix4f model, Mesh mesh) {
        super.loadIntUniform(textureSamplerLocation, 0);
        if (stage.getCamera() != null) {
            super.loadMatrix4fUniform(projectionLocation, stage.getCamera().getPerspectiveMatrix());
            super.loadMatrix4fUniform(viewMatrixLocation, stage.getCamera().getViewMatrix());
        }
        super.loadMatrix4fUniform(modelMatrixLocation, model);
    }

    public void loadAlpha(float alpha) {
        super.loadFloatUniform(alphaLocation, alpha);
    }
}
