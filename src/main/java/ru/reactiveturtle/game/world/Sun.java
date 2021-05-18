package ru.reactiveturtle.game.world;

import ru.reactiveturtle.engine.material.Material;
import ru.reactiveturtle.engine.model.base.Sphere;
import ru.reactiveturtle.engine.shader.TextureShader;

public class Sun extends Sphere {
    public Sun(TextureShader textureShader) {
        super(5f, 20, true);
        setShader(textureShader);
        Material material = new Material();
        material.setEmission(0.5f, 0.5f, 0.4f);
        setMaterial(material);
    }
}
