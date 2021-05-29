package ru.reactiveturtle.engine.toolkit;

public class Value<T> {
    public T value;

    public Value() {
    }

    public Value(T value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "\"value\":" + value;
    }
}
