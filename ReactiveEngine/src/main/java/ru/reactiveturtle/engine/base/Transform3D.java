package ru.reactiveturtle.engine.base;

import org.joml.Vector3f;

public class Transform3D {
    protected Vector3f position = new Vector3f();
    protected Vector3f rotation = new Vector3f();

    public void setPosition(Vector3f position) {
        setPosition(position.x, position.y, position.z);
    }

    public void setPosition(float x, float y, float z) {
        position.set(x, y, z);
    }

    public void setX(float x) {
        setPosition(x, position.y, position.z);
    }

    public void setY(float y) {
        setPosition(position.x, y, position.z);
    }

    public void setZ(float z) {
        setPosition(position.x, position.y, z);
    }

    public void addPosition(Vector3f vector) {
        addPosition(vector.x, vector.y, vector.z);
    }

    public void addPosition(float x, float y, float z) {
        setPosition(position.x + x, position.y + y, position.z + z);
    }

    public void addX(float x) {
        addPosition(x, 0, 0);
    }

    public void addY(float y) {
        addPosition(0, y, 0);
    }

    public void addZ(float z) {
        addPosition(0, 0, z);
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
        return new Vector3f(position);
    }

    public void setRotation(float x, float y, float z) {
        rotation.set(x, y, z);
    }

    public void setRotation(Vector3f rotation) {
        setRotation(rotation.x, rotation.y, rotation.z);
    }

    public void setRotationX(float x) {
        setRotation(x, rotation.y, rotation.z);
    }

    public void setRotationY(float y) {
        setRotation(rotation.x, y, rotation.z);
    }

    public void setRotationZ(float z) {
        setRotation(rotation.x, rotation.y, z);
    }

    public void addRotation(float x, float y, float z) {
        setRotation(rotation.x + x,
                rotation.y + y,
                rotation.z + z);
    }

    public void addRotation(Vector3f rotation) {
        addRotation(rotation.x, rotation.y, rotation.z);
    }

    public void addRotationX(float x) {
        addRotation(x, 0, 0);
    }

    public void addRotationY(float y) {
        addRotation(0, y, 0);
    }

    public void addRotationZ(float z) {
        addRotation(0, 0, z);
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
        return new Vector3f(rotation);
    }
}
