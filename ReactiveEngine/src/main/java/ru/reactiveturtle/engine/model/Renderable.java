package ru.reactiveturtle.engine.model;

import ru.reactiveturtle.engine.base.Stage;

public interface Renderable<T extends Stage> {
    void render(T stage);
}
