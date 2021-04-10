package ru.reactiveturtle.engine.model.base;

import org.joml.Vector3f;
import ru.reactiveturtle.engine.model.Model;
import ru.reactiveturtle.engine.model.mesh.Mesh;

public class Sphere extends Model {
    public Sphere(float radius, int quality, boolean isSmooth) {
        float[][] params = getParams(radius, quality, isSmooth);
        Mesh mesh = new Mesh("sphere", params[0], getIndices(quality));
        mesh.setNormals(params[1]);
        mesh.setTextureCoordinates(getTextureCoords(quality));
        meshes.put("sphere", mesh);
    }

    public static float[][] getParams(float radius, int quality, boolean isSmooth) {
        int vertexCount = 0;
        for (int i = 0; i < quality; i++) {
            vertexCount += i * 2 + 1;
        }
        float[][] params = new float[2][vertexCount * 9 * 4 * 2];
        float angleXStep = (float) (Math.PI / (quality * 2));
        float prevAngleYStep = 0;
        Vector3f startVector = new Vector3f(0, radius, 0);
        Vector3f topVector = new Vector3f(startVector);
        int levelPosition = 0;
        for (int i = 0; i < quality; i++) {
            startVector.rotateX(angleXStep);
            float angleYStep = (float) (Math.PI / ((i + 1) * 2));
            for (int j = 0; j < 4; j++) {
                int levelSubPosition = j * (i * 2 + 1) * 9;
                for (int k = 0; k < (i * 2 + 1); k++) {
                    int index = levelPosition + levelSubPosition + k * 9;
                    if (k % 2 == 0) {
                        params[0][index] = topVector.x;
                        params[0][index + 1] = topVector.y;
                        params[0][index + 2] = topVector.z;
                        params[0][index + 3] = startVector.x;
                        params[0][index + 4] = startVector.y;
                        params[0][index + 5] = startVector.z;
                        startVector.rotateY(angleYStep);
                    } else {
                        params[0][index] = topVector.x;
                        params[0][index + 1] = topVector.y;
                        params[0][index + 2] = topVector.z;
                        topVector.rotateY(prevAngleYStep);
                        params[0][index + 3] = topVector.x;
                        params[0][index + 4] = topVector.y;
                        params[0][index + 5] = topVector.z;
                    }
                    params[0][index + 6] = startVector.x;
                    params[0][index + 7] = startVector.y;
                    params[0][index + 8] = startVector.z;
                    System.arraycopy(params[0], index, params[1], index, 9);
                    Vector3f normal = new Vector3f(
                            (params[0][index] + params[0][index + 3] + params[0][index + 6]) / 3f,
                            (params[0][index + 1] + params[0][index + 4] + params[0][index + 7]) / 3f,
                            (params[0][index + 2] + params[0][index + 5] + params[0][index + 8]) / 3f);
                    for (int l = 0; l < 3; l++) {
                        if (isSmooth) {
                            params[1][index + l * 3] = params[0][index + l * 3];
                            params[1][index + l * 3 + 1] = params[0][index + l * 3 + 1];
                            params[1][index + l * 3 + 2] = params[0][index + l * 3 + 2];
                        } else {
                            params[1][index + l * 3] = normal.x;
                            params[1][index + l * 3 + 1] = normal.y;
                            params[1][index + l * 3 + 2] = normal.z;
                        }
                    }
                }
            }
            topVector.set(startVector);
            prevAngleYStep = angleYStep;
            levelPosition += (i * 2 + 1) * 4 * 9;
        }
        for (int i = quality - 1; i > -1; i--) {
            startVector.rotateX(angleXStep);
            float angleYStep = (float) (Math.PI / (i * 2));
            for (int j = 0; j < 4; j++) {
                int levelSubPosition = j * (i * 2 + 1) * 9;
                for (int k = 0; k < (i * 2 + 1); k++) {
                    int index = levelPosition + levelSubPosition + k * 9;
                    if (k % 2 == 0) {
                        params[0][index] = startVector.x;
                        params[0][index + 1] = startVector.y;
                        params[0][index + 2] = startVector.z;
                        params[0][index + 3] = topVector.x;
                        params[0][index + 4] = topVector.y;
                        params[0][index + 5] = topVector.z;
                        topVector.rotateY(prevAngleYStep);
                    } else {
                        params[0][index] = startVector.x;
                        params[0][index + 1] = startVector.y;
                        params[0][index + 2] = startVector.z;
                        startVector.rotateY(angleYStep);
                        params[0][index + 3] = startVector.x;
                        params[0][index + 4] = startVector.y;
                        params[0][index + 5] = startVector.z;
                    }
                    params[0][index + 6] = topVector.x;
                    params[0][index + 7] = topVector.y;
                    params[0][index + 8] = topVector.z;
                    System.arraycopy(params[0], index, params[1], index, 9);
                    Vector3f normal = new Vector3f(
                            (params[0][index] + params[0][index + 3] + params[0][index + 6]) / 3f,
                            (params[0][index + 1] + params[0][index + 4] + params[0][index + 7]) / 3f,
                            (params[0][index + 2] + params[0][index + 5] + params[0][index + 8]) / 3f);
                    for (int l = 0; l < 3; l++) {
                        if (isSmooth) {
                            params[1][index + l * 3] = params[0][index + l * 3];
                            params[1][index + l * 3 + 1] = params[0][index + l * 3 + 1];
                            params[1][index + l * 3 + 2] = params[0][index + l * 3 + 2];
                        } else {
                            params[1][index + l * 3] = normal.x;
                            params[1][index + l * 3 + 1] = normal.y;
                            params[1][index + l * 3 + 2] = normal.z;
                        }
                    }
                }
            }
            topVector.set(startVector);
            prevAngleYStep = angleYStep;
            levelPosition += (i * 2 + 1) * 4 * 9;
        }
        return params;
    }

    private static float[] getTextureCoords(int quality) {
        int vertexCount = 0;
        for (int i = 0; i < quality; i++) {
            vertexCount += i * 2 + 1;
        }
        float[] coords = new float[]{0, 0, 0, 1, 1, 0, 1, 0, 0, 1, 1, 1};
        float[] texCoords = new float[vertexCount * 4 * 2 * 2];
        for (int i = 0; i < vertexCount; i++) {
            System.arraycopy(coords, 0, texCoords, i * 8, 8);
        }
        return texCoords;
    }

    private static int[] getIndices(int quality) {
        int vertexCount = 0;
        for (int i = 0; i < quality; i++) {
            vertexCount += i * 2 + 1;
        }
        int[] indices = new int[vertexCount * 3 * 4 * 2];
        for (int i = 0; i < indices.length; i++) {
            indices[i] = i;
        }
        return indices;
    }
}
