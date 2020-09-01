package ru.reactiveturtle.game.tool;

import org.joml.Vector3f;
import ru.reactiveturtle.engine.model.Model;
import ru.reactiveturtle.game.tool.Tool;
import ru.reactiveturtle.game.types.Destroyer;

public class Axe extends Tool implements Destroyer {
    public Axe(int id, String name, Model[] model, Vector3f defaultPosition, Vector3f defaultRotation) {
        super(id, name, model, defaultPosition, defaultRotation);
    }

    private float mPower = 4;

    @Override
    public void setPower(float power) {
        mPower = power;
    }

    @Override
    public float getPower() {
        return mPower;
    }
}
