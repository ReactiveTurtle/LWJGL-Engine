package ru.reactiveturtle.engine.base;

import ru.reactiveturtle.engine.camera.PerspectiveCamera;
import ru.reactiveturtle.engine.light.Light;
import ru.reactiveturtle.engine.model.Renderable;

import java.util.ArrayList;
import java.util.List;

public abstract class Stage {
    public Stage(GameContext gameContext) {
        this.gameContext = gameContext;
    }

    public abstract void start();

    public abstract void render();

    private final GameContext gameContext;

    public GameContext getGameContext() {
        return gameContext;
    }


    private PerspectiveCamera camera;

    public void setCamera(PerspectiveCamera camera) {
        this.camera = camera;
    }

    public PerspectiveCamera getCamera() {
        return camera;
    }

    public static List<Renderable> s = new ArrayList<>();

    public static List<Light> lights = new ArrayList<>();

    protected void addLight(Light light) {
        lights.add(light);
    }

    protected void removeLight(int index) {
        lights.remove(index);
    }

    public List<Light> getLights() {
        return lights;
    }


    protected KeyCallback keyCallback;

    public void setKeyCallback(KeyCallback keyCallback) {
        this.keyCallback = keyCallback;
        gameContext.updateKeyCallback();
    }

    public interface KeyCallback {
        void onChange(int key, int action);
    }

    protected MouseCallback mouseCallback;

    public void setMouseCallback(MouseCallback mouseCallback) {
        this.mouseCallback = mouseCallback;
        gameContext.updateMouseCallback();
    }

    public abstract static class MouseCallback {
        public void onLeftButtonDown() {

        }

        public void onLeftButtonUp() {

        }

        public void onRightButtonDown() {

        }

        public void onRightButtonUp() {

        }

        public void onScroll(int direction) {

        }
    }
}
