package ru.reactiveturtle.game.tree;

import org.joml.Vector3f;
import ru.reactiveturtle.engine.model.Model;
import ru.reactiveturtle.game.player.GameObject;

public class Palm extends Tree {
    public Palm(int id, Model[] model, Vector3f defaultPosition, Vector3f defaultRotation, GameObject drop) {
        super(id, "Palm", model, defaultPosition, defaultRotation, drop);
    }
}
