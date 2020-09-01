package ru.reactiveturtle.game.food;

import org.joml.Vector3f;
import ru.reactiveturtle.engine.model.Model;
import ru.reactiveturtle.game.player.GameObject;
import ru.reactiveturtle.game.types.Collectable;

public class BananaStack extends GameObject implements Collectable {
    public BananaStack(int id, Model[] model, Vector3f defaultPosition, Vector3f defaultRotation, Collectable take) {
        super(id, "Banana stack", model, defaultPosition, defaultRotation);
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
