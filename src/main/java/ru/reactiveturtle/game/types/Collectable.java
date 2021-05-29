package ru.reactiveturtle.game.types;

import org.joml.Vector3f;
import ru.reactiveturtle.engine.toolkit.Pair;

public interface Collectable {
    float DISTANCE = 3f;

    Collectable take();

    void setCount(int count);

    int getCount();

    Pair<Vector3f> getPositionAndRotationRelativelyPlayer(Vector3f playerCameraPosition, Vector3f playerCameraRotation);
}
