package ru.reactiveturtle.physics;

import org.joml.*;

import java.lang.Math;
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
        float mapX = (x - getX()) / width * (textureWidth);
        float mapZ = (z - getZ()) / height * (textureHeight);
        mapX = (float) Math.floor(mapX);
        mapZ = (float) Math.floor(mapZ);
        if (mapX >= 0 && mapX < textureWidth && mapZ >= 0 && mapZ < textureHeight) {
            int positionLeftTop = ((int) (mapZ * (textureWidth) + mapX)) * 18 + 3;
            int positionRightTop = positionLeftTop + 3;
            int positionLeftBottom = positionLeftTop - 3;
            int positionRightBottom = positionLeftTop + 9;

            Vector3f A = new Vector3f(vertices[positionRightTop], vertices[positionRightTop + 1], vertices[positionRightTop + 2]);
            Vector3f B;
            Vector3f C = new Vector3f(vertices[positionLeftBottom], vertices[positionLeftBottom + 1], vertices[positionLeftBottom + 2]);
            if (new Vector2f(x, z).sub(new Vector2f(vertices[positionLeftTop], vertices[positionLeftTop + 2])).length() <
                    new Vector2f(x, z).sub(new Vector2f(vertices[positionRightBottom], vertices[positionRightBottom + 2])).length()) {
                B = new Vector3f(vertices[positionLeftTop], vertices[positionLeftTop + 1], vertices[positionLeftTop + 2]);
                Plane plane = new Plane(A, B, C);
                Line line = new Line(new Vector3f(0, 1, 0), new Vector3f(x - getX(), 0, z - getZ()));
                return line.intersects(plane).y;
            } else {
                B = new Vector3f(vertices[positionRightBottom], vertices[positionRightBottom + 1], vertices[positionRightBottom + 2]);

            }
            Plane plane = new Plane(A, B, C);
            Line line = new Line(new Vector3f(0, 1, 0), new Vector3f(x - getX(), 0, z - getZ()));
            return line.intersects(plane).y;
        } else {
            return null;
        }
    }
}
