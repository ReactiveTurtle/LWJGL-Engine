package ru.reactiveturtle.engine.toolkit;

public class Value<T> {
    public T value;

    public Value() {
    }

    public Value(T value) {
        this.value = value;
    }

    public boolean hasValue() {
        return value != null;
    }

    @Override
    public String toString() {
        return "\"value\":" + value;
    }
}
