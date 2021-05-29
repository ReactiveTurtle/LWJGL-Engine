package ru.reactiveturtle.game.buildings;

import ru.reactiveturtle.game.base.Entity;
import ru.reactiveturtle.game.base.EntityState;

public class Wall extends Entity {
    public Wall(int id, String name) {
        super(id, name);
    }

    @Override
    protected EntityState[] getDefaultEntityStates() {
        return new EntityState[0];
    }

    @Override
    public int getNextState() {
        return 0;
    }
}
