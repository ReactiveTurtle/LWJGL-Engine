package ru.reactiveturtle.engine.module.moving;

import org.joml.Vector3f;
import ru.reactiveturtle.engine.toolkit.lambda.Function;

public class MovingModule {
    private final Function<Movable, Vector3f> movingRule;

    public MovingModule(Function<Movable, Vector3f> movingRule) {
        this.movingRule = movingRule;
    }

    public Vector3f move(Movable movable) {
        return this.movingRule.call(movable);
    }
}
