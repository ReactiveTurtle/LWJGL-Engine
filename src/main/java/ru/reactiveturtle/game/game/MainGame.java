package ru.reactiveturtle.game.game;

import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFWScrollCallbackI;
import ru.reactiveturtle.game.engine.base.GameContext;
import ru.reactiveturtle.game.engine.camera.PerspectiveCamera;
import ru.reactiveturtle.game.engine.light.PointLight;
import ru.reactiveturtle.game.engine.light.SpotLight;
import ru.reactiveturtle.game.engine.material.Material;
import ru.reactiveturtle.game.engine.material.Texture;
import ru.reactiveturtle.game.engine.model.Model;
import ru.reactiveturtle.game.engine.model.base.Sphere;
import ru.reactiveturtle.game.engine.model.loader.ObjLoader;
import ru.reactiveturtle.game.engine.model.base.Parallelepiped;
import ru.reactiveturtle.game.engine.shader.TextureShader;
import ru.reactiveturtle.game.game.player.Player;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL30.*;

public class MainGame extends GameContext {
    private DayNight dayNight;
    private PointLight pointLight;
    private SpotLight spotLight;

    private Sphere sphere;
    private TextureShader textureShader;

    private Parallelepiped flat;
    private Material material, flatMaterial;

    private ArrayList<Model> palms = new ArrayList<>();
    private Vector4f[] randomPositions;

    private Model campfire, rock;

    private Texture normalMap;

    ArrayList<Model> lightModels = new ArrayList<>();

    private Player player;
    private Model log;
    private Parallelepiped intersectionTest;

    @Override
    protected void run() {
        setCamera(new PerspectiveCamera(67f, 0.1f, 1000f));
        textureShader = new TextureShader();
        material = new Material();
        material.setReflectance(20f);
        material.setDiffuse(1f, 1f, 1f);
        material.setSpecular(1f, 1f, 1f);
        material.setAmbient(1f, 1f, 1f);

        setCursorCallback(bias -> {
            player.addRotation(bias.y, bias.x, 0);
        });

        glfwSetKeyCallback(defaultWindow, (window, key, scancode, action, mods) -> {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_PRESS) {
                glfwSetWindowShouldClose(window, true);
            }
            if (key == GLFW_KEY_L && action == GLFW_PRESS) {
                player.setLockYMove(!player.isLockYMove());
            }
            if (key == GLFW_KEY_K && action == GLFW_PRESS) {
                player.setY(1.85f);
            }
            if (key == GLFW_KEY_F1 && action == GLFW_PRESS) {
                if (flatMaterial.getNormalMap() == null) {
                    flatMaterial.setNormalMap(normalMap);
                } else {
                    flatMaterial.setNormalMap(null);
                }
            }
            if (key == GLFW_KEY_F2 && action == GLFW_PRESS) {
                lights.set(0, dayNight.getLight());
            }
            if (key == GLFW_KEY_F3 && action == GLFW_PRESS) {
                lights.set(0, pointLight);
            }
            if (key == GLFW_KEY_F4 && action == GLFW_PRESS) {
                lights.set(0, spotLight);
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
                player.jump();
            }
        });

        glfwSetMouseButtonCallback(defaultWindow, (window, button, action, mods) -> {
            if (button == GLFW_MOUSE_BUTTON_LEFT && action == GLFW_PRESS) {
                player.hit();
            }
        });

        glfwSetScrollCallback(defaultWindow, (l, dx, dy) -> {
            player.wheelInventory(-dy);
        });

        flatMaterial = new Material();
        flatMaterial.setTexture(new Texture("texture/sand.jpg"));
        normalMap = new Texture("texture/sand_normal_map.jpg");
        flatMaterial.setDiffuse(1f, 1f, 1f);
        flat = new Parallelepiped(200f, 10f, 200f, 40f, 40f);
        flat.setY(-5f);
        flat.setShader(textureShader);
        flat.setMaterial(flatMaterial);

        try {
            palms.addAll(Arrays.asList(
                    ObjLoader.load("object/trees/palm_1"),
                    ObjLoader.load("object/trees/palm_2"),
                    ObjLoader.load("object/trees/palm_3")));
            for (Model palm : palms) {
                palm.setScale(2f);
                palm.setShader(textureShader);
            }
            randomPositions = Generator.genPalmForest(200, 100, 100);

            campfire = ObjLoader.load("object/campfire/campfire");
            campfire.setShader(textureShader);
            campfire.setSelectBox(1.5f, 0.5f, 1.5f);
            campfire.setSelectBoxY(0.25f);

            rock = ObjLoader.load("object/rocks/rock1", 1 / 128f, 1 / 128f);
            rock.getMeshes().get("Stone").getMaterial().setTexture(
                    new Texture("object/rocks/broken1.jpg"));
            rock.setShader(textureShader);
            rock.setPosition(10f, 0, 10f);
            rock.setScale(1f);

            log = ObjLoader.load("object/trees/log/log", 1 / 8f, 1 / 8f);
            log.setShader(textureShader);
        } catch (IOException e) {
            e.printStackTrace();
        }

        getShadowManager().setShadowEnabled(true);
        setupLights();

        sphere = new Sphere(1f, 128);
        sphere.setShader(textureShader);
        material = material.copy();
        material.setEmission(0.2f, 0.8f, 1f);
        sphere.setMaterial(material);
        material.setTexture(new Texture(16, 16, GL_RGB));
        sphere.setY(60f);

        player = new Player(textureShader, "/object/tools/axe");
        player.setPosition(1f, 45f, 2f);
        player.setRotationX(90);
        player.setLockYMove(true);
        player.setLockTopBottomRotation(true);
        player.setActionListener(new Player.ActionListener() {
            @Override
            public void onHitEnd() {
                if (glfwGetMouseButton(defaultWindow, GLFW_MOUSE_BUTTON_LEFT) == GLFW_PRESS) {
                    player.hit();
                }
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
        intersectionTest = new Parallelepiped(1, 1, 1);
        intersectionTest.setShader(textureShader);
        intersectionTest.setMaterial(material);
    }

    @Override
    protected void render() {
        glClearColor(0 / 255f, 160 / 255f, 200f / 255f, 1.0f);

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
        player.addPosition(translate, getDeltaTime());
        camera.setPosition(player.getCameraPosition());
        camera.setRotation(player.getRotation());
        if (lights.size() > 0 && lights.get(0) instanceof SpotLight) {
            spotLight.setPosition(camera.getPosition());
            spotLight.setDirection(new Vector3f(0, 0, 1)
                    .rotateX((float) Math.toRadians(camera.getRotationX()))
                    .rotateY((float) Math.toRadians(180 - camera.getRotationY()))
                    .rotateZ((float) Math.toRadians(-camera.getRotationZ()))
            );
        }

        glBindFramebuffer(GL_FRAMEBUFFER, dayNight.getLight().getShadowMap().getFrameBufferId());
        glViewport(0, 0, dayNight.getLight().getShadowMap().getShadowTexture().getWidth(),
                dayNight.getLight().getShadowMap().getShadowTexture().getHeight());
        glClear(GL_DEPTH_BUFFER_BIT);
        getShadowManager().startShadowRender();
        flat.renderShadow();
        sphere.renderShadow();
        player.renderShadow();
        rock.renderShadow();
        campfire.renderShadow();
        for (Vector4f vector4f : randomPositions) {
            Model palm = palms.get((int) vector4f.y);
            palm.setX(vector4f.x);
            palm.setZ(vector4f.z);
            palm.setRotationY(vector4f.w);
            palm.renderShadow();
        }
        getShadowManager().endShadowRender();

        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        glViewport(0, 0, width, height);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        textureShader.bind();
        dayNight.update(getDeltaTime());
        sphere.setPosition(new Vector3f(dayNight.getLight().getDirection()).mul(50f));
        flat.setShader(textureShader);
        flat.render();
        player.render(getDeltaTime());
        for (Vector4f vector4f : randomPositions) {
            Model palm = palms.get((int) vector4f.y);
            palm.setX(vector4f.x);
            palm.setZ(vector4f.z);
            palm.setRotationY(vector4f.w);
            palm.render();
        }
        for (Model lightModel : lightModels) {
            lightModel.render();
        }
        rock.render();
        sphere.render();
        campfire.render();
        log.render();
        Vector3f intersectionPoint = campfire.calcIntersectionPoint(camera.getPosition(), player.getDirection(), lightModels.get(0));
        if (intersectionPoint != null) {
            player.getInterface().setIntersectionText("Пересечение: " +
                    intersectionPoint.x + ", " + intersectionPoint.y + ", " + intersectionPoint.z);
        }
        textureShader.unbind();
        player.renderUI(getDeltaTime());
    }

    @Override
    protected void destroy() {

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

        pointLight = new PointLight();
        pointLight.setAmbient(0.2f, 0.2f, 0.15f);
        pointLight.setDiffuse(1.6f, 1.6f, 1.2f);
        pointLight.getAttenuation().linear = 0.05f;
        pointLight.setPosition(0, 20f, 0f);

        Model lamp = new Sphere(0.1f, 16);
        Material material = new Material();
        material.setEmission(1f, 1f, 0.75f);
        lamp.setMaterial(material);
        lamp.setShader(textureShader);
        lamp.setPosition(pointLight.getPosition());
        lightModels.add(lamp);
    }
}
