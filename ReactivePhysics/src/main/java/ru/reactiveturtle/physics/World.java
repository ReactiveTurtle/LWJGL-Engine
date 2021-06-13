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
        if (testCount < 1) {
            throw new IllegalArgumentException("Parameter testCount must be >= 1");
        }
        if (deltaTime <= 0) {
            throw new IllegalArgumentException("Parameter deltaTime must be >= 0");
        }

        for (CollisionResult rigidBodyCollision : rigidBodyCollisions) {
            rigidBodyCollision.clear();
        }

        for (int i = 0; i < rigidBodies.size(); i++) {
            RigidBody first = rigidBodies.get(i);
            if (first.getType() != RigidBody.Type.STATIC) {
                float lastFlyTime = first.flyTime;
                first.flyTime += deltaTime;
                float oldY = (first.startFlyY + first.startYVelocity * lastFlyTime +
                        (-9.8f * lastFlyTime * lastFlyTime / 2));
                float newY = (first.startFlyY + first.startYVelocity * first.flyTime +
                        (-9.8f * first.flyTime * first.flyTime / 2));
                float yTranslation = newY - oldY;
                first.translation.add(0, yTranslation, 0);
                CollisionResult collisionResult = rigidBodyCollisions.get(i);
                for (int j = 0; j < rigidBodies.size(); j++) {
                    if (i != j) {
                        RigidBody second = rigidBodies.get(j);
                        CollisionResult result = first.isCollide(second, testCount);
                        collisionResult.add(result);
                    }
                }
                rigidBodyCollisions.set(i, collisionResult);
                if (first.tag.equals("Dragunov Sniper Riffle")) {
                    System.out.println(first);
                    System.out.println(newY);
                    System.out.println(yTranslation);
                }
                if (rigidBodyCollisions.get(i).isYCollide()) {
                    first.startYVelocity = 0;
                    first.flyTime = 0;
                    first.startFlyY = first.getY();
                }
            }
        }

        for (int i = 0; i < rigidBodyCollisions.size(); i++) {
            RigidBody rigidBody = rigidBodies.get(i);
            rigidBody.log = rigidBody.tag + ": "
                    + rigidBodyCollisions.get(i).isXCollide() + ", "
                    + rigidBodyCollisions.get(i).isYCollide() + ", "
                    + rigidBodyCollisions.get(i).isZCollide() + ", "
                    + rigidBody.translation.x + ", "
                    + rigidBody.translation.y + ", "
                    + rigidBody.translation.z + ", ";
            if (rigidBody.getType() != RigidBody.Type.STATIC) {
                rigidBody.addPosition(rigidBody.translation);
                if (rigidBody.tag.equals("Dragunov Sniper Riffle")) {
                    System.out.println(rigidBody.log);
                }
            }
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
        int index = rigidBodies.indexOf(rigidBody);
        if (index > -1) {
            rigidBodies.remove(index);
            rigidBodyCollisions.remove(index);
            return true;
        }
        return false;
    }
}
