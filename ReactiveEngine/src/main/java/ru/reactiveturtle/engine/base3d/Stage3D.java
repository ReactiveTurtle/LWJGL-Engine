package ru.reactiveturtle.engine.base3d;

import ru.reactiveturtle.engine.base.GameContext;
import ru.reactiveturtle.engine.base.Stage;
import ru.reactiveturtle.engine.camera.Camera;
import ru.reactiveturtle.engine.light.DirectionalLight;
import ru.reactiveturtle.engine.light.Light;
import ru.reactiveturtle.engine.light.PointLight;
import ru.reactiveturtle.engine.light.SpotLight;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Базовая 3D сцена
 */
public abstract class Stage3D extends Stage {
    public Stage3D(GameContext gameContext) {
        super(gameContext);
    }

    private Camera camera;

    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    public Camera getCamera() {
        return camera;
    }

    private DirectionalLight directionalLight;
    private List<PointLight> pointLights = new ArrayList<>();
    private List<SpotLight> spotLights = new ArrayList<>();

    public void setDirectionalLight(DirectionalLight directionalLight) {
        this.directionalLight = directionalLight;
    }

    protected void addLight(PointLight pointLight) {
        pointLights.add(pointLight);
    }

    protected void addLight(SpotLight spotLight) {
        spotLights.add(spotLight);
    }

    protected void removeLight(PointLight pointLight) {
        pointLights.remove(pointLight);
    }

    protected void removeLight(SpotLight spotLight) {
        spotLights.remove(spotLight);
    }

    public DirectionalLight getDirectionalLight() {
        return directionalLight;
    }

    /**
     * @return Возвращает иммутабельный список точечных источников освещения
     */
    public List<PointLight> getPointLights() {
        return Collections.unmodifiableList(pointLights);
    }

    /**
     * @return Возвращает иммутабельный список направленных источников освещения
     */
    public List<SpotLight> getSpotLights() {
        return Collections.unmodifiableList(spotLights);
    }
}
