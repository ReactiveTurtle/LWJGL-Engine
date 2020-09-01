package ru.reactiveturtle.physics;

import org.joml.Vector2f;

public class HeightMap extends RigidBody {
    private float[] vertices;
    private final int textureWidth;
    private final int textureHeight;
    private final float width;
    private final float height;

    public HeightMap(float[] vertices, int textureWidth, int textureHeight, float width, float height) {
        this.vertices = new float[vertices.length];
        if (textureWidth <= 0 || textureHeight <= 0 || width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Your "
                    + "textureWidth: " + textureWidth
                    + ", textureHeight: " + textureHeight
                    + ", width: " + width + " or " +
                    "height: " + height + " must be > 0");
        }
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
        this.width = width;
        this.height = height;
        System.arraycopy(vertices, 0, this.vertices, 0, vertices.length);
    }

    @Override
    public CollisionResult isCollide(RigidBody rigidBody, int testCount) {
        return rigidBody instanceof PlaneBody ||
                rigidBody instanceof HeightMap ?
                new CollisionResult(false) :
                rigidBody.isCollide(this, testCount);
    }

    private final static double length45 = Math.sqrt(2) / 2;

    public float getY(float x, float z) {
        try {
            int mapWidth = textureWidth - 1;
            int mapHeight = textureHeight - 1;

            float mapX = (x + width / 2f + getX()) / width * (mapWidth + 1);
            float mapZ = (z + height / 2f + getZ()) / height * (mapHeight + 1) - 1;

            float mapModX = (float) (mapX - Math.floor(mapX));
            float mapModZ = (float) (mapZ - Math.floor(mapZ));

            mapX = (int) mapX;
            mapZ = (int) mapZ;

            int positionLeftTop = ((int) (mapZ * mapWidth
                    + mapX + mapWidth)) * 18 + 4;
            int positionRightTop = positionLeftTop + 3;
            int positionLeftBottom = positionLeftTop - 3;
            int positionRightBottom = positionLeftTop + 9;

            float yTop;
            float yBottom;
            float length = new Vector2f(mapModX, mapModZ).length();
            if (length < length45) {
                yTop = vertices[positionLeftTop]
                        - (vertices[positionLeftTop] - vertices[positionRightTop]) * mapModX;
                yBottom = vertices[positionLeftBottom]
                        - (vertices[positionLeftBottom] - vertices[positionRightTop]) * mapModX;
            } else if (length > length45) {
                yTop = vertices[positionLeftBottom]
                        - (vertices[positionLeftBottom] - vertices[positionRightTop]) * mapModX;
                yBottom = vertices[positionLeftBottom]
                        - (vertices[positionLeftBottom] - vertices[positionRightBottom]) * mapModX;
            } else {
                yTop = vertices[positionLeftBottom]
                        - (vertices[positionLeftBottom] - vertices[positionRightTop]) * mapModX;
                yBottom = yTop;
            }
            return yTop - (yTop - yBottom) * mapModZ;
        } catch (IndexOutOfBoundsException e) {
            return 0;
        }
    }
}
