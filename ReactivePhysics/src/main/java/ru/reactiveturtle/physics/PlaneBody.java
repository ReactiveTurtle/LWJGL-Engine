package ru.reactiveturtle.physics;

import org.joml.Vector3f;

public class PlaneBody extends RigidBody {
    private final Vector3f mNormal;
    public PlaneBody(Vector3f normal) {
        mNormal = normal;
    }

    public Vector3f getNormal() {
        return new Vector3f(mNormal);
    }

    @Override
    public CollisionResult isCollide(RigidBody rigidBody, int testCount) {
        return rigidBody instanceof PlaneBody ||
                rigidBody instanceof TerrainBody ?
                new CollisionResult(false) :
                rigidBody.isCollide(this, testCount);
    }

    @Override
    public RigidBody copy() {
        return null;
    }
}
