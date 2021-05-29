package ru.reactiveturtle.game.player;

import org.joml.Vector3f;
import ru.reactiveturtle.engine.base3d.Stage3D;
import ru.reactiveturtle.engine.camera.PerspectiveCamera;
import ru.reactiveturtle.engine.module.moving.Movable;
import ru.reactiveturtle.engine.module.moving.MovingModule;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;

public class PlayerMovingModule extends MovingModule {
    private Stage3D stage;

    public PlayerMovingModule(Stage3D stage) {
        super((movable -> new Vector3f()));
        this.stage = stage;
    }

    @Override
    public Vector3f move(Movable movable) {
        Player player = (Player) movable;
        double deltaTime = stage.getGameContext().getDeltaTime();
        PerspectiveCamera camera = stage.getCamera();
        Vector3f translation = new Vector3f();
        if (stage.getGameContext().isKeyPressed(GLFW_KEY_W)) {
            translation.z = -player.getMovementSpeed();
        }
        if (stage.getGameContext().isKeyPressed(GLFW_KEY_A)) {
            translation.x = -player.getMovementSpeed();
        }
        if (stage.getGameContext().isKeyPressed(GLFW_KEY_S)) {
            translation.z = player.getMovementSpeed();
        }
        if (stage.getGameContext().isKeyPressed(GLFW_KEY_D)) {
            translation.x = player.getMovementSpeed();
        }
        translation.mul((float) deltaTime);
        if (!player.isLockYMove()) {
            translation.rotateX(-camera.getRotationX());
        }
        translation.rotateY(-camera.getRotationY());

        if (player.isLockYMove()) {
            player.getRigidBody().translate(translation);
        }
        return translation;
    }
}
