package engine.model;

import engine.util.Texture;
import org.joml.Vector3f;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

@Deprecated
public class HeightMap extends Model {
    private static final int MAX_COLOR = 255 * 255 * 255;

    public static HeightMap create(Texture texture, float width, float height, float depth) {
        return new HeightMap(getVertices(texture, width, height, depth), texture);
    }

    private HeightMap(float[] vertices, Texture texture) {
        super(vertices, getIndices(texture.getWidth(), texture.getHeight()));
        super.setTextureCoordinates(getTextureCoordinates(texture.getWidth(), texture.getHeight()));
        super.setNormals(getNormals(vertices, texture.getWidth(), texture.getHeight()));
    }

    private static float[] getVertices(Texture texture, float width, float height, float depth) {
        int imageWidth = texture.getWidth(), imageHeight = texture.getHeight();
        float xBias = width / imageWidth, zBias = depth / imageHeight;
        float[] vertices = new float[18 * (imageWidth - 1) * (imageHeight - 1)];
        float halfWidth = -width / 2f;
        float halfDepth = -depth / 2f;
        System.out.println(halfDepth);
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
        System.out.println(vertices.length);
        return vertices;
    }

    private static int[] getIndices(int width, int height) {
        int[] indices = new int[width * height * 18];
        for (int i = 0; i < indices.length; i++) {
            indices[i] = i;
        }
        return indices;
    }


    private float[] getTextureCoordinates(int width, int height) {
        float[] coords = new float[(width - 1) * (height - 1) * 16];
        for (int i = 0; i < coords.length; i += 8) {
            coords[i] = 0;
            coords[i + 1] = 0;
            coords[i + 2] = 0;
            coords[i + 3] = 1;
            coords[i + 4] = 1;
            coords[i + 5] = 1;
            coords[i + 6] = 1;
            coords[i + 7] = 0;
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
        System.out.println(normals.size());
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

    @Deprecated
    private static void smoothNormals(List<Float> normals, int width, int height) {
        // Нужно сделать сглаживание сразу при вычислении нормалей
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
}
