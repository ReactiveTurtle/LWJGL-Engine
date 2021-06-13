package ru.reactiveturtle.physics;

import org.joml.Vector3f;
import org.joml.Vector4f;

public class Line {
    private Vector4f f1 = new Vector4f();
    private Vector4f f2 = new Vector4f();
    private Vector3f position;
    private Vector3f direction;

    public Line(Vector3f direction, Vector3f position) {
        this.position = new Vector3f(position);
        this.direction = new Vector3f(direction);
        f1.set(
                direction.y,
                -direction.x,
                0,
                -direction.y * position.x + direction.x * position.y);
        f2.set(
                direction.z,
                0,
                -direction.x,
                -direction.z * position.x + direction.x * position.z);
    }


    public Vector3f intersects(Plane plane) {
        Vector3f planeNormal = plane.getNormal();
        Vector3f planePosition = plane.getPosition();

        Vector3f lineDirection = direction;
        Vector3f linePosition = position;

        if (planeNormal.dot(lineDirection.normalize()) == 0) {
            return null;
        }

        float t = (planeNormal.dot(planePosition) - planeNormal.dot(linePosition)) / planeNormal.dot(lineDirection.normalize());
        return linePosition.add(lineDirection.normalize().mul(t));
    }
}
