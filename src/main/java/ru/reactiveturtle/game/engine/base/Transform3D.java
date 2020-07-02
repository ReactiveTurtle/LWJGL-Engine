package ru.reactiveturtle.game.engine.base;

import org.joml.Vector3f;

public class Transform3D {
    protected Vector3f position = new Vector3f();
    protected Vector3f rotation = new Vector3f();

    public void setPosition(Vector3f position) {
        this.position.set(position);
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
        position.add(vector);
    }

    public void addPosition(float x, float y, float z) {
        position.add(x, y, z);
    }

    public void addX(float x) {
        position.x += x;
    }

    public void addY(float y) {
        position.y += y;
    }

    public void addZ(float z) {
        position.z += z;
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
        addRotation(rotation.x, rotation.y, rotation.z);
    }

    public void addRotationX(float degreesX) {
        addRotation(degreesX, 0, 0);
    }

    public void addRotationY(float degreesY) {
        addRotation(0, degreesY, 0);
    }

    public void addRotationZ(float degreesZ) {
        addRotation(0, 0, degreesZ);
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
