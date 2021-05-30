package ru.reactiveturtle.engine.base;

public interface Renderable<T extends Stage> {
    void render(T stage);
}
