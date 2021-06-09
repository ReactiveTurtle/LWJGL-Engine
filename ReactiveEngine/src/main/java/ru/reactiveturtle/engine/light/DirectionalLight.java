package ru.reactiveturtle.engine.light;

import org.joml.Vector3f;
import ru.reactiveturtle.engine.shadow.ShadowMap;

public class DirectionalLight extends Light {
    private Vector3f direction = new Vector3f(0, 0, 0);
    //Мощность фонового освещения
    private Vector3f ambient = new Vector3f(0, 0, 0);
    //Мощность рассеянного освещения
    private Vector3f diffuse = new Vector3f(0, 0, 0);
    //Мощность отражённого освещения
    private Vector3f specular = new Vector3f(0, 0, 0);

    private ShadowMap shadowMap;

    public void setDirection(Vector3f direction) {
        this.direction.set(direction);
    }

    public void setDirection(float x, float y, float z) {
        direction.set(x, y, z);
    }

    public Vector3f getDirection() {
        return new Vector3f(direction);
    }

    public void setAmbient(Vector3f ambient) {
        this.ambient.set(ambient);
    }

    public void setAmbient(float r, float g, float b) {
        ambient.set(r, g, b);
    }

    public Vector3f getAmbient() {
        return ambient;
    }

    public void setDiffuse(Vector3f diffuse) {
        this.diffuse.set(diffuse);
    }

    public void setDiffuse(float r, float g, float b) {
        diffuse.set(r, g, b);
    }

    public Vector3f getDiffuse() {
        return new Vector3f(diffuse);
    }

    public void setSpecular(Vector3f specular) {
        this.specular.set(specular);
    }

    public void setSpecular(float r, float g, float b) {
        specular.set(r, g, b);
    }

    public Vector3f getSpecular() {
        return specular;
    }

    public void setShadowMap(ShadowMap shadowMap) {
        this.shadowMap = shadowMap;
    }

    public ShadowMap getShadowMap() {
        return shadowMap;
    }

    public DirectionalLight copy() {
        DirectionalLight directionalLight = new DirectionalLight();
        directionalLight.setDirection(direction);
        directionalLight.setAmbient(ambient);
        directionalLight.setDiffuse(diffuse);
        directionalLight.setSpecular(specular);
        return directionalLight;
    }
}
