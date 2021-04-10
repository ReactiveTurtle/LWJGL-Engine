package ru.reactiveturtle.game.world;

import ru.reactiveturtle.engine.base.Stage;
import ru.reactiveturtle.engine.material.Texture;
import ru.reactiveturtle.engine.model.HeightMap;
import ru.reactiveturtle.engine.model.Renderable;
import ru.reactiveturtle.engine.shader.TextureShader;
import ru.reactiveturtle.game.generator.HeightMapGenerator;

public class Chunk implements Renderable {
    public static final int X_PARTS = 32;
    public static final int Z_PARTS = 32;

    public static final float CHUNK_WIDTH = 256f;
    public static final float CHUNK_DEPTH = 256f;

    public static final float PART_WIDTH = CHUNK_WIDTH / X_PARTS;
    public static final float PART_DEPTH = CHUNK_DEPTH / Z_PARTS;

    private HeightMap heightMap;

    public Chunk(long seed, int x, int y, Texture texture) {
        heightMap = HeightMapGenerator.generateHeightMap(seed, x, y, texture);
    }

    public void setShader(TextureShader shader) {
        heightMap.setShader(shader);
    }

    @Override
    public void render(Stage stage) {
        heightMap.render(stage);
    }

    public HeightMap getHeightMap() {
        return heightMap;
    }
}
