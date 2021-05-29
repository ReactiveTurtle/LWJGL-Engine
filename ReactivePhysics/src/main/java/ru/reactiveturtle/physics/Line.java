package ru.reactiveturtle.physics;

import org.joml.Vector3f;
import org.joml.Vector4f;

public class Line {
    private Vector4f f1 = new Vector4f();
    private Vector4f f2 = new Vector4f();

    public Line(Vector3f direction, Vector3f position) {
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
}
