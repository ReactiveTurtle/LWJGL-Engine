package ru.reactiveturtle.game.tool;

import org.joml.Vector3f;
import ru.reactiveturtle.engine.model.Model;
import ru.reactiveturtle.game.player.GameObject;
import ru.reactiveturtle.game.types.Buildable;
import ru.reactiveturtle.game.types.Builder;

public class Hammer extends Tool implements Builder {
    public Hammer(int id, String name, Model[] model, Vector3f defaultPosition, Vector3f defaultRotation, Buildable buildable) {
        super(id, name, model, defaultPosition, defaultRotation);
        takeObject = (GameObject) buildable;
    }

    private GameObject takeObject;
    @Override
    public void renderDemo(Vector3f position, Vector3f rotation) {
        takeObject.getModel().setPosition(position);
        takeObject.getModel().setRotation(rotation);
        takeObject.getModel().render();
    }

    @Override
    public Buildable build() {
        return (Buildable) takeObject;
    }
}
