package ru.reactiveturtle.game.engine.model.base;

import ru.reactiveturtle.game.engine.model.Model;
import ru.reactiveturtle.game.engine.model.mesh.Mesh;

public class Flat extends Model {
    private float width, depth;

    public Flat(float width, float depth, float textureX, float textureY) {
        super(new Mesh("flat", getVertices(width / 2, depth / 2), getIndices()));
        this.width = width;
        this.depth = depth;
        Mesh mesh = meshes.get("flat");
        mesh.setTextureCoordinates(new float[]{
                0, 0, textureY, 0, textureY, textureX, 0, textureX
        });
        mesh.setNormals(new float[]{
                0f, 1f, 0f, 0f, 1f, 0f, 0f, 1f, 0f, 0f, 1f, 0f
        });
    }


    private float getWidth() {
        return width;
    }

    private float getDepth() {
        return depth;
    }

    private static float[] getVertices(float width, float depth) {
        return new float[]{
                -width, 0, depth,
                -width, 0, -depth,
                width, 0, -depth,
                width, 0, depth,
        };
    }

    private static int[] getIndices() {
        return new int[]{
                0, 1, 3, 3, 1, 2,
        };
    }
}
