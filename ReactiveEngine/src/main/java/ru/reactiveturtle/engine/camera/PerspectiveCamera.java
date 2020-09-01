package ru.reactiveturtle.engine.camera;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import ru.reactiveturtle.engine.base.GameContext;
import ru.reactiveturtle.engine.base.Transform3D;

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

    public Matrix4f getOrtho() {
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
        return new Matrix4f().identity().translate(new Vector3f(position.x, position.y, position.z).negate());
    }

    public void addPosition(Vector3f vector) {
        addPosition(vector.x, vector.y, vector.z);
    }

    public Vector3f getDirection() {
        Quaternionf quaternionf = new Quaternionf();
        quaternionf.rotateYXZ(
                (float) Math.toRadians(-getRotationY()),
                (float) Math.toRadians(-getRotationX()),
                (float) Math.toRadians(getRotationZ()));
        return new Vector3f(0, 0, -1).rotate(quaternionf);
    }

    @Override
    public void addRotation(float degreesX, float degreesY, float degreesZ) {
        super.addRotation(degreesX, degreesY, degreesZ);
    }
}
