package ru.reactiveturtle.game.world;

import org.joml.Vector3f;
import ru.reactiveturtle.engine.light.DirectionalLight;
import ru.reactiveturtle.engine.shadow.ShadowMap;

import static org.lwjgl.opengl.GL11.glClearColor;

public class DayNight {
    private DirectionalLight directionalLight;
    public static final float dayLongTime = 40 * 60; // minutes * seconds
    public static final float secondsPerRealSecond = 12 * 60 * 60 / dayLongTime;
    private float speed = (float) (Math.PI / dayLongTime);
    private float angle;
    private boolean isDay = true;
    private Vector3f ambient = new Vector3f(0.4f, 0.4f, 0.3f);
    private Vector3f diffuse = new Vector3f(1f, 1f, 0.8f);

    private Vector3f skyColor = new Vector3f();

    public DayNight() {
        directionalLight = new DirectionalLight();
        directionalLight.setDirection(1, 0, 1);
        directionalLight.setShadowMap(new ShadowMap());
        angle = (float) (Math.PI / 3f);
        if (!isDay) {
            skyColor.set(37 / 255f, 50 / 255f, 80 / 255f).div(2);
            diffuse.set(189 / 255f, 208f / 255f, 228 / 255f).div(8);
            ambient.set(new Vector3f(diffuse).div(4));
        } else {
            ambient.set(0.3f, 0.3f, 0.25f);
            diffuse.set(0.5f, 0.5f, 0.4f);
            skyColor.set(117 / 255f, 187 / 255f, 253 / 255f);
        }
    }

    public void update(double deltaTime) {
        angle += speed * deltaTime;
        directionalLight.setAmbient(new Vector3f(ambient).mul((float) Math.pow(Math.sin(angle), 1f / 4)));
        directionalLight.setDiffuse(new Vector3f(diffuse).mul((float) Math.pow(Math.sin(angle), 1f / 4)));
        directionalLight.setSpecular(directionalLight.getDiffuse());
        if (angle >= Math.PI) {
            angle = (float) (angle % Math.PI);
            isDay = !isDay;
            if (!isDay) {
                skyColor.set(37 / 255f, 50 / 255f, 80 / 255f).div(2);
                diffuse.set(189 / 255f, 208f / 255f, 228 / 255f).div(4);
                ambient.set(new Vector3f(diffuse).div(4));
            } else {
                ambient.set(0.3f, 0.3f, 0.2f);
                diffuse.set(0.5f, 0.5f, 0.3f);
                skyColor.set(117 / 255f, 187 / 255f, 253 / 255f);
            }
        }
        directionalLight.setDirection((float) Math.cos(Math.PI - angle), (float) Math.sin(angle), 0);
        Vector3f skyColor = new Vector3f(this.skyColor).mul(directionalLight.getDirection().y);
        glClearColor(skyColor.x,
                skyColor.y,
                skyColor.z, 1f);
    }

    public DirectionalLight getLight() {
        return directionalLight;
    }
}
