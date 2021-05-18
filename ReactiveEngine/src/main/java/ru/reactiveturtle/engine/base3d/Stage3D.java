package ru.reactiveturtle.engine.base3d;

import ru.reactiveturtle.engine.base.GameContext;
import ru.reactiveturtle.engine.base.Stage;
import ru.reactiveturtle.engine.camera.PerspectiveCamera;
import ru.reactiveturtle.engine.light.Light;

import java.util.ArrayList;
import java.util.List;

/**
 * Базовая 3D сцена
 */
public abstract class Stage3D extends Stage {
    public Stage3D(GameContext gameContext) {
        super(gameContext);
    }

    private PerspectiveCamera camera;

    public void setCamera(PerspectiveCamera camera) {
        this.camera = camera;
    }

    public PerspectiveCamera getCamera() {
        return camera;
    }

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

}
