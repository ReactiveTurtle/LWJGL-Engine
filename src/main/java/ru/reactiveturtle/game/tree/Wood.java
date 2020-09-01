package ru.reactiveturtle.game.tree;

import org.joml.Vector3f;
import ru.reactiveturtle.engine.model.Model;
import ru.reactiveturtle.game.player.GameObject;
import ru.reactiveturtle.game.types.Collectable;
import ru.reactiveturtle.game.types.Combustible;

public class Wood extends GameObject implements Collectable, Combustible {
    public Wood(int id, String name, Model[] model, Vector3f defaultPosition, Vector3f defaultRotation) {
        super(id, name, model, defaultPosition, defaultRotation);
    }

    private float mBurningTime = 0;

    @Override
    public void setBurningTime(float burningTime) {
        mBurningTime = burningTime;
    }

    @Override
    public float getBurningTime() {
        return mBurningTime;
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
}
