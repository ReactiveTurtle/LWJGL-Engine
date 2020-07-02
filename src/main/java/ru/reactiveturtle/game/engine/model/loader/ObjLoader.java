package ru.reactiveturtle.game.engine.model.loader;

import org.joml.Vector2f;
import org.joml.Vector3f;
import ru.reactiveturtle.game.engine.base.GameContext;
import ru.reactiveturtle.game.engine.material.Material;
import ru.reactiveturtle.game.engine.model.Model;
import ru.reactiveturtle.game.engine.material.Texture;
import ru.reactiveturtle.game.engine.model.mesh.Mesh;

import java.io.*;
import java.util.*;

public class ObjLoader {
    public static Model load(String path, float textureX, float textureY) throws IOException {
        ArrayList<Mesh> meshes = new ArrayList<>();
        HashMap<String, Material> materials = new HashMap<>();
        BufferedReader objReader = new BufferedReader(new FileReader(new File(GameContext.RESOURCE_PATH + path + ".obj")));

        String key = null;
        ArrayList<Float> vertices = new ArrayList<>();
        ArrayList<Float> texCoords = new ArrayList<>();
        ArrayList<Float> normals = new ArrayList<>();
        ArrayList<Integer> indices = null;
        String materialKey = null;
        ArrayList<Integer> newTexCoordIndices = new ArrayList<>();
        ArrayList<Integer> newNormalIndices = new ArrayList<>();

        String line;
        while ((line = objReader.readLine()) != null) {
            line = line.trim();
            if (!line.startsWith("#")) {
                String[] param = line.split(" ", 2);
                switch (param[0]) {
                    case "mtllib":
                        loadMaterials(materials, new File(path).getParent(), param[1].trim());
                        break;
                    case "o":
                        key = param[1].trim();
                        break;
                    case "v":
                        putVector3f(param[1], vertices);
                        break;
                    case "vt":
                        String[] texCoord = param[1].split(" ");
                        Vector2f texCoordVec = toVector2f(texCoord);
                        texCoords.add(texCoordVec.x * textureX);
                        texCoords.add(texCoordVec.y * textureY);
                        break;
                    case "vn":
                        putVector3f(param[1], normals);
                        break;
                    case "usemtl":
                        if (materialKey != null) {
                            Object[] vert = cut(vertices, indices, normals, newNormalIndices, texCoords, newTexCoordIndices);
                            Mesh mesh = new Mesh(materialKey, (float[]) vert[0], (int[]) vert[1]);
                            mesh.setMaterial(materials.get(materialKey));
                            mesh.setTextureCoordinates(newTexCoordIndices.size() > 0 ?
                                    (float[]) vert[3] : new float[]{0, 0});
                            mesh.setNormals(newNormalIndices.size() > 0 ?
                                    (float[]) vert[2] : new float[]{0, 0, 0});
                            meshes.add(mesh);
                        }
                        indices = new ArrayList<>();
                        newTexCoordIndices = new ArrayList<>();
                        newNormalIndices = new ArrayList<>();
                        materialKey = param[1].trim();
                        break;
                    case "f":
                        Integer[][] integers = parseFace(param[1]);

                        for (int i = 0; i < integers.length; i++) {
                            switch (i) {
                                case 0:
                                    Collections.addAll(indices, integers[i]);
                                    break;
                                case 1:
                                    if (integers[i] != null) {
                                        newTexCoordIndices.addAll(Arrays.asList(integers[i]));
                                    }
                                    break;
                                case 2:
                                    if (integers[i] != null) {
                                        newNormalIndices.addAll(Arrays.asList(integers[i]));
                                    }
                                    break;
                            }
                        }
                        break;
                }
            }
        }
        if (materialKey != null) {
            Object[] vert = cut(vertices, indices, normals, newNormalIndices, texCoords, newTexCoordIndices);
            Mesh mesh = new Mesh(materialKey, (float[]) vert[0], (int[]) vert[1]);
            mesh.setMaterial(materials.get(materialKey));
            mesh.setTextureCoordinates(newTexCoordIndices.size() > 0 ?
                    (float[]) vert[3] : new float[]{0, 0});
            mesh.setNormals(newNormalIndices.size() > 0 ?
                    (float[]) vert[2] : new float[]{0, 0, 0});
            meshes.add(mesh);
        }
        System.out.println("Объект загружен");
        return new Model(meshes.toArray(new Mesh[meshes.size()]));
    }

    public static Model load(String path) throws IOException {
        ArrayList<Mesh> meshes = new ArrayList<>();
        HashMap<String, Material> materials = new HashMap<>();
        BufferedReader objReader = new BufferedReader(new FileReader(new File(GameContext.RESOURCE_PATH + path + ".obj")));

        String key = null;
        ArrayList<Float> vertices = new ArrayList<>();
        ArrayList<Float> texCoords = new ArrayList<>();
        ArrayList<Float> normals = new ArrayList<>();
        ArrayList<Integer> indices = null;
        String materialKey = null;
        ArrayList<Integer> newTexCoordIndices = new ArrayList<>();
        ArrayList<Integer> newNormalIndices = new ArrayList<>();

        String line;
        while ((line = objReader.readLine()) != null) {
            line = line.trim();
            if (!line.startsWith("#")) {
                String[] param = line.split(" ", 2);
                switch (param[0]) {
                    case "mtllib":
                        loadMaterials(materials, new File(path).getParent(), param[1].trim());
                        break;
                    case "o":
                        key = param[1].trim();
                        break;
                    case "v":
                        putVector3f(param[1], vertices);
                        break;
                    case "vt":
                        String[] texCoord = param[1].split(" ");
                        Vector2f texCoordVec = toVector2f(texCoord);
                        texCoords.add(texCoordVec.x);
                        texCoords.add(texCoordVec.y);
                        break;
                    case "vn":
                        putVector3f(param[1], normals);
                        break;
                    case "usemtl":
                        if (materialKey != null) {
                            Object[] vert = cut(vertices, indices, normals, newNormalIndices, texCoords, newTexCoordIndices);
                            Mesh mesh = new Mesh(materialKey, (float[]) vert[0], (int[]) vert[1]);
                            mesh.setMaterial(materials.get(materialKey));
                            mesh.setTextureCoordinates(newTexCoordIndices.size() > 0 ?
                                    (float[]) vert[3] : new float[]{0, 0});
                            mesh.setNormals(newNormalIndices.size() > 0 ?
                                    (float[]) vert[2] : new float[]{0, 0, 0});
                            meshes.add(mesh);
                        }
                        indices = new ArrayList<>();
                        newTexCoordIndices = new ArrayList<>();
                        newNormalIndices = new ArrayList<>();
                        materialKey = param[1].trim();
                        break;
                    case "f":
                        Integer[][] integers = parseFace(param[1]);

                        for (int i = 0; i < integers.length; i++) {
                            switch (i) {
                                case 0:
                                    Collections.addAll(indices, integers[i]);
                                    break;
                                case 1:
                                    if (integers[i] != null) {
                                        newTexCoordIndices.addAll(Arrays.asList(integers[i]));
                                    }
                                    break;
                                case 2:
                                    if (integers[i] != null) {
                                        newNormalIndices.addAll(Arrays.asList(integers[i]));
                                    }
                                    break;
                            }
                        }
                        break;
                }
            }
        }
        if (materialKey != null) {
            Object[] vert = cut(vertices, indices, normals, newNormalIndices, texCoords, newTexCoordIndices);
            Mesh mesh = new Mesh(materialKey, (float[]) vert[0], (int[]) vert[1]);
            mesh.setMaterial(materials.get(materialKey));
            mesh.setTextureCoordinates(newTexCoordIndices.size() > 0 ?
                    (float[]) vert[3] : new float[]{0, 0});
            mesh.setNormals(newNormalIndices.size() > 0 ?
                    (float[]) vert[2] : new float[]{0, 0, 0});
            meshes.add(mesh);
        }
        System.out.println("Объект загружен");
        return new Model(meshes.toArray(new Mesh[meshes.size()]));
    }

    private static void loadMaterials(HashMap<String, Material> materials, String dir, String file) throws IOException {
        BufferedReader mtlReader = new BufferedReader(new FileReader(new File(GameContext.RESOURCE_PATH + dir + "/" + file)));
        String key = null;
        Material material = null;
        String line;
        while ((line = mtlReader.readLine()) != null) {
            line = line.trim();
            if (line.length() > 0 && !line.startsWith("#")) {
                String[] param = line.split(" ", 2);
                param[0] = param[0].toLowerCase();
                switch (param[0]) {
                    case "newmtl":
                        if (material != null) {
                            materials.put(key, material);
                        }
                        material = new Material();
                        key = param[1].trim();
                        break;
                    case "ns":
                        material.setReflectance(Float.parseFloat(param[1]));
                        break;
                    case "ka":
                        String[] ambient = param[1].split(" ", 3);
                        material.setAmbient(toVector3f(ambient));
                        break;
                    case "kd":
                        String[] diffuse = param[1].split(" ", 3);
                        material.setDiffuse(toVector3f(diffuse));
                        break;
                    case "ks":
                        String[] specular = param[1].split(" ", 3);
                        material.setSpecular(toVector3f(specular));
                        break;
                    case "ke":
                        String[] emission = param[1].split(" ", 3);
                        material.setEmission(toVector3f(emission));
                        break;
                    case "map_kd":
                        material.setTexture(new Texture(dir + "/" + param[1]));
                        break;
                    case "map_bump":
                        material.setTexture(new Texture(dir + "/" + param[1]));
                        break;
                }
            }
        }
        if (material != null) {
            materials.put(key, material);
        }
    }

    private static Integer[][] parseFace(String face) {
        // вершина/координата текстуры/нормаль
        String[] vertices = face.split(" ");
        Integer[][] faceInfo = new Integer[3][vertices.length];
        for (int i = 0; i < vertices.length; i++) {
            String vertexString = vertices[i].trim();
            String[] vertexInfo = vertexString.split("/");
            if (vertexInfo.length == 1) {
                faceInfo[0][i] = Integer.valueOf(vertexInfo[0]) - 1;
                faceInfo[1] = null;
                faceInfo[2] = null;
            } else if (vertexInfo.length == 2) {
                faceInfo[0][i] = Integer.valueOf(vertexInfo[0]) - 1;
                faceInfo[1][i] = Integer.valueOf(vertexInfo[1]) - 1;
                faceInfo[2] = null;
            } else if (vertexInfo.length == 3) {
                faceInfo[0][i] = Integer.valueOf(vertexInfo[0]) - 1;
                if (vertexInfo[1].length() == 0) {
                    faceInfo[1] = null;
                } else {
                    faceInfo[1][i] = Integer.valueOf(vertexInfo[1]) - 1;
                }
                faceInfo[2][i] = Integer.valueOf(vertexInfo[2]) - 1;
            }
        }
        return faceInfo;
    }

    private static void putVector3f(String stringVector, ArrayList<Float> list) {
        String[] vertice = stringVector.trim().split(" ");
        Vector3f verticeVec = toVector3f(vertice);
        list.add(verticeVec.x);
        list.add(verticeVec.y);
        list.add(verticeVec.z);
    }

    private static Vector2f toVector2f(String[] strings) {
        if (strings.length == 2) {
            return new Vector2f(
                    Float.parseFloat(strings[0].trim()),
                    Float.parseFloat(strings[1].trim()));
        } else {
            throw new IllegalArgumentException();
        }
    }

    private static Vector3f toVector3f(String[] strings) {
        if (strings.length == 3) {
            if (strings[1].length() == 0) {
                strings[1] = "0";
            }
            return new Vector3f(
                    Float.parseFloat(strings[0].trim()),
                    Float.parseFloat(strings[1].trim()),
                    Float.parseFloat(strings[2].trim()));
        } else {
            throw new IllegalArgumentException();
        }
    }

    private static int[] toIntArray(ArrayList<Integer> integers) {
        int[] array = new int[integers.size()];
        for (int i = 0; i < integers.size(); i++) {
            array[i] = integers.get(i);
        }
        return array;
    }

    private static float[] toFloatArray(List<Float> floats) {
        float[] array = new float[floats.size()];
        for (int i = 0; i < floats.size(); i++) {
            array[i] = floats.get(i);
        }
        return array;
    }


    private static Object[] cut(ArrayList<Float> vertices, ArrayList<Integer> indices,
                                ArrayList<Float> normals, ArrayList<Integer> normalIndices,
                                ArrayList<Float> texCoords, ArrayList<Integer> texCoordIndices) {
        int min = Integer.MAX_VALUE;
        for (Integer index : indices) {
            if (min > index) {
                min = index;
            }
        }
        ArrayList<Float> newVertices = new ArrayList<>();
        ArrayList<Float> newNormals = new ArrayList<>();
        ArrayList<Float> newTexCoords = new ArrayList<>();
        ArrayList<Integer> newIndices = new ArrayList<>();
        for (int j = 0, indicesSize = indices.size(); j < indicesSize; j++) {
            Integer i = indices.get(j);
            newVertices.add(vertices.get(i * 3));
            newVertices.add(vertices.get(i * 3 + 1));
            newVertices.add(vertices.get(i * 3 + 2));
            if (normalIndices.size() > 0) {
                newNormals.add(normals.get(normalIndices.get(j) * 3));
                newNormals.add(normals.get(normalIndices.get(j) * 3 + 1));
                newNormals.add(normals.get(normalIndices.get(j) * 3 + 2));
            }
            if (texCoordIndices.size() > 0) {
                newTexCoords.add(texCoords.get(texCoordIndices.get(j) * 2));
                newTexCoords.add(texCoords.get(texCoordIndices.get(j) * 2 + 1));
            }
        }
        for (int i = 0; i < newVertices.size(); i++) {
            newIndices.add(i);
        }

        return new Object[]{
                toFloatArray(newVertices),
                toIntArray(newIndices),
                toFloatArray(newNormals),
                toFloatArray(newTexCoords),
        };
    }
}
