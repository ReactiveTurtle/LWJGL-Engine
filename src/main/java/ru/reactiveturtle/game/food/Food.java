package ru.reactiveturtle.game.food;

import org.joml.Vector3f;
import ru.reactiveturtle.engine.model.Model;
import ru.reactiveturtle.game.player.GameObject;
import ru.reactiveturtle.game.types.Collectable;
import ru.reactiveturtle.game.types.Eatable;

public class Food extends GameObject implements Collectable, Eatable {
    public Food(int id, String name, Model[] model,
                Vector3f defaultPosition, Vector3f defaultRotation) {
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

    private float mCalories = 0;
    @Override
    public void setCalories(float calories) {
        mCalories = calories;
    }

    @Override
    public float getCalories() {
        return mCalories;
    }
}
