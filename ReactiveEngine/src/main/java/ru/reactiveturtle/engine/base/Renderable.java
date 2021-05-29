package ru.reactiveturtle.engine.base;

import ru.reactiveturtle.engine.base.Stage;

public interface Renderable<T extends Stage> {
    void render(T stage);
}
