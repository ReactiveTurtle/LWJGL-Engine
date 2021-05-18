package ru.reactiveturtle.engine.geometry;

import org.joml.Vector3f;
import org.joml.Vector4f;
import ru.reactiveturtle.engine.base.Value;

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

    public Vector3f intersects(Plane plane) {
        Vector4f f1 = new Vector4f(this.f1);
        Vector4f f2 = new Vector4f(this.f2);
        Vector4f f3 = plane.getFactors();

        if (f1.y == 0 || f2.z == 0) {
            return null;
        }

        float x = ((f3.y * f1.w / f1.y) + (f3.z * f2.w / f2.z) - f3.w) /
                (f3.x - (f3.y * f1.x / f1.y) - (f3.z * f2.x / f2.z));
        float y = (-f1.w - f1.x * x) / f1.y;
        float z = (-f2.w - f2.x * x) / f2.z;

        return new Vector3f(x, y, z);
    }
}
