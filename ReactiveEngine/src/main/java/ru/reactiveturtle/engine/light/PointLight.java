package ru.reactiveturtle.engine.light;

import org.joml.Vector3f;

public class PointLight extends Light {
    //Положение в пространстве
    private Vector3f position = new Vector3f(0, 0, 0);
    //Мощность фонового освещения
    private Vector3f ambient = new Vector3f(0, 0, 0);
    //Мощность рассеянного освещения
    private Vector3f diffuse = new Vector3f(0, 0, 0);
    //Мощность отражённого освещения
    private Vector3f specular = new Vector3f(0, 0, 0);
    //Коэффициенты затухания
    private Attenuation attenuation = new Attenuation();

    public void setPosition(Vector3f position) {
        this.position.set(position);
    }

    public void setPosition(float x, float y, float z) {
        position.set(x, y, z);
    }

    public void setX(float x) {
        position.x = x;
    }

    public void setY(float y) {
        position.y = y;
    }

    public void setZ(float z) {
        position.z = z;
    }

    public void addPosition(float x, float y, float z) {
        position.add(x, y, z);
    }

    public void addX(float x) {
        position.x += x;
    }

    public void addY(float y) {
        position.y += y;
    }

    public void addZ(float z) {
        position.z += z;
    }

    public float getX() {
        return position.x;
    }

    public float getY() {
        return position.y;
    }

    public float getZ() {
        return position.z;
    }

    public Vector3f getPosition() {
        return position;
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
        return diffuse;
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

    public Attenuation getAttenuation() {
        return attenuation;
    }

    public PointLight copy() {
        PointLight pointLight = new PointLight();
        pointLight.setPosition(position);
        pointLight.setAmbient(ambient);
        pointLight.setDiffuse(diffuse);
        pointLight.setSpecular(specular);
        pointLight.attenuation = attenuation;
        return pointLight;
    }
}
