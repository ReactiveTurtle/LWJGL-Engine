package ru.reactiveturtle.game.base;

import ru.reactiveturtle.engine.base.Disposeable;
import ru.reactiveturtle.engine.shader.ModelShader;
import ru.reactiveturtle.engine.toolkit.Value;

public final class ShaderLoader implements Disposeable {
    public ShaderLoader() {
    }

    private Value<ModelShader> textureShader = new Value<>();

    public ModelShader getTextureShader() {
        if (!textureShader.hasValue()) {
            textureShader.value = new ModelShader();
        }
        return textureShader.value;
    }

    @Override
    public void dispose() {
        if (textureShader.hasValue()) {
            textureShader.value.dispose();
            textureShader.value = null;
        }
    }
}
