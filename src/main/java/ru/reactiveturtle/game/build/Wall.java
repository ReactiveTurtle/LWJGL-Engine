package ru.reactiveturtle.game.build;

import org.joml.Vector3f;
import ru.reactiveturtle.engine.model.Model;
import ru.reactiveturtle.game.player.GameObject;
import ru.reactiveturtle.game.types.Buildable;

public class Wall extends GameObject implements Buildable {
    public Wall(int id, String name, Model[] model, Vector3f defaultPosition, Vector3f defaultRotation) {
        super(id, name, model, defaultPosition, defaultRotation);
    }
}
