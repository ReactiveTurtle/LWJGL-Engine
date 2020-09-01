package ru.reactiveturtle.game.game.player;

import org.joml.Vector3f;
import ru.reactiveturtle.game.engine.model.Model;

public class Collectable extends Static {
    public Type type;

    public Collectable(String name, Model model, Vector3f defaultPosition, Vector3f defaultRotation, Type type) {
        super(name, model, defaultPosition, defaultRotation);
        this.type = type;
    }

    public enum Type {
        STEEL_WEAPON, FOOD
    }
}
