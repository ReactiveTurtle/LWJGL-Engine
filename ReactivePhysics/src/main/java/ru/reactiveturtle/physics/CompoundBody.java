package ru.reactiveturtle.physics;

public class CompoundBody extends RigidBody {
    @Override
    public CollisionResult isCollide(RigidBody rigidBody, int testCount) {
        return new CollisionResult();
    }
}
