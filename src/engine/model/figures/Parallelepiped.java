package engine.model.figures;

import engine.model.Model;

public class Parallelepiped extends Model {
    private float width, height, depth;

    public Parallelepiped(float width, float height, float depth) {
        super(getVertices(width / 2, height / 2, depth / 2), getIndices());
        this.width = width;
        this.height = height;
        this.depth = depth;
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
                width, -height, depth,
                width, height, depth,
                //Right
                width, height, -depth,
                width, -height, -depth,
                width, -height, depth,
                width, height, depth,
                //Left
                -width, height, -depth,
                -width, -height, -depth,
                -width, -height, depth,
                -width, height, depth,
                //Back
                -width, height, -depth,
                -width, -height, -depth,
                width, -height, -depth,
                width, height, -depth,
                //Top
                -width, height, depth,
                -width, height, -depth,
                width, height, -depth,
                width, height, depth,
                //Bottom
                -width, -height, depth,
                -width, -height, -depth,
                width, -height, -depth,
                width, -height, depth,
        };
    }

    private static int[] getIndices() {
        return new int[]{
                0, 1, 3, 3, 1, 2,
                4, 5, 7, 7, 5, 6,
                8, 9, 11, 11, 9, 10,
                12, 13, 15, 15, 13, 14,
                16, 17, 19, 19, 17, 18,
                20, 21, 23, 23, 21, 22,
        };
    }
}
