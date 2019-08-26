package engine.util;

import engine.model.Model;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector3i;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ObjLoader {
    List<String> lines = new ArrayList<>();

    public static Model loadModel(String file) {
        List<Vector3f> verticesList = new ArrayList<>();
        List<Vector2f> coordsList = new ArrayList<>();
        List<Vector3f> normalsList = new ArrayList<>();
        List<Vector3f> faces = new ArrayList<>();

        List<Float> vertices = new ArrayList<>();
        List<Float> textureCoordinates = new ArrayList<>();
        List<Float> normals = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();

        try {
            BufferedReader reader = new BufferedReader(new FileReader("src/resources/objects/" + file));
            String line;
            StringBuilder buffer = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                String type = "unknown";
                for (int i = 0; i < line.length(); i++) {
                    if (line.charAt(i) == ' ') {
                        type = buffer.toString();
                        buffer.setLength(0);
                        line = line.substring(i + 1).trim() + " ";
                        break;
                    } else {
                        buffer.append(line.charAt(i));
                    }
                }

                switch (type) {
                    case "v":
                        verticesList.add(getVector3fFromLine(line));
                        break;
                    case "vt":
                        coordsList.add(getVector2fFromLine(line));
                        break;
                    case "vn":
                        normalsList.add(getVector3fFromLine(line));
                        break;
                    case "f":
                        for (int i = 0; i < line.length(); i++) {
                            if (line.charAt(i) == ' ') {
                                Vector3i vector3i = getVector3iFromLine(buffer.toString().replaceAll("/", " ") + " ");

                                Vector3f vertex = verticesList.get(vector3i.x - 1);
                                Vector3f normal = normalsList.get(vector3i.z - 1);

                                vertices.add(vertex.x);
                                vertices.add(vertex.y);
                                vertices.add(vertex.z);

                                normals.add(normal.x);
                                normals.add(normal.y);
                                normals.add(normal.z);

                                if (vector3i.y != 0) {
                                    Vector2f coord = coordsList.get(vector3i.y - 1);
                                    textureCoordinates.add(coord.x);
                                    textureCoordinates.add(1 - coord.y);
                                }

                                indices.add(vertices.size() / 3 - 1);

                                buffer.setLength(0);
                            } else {
                                buffer.append(line.charAt(i));
                            }
                        }
                        break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        float[] v = toFloatArray(vertices);
        float[] vt = toFloatArray(textureCoordinates);
        float[] vn = toFloatArray(normals);
        int[] ind = toIntArray(indices);

        Model model = new Model(v, ind);
        if (vt.length > 0) {
            model.setTextureCoordinates(vt);
        }
        model.setNormals(vn);
        return model;
    }

    private static Vector3f getVector3fFromLine(String string) {
        StringBuilder buffer = new StringBuilder();
        float[] floats = new float[3];
        int iteration = 0;
        for (int i = 0; i < string.length(); i++) {
            if (string.charAt(i) == ' ') {
                floats[iteration] = Float.parseFloat(buffer.toString());
                buffer.setLength(0);
                iteration++;
            } else {
                buffer.append(string.charAt(i));
            }
        }
        return new Vector3f(floats[0], floats[1], floats[2]);
    }

    private static Vector3i getVector3iFromLine(String string) {
        StringBuilder buffer = new StringBuilder();
        int[] ints = new int[3];
        int iteration = 0;
        for (int i = 0; i < string.length(); i++) {
            if (string.charAt(i) == ' ') {
                if (buffer.toString().equals("")) {
                    ints[iteration] = 0;
                } else {
                    ints[iteration] = Integer.parseInt(buffer.toString());
                }
                buffer.setLength(0);
                iteration++;
            } else {
                buffer.append(string.charAt(i));
            }
        }
        return new Vector3i(ints[0], ints[1], ints[2]);
    }

    private static Vector2f getVector2fFromLine(String string) {
        StringBuilder buffer = new StringBuilder();
        float[] floats = new float[2];
        int iteration = 0;
        for (int i = 0; i < string.length(); i++) {
            if (string.charAt(i) == ' ') {
                floats[iteration] = Float.parseFloat(buffer.toString());
                buffer.setLength(0);
                iteration++;
            } else {
                buffer.append(string.charAt(i));
            }
        }
        return new Vector2f(floats[0], floats[1]);
    }

    private static float[] toFloatArray(List<Float> list) {
        float[] floats = new float[list.size()];
        for (int i = 0; i < floats.length; i++) {
            floats[i] = list.get(i);
        }
        return floats;
    }

    private static int[] toIntArray(List<Integer> list) {
        int[] integers = new int[list.size()];
        for (int i = 0; i < integers.length; i++) {
            integers[i] = list.get(i);
        }
        return integers;
    }
}
