package ru.reactiveturtle.game.food;

import org.joml.Vector3f;
import ru.reactiveturtle.engine.model.Model;

public class Banana extends Food {
    public Banana(int id, Model[] model, Vector3f defaultPosition, Vector3f defaultRotation) {
        super(id, "Banana", model, defaultPosition, defaultRotation);
    }
}
