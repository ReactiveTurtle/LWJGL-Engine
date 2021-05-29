package ru.reactiveturtle.engine.camera;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import ru.reactiveturtle.engine.base.Transform3D;
import ru.reactiveturtle.engine.geometry.Frustum;

public class PerspectiveCamera extends Transform3D {
    private float aspectRatio;
    private float fov;
    private final float near;
    private final float far;

    private final Matrix4f orthographicMatrix = new Matrix4f();
    private final Matrix4f perspectiveMatrix = new Matrix4f();

    public PerspectiveCamera(float aspectRatio, float fieldOfView, float near, float far) {
        fov = (float) Math.toRadians(fieldOfView);
        this.near = near;
        this.far = far;
        updateAspectRatio(aspectRatio);
    }

    public void updateAspectRatio(float aspectRatio) {
        this.aspectRatio = aspectRatio;
        perspectiveMatrix.perspective(fov, aspectRatio, near, far);
        updateOrthographicMatrix();
    }

    private void updateOrthographicMatrix() {
        orthographicMatrix.ortho(-1 * aspectRatio, 1 * aspectRatio, -1, 1, 0, 1);
    }

    public Matrix4f getPerspectiveMatrix() {
        return new Matrix4f(perspectiveMatrix);
    }

    public Matrix4f getOrthographicMatrix() {
        return new Matrix4f(orthographicMatrix);
    }

    public Matrix4f getViewMatrix() {
        return new Matrix4f().identity()
                .rotateXYZ(rotation.x,
                        rotation.y,
                        rotation.z)
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
                -getRotationY(),
                -getRotationX(),
                getRotationZ());
        return new Vector3f(0, 0, -1).rotate(quaternionf);
    }

    @Override
    public void addRotation(float degreesX, float degreesY, float degreesZ) {
        super.addRotation(degreesX, degreesY, degreesZ);
    }

    public float getNear() {
        return near;
    }

    public float getFar() {
        return far;
    }

    public float getFOV() {
        return fov;
    }

    public Frustum getFrustum() {
        return new Frustum(this);
    }
}
