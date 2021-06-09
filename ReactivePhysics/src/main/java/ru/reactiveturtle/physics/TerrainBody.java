package ru.reactiveturtle.physics;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.Arrays;

public class TerrainBody extends RigidBody {
    private float[] vertices;
    private final int textureWidth;
    private final int textureHeight;
    private final float width;
    private final float height;

    public TerrainBody(float[] vertices, int textureWidth, int textureHeight, float width, float height) {
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
        this.vertices = Arrays.copyOf(vertices, vertices.length);
    }

    @Override
    public CollisionResult isCollide(RigidBody rigidBody, int testCount) {
        return rigidBody instanceof PlaneBody ||
                rigidBody instanceof TerrainBody ?
                new CollisionResult(false) :
                rigidBody.isCollide(this, testCount);
    }

    public Float getY(float x, float z) {
        try {
            float mapX = (x + width / 2f + getX()) / width * (textureWidth);
            float mapZ = (z + height / 2f + getZ()) / height * (textureHeight);

            float mapModX = (float) (mapX - Math.floor(mapX));
            float mapModZ = (float) (mapZ - Math.floor(mapZ));

            mapX = (int) mapX;
            mapZ = (int) mapZ;

            int positionLeftTop = ((int) (mapZ * (textureWidth) + mapX)) * 18 + 3;
            int positionRightTop = positionLeftTop + 3;
            int positionLeftBottom = positionLeftTop - 3;
            int positionRightBottom = positionLeftTop + 9;

            Vector3f A = new Vector3f(vertices[positionRightTop], vertices[positionRightTop + 1], vertices[positionRightTop + 2]);
            Vector3f C = new Vector3f(vertices[positionLeftBottom], vertices[positionLeftBottom + 1], vertices[positionLeftBottom + 2]);
            if (new Vector2f(x, z).sub(new Vector2f(vertices[positionLeftTop], vertices[positionLeftTop + 2])).length() <
                    new Vector2f(x, z).sub(new Vector2f(vertices[positionRightBottom], vertices[positionRightBottom + 2])).length()) {
                Vector3f B = new Vector3f(vertices[positionLeftTop], vertices[positionLeftTop + 1], vertices[positionLeftTop + 2]);
                float xsFactor = (B.y - A.y) * (C.z - A.z) - (B.z - A.z) * (C.y - A.y);
                float ysFactor = -((B.x - A.x) * (C.z - A.z) - (B.z - A.z) * (C.x - A.x));
                float zsFactor = (B.x - A.x) * (C.y - A.y) - (B.y - A.y) * (C.x - A.x);
                float D = -A.x * xsFactor - A.y * ysFactor - A.z * zsFactor;
                return (-xsFactor * x - zsFactor * z - D) / ysFactor;
            } else {
                Vector3f B = new Vector3f(vertices[positionRightBottom], vertices[positionRightBottom + 1], vertices[positionRightBottom + 2]);
                float xsFactor = (B.y - A.y) * (C.z - A.z) - (B.z - A.z) * (C.y - A.y);
                float ysFactor = -((B.x - A.x) * (C.z - A.z) - (B.z - A.z) * (C.x - A.x));
                float zsFactor = (B.x - A.x) * (C.y - A.y) - (B.y - A.y) * (C.x - A.x);
                float D = -A.x * xsFactor - A.y * ysFactor - A.z * zsFactor;
                return (-xsFactor * x - zsFactor * z - D) / ysFactor;
            }
        } catch (IndexOutOfBoundsException e) {
            return -200f;
        }
    }
}
