package ru.reactiveturtle.game.world;

import javafx.util.Pair;
import org.joml.Vector3f;
import org.joml.Vector4f;
import ru.reactiveturtle.engine.base.GameContext;
import ru.reactiveturtle.engine.material.Material;
import ru.reactiveturtle.engine.model.Releasable;
import ru.reactiveturtle.engine.model.HeightMap;
import ru.reactiveturtle.engine.model.Model;
import ru.reactiveturtle.engine.model.base.Parallelepiped;
import ru.reactiveturtle.engine.model.loader.ObjLoader;
import ru.reactiveturtle.engine.shader.TextureShader;
import ru.reactiveturtle.engine.shadow.Shadow;
import ru.reactiveturtle.game.build.Wall;
import ru.reactiveturtle.game.campfire.Flame;
import ru.reactiveturtle.game.player.GameObject;
import ru.reactiveturtle.game.tool.Axe;
import ru.reactiveturtle.game.tool.Hammer;
import ru.reactiveturtle.physics.BoxBody;
import ru.reactiveturtle.physics.RigidBody;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Arrangement implements Shadow, Releasable {
    private List<GameObject> gameObjects = new ArrayList<>();
    private HeightMap heightMap;
    private Forest forest;
    private Parallelepiped intersectionTest;

    public Flame campfireFlame;

    public Arrangement(TextureShader shader, HeightMap heightMap) {
        this.heightMap = heightMap;
        heightMap.setShader(shader);
        forest = new Forest(this, heightMap, shader);
        try {
            Model model = ObjLoader.load("/object/tools/axe");
            model.setShader(shader);
            Axe axe = new Axe(
                    generateId(),
                    "Axe",
                    new Model[]{model},
                    new Vector3f(10, 0.05f + heightMap.getY(10, -5), -5),
                    new Vector3f(0, 0, 90));
            axe.setSelectBox(0.05f, 0.685f, 0.4f);
            axe.setSelectBoxY(0.265f);
            axe.setSelectBoxX(-0.025f);
            axe.setPower(4f);
            gameObjects.add(axe);

            Material material = new Material();
            material.setDiffuse(0.1f, 0.1f, 0.2f);
            intersectionTest = new Parallelepiped(1f, 1f, 1f);
            intersectionTest.setShader(shader);
            intersectionTest.setMaterial(material);
            intersectionTest.setPosition(10f, 0, 10f);

            createHammer(shader);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<BoxBodyModel> models = new ArrayList<>();

    public void putModel(BoxBodyModel boxBodyModel) {
        models.add(boxBodyModel);
    }

    public void createHammer(TextureShader textureShader) throws IOException {
        Model woodWall = ObjLoader.load("object/wall/wallWoodDetailCross");
        woodWall.setShader(textureShader);
        Model model = ObjLoader.load("object/tools/hammer");
        model.setShader(textureShader);
        Wall wall = new Wall(generateId(), "Wood wall", new Model[]{woodWall}, new Vector3f(), new Vector3f());
        wall.setSelectBox(4f, 4f, 0.25f);
        wall.setSelectBoxY(2f);
        wall.setRigidBody(new BoxBody(4f, 4f, 0.25f));
        wall.getRigidBody().addY(2f);
        Hammer hammer = new Hammer(
                generateId(),
                "Hammer",
                new Model[]{model},
                new Vector3f(10, 0.05f + heightMap.getY(10, 5), 5),
                new Vector3f(0, 0, 90),
                wall);
        hammer.setSelectBox(0.05f, 0.685f, 0.4f);
        hammer.setSelectBoxY(0.265f);
        hammer.setSelectBoxX(-0.025f);
        gameObjects.add(hammer);
    }

    @Override
    public void renderShadow() {
        for (GameObject gameObject : gameObjects) {
            gameObject.renderShadow();
        }
    }

    public void render(Vector3f direction, Vector3f position) {
        for (GameObject gameObject : gameObjects) {
            if (gameObject.getRigidBody() != null)
                gameObject.setDefaultPosition(gameObject.getRigidBody().getPosition());
            gameObject.render(direction, position);
        }
        for (BoxBodyModel model : models) {
            model.render();
        }
        campfireFlame.render(GameContext.camera.getRotationY());
    }

    public Pair<GameObject, Vector3f> getNearestIntersection(Vector3f position, Vector3f direction, Model model) {
        Pair<GameObject, Vector3f> result = null;
        for (GameObject gameObject : gameObjects) {
            Vector3f intersectionPoint = gameObject.calcIntersectionPoint(position, direction, model);
            if (intersectionPoint != null && (result == null ||
                    new Vector3f(position).sub(intersectionPoint).length() < new Vector3f(position).sub(result.getValue()).length())) {
                result = new Pair<>(gameObject, intersectionPoint);
            }
        }
        return result;
    }

    public void putObject(GameObject gameObject) {
        gameObjects.add(gameObject);
    }

    public GameObject getObject(int id) {
        for (GameObject gameObject : gameObjects) {
            if (gameObject.getId() == id) {
                return gameObject;
            }
        }
        return null;
    }

    public void removeObject(int id) {
        for (int i = 0; i < gameObjects.size(); i++) {
            if (gameObjects.get(i).getId() == id) {
                gameObjects.remove(i);
                i--;
            }
        }
    }

    public int generateId() {
        int id = 0;
        boolean isUnique = false;
        while (!isUnique) {
            isUnique = true;
            for (GameObject aGameObject : gameObjects) {
                if (aGameObject.getId() == id) {
                    id++;
                    isUnique = false;
                    break;
                }
            }
        }
        return id;
    }

    public GameObject copyObject(GameObject src) {
        RigidBody rigidBody = src.getRigidBody() != null ?
                src.getRigidBody().copy() : null;
        GameObject gameObject = src.copy(generateId());
        gameObject.setRigidBody(rigidBody);
        return gameObject;
    }

    @Override
    public void release() {
        for (GameObject gameObject : gameObjects) {
            gameObject.release();
        }
        forest.release();
        heightMap.release();
    }

    private float[] biases = new float[]{
            2f, -2f,
            4f, 0f,
            2f, 2f,
            -2f, -2f,
            -4f, 0f,
            -2f, 2f,
    };

    private float[] rotations = new float[]{
            -90f, 0f, 90f, -90f, 0f, 90f
    };

    public Vector4f getNearestWallLocation(float cameraRotation, Vector3f cameraPosition, Vector3f direction) {
        Vector3f result = null;
        Vector3f position = null;
        float rotation = Math.round(-cameraRotation / 45f) * 45;
        for (GameObject gameObject : gameObjects) {
            if (!(gameObject instanceof Wall)) {
                continue;
            }
            Vector3f gameObjectDefaultPosition = new Vector3f(gameObject.getDefaultPosition());
            Vector3f gameObjectDefaultRotation = new Vector3f(gameObject.getDefaultRotation());
            for (int i = 0; i < 6; i++) {
                Vector3f bias = new Vector3f(biases[i * 2], 0, biases[i * 2 + 1]);
                bias.rotateY((float) Math.toRadians(gameObjectDefaultRotation.y));
                gameObject.setDefaultPosition(new Vector3f(gameObjectDefaultPosition).add(bias));
                gameObject.setDefaultRotation(new Vector3f(0, gameObjectDefaultRotation.y + rotations[i], 0));
                Vector3f intersectionPoint = gameObject.calcIntersectionPoint(cameraPosition, direction, intersectionTest);
                if (intersectionPoint != null && (result == null ||
                        new Vector3f(cameraPosition).sub(intersectionPoint).length() < new Vector3f(cameraPosition).sub(result).length())) {
                    result = intersectionPoint;
                    position = new Vector3f(gameObject.getDefaultPosition());
                    rotation = gameObject.getDefaultRotation().y;
                }
            }
            gameObject.setDefaultPosition(gameObjectDefaultPosition);
            gameObject.setDefaultRotation(gameObjectDefaultRotation);
        }
        return result != null ? new Vector4f(position.x, position.y, position.z, rotation) : null;
    }

    public CollisionInfo isCollide(Vector3f playerPosition, Vector3f translation) {
        CollisionInfo collisionInfo = new CollisionInfo();
        float[] playerBoxNormals = new float[]{
                0f, 0f, -1f,
                0f, 0f, 1f,
                -1f, 0f, 0f,
                1f, 0f, 0f,
                0f, -1f, 0f,
                0f, 1f, 0f
        };
        float[] playerBoxNormalPoints = new float[]{
                0f, 0.925f, 0.5f,
                0f, 0.925f, -0.5f,
                0.5f, 0.925f, 0f,
                -0.5f, 0.925f, 0f,
                0f, 1.85f, 0f,
                0f, 0f, 0f
        };
        for (int i = 0; i < 6; i++) {
            int index = i * 3;
            Vector3f normal = new Vector3f(playerBoxNormals[index],
                    playerBoxNormals[index + 1],
                    playerBoxNormals[index + 2]);
            Vector3f normalPoint = new Vector3f(playerBoxNormalPoints[index],
                    playerBoxNormalPoints[index + 1],
                    playerBoxNormalPoints[index + 2]);

            normalPoint.add(playerPosition).add(translation);

            playerBoxNormals[index] = normal.x;
            playerBoxNormals[index + 1] = normal.y;
            playerBoxNormals[index + 2] = normal.z;

            playerBoxNormalPoints[index] = normalPoint.x;
            playerBoxNormalPoints[index + 1] = normalPoint.y;
            playerBoxNormalPoints[index + 2] = normalPoint.z;
        }
        for (int i = 0; i < gameObjects.size() && !collisionInfo.isCollide; i++) {
            GameObject gameObject = gameObjects.get(i);
            // Можно ускорить сделав примитивы
            boolean isObjectCollide = true;
            for (int k = 0; k < 6; k++) {
                int index = k * 3;
                isObjectCollide &= gameObject.isSelectBoxInFrustum(
                        new Vector3f(playerBoxNormals[index], playerBoxNormals[index + 1], playerBoxNormals[index + 2]),
                        new Vector3f(playerBoxNormalPoints[index], playerBoxNormalPoints[index + 1], playerBoxNormalPoints[index + 2]));
            }
            System.out.println(isObjectCollide);
            collisionInfo.isCollide = isObjectCollide;
            if (isObjectCollide) {
                Vector3f point = gameObject.getIntersectionPoint();
                if (point != null) {
                    Vector3f vector3f = new Vector3f(translation);
                    vector3f.y = 0;
                    vector3f.normalize(1);
                    collisionInfo.bias = vector3f.sub(new Vector3f(point).sub(playerPosition).normalize(1)).rotateY((float) (Math.PI / 2));
                }
            }
        }
        return collisionInfo;
    }

    public GameObject[] getObjects() {
        GameObject[] gameObjects = new GameObject[this.gameObjects.size()];
        return this.gameObjects.toArray(gameObjects);
    }
}
