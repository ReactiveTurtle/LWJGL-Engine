package ru.reactiveturtle.game.engine.camera;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class Camera {
    public static Matrix4f getOrtho() {
        return new Matrix4f().identity().setOrtho(-20f, 20f,
                -20f, 20f, -100, 100);
    }
}
