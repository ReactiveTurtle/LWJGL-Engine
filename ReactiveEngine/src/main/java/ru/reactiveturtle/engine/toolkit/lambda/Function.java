package ru.reactiveturtle.engine.toolkit.lambda;

public interface Function<E, T> {
    T call(E e);
}
