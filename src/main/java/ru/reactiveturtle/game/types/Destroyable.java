package ru.reactiveturtle.game.types;

import ru.reactiveturtle.game.player.GameObject;

public interface Destroyable extends ru.reactiveturtle.game.types.Destructible {
    float DISTANCE = 1.5f;

    void hit(float power);

    GameObject destroy();
}
