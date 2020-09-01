package ru.reactiveturtle.game.tree;

import org.joml.Vector3f;
import ru.reactiveturtle.engine.model.Model;
import ru.reactiveturtle.game.player.GameObject;
import ru.reactiveturtle.game.types.Collectable;

public class WoodStack extends GameObject implements Collectable {
    public WoodStack(int id, String name, Model[] model,
                     Vector3f defaultPosition, Vector3f defaultRotation,
                     Collectable take) {
        super(id, name, model, defaultPosition, defaultRotation);
        mTake = take;
    }

    private Collectable mTake;
    @Override
    public Collectable take() {
        return mTake;
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
