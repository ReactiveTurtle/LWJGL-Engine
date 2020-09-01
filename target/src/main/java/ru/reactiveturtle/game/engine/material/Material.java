package ru.reactiveturtle.game.engine.material;

import org.joml.Vector3f;

import static org.lwjgl.opengl.GL11.GL_RGB;

public class Material {
    private Texture mTexture;
    private Texture mNormalMap;
    //Восприятие фонового освещения
    private Vector3f ambient = new Vector3f(0, 0, 0);
    //Восприятие рассеяного освещения
    private Vector3f diffuse = new Vector3f(0, 0, 0);
    //Восприятие отражённого освещения
    private Vector3f specular = new Vector3f(0, 0, 0);
    //Самостоятельное свечение
    private Vector3f emission = new Vector3f(0, 0, 0);
    //Коэффициент блеска
    private float reflectance = 0;

    public Material() {
        mTexture = new Texture(16, 16, GL_RGB);
    }

    public Material(Texture texture) {
        mTexture = texture;
    }

    public Material(Texture texture, Texture normalMap) {
        mTexture = texture;
        mNormalMap = normalMap;
    }

    public void setTexture(Texture texture) {
        this.mTexture = texture;
    }

    public Texture getTexture() {
        return mTexture;
    }

    public void setNormalMap(Texture normalMap) {
        mNormalMap = normalMap;
    }

    public Texture getNormalMap() {
        return mNormalMap;
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

    public void setEmission(Vector3f emission) {
        this.emission.set(emission);
    }

    public void setEmission(float r, float g, float b) {
        emission.set(r, g, b);
    }

    public Vector3f getEmission() {
        return emission;
    }

    public void setReflectance(float reflectance) {
        this.reflectance = reflectance;
    }

    public float getReflectance() {
        return reflectance;
    }

    public Material copy() {
        Material material = new Material();
        if (mTexture != null) material.setTexture(mTexture);
        material.setAmbient(ambient);
        material.setDiffuse(diffuse);
        material.setSpecular(specular);
        material.setEmission(emission);
        material.setReflectance(reflectance);
        return material;
    }
}
