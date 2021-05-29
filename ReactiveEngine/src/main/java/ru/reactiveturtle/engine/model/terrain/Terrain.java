package ru.reactiveturtle.engine.model.terrain;

import org.joml.Vector2f;
import org.joml.Vector3f;
import ru.reactiveturtle.engine.texture.Texture;
import ru.reactiveturtle.engine.model.Model;
import ru.reactiveturtle.engine.model.mesh.Mesh;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Terrain extends Model {
    private static final String MESH_KEY = "Terrain";
    private static final int MAX_COLOR = 255 * 255 * 255;

    private float[] vertices;
    private float[] normals;
    private float width, depth;
    private int mapWidth, mapDepth;

    public static Terrain create(Texture heightMapTexture, float width, float height, float depth, float textureX, float textureY) {
        return new Terrain(width, depth, getVertices(heightMapTexture, width, height, depth),
                heightMapTexture.getWidth(), heightMapTexture.getHeight(), textureX, textureY);
    }

    public Terrain(float width, float depth, float[] vertices, int mapWidth, int mapDepth, float textureX, float textureY) {
        super(new Mesh(MESH_KEY, vertices, getIndices(mapWidth, mapDepth)));
        this.mapWidth = mapWidth;
        this.mapDepth = mapDepth;
        this.width = width;
        this.depth = depth;
        this.vertices = vertices;
        Mesh mesh = meshes.get(MESH_KEY);
        mesh.setTextureCoordinates(getTextureCoordinates(mapWidth, mapDepth, textureX, textureY));
        this.normals = getNormals(vertices, mapWidth, mapDepth);
        mesh.setNormals(normals);
    }

    public float[] getVertices() {
        return Arrays.copyOf(vertices, vertices.length);
    }

    private static float[] getVertices(Texture texture, float width, float height, float depth) {
        int imageWidth = texture.getWidth(), imageHeight = texture.getHeight();
        float xBias = width / imageWidth, zBias = depth / imageHeight;
        float[] vertices = new float[18 * (imageWidth - 1) * (imageHeight - 1)];
        float halfWidth = -width / 2f;
        float halfDepth = -depth / 2f;
        for (int i = 0; i < imageHeight - 1; i++) {
            for (int j = 0; j < imageWidth - 1; j++) {
                int part = (i * (imageWidth - 1) + j) * 18;
                float center = getPixel(j, i, texture.getWidth(), height, texture.getPixelsBuffer());
                float right = getPixel(j + 1, i, texture.getWidth(), height, texture.getPixelsBuffer());
                float rightBottom = getPixel(j + 1, i + 1, texture.getWidth(), height, texture.getPixelsBuffer());
                float bottom = getPixel(j, i + 1, texture.getWidth(), height, texture.getPixelsBuffer());

                vertices[part] = halfWidth + xBias * j;
                vertices[part + 1] = bottom;
                vertices[part + 2] = halfDepth + zBias * (i + 1);

                vertices[part + 3] = halfWidth + xBias * j;
                vertices[part + 4] = center;
                vertices[part + 5] = halfDepth + zBias * i;

                vertices[part + 6] = halfWidth + xBias * (j + 1);
                vertices[part + 7] = right;
                vertices[part + 8] = halfDepth + zBias * i;

                vertices[part + 9] = halfWidth + xBias * (j + 1);
                vertices[part + 10] = right;
                vertices[part + 11] = halfDepth + zBias * i;

                vertices[part + 12] = halfWidth + xBias * (j + 1);
                vertices[part + 13] = rightBottom;
                vertices[part + 14] = halfDepth + zBias * (i + 1);

                vertices[part + 15] = halfWidth + xBias * j;
                vertices[part + 16] = bottom;
                vertices[part + 17] = halfDepth + zBias * (i + 1);
            }
        }
        return vertices;
    }

    private static int[] getIndices(int width, int height) {
        int[] indices = new int[width * height * 18];
        for (int i = 0; i < indices.length; i++) {
            indices[i] = i;
        }
        return indices;
    }


    private float[] getTextureCoordinates(int width, int height, float textureX, float textureY) {
        float[] coords = new float[(width - 1) * (height - 1) * 24];
        for (int i = 0; i < coords.length; i += 12) {
            coords[i] = 0;
            coords[i + 1] = textureY;
            coords[i + 2] = 0;
            coords[i + 3] = 0;
            coords[i + 4] = textureX;
            coords[i + 5] = 0;
            coords[i + 6] = textureX;
            coords[i + 7] = 0;
            coords[i + 8] = textureX;
            coords[i + 9] = textureY;
            coords[i + 10] = 0;
            coords[i + 11] = textureY;
        }
        return coords;
    }

    private float[] getNormals(float[] vertices, int width, int height) {
        Vector3f leftTop = new Vector3f();
        Vector3f rightTop = new Vector3f();
        Vector3f rightBottom = new Vector3f();
        Vector3f leftBottom = new Vector3f();

        Vector3f v0 = new Vector3f();
        Vector3f v1 = new Vector3f();

        List<Float> normals = new ArrayList<>();
        for (int i = 0; i < height - 1; i++) {
            for (int j = 0; j < width - 1; j++) {
                int part = (i * (width - 1) + j) * 18;
                leftBottom.set(vertices[part], vertices[part + 1], vertices[part + 2]);
                leftTop.set(vertices[part + 3], vertices[part + 4], vertices[part + 5]);
                rightTop.set(vertices[part + 6], vertices[part + 7], vertices[part + 8]);
                rightBottom.set(vertices[part + 12], vertices[part + 13], vertices[part + 14]);
                v0.set(leftBottom.x - leftTop.x, leftBottom.y - leftTop.y, leftBottom.z - leftTop.z);
                v1.set(rightTop.x - leftTop.x, rightTop.y - leftTop.y, rightTop.z - leftTop.z);
                addNormals(normals, v0, v1);
                v0.set(leftBottom.x - rightBottom.x, leftBottom.y - rightBottom.y, leftBottom.z - rightBottom.z);
                v1.set(rightTop.x - rightBottom.x, rightTop.y - rightBottom.y, rightTop.z - rightBottom.z);
                addNormals(normals, v0, v1);
            }
        }
        smoothNormals(normals, width - 1, height - 1);
        return toFloatArray(normals);
    }

    private static float getPixel(int x, int z, int imageWidth, float height, ByteBuffer buffer) {
        byte r = buffer.get(x * 4 + z * 4 * imageWidth);
        byte g = buffer.get(x * 4 + 1 + z * 4 * imageWidth);
        byte b = buffer.get(x * 4 + 2 + z * 4 * imageWidth);
        byte a = buffer.get(x * 4 + 3 + z * 4 * imageWidth);
        int argb = ((0xFF & a) << 24) | ((0xFF & r) << 16)
                | ((0xFF & g) << 8) | (0xFF & b);
        return height * (1 + ((float) argb / (float) MAX_COLOR));
    }

    private static float[] toFloatArray(List<Float> list) {
        float[] array = new float[list.size()];
        for (int i = 0; i < array.length; i++) {
            array[i] = list.get(i);
        }
        return array;
    }

    private static void addNormals(List<Float> normals, Vector3f v0, Vector3f v1) {
        Vector3f vector = new Vector3f();
        v0.cross(v1, vector);
        vector.normalize();
        for (int k = 0; k < 3; k++) {
            normals.add(vector.x);
            normals.add(vector.y);
            normals.add(vector.z);
        }
    }

    private static void smoothNormals(List<Float> normals, int width, int height) {
        for (int i = 1; i < height; i++) {
            for (int j = 1; j < width; j++) {
                int part = (i * width + j) * 18 + 3;
                Vector3f vector = new Vector3f(normals.get(part), normals.get(part + 1), normals.get(part + 2));

                int top = ((i - 1) * width + j) * 18;
                int left = (i * width + j - 1) * 18 + 6;
                int leftTop = ((i - 1) * width + j - 1) * 18 + 12;

                vector.x = (vector.x + normals.get(top) + normals.get(left)) / 3;
                vector.y = (vector.y + normals.get(top + 1) + normals.get(left + 1)) / 3;
                vector.z = (vector.z + normals.get(top + 2) + normals.get(left + 2)) / 3;
                normals.set(leftTop, vector.x);
                normals.set(leftTop + 1, vector.y);
                normals.set(leftTop + 2, vector.z);

                normals.set(top, vector.x);
                normals.set(top + 1, vector.y);
                normals.set(top + 2, vector.z);
                normals.set(top + 15, vector.x);
                normals.set(top + 16, vector.y);
                normals.set(top + 17, vector.z);

                normals.set(left, vector.x);
                normals.set(left + 1, vector.y);
                normals.set(left + 2, vector.z);
                normals.set(left + 3, vector.x);
                normals.set(left + 4, vector.y);
                normals.set(left + 5, vector.z);

                normals.set(part, vector.x);
                normals.set(part + 1, vector.y);
                normals.set(part + 2, vector.z);
            }
        }

        for (int i = 0; i < width - 1; i++) {
            int center = i * 18 + 6;
            int right = (i + 1) * 18 + 3;
            float middleX = (normals.get(center) + normals.get(right)) / 2;
            float middleY = (normals.get(center + 1) + normals.get(right + 1)) / 2;
            float middleZ = (normals.get(center + 2) + normals.get(right + 2)) / 2;

            normals.set(center, middleX);
            normals.set(center + 1, middleY);
            normals.set(center + 2, middleZ);
            normals.set(center + 3, middleX);
            normals.set(center + 4, middleY);
            normals.set(center + 5, middleZ);
            normals.set(right, middleX);
            normals.set(right + 1, middleY);
            normals.set(right + 2, middleZ);
        }

        for (int i = 0; i < height - 1; i++) {
            int center = i * width * 18;
            int bottom = (i + 1) * width * 18 + 3;
            float middleX = (normals.get(center) + normals.get(bottom)) / 2;
            float middleY = (normals.get(center + 1) + normals.get(bottom + 1)) / 2;
            float middleZ = (normals.get(center + 2) + normals.get(bottom + 2)) / 2;

            normals.set(center, middleX);
            normals.set(center + 1, middleY);
            normals.set(center + 2, middleZ);
            normals.set(center + 15, middleX);
            normals.set(center + 16, middleY);
            normals.set(center + 17, middleZ);
            normals.set(bottom, middleX);
            normals.set(bottom + 1, middleY);
            normals.set(bottom + 2, middleZ);
        }

        for (int i = 1; i < width; i++) {
            int center = ((height - 1) * width + i) * 18;
            int left = ((height - 1) * width + i - 1) * 18;
            float middleX = normals.get(center);
            float middleY = normals.get(center + 1);
            float middleZ = normals.get(center + 2);

            normals.set(center + 15, middleX);
            normals.set(center + 16, middleY);
            normals.set(center + 17, middleZ);
            normals.set(left + 12, middleX);
            normals.set(left + 13, middleY);
            normals.set(left + 14, middleZ);
        }

        for (int i = 1; i < height; i++) {
            int center = (i * width + width - 1) * 18 + 6;
            int bottom = ((i - 1) * width + width - 1) * 18 + 12;
            float middleX = normals.get(center);
            float middleY = normals.get(center + 1);
            float middleZ = normals.get(center + 2);

            normals.set(center + 3, middleX);
            normals.set(center + 4, middleY);
            normals.set(center + 5, middleZ);
            normals.set(bottom, middleX);
            normals.set(bottom + 1, middleY);
            normals.set(bottom + 2, middleZ);
        }
        int rightTop = (width - 1) * 18 + 6;
        normals.set(rightTop + 3, normals.get(rightTop));
        normals.set(rightTop + 4, normals.get(rightTop + 1));
        normals.set(rightTop + 5, normals.get(rightTop + 2));

        int rightBottom = ((height - 1) * width + width - 1) * 18 + 9;
        float middleX = (normals.get(rightBottom) + normals.get(rightBottom + 6)) / 2;
        float middleY = (normals.get(rightBottom + 1) + normals.get(rightBottom + 7)) / 2;
        float middleZ = (normals.get(rightBottom + 2) + normals.get(rightBottom + 8)) / 2;

        normals.set(rightBottom + 3, middleX);
        normals.set(rightBottom + 4, middleY);
        normals.set(rightBottom + 5, middleZ);

        int leftBottom = (height - 1) * width * 18;

        normals.set(leftBottom + 15, normals.get(leftBottom));
        normals.set(leftBottom + 16, normals.get(leftBottom + 1));
        normals.set(leftBottom + 17, normals.get(leftBottom + 2));
    }

    public float getY(float x, float z) {
        try {
            float mapX = (x + width / 2f + getX()) / width * (mapWidth);
            float mapZ = (z + depth / 2f + getZ()) / depth * (mapDepth);

            float mapModX = (float) (mapX - Math.floor(mapX));
            float mapModZ = (float) (mapZ - Math.floor(mapZ));

            mapX = (int) mapX;
            mapZ = (int) mapZ;

            int positionLeftTop = ((int) (mapZ * (mapWidth - 1)
                    + mapX)) * 18 + 3;
            int positionRightTop = positionLeftTop + 3;
            int positionLeftBottom = positionLeftTop - 3;
            int positionRightBottom = positionLeftTop + 9;

            Vector3f A = new Vector3f(vertices[positionRightTop], vertices[positionRightTop + 1], vertices[positionRightTop + 2]);
            Vector3f C = new Vector3f(vertices[positionLeftBottom], vertices[positionLeftBottom + 1], vertices[positionLeftBottom + 2]);
            float length = new Vector2f(mapModX, mapModZ).length();
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
            return 0;
        }
    }

    public Vector3f getRotation(float x, float z) {
        try {
            float mapX = (x + width / 2f + getX()) / width * (mapWidth + 1);
            float mapZ = (z + depth / 2f + getZ()) / depth * (mapDepth + 1);

            mapX = (int) mapX;
            mapZ = (int) mapZ;

            int positionLeftTop = ((int) (mapZ * mapWidth
                    + mapX + mapWidth)) * 18 + 3;
            int positionRightTop = positionLeftTop + 3;
            int positionLeftBottom = positionLeftTop - 3;
            int positionRightBottom = positionLeftTop + 9;

            Vector3f normal = new Vector3f();
            normal.add(normals[positionLeftBottom],
                    normals[positionLeftBottom + 1],
                    normals[positionLeftBottom + 2]);
            normal.add(normals[positionRightTop],
                    normals[positionRightTop + 1],
                    normals[positionRightTop + 2]);
            if (new Vector2f(x, z).sub(new Vector2f(vertices[positionLeftTop], vertices[positionLeftTop + 2])).length() <
                    new Vector2f(x, z).sub(new Vector2f(vertices[positionRightBottom], vertices[positionRightBottom + 2])).length()) {
                normal.add(normals[positionLeftTop],
                        normals[positionLeftTop + 1],
                        normals[positionLeftTop + 2]);
            } else {
                normal.add(normals[positionRightBottom],
                        normals[positionRightBottom + 1],
                        normals[positionRightBottom + 2]);
            }

            normal.div(3);

            Vector3f normalYZ = new Vector3f(0, normal.y, normal.z).normalize(1);
            Vector3f normalXY = new Vector3f(normal.x, normal.y, 0).normalize(1);
            Vector3f rotation = new Vector3f();
            rotation.x = 90 - (float) Math.toDegrees(Math.acos(normalYZ.z) * Math.signum(normalYZ.y));
            rotation.z = (float) Math.toDegrees(Math.acos(normalXY.x) * Math.signum(normalXY.y)) - 90;
            return rotation;
        } catch (IndexOutOfBoundsException e) {
            return new Vector3f(0, 1, 0);
        }
    }

    public int getMapWidth() {
        return mapWidth;
    }

    public int getMapDepth() {
        return mapDepth;
    }

    public float getWidth() {
        return width;
    }

    public float getDepth() {
        return depth;
    }
}