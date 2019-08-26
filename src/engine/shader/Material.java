package engine.shader;

import org.joml.Vector3f;

public class Material {
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
        material.setAmbient(ambient);
        material.setDiffuse(diffuse);
        material.setSpecular(specular);
        material.setEmission(emission);
        material.setReflectance(reflectance);
        return material;
    }
}
