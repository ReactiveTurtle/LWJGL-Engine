package ru.reactiveturtle.engine.camera;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class CameraExtensions {
    public static Matrix4f getOrtho() {
        return new Matrix4f().identity().setOrtho(-10f, 10f,
                -10f, 10f, -100, 100);
    }
}
