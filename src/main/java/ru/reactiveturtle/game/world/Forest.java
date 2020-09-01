package ru.reactiveturtle.game.world;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;
import ru.reactiveturtle.engine.model.HeightMap;
import ru.reactiveturtle.engine.model.Model;
import ru.reactiveturtle.engine.model.Releasable;
import ru.reactiveturtle.engine.model.loader.ObjLoader;
import ru.reactiveturtle.engine.shader.TextureShader;
import ru.reactiveturtle.game.Helper;
import ru.reactiveturtle.game.food.Banana;
import ru.reactiveturtle.game.food.BananaStack;
import ru.reactiveturtle.game.tree.Tree;
import ru.reactiveturtle.game.tree.Wood;
import ru.reactiveturtle.game.tree.WoodStack;
import ru.reactiveturtle.physics.BoxBody;

import java.io.IOException;
import java.util.*;

public class Forest implements Releasable {
    private final Model[] palms = new Model[4];
    private final Model[] bananas = new Model[5];
    private Model log;
    private final Model[] logs = new Model[3];

    public Forest(Arrangement arrangement, HeightMap heightMap, TextureShader shader) {
        try {
            createForest(arrangement, heightMap, shader);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createForest(Arrangement arrangement, HeightMap heightMap, TextureShader shader) throws IOException {
        createDefaultModels(shader);

        Wood wood = new Wood(
                arrangement.generateId(),
                "Log",
                new Model[]{log},
                new Vector3f(),
                new Vector3f()
        );
        wood.setBurningTime(60 * 90);
        wood.setSelectBox(0.5f, 0.46f, 1.8f);
        wood.setSelectBoxY(0.23f);

        Banana banana = new Banana(arrangement.generateId(),
                new Model[]{bananas[0]},
                new Vector3f(), new Vector3f());
        banana.setCalories(300);
        banana.setSelectBox(0.25f, 0.25f, 0.25f);
        banana.setSelectBoxY(0.125f);

        Vector4f[] randomPositions = Generator.genPalmForest(200, 200, 200);
        for (Vector4f vector4f : randomPositions) {
            WoodStack woodStack = createWoodStack(wood, arrangement.generateId(),
                    vector4f.x, vector4f.z, vector4f.w);
            Tree palm = createPalm(heightMap, woodStack,
                    arrangement.generateId(), (int) vector4f.y,
                    vector4f.x, vector4f.z, vector4f.w + 40);
            arrangement.putObject(palm);

            int stackCount = (int) Math.round(Math.pow(Math.random(), 2) * 3);
            for (int j = 0; j < stackCount; j++) {
                arrangement.putObject(createBananaStack(palm,
                        banana, arrangement.generateId()));
            }
        }
    }

    private WoodStack createWoodStack(Wood wood, int id,
                                      float x, float z, float yRotation) {
        WoodStack woodStack = new WoodStack(
                id, "Log stack",
                Arrays.copyOf(logs, logs.length),
                new Vector3f(x, 0, z),
                new Vector3f(0, yRotation, 0),
                wood);
        woodStack.setCount(3);
        woodStack.setSelectBox(1, 0.9f, 1.8f);
        woodStack.setSelectBoxY(0.45f);
        return woodStack;
    }

    private Tree createPalm(HeightMap heightMap, WoodStack woodStack,
                            int id, int number,
                            float x, float z, float yRotation) {
        Vector3f rotation = heightMap.getRotation(x, z);
        rotation.y = yRotation;
        Tree palm = new Tree(
                id,
                "Palm",
                new Model[]{palms[number]},
                new Vector3f(x, heightMap.getY(x, z), z),
                rotation,
                woodStack);
        palm.setStrength(100);
        palm.setSelectBox(1f, 6.4f, 1f);

        BoxBody palmBody = new BoxBody(1f, 6.4f, 1f);
        palmBody.setPosition(palm.getDefaultPosition());
        palmBody.setRotation(Helper.toRadians(rotation));
        palmBody.setCenter(new Vector3f(0f, -3.2f, 0f));
        palmBody.setId(palm.getId());
        palm.setRigidBody(palmBody);

        palm.setSelectBoxY(3.2f);
        return palm;
    }

    private BananaStack createBananaStack(Tree palm,
                                          Banana bananaCollect, int id) {
        double degrees = Math.random() * Math.PI * 2;
        float biasX = (float) Math.cos(degrees) * 2f;
        float biasZ = (float) Math.sin(degrees);
        int bananasCount = (int) Math.round(Math.pow(Math.random(), 2) * 4 + 1);
        Model[] models = new Model[bananasCount];
        System.arraycopy(bananas, 0, models, 0, bananasCount);
        BananaStack bananaStack = new BananaStack(
                id,
                models,
                new Vector3f(palm.getDefaultPosition()).add(biasX, 0, biasZ),
                new Vector3f(0, (float) (Math.random() * 360), 0),
                bananaCollect);
        bananaStack.setCount(bananasCount);
        bananaStack.setSelectBox(0.5f, 0.5f, 0.5f);
        bananaStack.setSelectBoxY(0.25f);
        return bananaStack;
    }

    private void createDefaultModels(TextureShader shader) throws IOException {
        createDefaultLogs(shader);
        createDefaultBananas(shader);
        createDefaultPalms(shader);
    }

    private void createDefaultLogs(TextureShader shader) throws IOException {
        log = ObjLoader.load("object/trees/log/log");
        logs[0] = ObjLoader.load("object/trees/log/log_stack1");
        logs[1] = ObjLoader.load("object/trees/log/log_stack2");
        logs[2] = ObjLoader.load("object/trees/log/log_stack3");
        log.setScale(2.5f);
        log.setShader(shader);
        for (Model logStack : logs) {
            logStack.setScale(2.5f);
            logStack.setShader(shader);
        }
    }

    private void createDefaultBananas(TextureShader shader) throws IOException {
        bananas[0] = ObjLoader.load("object/food/banana");
        bananas[1] = ObjLoader.load("object/food/banana_stack2");
        bananas[2] = ObjLoader.load("object/food/banana_stack3");
        bananas[3] = ObjLoader.load("object/food/banana_stack4");
        bananas[4] = ObjLoader.load("object/food/banana_stack5");
        for (Model banana : bananas) {
            banana.setShader(shader);
        }
    }

    private void createDefaultPalms(TextureShader shader) throws IOException {
        palms[0] = ObjLoader.load("object/trees/palm/tree_palmDetailedShort");
        palms[1] = ObjLoader.load("object/trees/palm/tree_palmDetailedTall");
        palms[2] = ObjLoader.load("object/trees/palm/tree_palmShort");
        palms[3] = ObjLoader.load("object/trees/palm/tree_palmTall");
        for (Model palm : palms) {
            palm.setScale(6f);
            palm.setShader(shader);
        }
    }

    @Override
    public void release() {
        log.release();
        release(logs);
        release(bananas);
        release(palms);
    }

    private void release(Model[] models) {
        for (Model model : models) {
            model.release();
        }
    }
}
