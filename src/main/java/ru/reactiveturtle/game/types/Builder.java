package ru.reactiveturtle.game.types;

import org.joml.Vector3f;

public interface Builder {
    float DISTANCE = 4f;

    void renderDemo(Vector3f position, Vector3f rotation);

    Buildable build();
}
