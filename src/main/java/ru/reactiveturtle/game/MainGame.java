package ru.reactiveturtle.game;

import javafx.util.Pair;
import org.joml.Vector3f;
import org.joml.Vector4f;
import ru.reactiveturtle.engine.base.GameContext;
import ru.reactiveturtle.engine.camera.PerspectiveCamera;
import ru.reactiveturtle.engine.light.PointLight;
import ru.reactiveturtle.engine.light.SpotLight;
import ru.reactiveturtle.engine.material.Material;
import ru.reactiveturtle.engine.material.Texture;
import ru.reactiveturtle.engine.model.HeightMap;
import ru.reactiveturtle.engine.model.Model;
import ru.reactiveturtle.game.player.Selectable;
import ru.reactiveturtle.engine.model.base.Sphere;
import ru.reactiveturtle.engine.model.loader.ObjLoader;
import ru.reactiveturtle.engine.model.base.Parallelepiped;
import ru.reactiveturtle.engine.particle.ParticleShader;
import ru.reactiveturtle.engine.shader.TextureShader;
import ru.reactiveturtle.game.campfire.Campfire;
import ru.reactiveturtle.game.player.Player;
import ru.reactiveturtle.game.player.GameObject;
import ru.reactiveturtle.game.tool.Hammer;
import ru.reactiveturtle.game.tool.Tool;
import ru.reactiveturtle.game.types.*;
import ru.reactiveturtle.game.world.Arrangement;
import ru.reactiveturtle.game.world.BoxBodyModel;
import ru.reactiveturtle.game.world.DayNight;
import ru.reactiveturtle.game.world.Physic;
import ru.reactiveturtle.physics.BoxBody;
import ru.reactiveturtle.physics.RigidBody;

import java.io.IOException;
import java.util.ArrayList;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL30.*;

public class MainGame extends GameContext {
    private DayNight dayNight;
    private SpotLight spotLight;

    private TextureShader textureShader;

    private Material material;

    private Texture normalMap;

    ArrayList<Model> lightModels = new ArrayList<>();

    private Player player;

    private Arrangement mArrangement;

    private Pair<GameObject, Vector3f> intersectionPoint;

    private Physic mPhysic;

    private HeightMap heightMap;

    private Material heightMapMaterial;

    @Override
    protected void run() {
        setCamera(new PerspectiveCamera(67f, 0.1f, 10000f));
        textureShader = new TextureShader();
        material = new Material();
        material.setReflectance(20f);
        material.setDiffuse(1f, 1f, 1f);
        material.setSpecular(1f, 1f, 1f);
        material.setAmbient(1f, 1f, 1f);

        setCursorCallback(bias -> {
            player.addRotation(bias.y, bias.x, 0);
            intersectionPoint = mArrangement.getNearestIntersection(
                    camera.getPosition(), camera.getDirection(), null);
        });

        glfwSetKeyCallback(defaultWindow, (window, key, scancode, action, mods) -> {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_PRESS) {
                glfwSetWindowShouldClose(window, true);
                System.exit(0);
            }
            if (key == GLFW_KEY_L && action == GLFW_PRESS) {
                player.setLockYMove(!player.isLockYMove());
            }
            if (key == GLFW_KEY_F1 && action == GLFW_PRESS) {
                if (heightMapMaterial.getNormalMap() == null) {
                    heightMapMaterial.setNormalMap(normalMap);
                } else {
                    heightMapMaterial.setNormalMap(null);
                }
            }
            if (player.getMovement() == Player.Movement.WALK && key == GLFW_KEY_LEFT_SHIFT && action == GLFW_PRESS) {
                player.setMovement(Player.Movement.RUN);
                if (glfwGetKey(defaultWindow, GLFW_KEY_W) == GLFW_PRESS) {
                    player.startShaking();
                }
            } else if (player.getMovement() == Player.Movement.WALK && key == GLFW_KEY_LEFT_CONTROL && action == GLFW_PRESS) {
                player.setMovement(Player.Movement.CROUCH);
            } else if ((key == GLFW_KEY_LEFT_CONTROL || key == GLFW_KEY_LEFT_SHIFT) && action == GLFW_RELEASE) {
                if (player.getMovement() == Player.Movement.RUN && glfwGetKey(defaultWindow, GLFW_KEY_W) == GLFW_PRESS) {
                    player.stopShaking();
                }
                player.setMovement(Player.Movement.WALK);
            }
            if (key == GLFW_KEY_Q && action == GLFW_PRESS) {
                if (!player.isRightHandEmpty()) {
                    GameObject throwed = mArrangement.copyObject((GameObject) player.throwIntentoryItem());
                    throwed.setDefaultPosition(new Vector3f(player.getX(), throwed.getDefaultPosition().y, player.getZ()));
                    throwed.setDefaultRotation(new Vector3f(0, player.getRotationY(), throwed.getDefaultRotation().z));
                    mArrangement.putObject(throwed);
                }
            }
            if (key == GLFW_KEY_E && action == GLFW_PRESS) {
                if (player.getObservableId() != null) {
                    GameObject item = mArrangement.getObject(player.getObservableId());
                    if (item != null) {
                        if (item instanceof Collectable) {
                            Collectable collectable = (Collectable) item;
                            if (player.takeInventoryItem(collectable.take())) {
                                collectable.setCount(((Collectable) item).getCount() - 1);
                                if (collectable.getCount() == 0) {
                                    collectable.setCount(1);
                                    mArrangement.removeObject(player.getObservableId());
                                }
                            }
                        } else if (item instanceof Container) {
                            Container container = (Container) item;
                            if (container.put(player.getRightHandTool())) {
                                player.removeCurrentInventoryItem();
                            }
                        }
                    }
                }
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

        glfwSetMouseButtonCallback(defaultWindow, (window, button, action, mods) -> {
            pressLeftMouseButton();
            pressRightMouseButton();
        });

        glfwSetScrollCallback(defaultWindow, (l, dx, dy) -> player.wheelInventory(-dy));

        getShadowManager().setShadowEnabled(true);
        setupLights();

        player = new Player();
        BoxBody playerBoxBody = new BoxBody(0.5f, 1.85f, 0.5f);
        player.setRigidBody(playerBoxBody);
        playerBoxBody.setCenter(new Vector3f(0, -0.925f, 0));
        player.getRigidBody().setY(1000f);
        player.getRigidBody().setZ(1);
        player.getRigidBody().tag = "player";
        player.getRigidBody().setType(RigidBody.Type.DYNAMIC);

        player.setPosition(1f, 45f, 2f);
        player.setRotationX(90);
        player.setLockYMove(true);
        player.setLockTopBottomRotation(true);
        player.setActionListener(new Player.ActionListener() {
            @Override
            public void onHitEnd() {
                pressLeftMouseButton();
            }

            @Override
            public void onJumpEnd() {
                if (glfwGetKey(defaultWindow, GLFW_KEY_SPACE) == GLFW_PRESS) {
                    player.jump();
                } else {
                    if (glfwGetKey(defaultWindow, GLFW_KEY_W) == GLFW_PRESS &&
                            glfwGetKey(defaultWindow, GLFW_KEY_LEFT_SHIFT) == GLFW_PRESS) {
                        player.startShaking();
                    }
                }
            }
        });


        heightMapMaterial = new Material();
        normalMap = new Texture("texture/wall.jpg");
        heightMapMaterial.setDiffuse(1f, 1f, 1f);
        heightMapMaterial.setTexture(new Texture("texture/sand.jpg"));
        heightMap = HeightMap.create(
                new Texture("texture/heightmap.png"),
                10000, 400, 10000, 8, 8);
        heightMap.setMaterial(heightMapMaterial);

        PointLight campfireLight = new PointLight();
        campfireLight.setDiffuse(0.6f, 0.6f, 0.4f);
        campfireLight.getAttenuation().linear = 0.2f;
        campfireLight.setPosition(0, 1f, 0f);
        addLight(campfireLight);
        mArrangement = new Arrangement(textureShader, heightMap);
        Campfire campfire = new Campfire(mArrangement.generateId(), "Campfire", campfireLight, textureShader, new ParticleShader());
        campfire.getDefaultPosition().y = heightMap.getY(campfire.getDefaultPosition().x, campfire.getDefaultPosition().z);
        campfire.setDefaultPosition(campfire.getDefaultPosition());
        campfire.setDefaultRotation(
                heightMap.getRotation(campfire.getDefaultPosition().x,
                        campfire.getDefaultPosition().z));
        mArrangement.putObject(campfire);
        mArrangement.campfireFlame = campfire.getFlame();

        mPhysic = new Physic();
        mPhysic.putBody(new ru.reactiveturtle.physics.HeightMap(
                heightMap.getVertices(),
                heightMap.getHeightMap().getWidth(),
                heightMap.getHeightMap().getHeight(),
                heightMap.getWidth(),
                heightMap.getHeight()
        ));
        mPhysic.putObjects(mArrangement.getObjects());
        mPhysic.putBody(player.getRigidBody());
    }

    @Override
    protected void render() {
        System.out.println("-----------------------------------------------");
        dayNight.update(getDeltaTime());

        Vector3f translate = new Vector3f();
        if (glfwGetKey(defaultWindow, GLFW_KEY_W) == GLFW_PRESS) {
            translate.z = -player.getMovementSpeed();
        }
        if (glfwGetKey(defaultWindow, GLFW_KEY_A) == GLFW_PRESS) {
            translate.x = -player.getMovementSpeed();
        }
        if (glfwGetKey(defaultWindow, GLFW_KEY_S) == GLFW_PRESS) {
            translate.z = player.getMovementSpeed();
        }
        if (glfwGetKey(defaultWindow, GLFW_KEY_D) == GLFW_PRESS) {
            translate.x = player.getMovementSpeed();
        }
        translate.mul((float) getDeltaTime());
        if (!player.isLockYMove()) {
            translate.rotateX((float) Math.toRadians(-camera.getRotationX()));
        }
        translate.rotateY((float) Math.toRadians(-camera.getRotationY()));

        if (player.isLockYMove()) {
            player.getRigidBody().translate(translate);
        }
        camera.setRotation(player.getRotation());
        mPhysic.update(getDeltaTime());
        player.getUI().setIntersectionText(player.getRigidBody().log);

        if (!player.isLockYMove()) {
            camera.addPosition(translate);
        } else {
            player.setPosition(player.getRigidBody().getPosition().add(0, 1.85f, 0));
            camera.setPosition(player.getCameraPosition());
        }

        getShadowManager().renderShadow(player, mArrangement);

        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        glViewport(0, 0, width, height);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        player.render(getDeltaTime());
        for (Model lightModel : lightModels) {
            lightModel.render();
        }

        float length;
        if (intersectionPoint != null &&
                ((length = player.getCameraPosition().sub(intersectionPoint.getValue()).length()) <= Collectable.DISTANCE &&
                        (intersectionPoint.getKey() instanceof Collectable || intersectionPoint.getKey() instanceof Container) ||
                        length <= Destroyable.DISTANCE &&
                                intersectionPoint.getKey() instanceof Destructible)) {
            player.getUI().notify(intersectionPoint.getKey());
            player.setObservableObject(intersectionPoint.getKey().getId());

        } else if (player.getObservableId() != null) {
            player.getUI().notify(null);
            player.setObservableObject(null);
        }
        if (player.getRightHandTool() != null) {
            if (player.getRightHandTool() instanceof Hammer) {
                Vector3f intersect = Selectable.intersectPoint(camera.getDirection(),
                        player.getCameraPosition(),
                        new Vector3f(0, 1, 0),
                        new Vector3f());
                Vector4f location = mArrangement.getNearestWallLocation(camera.getRotationY(),
                        player.getCameraPosition(), camera.getDirection());
                if (new Vector3f(intersect).sub(player.getCameraPosition()).length() <= Builder.DISTANCE ||
                        location != null && new Vector3f(location.x, location.y, location.z).sub(player.getCameraPosition()).length() <= Builder.DISTANCE) {
                    Builder builder = (Builder) player.getRightHandTool();
                    if (location == null) {
                        location = new Vector4f(intersect, -player.getRotationY());
                        if (glfwGetKey(defaultWindow, GLFW_KEY_T) == GLFW_PRESS) {
                            location.w = Math.round(location.w / 45f) * 45;
                        }
                    }
                    builder.renderDemo(new Vector3f(location.x, location.y, location.z), new Vector3f(0, location.w, 0));
                }
            }
        }
        heightMap.render();
        mArrangement.render(camera.getDirection(), camera.getPosition());
        player.renderUI(getDeltaTime());
    }

    @Override
    protected void destroy() {
        mArrangement.release();
    }

    private void pressLeftMouseButton() {
        if (glfwGetMouseButton(defaultWindow, GLFW_MOUSE_BUTTON_LEFT) == GLFW_PRESS) {
            Integer observable = player.getObservableId();
            if (!player.isHit() && !player.isRightHandEmpty()) {
                if (player.getRightHandTool() instanceof Tool) {
                    Tool tool = (Tool) player.getRightHandTool();
                    if (tool instanceof Builder) {
                        Builder builder = (Builder) tool;
                        Vector3f intersect = Selectable.intersectPoint(
                                camera.getDirection(),
                                camera.getPosition(),
                                new Vector3f(0, 1, 0),
                                new Vector3f());
                        Vector4f location = mArrangement.getNearestWallLocation(camera.getRotationY(),
                                player.getCameraPosition(), camera.getDirection());
                        if (new Vector3f(intersect).sub(player.getCameraPosition()).length() <= Builder.DISTANCE ||
                                location != null && new Vector3f(location.x, location.y, location.z).sub(player.getCameraPosition()).length() <= Builder.DISTANCE) {
                            GameObject buildable = mArrangement.copyObject((GameObject) builder.build());
                            if (location == null) {
                                location = new Vector4f(intersect, -player.getRotationY());
                                if (glfwGetKey(defaultWindow, GLFW_KEY_T) == GLFW_PRESS) {
                                    location.w = Math.round(location.w / 45f) * 45;
                                }
                            }
                            buildable.setDefaultPosition(new Vector3f(location.x, location.y, location.z));
                            buildable.setDefaultRotation(new Vector3f(0, location.w, 0));

                            buildable.getRigidBody().setPosition(buildable.getDefaultPosition());
                            buildable.getRigidBody().setRotation(0, (float) Math.toRadians(location.w), 0);

                            mArrangement.putObject(buildable);
                            mPhysic.putObjects(buildable);
                        }
                    }
                    if (tool instanceof Destroyer && observable != null) {
                        Destroyer destroyer = (Destroyer) tool;
                        GameObject gameObject = mArrangement.getObject(observable);
                        if (gameObject instanceof Destroyable) {
                            Destroyable destroyable = (Destroyable) gameObject;
                            destroyable.hit(destroyer.getPower());
                            if (destroyable.getStrength() <= 0) {
                                GameObject destroyableObject = ((GameObject) destroyable);
                                mPhysic.removeBody(destroyableObject.getRigidBody());
                                mArrangement.removeObject(destroyableObject.getId());
                                GameObject drop = destroyable.destroy();
                                if (drop != null) {
                                    drop.getDefaultPosition().y = heightMap.getY(
                                            destroyableObject.getDefaultPosition().x, destroyableObject.getDefaultPosition().z);
                                    mArrangement.putObject(drop);
                                }
                            }
                        }
                    }
                }
                player.hit();
            }
        }
    }

    private void pressRightMouseButton() {
        if (glfwGetMouseButton(defaultWindow, GLFW_MOUSE_BUTTON_RIGHT) == GLFW_PRESS) {
            if (!player.isRightHandEmpty() && player.getRightHandTool() instanceof Eatable) {
                Eatable eatable = (Eatable) player.getRightHandTool();
                if (player.updateNeeds(eatable.getCalories(), 0)) {
                    player.removeCurrentInventoryItem();
                }
            }
        }
    }

    private void setupLights() {
        dayNight = new DayNight();
        addLight(dayNight.getLight());

        spotLight = new SpotLight();
        spotLight.setDiffuse(0.8f, 0.8f, 0.6f);
        spotLight.getAttenuation().linear = 0.05f;
        spotLight.setDirection(0f, -0.5f, 1f);
        spotLight.setExponent(1f);
        spotLight.setCutoff(30f);
    }
}
