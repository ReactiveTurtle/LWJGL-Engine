package ru.reactiveturtle.game.engine.model.base;

import ru.reactiveturtle.game.engine.model.Model;
import ru.reactiveturtle.game.engine.model.mesh.Mesh;

public class Parallelepiped extends Model {
    private float width, height, depth;

    public Parallelepiped(float width, float height, float depth) {
        super(new Mesh("parallelepiped", getVertices(width / 2, height / 2, depth / 2), getIndices()));
        this.width = width;
        this.height = height;
        this.depth = depth;
        Mesh mesh = meshes.get("parallelepiped");
        mesh.setTextureCoordinates(new float[]{
                0, 0, 0, 1, 1, 0, 1, 0, 0, 1, 1, 1,
                0, 0, 0, 1, 1, 0, 1, 0, 0, 1, 1, 1,
                0, 0, 0, 1, 1, 0, 1, 0, 0, 1, 1, 1,
                0, 0, 0, 1, 1, 0, 1, 0, 0, 1, 1, 1,
                0, 0, 0, 1, 1, 0, 1, 0, 0, 1, 1, 1,
                0, 0, 0, 1, 1, 0, 1, 0, 0, 1, 1, 1,
        });
        mesh.setNormals(new float[]{
                0f, 0f, 1f, 0f, 0f, 1f, 0f, 0f, 1f,
                0f, 0f, 1f, 0f, 0f, 1f, 0f, 0f, 1f,

                1f, 0f, 0f, 1f, 0f, 0f, 1f, 0f, 0f,
                1f, 0f, 0f, 1f, 0f, 0f, 1f, 0f, 0f,

                -1f, 0f, 0f, -1f, 0f, 0f, -1f, 0f, 0f,
                -1f, 0f, 0f, -1f, 0f, 0f, -1f, 0f, 0f,

                0f, 0f, -1f, 0f, 0f, -1f, 0f, 0f, -1f,
                0f, 0f, -1f, 0f, 0f, -1f, 0f, 0f, -1f,

                0f, 1f, 0f, 0f, 1f, 0f, 0f, 1f, 0f,
                0f, 1f, 0f, 0f, 1f, 0f, 0f, 1f, 0f,

                0f, -1f, 0f, 0f, -1f, 0f, 0f, -1f, 0f,
                0f, -1f, 0f, 0f, -1f, 0f, 0f, -1f, 0f,
        });
    }

    public Parallelepiped(float width, float height, float depth, float textureX, float textureY) {
        super(new Mesh("parallelepiped", getVertices(width / 2, height / 2, depth / 2), getIndices()));
        this.width = width;
        this.height = height;
        this.depth = depth;
        Mesh mesh = meshes.get("parallelepiped");

        float[] texCoords = new float[72];
        for (int i = 0; i < 6; i++) {
            System.arraycopy(new float[]{0, 0, 0, textureY, textureX, 0, textureX, 0, 0, textureY, textureX, textureY}, 0, texCoords, i * 12, 12);
        }
        mesh.setTextureCoordinates(texCoords);
        mesh.setNormals(new float[]{
                0f, 0f, 1f, 0f, 0f, 1f, 0f, 0f, 1f,
                0f, 0f, 1f, 0f, 0f, 1f, 0f, 0f, 1f,

                1f, 0f, 0f, 1f, 0f, 0f, 1f, 0f, 0f,
                1f, 0f, 0f, 1f, 0f, 0f, 1f, 0f, 0f,

                -1f, 0f, 0f, -1f, 0f, 0f, -1f, 0f, 0f,
                -1f, 0f, 0f, -1f, 0f, 0f, -1f, 0f, 0f,

                0f, 0f, -1f, 0f, 0f, -1f, 0f, 0f, -1f,
                0f, 0f, -1f, 0f, 0f, -1f, 0f, 0f, -1f,

                0f, 1f, 0f, 0f, 1f, 0f, 0f, 1f, 0f,
                0f, 1f, 0f, 0f, 1f, 0f, 0f, 1f, 0f,

                0f, -1f, 0f, 0f, -1f, 0f, 0f, -1f, 0f,
                0f, -1f, 0f, 0f, -1f, 0f, 0f, -1f, 0f,
        });
    }


    private float getWidth() {
        return width;
    }

    private float getHeight() {
        return height;
    }

    private float getDepth() {
        return depth;
    }

    private static float[] getVertices(float width, float height, float depth) {
        return new float[]{
                //Front
                -width, height, depth,
                -width, -height, depth,
                width, height, depth,
                width, height, depth,
                -width, -height, depth,
                width, -height, depth,
                //Right
                width, height, depth,
                width, -height, depth,
                width, height, -depth,
                width, height, -depth,
                width, -height, depth,
                width, -height, -depth,
                //Left
                -width, height, -depth,
                -width, -height, -depth,
                -width, height, depth,
                -width, height, depth,
                -width, -height, -depth,
                -width, -height, depth,
                //Back
                width, height, -depth,
                width, -height, -depth,
                -width, height, -depth,
                -width, height, -depth,
                width, -height, -depth,
                -width, -height, -depth,
                //Top
                -width, height, depth,
                -width, height, -depth,
                width, height, depth,
                width, height, depth,
                -width, height, -depth,
                width, height, -depth,
                //Bottom
                -width, -height, depth,
                -width, -height, -depth,
                width, -height, depth,
                width, -height, depth,
                -width, -height, -depth,
                width, -height, -depth,
        };
    }

    private static int[] getIndices() {
        return new int[]{
                0, 1, 2, 3, 4, 5,
                6, 7, 8, 9, 10, 11,
                12, 13, 14, 15, 16, 17,
                18, 19, 20, 21, 22, 23,
                24, 25, 26, 27, 28, 29,
                30, 31, 32, 33, 34, 35
        };
    }
}
