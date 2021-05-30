package ru.reactiveturtle.game.base;

import org.joml.Vector3f;
import ru.reactiveturtle.engine.base.Renderable;
import ru.reactiveturtle.engine.base.Shader;
import ru.reactiveturtle.engine.base3d.Stage3D;
import ru.reactiveturtle.engine.base.Disposeable;
import ru.reactiveturtle.engine.shadow.ShadowRenderable;
import ru.reactiveturtle.engine.toolkit.GeometryExtensions;
import ru.reactiveturtle.game.MainGame;
import ru.reactiveturtle.physics.BoxBody;
import ru.reactiveturtle.physics.Transform3D;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public abstract class Entity extends Transform3D implements Renderable<Stage3D>, ShadowRenderable, Disposeable {
    protected MainGame gameContext;

    private int id;
    protected String tag;
    protected List<EntityPhase> entityPhases = new ArrayList<>();
    protected int currentStateIndex = 0;

    public Entity(MainGame gameContext, String tag) {
        this.gameContext = gameContext;
        this.id = gameContext.generateId();
        this.tag = tag;
        this.entityPhases.addAll(Arrays.asList(getDefaultEntityPhases(gameContext.getModelLoader())));
        Shader shader = initShader();
        Objects.requireNonNull(shader);
        for (EntityPhase phase : entityPhases) {
            phase.getModel().setShader(shader);
        }
    }

    protected abstract EntityPhase[] getDefaultEntityPhases(ModelLoader modelLoader);

    protected abstract Shader initShader();

    protected abstract int getNextState();

    protected int nextState() {
        currentStateIndex = (currentStateIndex + 1) % entityPhases.size();
        return currentStateIndex;
    }

    @Override
    public void render(Stage3D stage) {
        EntityPhase entityPhase = entityPhases.get(currentStateIndex);
        entityPhase.render(stage);
    }

    @Override
    public void renderShadow(Stage3D stage) {
        EntityPhase entityPhase = entityPhases.get(currentStateIndex);
        entityPhase.renderShadow(stage);
    }

    @Override
    public void dispose() {
        for (EntityPhase phase : entityPhases) {
            phase.dispose();
        }
        entityPhases.clear();
        gameContext.freeId(id);
    }

    public int getId() {
        return id;
    }

    public String getTag() {
        return tag;
    }

    public EntityPhase getCurrentState() {
        return entityPhases.get(currentStateIndex);
    }

    protected Float intersectEntity(Vector3f cameraDirection, Vector3f cameraPosition) {
        EntityPhase currentState = getCurrentState();
        BoxBody boxBody = currentState.getBody();
        return GeometryExtensions.intersectBox(
                cameraPosition,
                cameraDirection,
                boxBody.getBoxDefaultNormals(),
                boxBody.getBoxDefaultNormalPoints(),
                boxBody.getCenter(),
                boxBody.getPosition(),
                boxBody.getRotation());
    }

    @Override
    public String toString() {
        return "Entity{" +
                "\ngame=" + gameContext +
                "\nid=" + id +
                "\ntag='" + tag + '\'' +
                "\nentityPhasesCount=" + entityPhases.size() +
                "\n}";
    }
}
