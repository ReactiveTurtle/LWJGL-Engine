package ru.reactiveturtle.game.game;

import javafx.util.Pair;
import org.joml.Vector3f;
import org.joml.Vector4f;
import ru.reactiveturtle.game.engine.material.Material;
import ru.reactiveturtle.game.engine.model.Model;
import ru.reactiveturtle.game.engine.model.base.Parallelepiped;
import ru.reactiveturtle.game.engine.model.loader.ObjLoader;
import ru.reactiveturtle.game.engine.shader.TextureShader;
import ru.reactiveturtle.game.game.player.Collectable;
import ru.reactiveturtle.game.game.player.Destroyable;
import ru.reactiveturtle.game.game.player.Static;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Arrangement {
    private List<Static> statics = new ArrayList<>();
    private List<Collectable> collectables = new ArrayList<>();
    private List<Destroyable> destroyables = new ArrayList<>();
    private Parallelepiped intersectionTest;

    protected Arrangement(TextureShader shader) {
        try {
            Model model = ObjLoader.load("/object/tools/axe");
            model.setShader(shader);
            Collectable collectable = new Collectable(
                    "Axe",
                    model,
                    new Vector3f(10, 0.05f, -5),
                    new Vector3f(0, 0, 90),
                    Collectable.Type.STEEL_WEAPON);
            collectable.setSelectBox(0.05f, 0.685f, 0.4f);
            collectable.setSelectBoxY(0.265f);
            collectable.setSelectBoxX(-0.025f);
            collectables.add(collectable);

            Material material = new Material();
            material.setDiffuse(0.1f, 0.1f, 0.2f);
            intersectionTest = new Parallelepiped(1f, 1f, 1f);
            intersectionTest.setShader(shader);
            intersectionTest.setMaterial(material);
            intersectionTest.setPosition(10f, 0, 10f);

            createForest(shader);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createForest(TextureShader shader) throws IOException {
        Model stump = ObjLoader.load("object/trees/stump/stump_1");
        stump.setShader(shader);
        ArrayList<Model> palms = new ArrayList<>(Arrays.asList(
                ObjLoader.load("object/trees/palm_1"),
                ObjLoader.load("object/trees/palm_2"),
                ObjLoader.load("object/trees/palm_3")));
        for (Model palm : palms) {
            palm.setScale(2f);
            palm.setShader(shader);
        }
        Vector4f[] randomPositions = Generator.genPalmForest(200, 100, 100);
        for (int i = 0; i < randomPositions.length; i++) {
            Vector4f vector4f = randomPositions[i];
            Destroyable destroyable = new Destroyable(
                    "Palm" + i,
                    palms.get((int) vector4f.y),
                    new Vector3f(vector4f.x, 0, vector4f.z),
                    new Vector3f(0, vector4f.w, 0),
                    stump);
            destroyable.setSelectBox(1f, 7.6f, 1f);
            destroyable.setSelectBoxY(3.8f);
            destroyables.add(destroyable);
        }
    }

    public void renderShadow() {
        for (Static staticObject : statics) {
            staticObject.renderShadow();
        }
        for (Destroyable destroyable : destroyables) {
            destroyable.renderShadow();
        }
        for (Collectable collectable : collectables) {
            collectable.renderShadow();
        }
    }

    public void render(Vector3f direction, Vector3f position) {
        for (Static staticObject : statics) {
            staticObject.render(direction, position);
        }
        for (Destroyable destroyable : destroyables) {
            destroyable.render(direction, position);
        }
        for (Collectable collectable : collectables) {
            collectable.render(direction, position);
        }
    }

    public Pair<Static, Vector3f> getNearestIntersection(Vector3f position, Vector3f direction, Model model) {
        Pair<Static, Vector3f> result = null;
        for (Collectable collectable : collectables) {
            Vector3f intersectionPoint = collectable.calcIntersectionPoint(position, direction, model);
            if (intersectionPoint != null && (result == null ||
                    new Vector3f(position).sub(intersectionPoint).length() < new Vector3f(position).sub(result.getValue()).length())) {
                result = new Pair<>(collectable, intersectionPoint);
            }
        }
        for (Destroyable destroyable : destroyables) {
            Vector3f intersectionPoint = destroyable.calcIntersectionPoint(position, direction, intersectionTest);
            if (intersectionPoint != null && (result == null ||
                    new Vector3f(position).sub(intersectionPoint).length() < new Vector3f(position).sub(result.getValue()).length())) {
                result = new Pair<>(destroyable, intersectionPoint);
            }
        }
        return result;
    }

    public void putCollectable(Collectable collectable) {
        collectables.add(collectable);
    }

    public <T> T getStatic(String key, Class<? extends Static> c) {
        if (c.equals(Collectable.class)) {
            for (Collectable collectable : collectables) {
                if (collectable.name.equals(key)) {
                    return (T) collectable;
                }
            }
        } else if (c.equals(Destroyable.class)) {
            for (Destroyable destroyable : destroyables) {
                if (destroyable.name.equals(key)) {
                    return (T) destroyable;
                }
            }
        } else {
            return null;
        }
        return null;
    }

    public void removeStatic(String key, Class<? extends Static> c) {
        Static staticObjcet = null;
        if (c.equals(Collectable.class)) {
            for (int i = 0, collectablesSize = collectables.size(); i < collectablesSize; i++) {
                if (collectables.get(i).name.equals(key)) {
                    collectables.remove(i);
                }
            }
        } else if (c.equals(Destroyable.class)) {
            for (int i = 0, destroyablesSize = destroyables.size(); i < destroyablesSize; i++) {
                if (destroyables.get(i).name.equals(key)) {
                    destroyables.remove(i);
                }
            }
        }
    }

    public void destroy(String key) {
        for (int i = 0, destroyablesSize = destroyables.size(); i < destroyablesSize; i++) {
            if (destroyables.get(i).name.equals(key)) {
                statics.add(destroyables.remove(i).getSubstitute());
                break;
            }
        }
    }
}
