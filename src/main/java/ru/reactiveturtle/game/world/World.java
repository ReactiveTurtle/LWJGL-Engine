package ru.reactiveturtle.game.world;

import org.joml.Vector3f;
import ru.reactiveturtle.engine.base.GameContext;
import ru.reactiveturtle.engine.base.Stage;
import ru.reactiveturtle.engine.camera.PerspectiveCamera;
import ru.reactiveturtle.engine.material.Material;
import ru.reactiveturtle.engine.material.Texture;
import ru.reactiveturtle.engine.model.base.Sphere;
import ru.reactiveturtle.engine.module.moving.MovingModule;
import ru.reactiveturtle.engine.shader.TextureShader;
import ru.reactiveturtle.game.player.Player;
import ru.reactiveturtle.game.player.PlayerMovingModule;
import ru.reactiveturtle.physics.BoxBody;
import ru.reactiveturtle.physics.HeightMap;
import ru.reactiveturtle.physics.RigidBody;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;

public class World extends Stage {
    private MovingModule playerMovingModule;

    private DayNight dayNight;
    private Physic physic;
    private Player player;
    private List<Chunk> chunks = new ArrayList<>();
    private LootMap lootMap;

    private Sphere sphere;

    public World(GameContext gameContext) {
        super(gameContext);
    }

    @Override
    public void start() {
        setCamera(new PerspectiveCamera(getGameContext().getAspectRatio(), 67f, 0.1f, 10000f));
        dayNight = new DayNight();
        physic = new Physic();
        player = new Player(getGameContext().getAspectRatio());
        TextureShader textureShader = new TextureShader();

        setKeyCallback((key, action) -> {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_PRESS) {
                getGameContext().closeWindow();
            }
            if (key == GLFW_KEY_L && action == GLFW_PRESS) {
                player.setLockYMove(!player.isLockYMove());
            }
            if (player.getMovement() == Player.Movement.WALK && key == GLFW_KEY_LEFT_SHIFT && action == GLFW_PRESS) {
                player.setMovement(Player.Movement.RUN);
                if (getGameContext().isKeyPressed(GLFW_KEY_W)) {
                    player.startShaking();
                }
            } else if (player.getMovement() == Player.Movement.WALK && key == GLFW_KEY_LEFT_CONTROL && action == GLFW_PRESS) {
                player.setMovement(Player.Movement.CROUCH);
            } else if ((key == GLFW_KEY_LEFT_CONTROL || key == GLFW_KEY_LEFT_SHIFT) && action == GLFW_RELEASE) {
                if (player.getMovement() == Player.Movement.RUN && getGameContext().isKeyPressed(GLFW_KEY_W)) {
                    player.stopShaking();
                }
                player.setMovement(Player.Movement.WALK);
            }
            if (key == GLFW_KEY_W && action == GLFW_PRESS) {
                if (player.getMovement() == Player.Movement.RUN) {
                    player.startShaking();
                }
            } else if (key == GLFW_KEY_W && action == GLFW_RELEASE) {
                if (player.getMovement() == Player.Movement.RUN) {
                    player.stopShaking();
                }
            }
            if (key == GLFW_KEY_SPACE && action == GLFW_PRESS) {
                if (!player.isJump()) {
                    player.jump();
                }
            }
        });

        getGameContext().setCursorCallback(bias -> {
            player.addRotation(bias.y, bias.x, 0);
        });

        setMouseCallback(new MouseCallback() {
            @Override
            public void onScroll(int direction) {
                super.onScroll(direction);
                player.wheelInventory(-direction);
            }
        });

        getGameContext().getShadowManager().setShadowEnabled(true);

        player = new Player(getGameContext().getAspectRatio());
        BoxBody playerBoxBody = new BoxBody(0.5f, 1.85f, 0.5f);
        player.setRigidBody(playerBoxBody);
        playerBoxBody.setCenter(new Vector3f(0, -0.925f, 0));
        player.getRigidBody().setY(10f);
        player.getRigidBody().setZ(1);
        player.getRigidBody().tag = "player";
        player.getRigidBody().setType(RigidBody.Type.DYNAMIC);
        physic.putBody(playerBoxBody);

        player.setPosition(1f, 45f, 2f);
        player.setRotationX(90);
        player.setLockYMove(true);
        player.setLockTopBottomRotation(true);
        player.setActionListener(new Player.ActionListener() {
            @Override
            public void onHitEnd() {

            }

            @Override
            public void onJumpEnd() {
                if (getGameContext().isKeyPressed(GLFW_KEY_SPACE)) {
                    player.jump();
                } else {
                    if (getGameContext().isKeyPressed(GLFW_KEY_W) &&
                            getGameContext().isKeyPressed(GLFW_KEY_LEFT_SHIFT)) {
                        player.startShaking();
                    }
                }
            }
        });

        addLight(dayNight.getLight());
        Texture texture = new Texture("texture/ground.jpg");
        for (int i = -10; i < 10; i++) {
            for (int j = -10; j < 10; j++) {
                Chunk chunk = new Chunk(314124, j, i, texture);
                chunk.setShader(textureShader);
                chunks.add(chunk);
                physic.putBody(new HeightMap(
                        chunk.getHeightMap().getVertices(),
                        Chunk.X_PARTS,
                        Chunk.Z_PARTS,
                        Chunk.CHUNK_WIDTH,
                        Chunk.CHUNK_DEPTH));
            }
        }
        lootMap = new LootMap(physic, textureShader);

        playerMovingModule = new PlayerMovingModule(this);

        sphere = new Sphere(2, 16, false);
        sphere.addY(2);
        Material sphereMaterial = new Material();
        sphereMaterial.setAmbient(0.1f, 0.1f, 0.2f);
        sphereMaterial.setDiffuse(0.7f, 0.4f, 0.4f);
        sphere.setMaterial(sphereMaterial);
        sphere.setShader(textureShader);
    }

    @Override
    public void render() {
        double deltaTime = getGameContext().getDeltaTime();
        PerspectiveCamera camera = getCamera();
        Vector3f playerTranslation = playerMovingModule.move(player);

        camera.setRotation(player.getRotation());
        physic.update(deltaTime);

        if (!player.isLockYMove()) {
            camera.addPosition(playerTranslation);
        } else {
            player.setPosition(player.getRigidBody().getPosition().add(0, 1.85f, 0));
            camera.setPosition(player.getCameraPosition());
        }

        getGameContext().getShadowManager().renderShadow(this, player);

        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        glViewport(0, 0, getGameContext().width, getGameContext().height);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        dayNight.update(deltaTime);
        for (Chunk chunk : chunks) {
            chunk.render(this);
        }
        lootMap.render(this);
        player.render(this, deltaTime);
        player.renderUI(this, deltaTime);
    }
}
