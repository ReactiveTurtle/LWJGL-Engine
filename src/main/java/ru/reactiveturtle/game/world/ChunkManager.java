package ru.reactiveturtle.game.world;

import org.joml.Vector3f;

import ru.reactiveturtle.engine.base.Renderable;
import ru.reactiveturtle.engine.base3d.Stage3D;
import ru.reactiveturtle.engine.material.Material;
import ru.reactiveturtle.engine.texture.Texture;
import ru.reactiveturtle.game.MainGame;
import ru.reactiveturtle.physics.TerrainBody;

public class ChunkManager implements Renderable<Stage3D> {
    private Vector3f position;
    private int chunksRenderCount = 2;
    public Chunk[][] chunks = new Chunk[chunksRenderCount * 2][chunksRenderCount * 2];
    public TerrainBody[][] bodies = new TerrainBody[chunksRenderCount * 2][chunksRenderCount * 2];
    public Texture texture;
    private Material material;

    public ChunkManager(MainGame gameContext, Physic physic, Vector3f position) {
        this.position = position;
        texture = new Texture("texture/sand.jpg");
        int chunkX = (int) Math.floor(position.x / Chunk.CHUNK_WIDTH);
        int chunkZ = (int) Math.floor(position.z / Chunk.CHUNK_DEPTH);
        material = new Material();
        material.setDiffuse(1f, 1f, 1f);
        material.setAmbient(0.4f, 0.4f, 0.2f);
        material.setTexture(texture);
        for (int i = -chunksRenderCount; i < chunksRenderCount; i++) {
            for (int j = -chunksRenderCount; j < chunksRenderCount; j++) {
                Chunk chunk = new Chunk(0, j + chunkX, i + chunkZ, texture);
                chunk.getTerrain().setMaterial(material);
                chunk.setShader(gameContext.getShaderLoader().getModelShader());
                chunks[i + chunksRenderCount][j + chunksRenderCount] = chunk;
                physic.putBody(chunk.getTerrainBody());
            }
        }
    }

    @Override
    public void render(Stage3D stage) {
        for (Chunk[] chunksLine : chunks) {
            for (Chunk chunk : chunksLine) {
                chunk.render(stage);
            }
        }
    }

    public void update(Vector3f position, MainGame gameContext, Physic physic) {
        int chunkX = (int) Math.floor(this.position.x / Chunk.CHUNK_WIDTH);
        int chunkZ = (int) Math.floor(this.position.z / Chunk.CHUNK_DEPTH);

        int newChunkX = (int) Math.floor(position.x / Chunk.CHUNK_WIDTH);
        int newChunkZ = (int) Math.floor(position.z / Chunk.CHUNK_DEPTH);
        if (chunkX == newChunkX && chunkZ == newChunkZ) {
            return;
        }
        this.position = position;

        for (int i = -chunksRenderCount; i < chunksRenderCount; i++) {
            for (int j = -chunksRenderCount; j < chunksRenderCount; j++) {
                Chunk oldChunk = chunks[i + chunksRenderCount][j + chunksRenderCount];
                oldChunk.dispose();
                physic.removeBody(oldChunk.getTerrainBody());

                Chunk chunk = new Chunk(0, j + newChunkX, i + newChunkZ, texture);
                chunk.setShader(gameContext.getShaderLoader().getModelShader());
                chunk.getTerrain().setMaterial(material);
                chunks[i + chunksRenderCount][j + chunksRenderCount] = chunk;
                physic.putBody(chunk.getTerrainBody());
            }
        }
    }
}
