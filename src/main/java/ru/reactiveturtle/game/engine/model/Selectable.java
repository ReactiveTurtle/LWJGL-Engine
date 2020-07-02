package ru.reactiveturtle.game.engine.model;

import org.joml.Vector3f;

public interface Selectable {
    void setSelectBox(float width, float height, float depth);

    void setSelectBoxY(float y);

    Vector3f calcIntersectionPoint(Vector3f point, Vector3f direction, Model model);

    Vector3f getIntersectionPoint();
}
