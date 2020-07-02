package ru.reactiveturtle.game.engine.light;

import org.joml.Vector3f;

public class SpotLight extends Light {
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
    //Направление
    private Vector3f direction = new Vector3f(0, 0, 0);
    //Угол влияния
    private float cutoff = 0f;
    //Коэффициент влияния
    private float exponent = 0;

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

    public void setDirection(Vector3f direction) {
        this.direction.set(direction);
    }

    public void setDirection(float x, float y, float z) {
        direction.set(x, y, z);
    }

    public Vector3f getDirection() {
        return direction;
    }

    public void setCutoff(float cutoff) {
        this.cutoff = (float) Math.toRadians(cutoff);
    }

    public float getCutoff() {
        return (float) Math.toDegrees(cutoff);
    }

    public void setExponent(float exponent) {
        this.exponent = exponent;
    }

    public float getExponent() {
        return exponent;
    }

    public SpotLight copy() {
        SpotLight spotLight = new SpotLight();
        spotLight.setPosition(position);
        spotLight.setAmbient(ambient);
        spotLight.setDiffuse(diffuse);
        spotLight.setSpecular(specular);
        spotLight.attenuation.constant = attenuation.constant;
        spotLight.attenuation.exponent = attenuation.exponent;
        spotLight.attenuation.linear = attenuation.linear;
        spotLight.setDirection(direction);
        spotLight.setCutoff(cutoff);
        spotLight.setExponent(exponent);
        return spotLight;
    }
}
