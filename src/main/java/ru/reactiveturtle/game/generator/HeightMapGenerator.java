package ru.reactiveturtle.game.generator;

import ru.reactiveturtle.engine.material.Material;
import ru.reactiveturtle.engine.texture.Texture;
import ru.reactiveturtle.engine.model.terrain.Terrain;

import java.util.Random;

import static ru.reactiveturtle.game.world.Chunk.*;

public class HeightMapGenerator {
    public static Terrain generateHeightMap(long seed,
                                            int chunkX,
                                            int chunkZ,
                                            Texture texture) {
        float[] vertices = new float[(X_PARTS) * (Z_PARTS) * 18];
        int startIntX = chunkX * X_PARTS, startIntZ = chunkZ * Z_PARTS;
        float startX = -CHUNK_WIDTH / 2, startZ = -CHUNK_DEPTH / 2;
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
        Material heightMapMaterial = new Material();
        heightMapMaterial.setDiffuse(1f, 1f, 1f);
        heightMapMaterial.setTexture(texture);
        Terrain terrain = new Terrain(CHUNK_WIDTH, CHUNK_DEPTH, vertices, X_PARTS + 1, Z_PARTS + 1, 1f, 1f);
        terrain.setPosition(chunkX * CHUNK_WIDTH + startX, 0, chunkZ * CHUNK_DEPTH + startZ);
        terrain.setMaterial(heightMapMaterial);
        return terrain;
    }

    public static float getHeight(long seed, int x, int y) {
        float left = genSmoothNumber(seed, x - 1, y);
        float leftTop = genSmoothNumber(seed, x - 1, y - 1);
        float top = genSmoothNumber(seed, x, y - 1);
        float rightTop = genSmoothNumber(seed, x + 1, y - 1);
        float right = genSmoothNumber(seed, x + 1, y);
        float rightBottom = genSmoothNumber(seed, x + 1, y + 1);
        float bottom = genSmoothNumber(seed, x, y + 1);
        float leftBottom = genSmoothNumber(seed, x - 1, y + 1);
        float center = genSmoothNumber(seed, x, y);
        return (left / 8 + leftTop / 8 + top / 8 + rightTop / 8 +
                right / 8 + rightBottom / 8 + bottom / 8 + leftBottom / 8) * 0;
    }

    public static float genSmoothNumber(long seed, int x, int y) {
        float left = genNumber(seed, x - 1, y);
        float leftTop = genNumber(seed, x - 1, y - 1);
        float top = genNumber(seed, x, y - 1);
        float rightTop = genNumber(seed, x + 1, y - 1);
        float right = genNumber(seed, x + 1, y);
        float rightBottom = genNumber(seed, x + 1, y + 1);
        float bottom = genNumber(seed, x, y + 1);
        float leftBottom = genNumber(seed, x - 1, y + 1);
        float center = genNumber(seed, x, y);
        return (left / 8 + leftTop / 8 + top / 8 + rightTop / 8 +
                right / 8 + rightBottom / 8 + bottom / 8 + leftBottom / 8);
    }

    public static float genNumber(long seed, int x, int y) {
        long factor1 = seed - (x * y);
        long factor2 = factor1 * (x - y);
        Random random = new Random((int) (factor1 * (x - y - factor2 * factor2) * x * y));
        return random.nextFloat();
    }
}
