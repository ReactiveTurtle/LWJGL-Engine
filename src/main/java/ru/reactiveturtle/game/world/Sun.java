package ru.reactiveturtle.game.world;

import ru.reactiveturtle.engine.material.Material;
import ru.reactiveturtle.engine.model.base.Sphere;
import ru.reactiveturtle.engine.shader.ModelShader;

public class Sun extends Sphere {
    public Sun(ModelShader modelShader) {
        super(5f, 20, true);
        setShader(modelShader);
        Material material = new Material();
        material.setEmission(1f, 1f, 0.8f);
        setMaterial(material);
    }
}
