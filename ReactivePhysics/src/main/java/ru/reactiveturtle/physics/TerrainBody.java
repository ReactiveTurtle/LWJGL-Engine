package ru.reactiveturtle.physics;

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
            x -= getX();
            z -= getZ();
            if (!(-width / 2 <= x && x <= width / 2 && -height / 2 <= z && z <= height / 2)) {
                return null;
            }

            float mapX = (x + width / 2) / width * textureWidth;
            float mapZ = (z + height / 2) / height * textureHeight;

            mapX = (int) mapX;
            mapZ = (int) mapZ;

            int positionLeftTop = ((int) (mapZ * (textureWidth - 1)
                    + mapX)) * 18 + 3;
            int positionRightTop = positionLeftTop + 3;
            int positionLeftBottom = positionLeftTop - 3;
            int positionRightBottom = positionLeftTop + 9;

            Vector3f position = new Vector3f(x, 0, z);
            Vector3f leftTop = new Vector3f(vertices[positionLeftTop], vertices[positionLeftTop + 1], vertices[positionLeftTop + 2]);
            Vector3f rightTop = new Vector3f(vertices[positionRightTop], vertices[positionRightTop + 1], vertices[positionRightTop + 2]);
            Vector3f leftBottom = new Vector3f(vertices[positionLeftBottom], vertices[positionLeftBottom + 1], vertices[positionLeftBottom + 2]);
            Vector3f rightBottom = new Vector3f(vertices[positionRightBottom], vertices[positionRightBottom + 1], vertices[positionRightBottom + 2]);

            Plane[] leftTopTrianglePlanes = {
                    new Plane(leftBottom, leftTop, new Vector3f(leftTop.x, leftTop.y - 1, leftTop.z)),
                    new Plane(leftTop, rightTop, new Vector3f(rightTop.x, rightTop.y - 1, rightTop.z)),
                    new Plane(rightTop, leftBottom, new Vector3f(leftBottom.x, leftBottom.y - 1, leftBottom.z))
            };
            Plane plane;
            if (leftTopTrianglePlanes[0].isPointAtFrontOrIn(position) &&
                    !leftTopTrianglePlanes[1].isPointAtFrontOrIn(position) &&
                    leftTopTrianglePlanes[2].isPointAtFrontOrIn(position)) {
                plane = new Plane(leftBottom, leftTop, rightTop);
                return leftTop.y;
            } else {
                plane = new Plane(leftBottom, rightBottom, rightTop);
                return rightBottom.y;
            }
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }
}
