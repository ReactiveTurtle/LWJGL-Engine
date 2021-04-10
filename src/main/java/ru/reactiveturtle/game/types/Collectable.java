package ru.reactiveturtle.game.types;

import org.joml.Vector3f;

public interface Collectable {
    float DISTANCE = 3f;

    Collectable take();

    void setCount(int count);

    int getCount();
}
