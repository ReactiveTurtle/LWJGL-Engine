package ru.reactiveturtle.game.hammer;

import ru.reactiveturtle.game.base.Entity;
import ru.reactiveturtle.game.base.EntityState;

public class Hammer extends Entity {
    public Hammer(int id, String name) {
        super(id, name);
    }

    @Override
    protected EntityState[] getDefaultEntityStates() {
        return new EntityState[0];
    }

    @Override
    protected int getNextState() {
        return 0;
    }
}
