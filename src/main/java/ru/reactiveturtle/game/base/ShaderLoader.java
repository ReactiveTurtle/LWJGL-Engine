package ru.reactiveturtle.game.base;

import ru.reactiveturtle.engine.base.Disposeable;
import ru.reactiveturtle.engine.shader.TextureShader;
import ru.reactiveturtle.engine.toolkit.Value;

public final class ShaderLoader implements Disposeable {
    public ShaderLoader() {
    }

    private Value<TextureShader> textureShader = new Value<>();

    public TextureShader getTextureShader() {
        if (!textureShader.hasValue()) {
            textureShader.value = new TextureShader();
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
