package ru.reactiveturtle.engine.exceptions;

public class ObjectDisposedException extends RuntimeException{
    public ObjectDisposedException(Class<?> c) {
        super("Object of class \"" + c.getName() + "\" disposed. You shouldn't use this object.");
    }
}
