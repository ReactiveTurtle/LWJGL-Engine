package ru.reactiveturtle.game.tree;

import org.joml.Vector3f;
import ru.reactiveturtle.engine.model.Model;
import ru.reactiveturtle.game.player.GameObject;
import ru.reactiveturtle.game.types.Destroyable;

public class Tree extends GameObject implements Destroyable {
    public Tree(int id, String name, Model[] model,
                Vector3f defaultPosition, Vector3f defaultRotation,
                GameObject drop) {
        super(id, name, model, defaultPosition, defaultRotation);
        mDrop = drop;
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

    @Override
    public void hit(float power) {
        mStrength -= power;
    }

    private GameObject mDrop;
    @Override
    public GameObject destroy() {
        return mDrop;
    }
}
