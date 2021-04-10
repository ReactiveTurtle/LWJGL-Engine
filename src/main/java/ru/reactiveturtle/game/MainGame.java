package ru.reactiveturtle.game;

import ru.reactiveturtle.engine.base.GameContext;
import ru.reactiveturtle.game.world.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainGame extends GameContext {
    private World world;

    private static final List<Integer> ID_LIST = new ArrayList<>();
    private static final Random random = new Random();

    public static int generateId() {
        int randInt = random.nextInt();
        while (ID_LIST.contains(randInt)) {
            randInt = random.nextInt();
        }
        return randInt;
    }

    @Override
    protected void run() {
        world = new World(this);
        world.start();
        setStage(world);
    }

    @Override
    protected void destroy() {
    }
}
