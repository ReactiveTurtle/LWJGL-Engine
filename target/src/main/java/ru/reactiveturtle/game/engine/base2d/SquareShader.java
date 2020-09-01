package ru.reactiveturtle.game.engine.base2d;

import org.joml.Matrix4f;
import ru.reactiveturtle.game.engine.base.GameContext;
import ru.reactiveturtle.game.engine.base.Shader;
import ru.reactiveturtle.game.engine.model.mesh.Mesh;

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
            super.loadMatrix4fUniform(projectionLocation, GameContext.camera.getOrho());
        }
        super.loadMatrix4fUniform(modelMatrixLocation, modelMatrix);
    }
}
