package ru.reactiveturtle.physics;

import org.joml.Vector3f;
import org.joml.Vector4f;

public class Plane {
    private final float xFactor, yFactor, zFactor, dFactor;
    private Vector3f position;

    public Plane(Vector3f position, Vector3f normal) {
        this.position = new Vector3f(position);
        xFactor = normal.x;
        yFactor = normal.y;
        zFactor = normal.z;
        dFactor = -position.x * xFactor - position.y * yFactor - position.z * zFactor;
    }

    public Plane(Vector3f first, Vector3f second, Vector3f third) {
        this.position = new Vector3f(first);
        xFactor = (second.y - first.y) * (third.z - first.z) - (second.z - first.z) * (third.y - first.y);
        yFactor = -((second.x - first.x) * (third.z - first.z) - (second.z - first.z) * (third.x - first.x));
        zFactor = (second.x - first.x) * (third.y - first.y) - (second.y - first.y) * (third.x - first.x);
        dFactor = -first.x * xFactor - first.y * yFactor - first.z * zFactor;
    }

    public Vector3f getNormal() {
        return new Vector3f(xFactor, yFactor, zFactor);
    }

    public Vector3f getPosition() {
        return position;
    }

    public boolean isPointAtFront(Vector3f point) {
        return xFactor * point.x + yFactor * point.y + zFactor * point.z + dFactor > 1;
    }

    public boolean isPointAtFrontOrIn(Vector3f point) {
        return xFactor * point.x + yFactor * point.y + zFactor * point.z + dFactor >= 1;
    }

    public Vector4f getFactors() {
        return new Vector4f(xFactor, yFactor, zFactor, dFactor);
    }
}
