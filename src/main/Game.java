package main;

import engine.Base;
import engine.Camera;
import engine.environment.DirectionalLight;
import engine.environment.PointLight;
import engine.environment.SpotLight;
import engine.model.Terrain;
import engine.model.Model;
import engine.shader.*;
import engine.util.ObjLoader;
import engine.model.figures.Parallelepiped;
import engine.util.Texture;
import engine.model.figures.SkyBox;
import org.joml.Vector2d;
import org.joml.Vector3f;

import java.util.ArrayList;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class Game extends Base {
    //Лунный свет 0.741f, 0.816f, 0.894f;
    private Camera camera;
    private TextureShader textureShader;
    private ColorShader colorShader;

    private ShadowShader shadowShader;
    private ShadowMap shadowMap;

    private Parallelepiped skyBox;
    private ArrayList<Parallelepiped> suns = new ArrayList<>();
    private Model model, male, car;
    private Terrain terrain;
    private SpotLight flashLight;
    private int flashLightIndex = -1;
    private boolean isFirstPersonCamera = false;

    private float gravity = 9.81f;
    private float h = 4f;
    private float fallTime = (float) (Math.sqrt(2 * h / gravity) * 1000);
    private float time = 0;

    private float bias = 0.001f;

    @Override
    public void run() {
        camera = new Camera(60f, 0.1f, 1000f);
        camera.setRotationY(180);
        setCamera(camera);
        camera.setPosition(0f, 3f, -3f);

        shadowShader = new ShadowShader();
        shadowMap = new ShadowMap();
        setShadowMap(shadowMap);

        textureShader = new TextureShader();
        textureShader.create();
        colorShader = new ColorShader();
        colorShader.create();

        setDirectionalLight(new DirectionalLight());
        getDirectionalLight().setDirection(1, 0.5f, 1);
        getDirectionalLight().setAmbient(0.1f, 0.1f, 0.2f);
        getDirectionalLight().setDiffuse(0.3f, 0.3f, 0.6f);
        getDirectionalLight().setSpecular(0.05f, 0.05f, 0.1f);
        //getDirectionalLight().setDirection(0.5f, 1f, 0.5f);
        //getDirectionalLight().setAmbient(0.5f, 0.5f, 0.5f);
        //getDirectionalLight().setDiffuse(1f, 1f, 1f);

        PointLight pointLight = new PointLight();
        pointLight.setPosition(300, 3, 0);
        pointLight.setAmbient(0.75f, 0.75f, 0.75f);
        pointLight.setDiffuse(4f, 4f, 3.5f);
        pointLight.setSpecular(4f, 4f, 3.5f);
        //addPointLight(pointLight);

        skyBox = new SkyBox(1024, 1024, 1024);
        skyBox.setPosition(camera.getPosition());
        skyBox.setTexture(new Texture("night_skybox.png"));
        skyBox.addY(128);

        Texture terrainTexture = new Texture("terrain_map.png");
        terrain = Terrain.create(terrainTexture, 64f, 16f, 64f);
        terrain.setTexture(new Texture("terrain_grass.jpg"));

        Parallelepiped sun = new Parallelepiped(0.25f, 0.25f, 0.25f);
        sun.setColor(1f, 1f, 0.875f);
        sun.setPosition(64f, 128f, 64f);
        suns.add(sun);

        Material material = new Material();
        material.setAmbient(1f, 1f, 1f);
        material.setDiffuse(1f, 1f, 1f);
        material.setSpecular(1f, 1f, 1f);
        material.setReflectance(20f);

        Material mapMaterial = material.copy();
        mapMaterial.setReflectance(1f);
        terrain.setMaterial(mapMaterial);

        glfwSetScrollCallback(defaultWindow, (window, xoffset, yoffset) -> {

        });

        model = ObjLoader.loadModel("cube.obj");
        model.setZ(-3f);
        model.setTexture(new Texture("black_wall.png"));
        model.setMaterial(material);

        flashLight = new SpotLight();
        flashLight.setDiffuse(4.2f, 4.2f, 4.8f);
        flashLight.setSpecular(3.5f, 3.5f, 4f);
        flashLight.setCutoff(20f);
        flashLight.setExponent(1);
        glfwSetKeyCallback(defaultWindow, (window, key, scancode, action, mods) -> {
                    if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
                        glfwSetWindowShouldClose(window, true);

                    if (key == GLFW_KEY_L && action == GLFW_RELEASE) {
                        if (flashLightIndex == -1) {
                            flashLightIndex = spotLightsList.size();
                            addSpotLight(flashLight);
                        } else {
                            spotLightsList.remove(flashLightIndex);
                            flashLightIndex = -1;
                        }
                    }

                    if (key == GLFW_KEY_F1 && action == GLFW_RELEASE)
                        isFirstPersonCamera = !isFirstPersonCamera;
                }
        );

        male = ObjLoader.loadModel("male.obj");
        male.setMaterial(mapMaterial);
        male.addY(-0.1825f);
        male.setColor(toFloat(242), toFloat(172), toFloat(159));
        male.setScale(0.125f);

        car = ObjLoader.loadModel("bmw.obj");
        car.setColor(1f, 0.25f, 0.25f);
        car.setZ(6);
        Material carMaterial = material.copy();
        carMaterial.setReflectance(40f);
        carMaterial.setDiffuse(0.15f, 0.15f, 0.15f);
        carMaterial.setSpecular(2f, 2f, 2f);
        car.setMaterial(carMaterial);
        shadowShader.addModel(terrain);
        shadowShader.addModel(male);
    }

    @Override
    public void render() {
        glClearColor(0 / 255f, 0 / 255f, 0 / 255f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glfwPollEvents();
        if (isFirstPersonCamera) {
            moveFirstPersonCamera(camera);
            moveFlashLight(camera);
        } else {
            moveThirdPersonCamera(camera, male, 0, 2.5f, 0);
            moveFlashLight(male, camera, -0.75f, 1.5f, 0.25f);
        }
        skyBox.setPosition(new Vector3f().set(camera.getPosition()));
        skyBox.addY(128);
        checkCollision(male);

        shadowMap.render(shadowShader);

        textureShader.bind();
        skyBox.render(textureShader);
        terrain.render(textureShader);
        model.render(textureShader);
        for (Parallelepiped sun : suns) {
            sun.render(textureShader);
        }
        textureShader.unbind();

        colorShader.bind();
        male.render(colorShader);
        car.render(colorShader);
        colorShader.unbind();
        glfwSwapBuffers(defaultWindow);
    }

    @Override
    public void destroy() {

    }

    private void moveFirstPersonCamera(Camera camera) {
        Vector2d vector2d = getCursorPosition();
        float degreesX = (float) ((vector2d.y - Base.height / 2) / 10);
        camera.addRotationX(Math.abs(camera.getRotationX()) > 90 ? 0 : degreesX);
        if (Math.abs(camera.getRotationX()) > 90) camera.addRotationX(-camera.getRotationX() % 90);
        camera.addRotationY((float) ((vector2d.x - Base.width / 2) / 10));
        moveCursorToCenter();

        Vector3f vector3f = new Vector3f();
        if (glfwGetKey(defaultWindow, GLFW_KEY_W) == 1) {
            vector3f.z -= 0.075f;
            if (glfwGetKey(defaultWindow, GLFW_KEY_LEFT_SHIFT) == 1) {
                vector3f.z -= 0.125f;
                if (glfwGetMouseButton(defaultWindow, GLFW_MOUSE_BUTTON_RIGHT) == 1) vector3f.z -= 1f;
            }
        }
        if (glfwGetKey(defaultWindow, GLFW_KEY_S) == 1) {
            vector3f.z += 0.075f;
            if (glfwGetKey(defaultWindow, GLFW_KEY_LEFT_SHIFT) == 1) {
                vector3f.z += 0.125f;
                if (glfwGetMouseButton(defaultWindow, GLFW_MOUSE_BUTTON_RIGHT) == 1) vector3f.z += 1f;
            }
        }
        if (glfwGetKey(defaultWindow, GLFW_KEY_A) == 1) vector3f.x -= 0.1f;
        if (glfwGetKey(defaultWindow, GLFW_KEY_D) == 1) vector3f.x += 0.1f;
        camera.addPosition(vector3f);
        if (skyBox.getRotationX() >= 0.5f) {
            bias = -0.001f;
        } else if (skyBox.getRotationX() <= -0.5f) {
            bias = 0.001f;
        }
        skyBox.addRotationX(bias);
    }

    private void moveThirdPersonCamera(Camera camera, Model model, float xBias, float yBias, float zBias) {
        Vector3f vector3f = new Vector3f();
        if (glfwGetKey(defaultWindow, GLFW_KEY_W) == 1) {
            model.addX(-0.075f * (float) Math.sin(Math.toRadians(model.getRotationY())) * -1.0f * (float) Math.cos(Math.toRadians(model.getRotationX())));
            model.addZ(0.075f * (float) Math.cos(Math.toRadians(model.getRotationY())) * (float) Math.cos(Math.toRadians(model.getRotationX())));
            model.setRotationY(-camera.getRotationY() + 180);
            if (glfwGetKey(defaultWindow, GLFW_KEY_LEFT_SHIFT) == 1) {
                model.addX(-0.125f * (float) Math.sin(Math.toRadians(model.getRotationY())) * -1.0f * (float) Math.cos(Math.toRadians(model.getRotationX())));
                model.addZ(0.125f * (float) Math.cos(Math.toRadians(model.getRotationY())) * (float) Math.cos(Math.toRadians(model.getRotationX())));
                if (glfwGetMouseButton(defaultWindow, GLFW_MOUSE_BUTTON_RIGHT) == 1) {
                    model.addX(-1f * (float) Math.sin(Math.toRadians(model.getRotationY())) * -1.0f * (float) Math.cos(Math.toRadians(model.getRotationX())));
                    model.addZ(1f * (float) Math.cos(Math.toRadians(model.getRotationY())) * (float) Math.cos(Math.toRadians(model.getRotationX())));
                }
            }
        }
        if (glfwGetKey(defaultWindow, GLFW_KEY_S) == 1) {
            model.addX(0.075f * (float) Math.sin(Math.toRadians(model.getRotationY())) * -1.0f * (float) Math.cos(Math.toRadians(model.getRotationX())));
            model.addZ(-0.075f * (float) Math.cos(Math.toRadians(model.getRotationY())) * (float) Math.cos(Math.toRadians(model.getRotationX())));
            model.setRotationY(-camera.getRotationY() + 180);
            if (glfwGetKey(defaultWindow, GLFW_KEY_LEFT_SHIFT) == 1) {
                model.addX(0.125f * (float) Math.sin(Math.toRadians(model.getRotationY())) * -1.0f * (float) Math.cos(Math.toRadians(model.getRotationX())));
                model.addZ(-0.125f * (float) Math.cos(Math.toRadians(model.getRotationY())) * (float) Math.cos(Math.toRadians(model.getRotationX())));
                if (glfwGetMouseButton(defaultWindow, GLFW_MOUSE_BUTTON_RIGHT) == 1) {
                    model.addX(1f * (float) Math.sin(Math.toRadians(model.getRotationY())) * -1.0f * (float) Math.cos(Math.toRadians(model.getRotationX())));
                    model.addZ(-1f * (float) Math.cos(Math.toRadians(model.getRotationY())) * (float) Math.cos(Math.toRadians(model.getRotationX())));
                }
            }
        }
        if (glfwGetKey(defaultWindow, GLFW_KEY_A) == 1) {
            model.addX(0.1f * (float) Math.sin(Math.toRadians(model.getRotationY() - 90)) * -1.0f);
            model.addZ(-0.1f * (float) Math.cos(Math.toRadians(model.getRotationY() - 90)));
            model.setRotationY(-camera.getRotationY() + 180);
        }
        if (glfwGetKey(defaultWindow, GLFW_KEY_D) == 1) {
            model.addX(-0.1f * (float) Math.sin(Math.toRadians(model.getRotationY() - 90)) * -1.0f);
            model.addZ(0.1f * (float) Math.cos(Math.toRadians(model.getRotationY() - 90)));
            model.setRotationY(-camera.getRotationY() + 180);
        }
        model.addPosition(vector3f);
        if (skyBox.getRotationX() >= 0.5f) {
            bias = -0.001f;
        } else if (skyBox.getRotationX() <= -0.5f) {
            bias = 0.001f;
        }
        skyBox.addRotationX(bias);

        Vector2d vector2d = getCursorPosition();
        if (vector2d.x != 0f || vector2d.y != 0f) {
            float distance = 3.5f;
            float degreesX = (float) ((vector2d.y - Base.height / 2) / 10);
            float degressY = (float) ((vector2d.x - Base.width / 2) / 10);
            camera.addRotationX(Math.abs(camera.getRotationX()) > 90 ? 0 : degreesX);
            if (Math.abs(camera.getRotationX()) > 90) camera.addRotationX(-camera.getRotationX() % 90);
            camera.addRotationY(degressY);

            float y = (float) (Math.sin(Math.toRadians(camera.getRotationX())) * distance);
            float square = (float) (Math.cos(Math.toRadians(camera.getRotationX())) * distance);
            float x = (float) (Math.sin(Math.toRadians(camera.getRotationY())) * -square);
            float z = (float) (Math.cos(Math.toRadians(camera.getRotationY())) * square);
            camera.setPosition(model.getX() + xBias + x,
                    model.getY() + yBias + y, model.getZ() + zBias + z);

            moveCursorToCenter();
        }
    }

    private static float[] getRandomColorPalette(int count) {
        float[] palette = new float[count];
        for (int i = 0; i < palette.length; i++) {
            palette[i] = (float) Math.random();
        }
        return palette;
    }

    private static float toFloat(int v) {
        return v / 255f;
    }

    private void moveFlashLight(Model model, Camera camera, float xBias, float yBias, float zBias) {
        if (flashLightIndex != -1) {
            SpotLight flashLight = spotLightsList.get(flashLightIndex);
            Vector3f position = new Vector3f().set(model.getPosition());
            if (xBias != 0) {
                position.x += xBias * Math.sin(Math.toRadians(model.getRotationY() - 90)) * -1.0f;
                position.z += -xBias * Math.cos(Math.toRadians(model.getRotationY() - 90));
            }
            if (zBias != 0) {
                float b = zBias * (float) Math.cos(Math.toRadians(model.getRotationX()));
                position.x += (float) Math.sin(Math.toRadians(model.getRotationY())) * -1.0f * b;
                position.z += (float) Math.cos(Math.toRadians(model.getRotationY())) * b;
            }
            position.y += yBias;
            flashLight.setPosition(position);

            Vector3f rotation = new Vector3f().set(model.getRotation());
            Vector3f direction = new Vector3f();
            float b = -16f * (float) Math.cos(Math.toRadians(camera.getRotationX()));
            direction.x = (float) Math.sin(Math.toRadians(rotation.y)) * -1.0f * b;
            direction.y = (float) Math.sin(Math.toRadians(camera.getRotationX())) * -16f;
            direction.z = (float) Math.cos(Math.toRadians(rotation.y)) * -b;
            spotLightsList.get(flashLightIndex).setDirection(direction);
        }
    }

    private void moveFlashLight(Camera camera) {
        if (flashLightIndex != -1) {
            spotLightsList.get(flashLightIndex).setPosition(camera.getPosition());
            Vector3f rotation = camera.getRotation();
            Vector3f direction = new Vector3f();
            float b = -16f * (float) Math.cos(Math.toRadians(rotation.x));
            direction.x = (float) Math.sin(Math.toRadians(rotation.y)) * -1.0f * b;
            direction.y = (float) Math.sin(Math.toRadians(rotation.x)) * -16f;
            direction.z = (float) Math.cos(Math.toRadians(rotation.y)) * b;
            spotLightsList.get(flashLightIndex).setDirection(direction);
        }
    }


    private void checkCollision(Model model) {
        int x = (int) ((model.getX() + 32f) * 4);
        int z = (int) ((model.getZ() + 32f) * 4);
        if (model.getZ() < 32f && model.getZ() > -32f && model.getX() > -32f && model.getX() < 32f &&
                model.getY() != terrain.getHeightMap()[z * 256 + x]) {
            model.setY(terrain.getHeightMap()[z * 256 + x]);
        }
    }
}
