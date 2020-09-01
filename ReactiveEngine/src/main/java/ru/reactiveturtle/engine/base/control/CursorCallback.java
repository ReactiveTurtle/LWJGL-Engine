package ru.reactiveturtle.engine.base.control;

import org.joml.Vector2f;

public interface CursorCallback {
    void onMouseMove(Vector2f bias);
}
