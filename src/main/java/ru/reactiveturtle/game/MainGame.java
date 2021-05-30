package ru.reactiveturtle.game;

import ru.reactiveturtle.engine.base.GameContext;
import ru.reactiveturtle.game.base.ModelLoader;
import ru.reactiveturtle.game.base.ShaderLoader;
import ru.reactiveturtle.game.world.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainGame extends GameContext {
    private World world;
    private ModelLoader modelLoader;
    private ShaderLoader shaderLoader;

    @Override
    protected void run() {
        modelLoader = new ModelLoader();
        shaderLoader = new ShaderLoader();
        world = new World(this);
        setStage(world);
        world.start();
    }

    @Override
    public void dispose() {
    }

    public ModelLoader getModelLoader() {
        return modelLoader;
    }

    public ShaderLoader getShaderLoader() {
        return shaderLoader;
    }

    private final List<Integer> ID_LIST = new ArrayList<>();
    private final Random random = new Random();

    public synchronized int generateId() {
        int randInt = random.nextInt();
        while (ID_LIST.contains(randInt)) {
            randInt = random.nextInt();
        }
        return randInt;
    }

    public synchronized void freeId(int id) {
        ID_LIST.remove(Integer.valueOf(id));
    }
}
