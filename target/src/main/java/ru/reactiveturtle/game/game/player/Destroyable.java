package ru.reactiveturtle.game.game.player;

import org.joml.Vector3f;
import ru.reactiveturtle.game.engine.model.Model;

public class Destroyable extends Static {
    private Model substitute;

    public Destroyable(String name, Model model, Vector3f defaultPosition, Vector3f defaultRotation, Model substitute) {
        super(name, model, defaultPosition, defaultRotation);
        this.substitute = substitute;
    }

    public Static getSubstitute() {
        Static staticObject = new Static(name, substitute, defaultPosition, defaultRotation);
        staticObject.mBoxParams = mBoxParams;
        staticObject.mSelectBoxX = mSelectBoxX;
        staticObject.mSelectBoxY = mSelectBoxY;
        return staticObject;
    }
}
