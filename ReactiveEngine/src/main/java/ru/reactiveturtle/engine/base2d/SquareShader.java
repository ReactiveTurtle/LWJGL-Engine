package ru.reactiveturtle.engine.base2d;

import org.joml.Matrix4f;
import ru.reactiveturtle.engine.model.mesh.Mesh;
import ru.reactiveturtle.engine.base.GameContext;
import ru.reactiveturtle.engine.base.Shader;

public class SquareShader extends Shader {
    private int projectionLocation;
    private int modelMatrixLocation;

    private int textureSamplerLocation;

    public SquareShader() {
        super(SQUARE_VERTEX_SHADER, SQUARE_FRAGMENT_SHADER);
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
        modelMatrixLocation = super.getUniform("modelMatrix");

        textureSamplerLocation = super.getUniform("textureSampler");
    }

    @Override
    public void load(Matrix4f modelMatrix, Mesh mesh) {
        super.loadIntUniform(textureSamplerLocation, 0);
        if (GameContext.camera != null) {
            super.loadMatrix4fUniform(projectionLocation, GameContext.camera.getOrtho());
        }
        super.loadMatrix4fUniform(modelMatrixLocation, modelMatrix);
    }
}
