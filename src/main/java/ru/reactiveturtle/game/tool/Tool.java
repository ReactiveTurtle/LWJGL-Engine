package ru.reactiveturtle.game.tool;

import org.joml.Vector3f;
import ru.reactiveturtle.engine.model.Model;
import ru.reactiveturtle.game.player.GameObject;
import ru.reactiveturtle.game.types.Collectable;
import ru.reactiveturtle.game.types.Destructible;

public class Tool extends GameObject implements Collectable, Destructible {
    public Tool(int id, String name, Model[] model, Vector3f defaultPosition, Vector3f defaultRotation) {
        super(id, name, model, defaultPosition, defaultRotation);
    }

    @Override
    public Collectable take() {
        return this;
    }

    private int mCount = 1;
    @Override
    public void setCount(int count) {
        mCount = count;
        currentModel = mCount - 1;
    }

    @Override
    public int getCount() {
        return mCount;
    }

    private float mStrength = 0;
    @Override
    public void setStrength(float strength) {
        mStrength = strength;
    }

    @Override
    public float getStrength() {
        return mStrength;
    }
}
