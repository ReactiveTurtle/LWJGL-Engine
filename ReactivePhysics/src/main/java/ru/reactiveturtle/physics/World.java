package ru.reactiveturtle.physics;

import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class World {
    private Vector3f gravity = new Vector3f();
    private List<RigidBody> rigidBodies = new ArrayList<>();
    private List<CollisionResult> rigidBodyCollisions = new ArrayList<>();

    public World(Vector3f gravity) {
        this.gravity.set(gravity);
    }

    public void update(double deltaTime, int testCount) {
        long startTime = System.currentTimeMillis();
        if (testCount < 1) {
            throw new IllegalArgumentException("Parameter testCount must be >= 1");
        }
        if (deltaTime < 0) {
            throw new IllegalArgumentException("Parameter deltaTime must be >= 0");
        }

        for (CollisionResult rigidBodyCollision : rigidBodyCollisions) {
            rigidBodyCollision.clear();
        }

        for (int i = 0; i < rigidBodies.size(); i++) {
            RigidBody first = rigidBodies.get(i);
            if (first.getType() != RigidBody.Type.STATIC) {
                first.flyTime += deltaTime;
                float yTranslation = (first.startFlyY + first.startYVelocity * first.flyTime +
                        (-9.8f * first.flyTime * first.flyTime / 2)) - first.lastY;
                first.translation.add(0, yTranslation, 0);
                CollisionResult collisionResult = rigidBodyCollisions.get(i);
                for (int j = 0; j < rigidBodies.size(); j++) {
                    if (i != j) {
                        RigidBody second = rigidBodies.get(j);
                        CollisionResult result = first.isCollide(second, testCount);

                        CollisionResult newCollision = rigidBodyCollisions.get(j);
                        newCollision.add(result);
                        collisionResult.add(newCollision);
                        rigidBodyCollisions.set(j, newCollision);
                    }
                }
                rigidBodyCollisions.set(i, collisionResult);
                if (rigidBodyCollisions.get(i).isYCollide()) {
                    first.startYVelocity = 0;
                    first.flyTime = 0;
                    first.startFlyY = first.getY();
                }
            }
        }

        for (int i = 0; i < rigidBodyCollisions.size(); i++) {
            RigidBody rigidBody = rigidBodies.get(i);
            if (rigidBody.getType() != RigidBody.Type.STATIC) {
                rigidBody.addPosition(rigidBody.translation);
                rigidBody.lastY = rigidBody.getY();
            }
            rigidBody.log = rigidBodyCollisions.get(i).isXCollide() + ", "
                    + rigidBodyCollisions.get(i).isYCollide() + ", "
                    + rigidBodyCollisions.get(i).isZCollide() + ", "
                    + rigidBody.translation.x + ", "
                    + rigidBody.translation.y + ", "
                    + rigidBody.translation.z + ", ";
            rigidBody.translation.set(0);
        }
    }

    public void addRigidBodies(RigidBody... rigidBodies) {
        this.rigidBodies.addAll(Arrays.asList(rigidBodies));
        for (int i = 0; i < rigidBodies.length; i++) {
            this.rigidBodyCollisions.add(new CollisionResult());
        }
    }

    public boolean removeRigidBody(RigidBody rigidBody) {
        for (int i = 0; i < rigidBodies.size(); i++) {
            if (rigidBodies.get(i).getId() != null && rigidBody.getId() != null
                    && rigidBodies.get(i).getId().equals(rigidBody.getId())) {
                rigidBodies.remove(i);
                rigidBodyCollisions.remove(i);
                return true;
            }
        }
        return false;
    }
}
