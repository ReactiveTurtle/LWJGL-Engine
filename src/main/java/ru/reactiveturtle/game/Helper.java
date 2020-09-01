package ru.reactiveturtle.game;

import org.joml.Vector3f;

public class Helper {
    public static Vector3f toRadians(Vector3f rotation) {
        Vector3f vector3f = new Vector3f();
        vector3f.x = (float) Math.toRadians(rotation.x);
        vector3f.y = (float) Math.toRadians(rotation.y);
        vector3f.z = (float) Math.toRadians(rotation.z);
        return vector3f;
    }

    public static Vector3f toDegrees(Vector3f rotation) {
        Vector3f vector3f = new Vector3f();
        vector3f.x = (float) Math.toDegrees(rotation.x);
        vector3f.y = (float) Math.toDegrees(rotation.y);
        vector3f.z = (float) Math.toDegrees(rotation.z);
        return vector3f;
    }
}
