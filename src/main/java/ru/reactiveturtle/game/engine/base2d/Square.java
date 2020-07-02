package ru.reactiveturtle.game.engine.base2d;

import ru.reactiveturtle.game.engine.material.Material;
import ru.reactiveturtle.game.engine.material.Texture;
import ru.reactiveturtle.game.engine.model.mesh.Mesh;

public class Square extends Model2d {
    private float width, height;
    private static final String MESH_NAME = "square";

    public Square(float width, float height,
                  Texture texture,
                  float textureX, float textureY) {
        super(new Mesh(MESH_NAME, getVertices(width, height), getIndices()));
        this.width = width;
        this.height = height;
        meshes.get(MESH_NAME).setTextureCoordinates(new float[]{
                0, 0,
                0, textureX,
                textureY, textureX,
                textureY, 0,
        });
        meshes.get(MESH_NAME).setMaterial(new Material());
        setTexture(texture);
    }

    public void setTexture(Texture texture) {
        meshes.get(MESH_NAME).getMaterial().setTexture(texture);
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    private static float[] getVertices(float width, float depth) {
        return new float[]{
                -width, depth, 0,
                -width, -depth, 0,
                width, -depth, 0,
                width, depth, 0,
        };
    }

    private static int[] getIndices() {
        return new int[]{
                0, 1, 3, 3, 1, 2,
        };
    }
}
