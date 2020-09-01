package ru.reactiveturtle.game.engine.camera;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import ru.reactiveturtle.game.engine.base.GameContext;
import ru.reactiveturtle.game.engine.base.Transform3D;

public class PerspectiveCamera extends Transform3D {
    private Matrix4f projectionMatrix;
    private float aspectRatio;

    public PerspectiveCamera(float fieldOfView, float near, float far) {
        projectionMatrix = new Matrix4f().perspective((float) Math.toRadians(fieldOfView), (float) GameContext.width / GameContext.height, near, far);
        aspectRatio = (float) GameContext.width / GameContext.height;
    }

    public Matrix4f getProjectionMatrix() {
        return projectionMatrix;
    }

    public Matrix4f getOrho() {
        return new Matrix4f().identity().ortho(-1 * aspectRatio, 1 * aspectRatio, -1, 1, 0, 1);
    }

    public Matrix4f getViewMatrix() {
        return new Matrix4f().identity()
                .rotateXYZ((float) Math.toRadians(rotation.x),
                        (float) Math.toRadians(rotation.y),
                        (float) Math.toRadians(rotation.z))
                .translate(-position.x, -position.y, -position.z);
    }

    public Matrix4f getFlatTranslationMatrix() {
        return new Matrix4f().identity().translate(new Vector3f(position.x, 0, position.z).negate());
    }

    public void addPosition(Vector3f vector) {
        addPosition(vector.x, vector.y, vector.z);
    }

    public void addPosition(float x, float y, float z) {

    }

    @Override
    public void addRotation(float degreesX, float degreesY, float degreesZ) {
        super.addRotation(degreesX, degreesY, degreesZ);
    }
}
