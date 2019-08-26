package engine.model;

import engine.util.Texture;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.glfwGetTime;

public class Terrain extends Model {
    private static final int MAX_COLOR = 255 * 255 * 255;

    private float[] heightMap;

    public static Terrain create(Texture texture, float width, float height, float depth) {
        float[][] maps = generateVertices(texture, width, height, depth);
        return new Terrain(maps[0], maps[1], texture);
    }

    private Terrain(float[] vertices, float[] heightMap, Texture texture) {
        super(vertices, getIndices(texture.getWidth(), texture.getHeight()));
        super.setTextureCoordinates(getTextureCoordinates(texture.getWidth(), texture.getHeight()));
        super.setNormals(generateNormals(vertices, texture.getWidth(), texture.getHeight()));
        this.heightMap = heightMap;
    }

    public float[] getHeightMap() {
        return heightMap;
    }

    private static float[][] generateVertices(Texture texture, float width, float height, float depth) {
        System.out.println("HeightMap(" + glfwGetTime() + "): генерация вершин");
        int imageWidth = texture.getWidth(), imageHeight = texture.getHeight();
        float xBias = width / imageWidth, zBias = depth / imageHeight;
        float leftRange = -width / 2f;
        float topRange = -depth / 2f;

        float[] vertices = new float[imageWidth * imageHeight * 18];
        float[] heightMap = new float[(imageWidth + 1) * (imageHeight + 1)];

        for (int i = 0; i < imageHeight + 1; i++) {
            for (int j = 0; j < imageWidth + 1; j++) {
                int centerPart = (i * imageWidth + j) * 18;
                int topPart = ((i - 1) * imageWidth + j) * 18;
                int leftPart = (i * imageWidth + j - 1) * 18;
                int leftTopPart = ((i - 1) * imageWidth + j - 1) * 18;

                int pixelsCount = 0;
                float y = 0;

                if (i < imageHeight) {
                    if (j < imageWidth) {
                        y += getPixel(j, i, imageWidth, height, texture.getPixelsBuffer());
                        pixelsCount++;
                    }
                    if (j > 0) {
                        y += getPixel(j - 1, i, imageWidth, height, texture.getPixelsBuffer());
                        pixelsCount++;
                    }
                }

                if (i > 0) {
                    if (j > 0) {
                        y += getPixel(j - 1, i - 1, imageWidth, height, texture.getPixelsBuffer());
                        pixelsCount++;
                    }
                    if (j < imageWidth) {
                        y += getPixel(j, i - 1, imageWidth, height, texture.getPixelsBuffer());
                        pixelsCount++;
                    }
                }

                y /= pixelsCount;

                if (i < imageHeight) {
                    if (j < imageWidth) {
                        Vector2f leftBottom = new Vector2f(leftRange + xBias * j, topRange + zBias * (i + 1));
                        Vector2f leftTop = new Vector2f(leftBottom.x, topRange + zBias * i);
                        Vector2f rightTop = new Vector2f(leftRange + xBias * (j + 1), leftTop.y);
                        Vector2f rightBottom = new Vector2f(rightTop.x, leftBottom.y);

                        vertices[centerPart] = leftBottom.x;
                        vertices[centerPart + 2] = leftBottom.y;

                        vertices[centerPart + 3] = leftTop.x;
                        vertices[centerPart + 4] = y;
                        vertices[centerPart + 5] = leftTop.y;

                        vertices[centerPart + 6] = rightTop.x;
                        vertices[centerPart + 8] = rightTop.y;

                        vertices[centerPart + 9] = rightTop.x;
                        vertices[centerPart + 11] = rightTop.y;

                        vertices[centerPart + 12] = rightBottom.x;
                        vertices[centerPart + 14] = rightBottom.y;

                        vertices[centerPart + 15] = leftBottom.x;
                        vertices[centerPart + 17] = leftBottom.y;
                    }
                    if (j > 0) {
                        vertices[leftPart + 7] = y;
                        vertices[leftPart + 10] = y;
                    }
                }


                if (i > 0) {
                    if (j < imageWidth) {
                        vertices[topPart + 1] = y;
                        vertices[topPart + 16] = y;
                    }
                    if (j > 0) {
                        vertices[leftTopPart + 13] = y;
                    }
                }

                heightMap[centerPart / 18] = y;
            }
        }
        System.out.println("HeightMap(" + glfwGetTime() + "): генерация завершена. Количество координат вершин: " + vertices.length);
        System.out.println("HeightMap(" + glfwGetTime() + "): генерация завершена. Количество координат высот: " + heightMap.length);
        return new float[][]{vertices, heightMap};
    }

    private static int[] getIndices(int width, int height) {
        int[] indices = new int[width * height * 18];
        for (int i = 0; i < indices.length; i++) {
            indices[i] = i;
        }
        return indices;
    }

    private static float[] getTextureCoordinates(int width, int height) {
        float[] coords = new float[width * height * 8 * 2];
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

    private static float getPixel(int x, int z, int imageWidth, float height, ByteBuffer buffer) {
        byte r = buffer.get(x * 4 + z * 4 * imageWidth);
        byte g = buffer.get(x * 4 + 1 + z * 4 * imageWidth);
        byte b = buffer.get(x * 4 + 2 + z * 4 * imageWidth);
        byte a = buffer.get(x * 4 + 3 + z * 4 * imageWidth);
        int argb = ((0xFF & a) << 24) | ((0xFF & r) << 16)
                | ((0xFF & g) << 8) | (0xFF & b);
        return height * (1f + ((float) argb / (float) MAX_COLOR));
    }

    private static float[] generateNormals(float[] vertices, int width, int height) {
        System.out.println("HeightMap(" + glfwGetTime() + "): генерация нормалей");
        Vector3f leftTop = new Vector3f();
        Vector3f rightTop = new Vector3f();
        Vector3f rightBottom = new Vector3f();
        Vector3f leftBottom = new Vector3f();

        Vector3f v0 = new Vector3f();
        Vector3f v1 = new Vector3f();

        List<Float> normals = new ArrayList<>();
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int part = (i * width + j) * 18;
                leftBottom.set(vertices[part], vertices[part + 1], vertices[part + 2]);
                leftTop.set(vertices[part + 3], vertices[part + 4], vertices[part + 5]);
                rightTop.set(vertices[part + 6], vertices[part + 7], vertices[part + 8]);
                rightBottom.set(vertices[part + 12], vertices[part + 13], vertices[part + 14]);
                v0.set(leftBottom.x - leftTop.x, leftBottom.y - leftTop.y, leftBottom.z - leftTop.z);
                v1.set(rightTop.x - leftTop.x, rightTop.y - leftTop.y, rightTop.z - leftTop.z);
                addNormal(normals, v0, v1);
                v0.set(leftBottom.x - rightBottom.x, leftBottom.y - rightBottom.y, leftBottom.z - rightBottom.z);
                v1.set(rightTop.x - rightBottom.x, rightTop.y - rightBottom.y, rightTop.z - rightBottom.z);
                addNormal(normals, v0, v1);
            }
        }
        System.out.println("HeightMap(" + glfwGetTime() + "): генерация завершена. Количество координат нормалей: " + normals.size());
        smoothNormals(normals, width, height);
        return toFloatArray(normals);
    }

    private static void addNormal(List<Float> normals, Vector3f v0, Vector3f v1) {
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
        // Нужно сделать сглаживание сразу при вычислении нормалей
        System.out.println("HeightMap(" + glfwGetTime() + "): сглаживание нормалей");
        for (int i = 0; i < height + 1; i++) {
            for (int j = 0; j < width + 1; j++) {
                Vector3f vector = new Vector3f();

                int part = (i * width + j) * 18 + 3;
                int top = ((i - 1) * width + j) * 18;
                int left = (i * width + j - 1) * 18 + 6;
                int leftTop = ((i - 1) * width + j - 1) * 18 + 12;

                int count = 0;
                if (i < height) {
                    if (j < width) {
                        vector.x += normals.get(part);
                        vector.y += normals.get(part + 1);
                        vector.z += normals.get(part + 2);
                        count++;
                    }
                    if (j > 0) {
                        vector.x += normals.get(left);
                        vector.y += normals.get(left + 1);
                        vector.z += normals.get(left + 2);
                        count++;
                    }
                }

                if (i > 0) {
                    if (j < width) {
                        vector.x += normals.get(top);
                        vector.y += normals.get(top + 1);
                        vector.z += normals.get(top + 2);
                        count++;
                    }
                }

                vector.x /= count;
                vector.y /= count;
                vector.z /= count;

                if (i < height) {
                    if (j < width) {
                        normals.set(part, vector.x);
                        normals.set(part + 1, vector.y);
                        normals.set(part + 2, vector.z);
                    }
                    if (j > 0) {
                        normals.set(left, vector.x);
                        normals.set(left + 1, vector.y);
                        normals.set(left + 2, vector.z);
                        normals.set(left + 3, vector.x);
                        normals.set(left + 4, vector.y);
                        normals.set(left + 5, vector.z);
                    }
                }

                if (i > 0) {
                    if (j < width) {
                        normals.set(top, vector.x);
                        normals.set(top + 1, vector.y);
                        normals.set(top + 2, vector.z);
                        normals.set(top + 15, vector.x);
                        normals.set(top + 16, vector.y);
                        normals.set(top + 17, vector.z);
                    }
                    if (j > 0) {
                        normals.set(leftTop, vector.x);
                        normals.set(leftTop + 1, vector.y);
                        normals.set(leftTop + 2, vector.z);
                    }
                }
            }
        }
        System.out.println("HeightMap(" + glfwGetTime() + "): сглаживание нормалей завершено");
    }

    private static float[] toFloatArray(List<Float> list) {
        float[] array = new float[list.size()];
        for (int i = 0; i < array.length; i++) {
            array[i] = list.get(i);
        }
        return array;
    }
}
