package ru.reactiveturtle.game.base;

import org.joml.Vector3f;
import ru.reactiveturtle.engine.base3d.Stage3D;
import ru.reactiveturtle.engine.base.Disposeable;
import ru.reactiveturtle.engine.toolkit.GeometryExtensions;
import ru.reactiveturtle.game.MainGame;
import ru.reactiveturtle.physics.BoxBody;
import ru.reactiveturtle.physics.Transform3D;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class Entity extends Transform3D implements Disposeable, Cloneable {
    private int id;
    protected String tag;
    protected List<EntityState> entityStates = new ArrayList<>();
    protected int currentStateIndex = 0;

    public Entity(int id, String tag) {
        this.entityStates.addAll(Arrays.asList(getDefaultEntityStates()));
        this.id = id;
        this.tag = tag;
    }

    protected abstract EntityState[] getDefaultEntityStates();

    protected abstract int getNextState();

    protected int nextState() {
        currentStateIndex = (currentStateIndex + 1) % entityStates.size();
        return currentStateIndex;
    }

    public void renderShadow(Stage3D stage) {
        EntityState entityState = entityStates.get(currentStateIndex);
        entityState.renderShadow(stage);
    }

    public void render(Stage3D stage) {
        EntityState entityState = entityStates.get(currentStateIndex);
        entityState.render(stage);
    }

    @Override
    public void dispose() {
        MainGame.freeId(id);
    }

    public int getId() {
        return id;
    }

    public String getTag() {
        return tag;
    }

    public EntityState getCurrentState() {
        return entityStates.get(currentStateIndex);
    }

    public Entity copy(int id) {
        try {
            Entity entity = (Entity) clone();
            entity.id = id;
            return entity;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected Float intersectEntity(Vector3f cameraDirection, Vector3f cameraPosition) {
        EntityState currentState = getCurrentState();
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
}
