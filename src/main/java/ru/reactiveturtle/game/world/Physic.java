package ru.reactiveturtle.game.world;

import org.joml.Vector3f;
import ru.reactiveturtle.game.player.GameObject;
import ru.reactiveturtle.physics.PlaneBody;
import ru.reactiveturtle.physics.RigidBody;
import ru.reactiveturtle.physics.World;

public class Physic {
    private World world;

    public Physic() {
        world = new World(new Vector3f(0, 9.8f, 0));
        PlaneBody planeBody = new PlaneBody(new Vector3f(0f, 1f, 0f));
        world.addRigidBodies(planeBody);
    }

    public void putObjects(GameObject... objects) {
        for (GameObject object : objects) {
            if (object.getRigidBody() != null) {
                world.addRigidBodies(object.getRigidBody());
            }
        }
    }

    public void putBody(RigidBody rigidBody) {
        if (rigidBody != null) {
            world.addRigidBodies(rigidBody);
        }
    }


    public void removeBody(RigidBody rigidBody) {
        world.removeRigidBody(rigidBody);
    }

    public void update(double deltaTime) {
        world.update(deltaTime, 32);
    }
}
