package ru.reactiveturtle.game.buildings;

import ru.reactiveturtle.engine.base.Shader;
import ru.reactiveturtle.game.MainGame;
import ru.reactiveturtle.game.base.Entity;
import ru.reactiveturtle.game.base.EntityPhase;
import ru.reactiveturtle.game.base.ModelLoader;

public class Wall extends Entity {
    public Wall(MainGame gameContext, String name) {
        super(gameContext, name);
    }

    @Override
    protected EntityPhase[] getDefaultEntityPhases(ModelLoader modelLoader) {
        return new EntityPhase[0];
    }

    @Override
    protected Shader initShader() {
        return gameContext.getShaderLoader().getModelShader();
    }

    @Override
    public int getNextState() {
        return 0;
    }
}
