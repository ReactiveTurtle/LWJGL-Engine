package ru.reactiveturtle.game.game;

import org.joml.Vector3f;
import ru.reactiveturtle.game.engine.light.DirectionalLight;
import ru.reactiveturtle.game.engine.shadow.ShadowMap;

public class DayNight {
    private DirectionalLight directionalLight;
    private float speed = (float) (Math.PI / (10 * 60));
    private float angle = 0;
    private boolean isDay = true;
    private Vector3f ambient = new Vector3f(0.4f, 0.4f, 0.3f);
    private Vector3f diffuse = new Vector3f(2f, 2f, 1.6f);

    public DayNight() {
        directionalLight = new DirectionalLight();
        directionalLight.setDirection(1, 0, 1);
        directionalLight.setShadowMap(new ShadowMap());
        angle = (float) (Math.PI / 3f);
        if (!isDay) {
            ambient.set(0.1f, 0.2f, 0.3f);
            diffuse.set(0.1f, 0.2f, 0.3f);
        } else {
            ambient.set(0.4f, 0.4f, 0.3f);
            diffuse.set(2f, 2f, 1.6f);
        }
    }

    public void update(double deltaTime) {
        angle += speed * deltaTime;
        directionalLight.setAmbient(new Vector3f(ambient).mul((float) Math.sin(angle)));
        directionalLight.setDiffuse(new Vector3f(diffuse).mul((float) Math.sin(angle)));
        if (angle >= Math.PI) {
            angle = (float) (angle % Math.PI);
            isDay = !isDay;
            if (!isDay) {
                ambient.set(0.01f, 0.04f, 0.06f);
                diffuse.set(0.1f, 0.2f, 0.3f);
            } else {
                ambient.set(0.4f, 0.4f, 0.3f);
                diffuse.set(2f, 2f, 1.6f);
            }
        }
        directionalLight.getDirection().set((float) Math.cos(Math.PI - angle), (float) Math.sin(angle), 0);
    }

    public DirectionalLight getLight() {
        return directionalLight;
    }
}
