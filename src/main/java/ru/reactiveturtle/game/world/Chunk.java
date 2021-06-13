package ru.reactiveturtle.game.world;

import ru.reactiveturtle.engine.base.Disposeable;
import ru.reactiveturtle.engine.base3d.Stage3D;
import ru.reactiveturtle.engine.texture.Texture;
import ru.reactiveturtle.engine.model.terrain.Terrain;
import ru.reactiveturtle.engine.base.Renderable;
import ru.reactiveturtle.engine.shader.ModelShader;
import ru.reactiveturtle.game.generator.HeightMapGenerator;
import ru.reactiveturtle.physics.TerrainBody;

public class Chunk implements Renderable<Stage3D>, Disposeable {
    public static final int X_PARTS = 32;
    public static final int Z_PARTS = 32;

    public static final float CHUNK_WIDTH = 128f;
    public static final float CHUNK_DEPTH = 128f;

    public static final float PART_WIDTH = CHUNK_WIDTH / X_PARTS;
    public static final float PART_DEPTH = CHUNK_DEPTH / Z_PARTS;

    private Terrain terrain;
    private TerrainBody terrainBody;

    public Chunk(long seed, int x, int y, Texture texture) {
        terrain = HeightMapGenerator.generateHeightMap(seed, x, y, texture);
        terrainBody = new TerrainBody(
                terrain.getVertices(),
                Chunk.X_PARTS,
                Chunk.Z_PARTS,
                Chunk.CHUNK_WIDTH,
                Chunk.CHUNK_DEPTH);
        terrainBody.setPosition(terrain.getPosition());
    }

    public void setShader(ModelShader shader) {
        terrain.setShader(shader);
    }

    @Override
    public void render(Stage3D stage) {
        terrain.render(stage);
    }

    public Terrain getTerrain() {
        return terrain;
    }

    public TerrainBody getTerrainBody() {
        return terrainBody;
    }

    @Override
    public void dispose() {
        terrain.dispose();
    }
}
