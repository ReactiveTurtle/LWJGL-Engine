package engine;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera {
    private static final int PERSPECTIVE = 0;
    private static final int ORTHOGRAPHIC = 1;

    private Matrix4f projectionMatrix;

    private Vector3f position = new Vector3f();
    private Vector3f rotation = new Vector3f();

    private int type = -1;

    public Camera(float fieldOfView, float near, float far) {
        type = PERSPECTIVE;
        projectionMatrix = getPerspective(fieldOfView, near, far);
    }

    public Camera(float fieldOfView, float near, float far, Vector3f position, Vector3f rotation) {
        type = PERSPECTIVE;
        projectionMatrix = getPerspective(fieldOfView, near, far);
        this.position.set(position.x, position.y, position.z);
        this.rotation.set(rotation.x, rotation.y, rotation.z);
    }

    public Camera(float left, float top, float right, float bottom, float near, float far) {
        type = ORTHOGRAPHIC;
        projectionMatrix = getOrtho(left, right, bottom, top, near, far);
    }

    public Camera(float left, float right, float bottom, float top, float near, float far, Vector3f position, Vector3f rotation) {
        type = ORTHOGRAPHIC;
        projectionMatrix = getOrtho(left, right, bottom, top, near, far);
        this.position.set(position.x, position.y, position.z);
        this.rotation.set(rotation.x, rotation.y, rotation.z);
    }

    public static Matrix4f getPerspective(float fieldOfView, float near, float far) {
        float aspectRatio = (float) Base.width / (float) Base.height;
        return new Matrix4f().identity().perspective(
                (float) Math.toRadians(fieldOfView), aspectRatio, near, far);
    }

    public static Matrix4f getOrtho(float left, float right, float bottom, float top, float near, float far) {
        return new Matrix4f().identity().ortho(left, right, bottom, top, near, far);
    }

    public Matrix4f getProjectionMatrix() {
        return projectionMatrix;
    }

    public int getType() {
        return type;
    }

    public Matrix4f getViewMatrix() {
        return new Matrix4f().identity()
                .rotateXYZ((float) Math.toRadians(rotation.x),
                        (float) Math.toRadians(rotation.y),
                        (float) Math.toRadians(rotation.z))
                .translate(-position.x, -position.y, -position.z);
    }

    public void setPosition(float x, float y, float z) {
        position.set(x, y, z);
    }

    public void setX(float x) {
        position.x = x;
    }

    public void setY(float y) {
        position.y = y;
    }

    public void setZ(float z) {
        position.z = z;
    }

    public void addPosition(Vector3f vector) {
        if (vector.z != 0) {
            float b = vector.z * (float) Math.cos(Math.toRadians(rotation.x));
            position.x += (float) Math.sin(Math.toRadians(rotation.y)) * -1.0f * b;
            position.y += (float) Math.sin(Math.toRadians(rotation.x)) * vector.z;
            position.z += (float) Math.cos(Math.toRadians(rotation.y)) * b;
        }
        if (vector.x != 0) {
            position.x += (float) Math.sin(Math.toRadians(rotation.y - 90)) * -1.0f * vector.x;
            position.z += (float) Math.cos(Math.toRadians(rotation.y - 90)) * vector.x;
        }
    }

    public void addPosition(float x, float y, float z) {
        if (z != 0) {
            float b = z * (float) Math.cos(Math.toRadians(rotation.x));
            position.x += (float) Math.sin(Math.toRadians(rotation.y)) * -1.0f * b;
            position.y += (float) Math.sin(Math.toRadians(rotation.x)) * z;
            position.z += (float) Math.cos(Math.toRadians(rotation.y)) * b;
        }
        if (x != 0) {
            position.x += (float) Math.sin(Math.toRadians(rotation.y - 90)) * -1.0f * x;
            position.z += (float) Math.cos(Math.toRadians(rotation.y - 90)) * x;
        }
    }

    public float getX() {
        return position.x;
    }

    public float getY() {
        return position.y;
    }

    public float getZ() {
        return position.z;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setRotation(float degreesX, float degreesY, float degreesZ) {
        rotation.set(degreesX, degreesY, degreesZ);
    }

    public void setRotation(Vector3f rotation) {
        this.rotation.set(rotation);
    }

    public void setRotationX(float degreesX) {
        rotation.x = degreesX;
    }

    public void setRotationY(float degreesY) {
        rotation.y = degreesY;
    }

    public void setRotationZ(float degreesZ) {
        rotation.z = degreesZ;
    }

    public void addRotation(float degreesX, float degreesY, float degreesZ) {
        rotation.add(degreesX, degreesY, degreesZ);
    }

    public void addRotation(Vector3f rotation) {
        this.rotation.add(rotation);
    }

    public void addRotationX(float degreesX) {
        rotation.x += degreesX;
    }

    public void addRotationY(float degreesY) {
        rotation.y += degreesY;
    }

    public void addRotationZ(float degreesZ) {
        rotation.z += degreesZ;
    }

    public float getRotationX() {
        return rotation.x;
    }

    public float getRotationY() {
        return rotation.y;
    }

    public float getRotationZ() {
        return rotation.z;
    }

    public Vector3f getRotation() {
        return rotation;
    }
}
