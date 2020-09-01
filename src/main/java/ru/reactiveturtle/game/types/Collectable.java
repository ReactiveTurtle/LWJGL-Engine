package ru.reactiveturtle.game.types;

public interface Collectable {
    float DISTANCE = 3f;

    Collectable take();

    void setCount(int count);

    int getCount();
}
