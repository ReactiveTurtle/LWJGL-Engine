package ru.reactiveturtle.game.base;

import ru.reactiveturtle.engine.base.Disposeable;
import ru.reactiveturtle.engine.model.Model;
import ru.reactiveturtle.engine.model.loader.ObjLoader;
import ru.reactiveturtle.engine.texture.Texture;
import ru.reactiveturtle.engine.toolkit.Value;

import java.io.IOException;

public final class ModelLoader implements Disposeable {
    public ModelLoader() {
    }

    private final Value<Model> dragunovSniperRiffle = new Value<>();

    public Model getDragunovSniperRiffle() {
        if (dragunovSniperRiffle.hasValue()) {
            return dragunovSniperRiffle.value;
        }
        try {
            Model model = ObjLoader.load("object/weapon/DSR", 1, 1);
            model.getMeshes().values().iterator().next().getMaterial()
                    .setTexture(new Texture("object/weapon/DSR.bmp"));
            model.setScale(0.625f);
            dragunovSniperRiffle.value = model;
            return model;
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new RuntimeException("Model can't be load");
    }

    @Override
    public void dispose() {
        if (dragunovSniperRiffle.hasValue()) {
            dragunovSniperRiffle.value.dispose();
            dragunovSniperRiffle.value = null;
        }
    }
}
