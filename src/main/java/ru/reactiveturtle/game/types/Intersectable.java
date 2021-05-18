package ru.reactiveturtle.game.types;

import ru.reactiveturtle.engine.camera.PerspectiveCamera;

public interface Intersectable {
    /**
     * @param camera allow get direction and position of player
     * @return must return null when direction isn't intersects object
     * or must return distance to object when direction intersects it
     */
    Float intersect(PerspectiveCamera camera);
}
