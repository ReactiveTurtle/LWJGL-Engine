package ru.reactiveturtle.game.generator;

import ru.reactiveturtle.engine.material.Material;
import ru.reactiveturtle.engine.texture.Texture;
import ru.reactiveturtle.engine.model.terrain.Terrain;
import ru.reactiveturtle.engine.toolkit.MathExtensions;

import java.util.Random;

import static ru.reactiveturtle.game.world.Chunk.*;

public class HeightMapGenerator {
    public static Terrain generateHeightMap(long seed,
                                            int chunkX,
                                            int chunkZ,
                                            Texture texture) {
        float[] vertices = new float[(X_PARTS) * (Z_PARTS) * 18];
        int startIntX = chunkX * X_PARTS, startIntZ = chunkZ * Z_PARTS;
        float startX = 0, startZ = 0;
        for (int i = 0; i < Z_PARTS; i++) {
            for (int j = 0; j < X_PARTS; j++) {
                int index = (i * (X_PARTS) + j) * 18;
                vertices[index] = startX + PART_WIDTH * j;
                vertices[index + 1] = getHeight(seed, startIntX + j, startIntZ + i + 1);
                vertices[index + 2] = startZ + PART_DEPTH * (i + 1);

                vertices[index + 3] = startX + PART_WIDTH * j;
                vertices[index + 4] = getHeight(seed, startIntX + j, startIntZ + i);
                vertices[index + 5] = startZ + PART_DEPTH * i;

                vertices[index + 6] = startX + PART_WIDTH * (j + 1);
                vertices[index + 7] = getHeight(seed, startIntX + j + 1, startIntZ + i);
                vertices[index + 8] = startZ + PART_DEPTH * i;

                vertices[index + 9] = startX + PART_WIDTH * (j + 1);
                vertices[index + 10] = getHeight(seed, startIntX + j + 1, startIntZ + i);
                vertices[index + 11] = startZ + PART_DEPTH * i;

                vertices[index + 12] = startX + PART_WIDTH * (j + 1);
                vertices[index + 13] = getHeight(seed, startIntX + j + 1, startIntZ + i + 1);
                vertices[index + 14] = startZ + PART_DEPTH * (i + 1);

                vertices[index + 15] = startX + PART_WIDTH * j;
                vertices[index + 16] = getHeight(seed, startIntX + j, startIntZ + i + 1);
                vertices[index + 17] = startZ + PART_DEPTH * (i + 1);
            }
        }
        Terrain terrain = new Terrain(CHUNK_WIDTH, CHUNK_DEPTH, vertices, X_PARTS + 1, Z_PARTS + 1, 1f, 1f);
        terrain.setPosition(chunkX * CHUNK_WIDTH + startX, 0, chunkZ * CHUNK_DEPTH + startZ);
        return terrain;
    }

    public static float getHeight(long seed, float x, float y) {
        return (genNumber(seed, x, y)) * 10;
    }

    private static float genNumber(long seed, float x, float z) {
        double dx = x / CHUNK_WIDTH;
        double dz = z / CHUNK_DEPTH;
        Random random = new Random(seed);
        double frequencyX = random.nextDouble() + 2;
        double frequencyY = random.nextDouble() + 2;
        return (float) MathExtensions.noise(dx * frequencyX + seed, dz * frequencyY + seed);
    }
}
