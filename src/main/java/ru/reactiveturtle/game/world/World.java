package ru.reactiveturtle.game.world;

import org.joml.Vector3f;
import ru.reactiveturtle.engine.base.GameContext;
import ru.reactiveturtle.engine.base3d.Stage3D;
import ru.reactiveturtle.engine.camera.PerspectiveCamera;
import ru.reactiveturtle.engine.material.Material;
import ru.reactiveturtle.engine.texture.Texture;
import ru.reactiveturtle.engine.model.base.Sphere;
import ru.reactiveturtle.engine.module.moving.MovingModule;
import ru.reactiveturtle.engine.shader.TextureShader;
import ru.reactiveturtle.engine.toolkit.MathExtensions;
import ru.reactiveturtle.engine.ui.Label;
import ru.reactiveturtle.game.Log;
import ru.reactiveturtle.game.base.Entity;
import ru.reactiveturtle.game.player.Player;
import ru.reactiveturtle.game.player.PlayerMovingModule;
import ru.reactiveturtle.game.types.Collectable;
import ru.reactiveturtle.physics.TerrainBody;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;

public class World extends Stage3D {
    private MovingModule playerMovingModule;

    private DayNight dayNight;
    private Physic physic;
    private Player player;
    private List<Chunk> chunks = new ArrayList<>();
    private LootMap lootMap;

    private Sun sun;
    private Sphere sphere;

    public World(GameContext gameContext) {
        super(gameContext);
    }

    private Label logLabel, intersectionLabel;

    @Override
    public void start() {
        setCamera(new PerspectiveCamera(getGameContext().getAspectRatio(), 67f, 0.01f, 10000f));
        dayNight = new DayNight();
        physic = new Physic();
        player = new Player(gameContext);
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
            if (action == GLFW_PRESS) {
                switch (key) {
                    case GLFW_KEY_E:
                        Entity observable = player.getObservableEntity();
                        if (player.takeObservable()) {
                            physic.removeBody(observable.getCurrentState().getBody());
                            lootMap.remove(observable.getId());
                            intersectionLabel.setText("");
                        }
                        break;
                    case GLFW_KEY_Q:
                        Entity throwed = player.throwCurrentInventoryItem();
                        if (throwed != null) {
                            physic.putBody(throwed.getCurrentState().getBody());
                            lootMap.add(throwed);
                        }
                        break;
                }
            }
        });

        getGameContext().getShadowManager().setShadowEnabled(true);

        physic.putBody(player.getRigidBody());

        player.setPosition(1f, 45f, 2f);
        player.setRotationX(0);
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
        Texture texture = new Texture("texture/sand.jpg");
        for (int i = -10; i < 10; i++) {
            for (int j = -10; j < 10; j++) {
                Chunk chunk = new Chunk(314124, j, i, texture);
                chunk.setShader(textureShader);
                chunks.add(chunk);
                TerrainBody terrainBody = new TerrainBody(
                        chunk.getTerrain().getVertices(),
                        Chunk.X_PARTS,
                        Chunk.Z_PARTS,
                        Chunk.CHUNK_WIDTH,
                        Chunk.CHUNK_DEPTH);
                terrainBody.setPosition(chunk.getTerrain().getPosition());
                physic.putBody(terrainBody);
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

        sun = new Sun(textureShader);
        sun.setPosition(0, 40, 0);

        logLabel = new Label(uiContext);
        logLabel.setFontSize(40);
        logLabel.setPosition(logLabel.getWidth() - gameContext.getAspectRatio(), 1 - logLabel.getHeight(), 0);
        logLabel.setBackground(0, 0, 0, 0x42);
        uiContext.getUILayout().add(logLabel);

        intersectionLabel = new Label(uiContext);
        intersectionLabel.setPosition(0, 0, 0);
        intersectionLabel.setFontSize(40);
        uiContext.getUILayout().add(intersectionLabel);
        lootMap.setIntersectionListener(new LootMap.IntersectionListener() {
            @Override
            public void onIntersect(Entity entity, Float distance) {
                if (player.getObservableEntity() != entity) {
                    player.setObservableObject(entity);
                }
                Entity observable = player.getObservableEntity();
                String text = "";
                if (observable instanceof Collectable) {
                    text += "Нажмите E чтобы подобрать";
                }
                text += "\n" + observable.getTag() + ": " + MathExtensions.round(distance, 2) + " m";
                intersectionLabel.setText(text);
            }

            @Override
            public void onNotIntersect() {
                if (player.getObservableEntity() != null) {
                    player.setObservableObject(null);
                    intersectionLabel.setText("");
                }
            }
        });
    }

    @Override
    public void render() {
        Log.inFrustum = "";
        double deltaTime = getGameContext().getDeltaTime();
        PerspectiveCamera camera = getCamera();
        Vector3f playerTranslation = playerMovingModule.move(player);

        camera.setRotation(player.getRotation());
        physic.update(deltaTime);

        if (!player.isLockYMove()) {
            camera.addPosition(playerTranslation.mul(10));
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
        sun.render(this);
        player.render(this, deltaTime);

        Vector3f playerPosition = player.getPosition();
        playerPosition.x = MathExtensions.round(playerPosition.x, 1);
        playerPosition.y = MathExtensions.round(playerPosition.y, 1);
        playerPosition.z = MathExtensions.round(playerPosition.z, 1);
        logLabel.setText("x: " + playerPosition.x
                + "\ny: " + playerPosition.y
                + "\nz: " + playerPosition.z
                + "\nCamera position: "
                + "\nx: " + MathExtensions.round(camera.getX(), 1)
                + "\ny: " + MathExtensions.round(camera.getY(), 1)
                + "\nz: " + MathExtensions.round(camera.getZ(), 1)
                + "\n" + Log.inFrustum);
        logLabel.setPosition(logLabel.getWidth() - gameContext.getAspectRatio(), 1 - logLabel.getHeight(), 0);
    }
}
