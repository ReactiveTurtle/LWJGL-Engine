package ru.reactiveturtle.game.base;

import org.joml.Vector3f;
import ru.reactiveturtle.engine.base.Stage;
import ru.reactiveturtle.engine.base.Value;
import ru.reactiveturtle.engine.model.Releasable;
import ru.reactiveturtle.engine.toolkit.IntersectionExtensions;
import ru.reactiveturtle.physics.BoxBody;
import ru.reactiveturtle.physics.Transform3D;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class Entity extends Transform3D implements Releasable, Cloneable {
    private int id;
    protected String name;
    protected List<EntityState> entityStates = new ArrayList<>();
    protected int currentStateIndex = 0;

    public Entity(int id, String name, EntityState... entityStates) {
        this.entityStates.addAll(Arrays.asList(entityStates));
        this.id = id;
        this.name = name;
    }

    public abstract int getNextState();

    protected int nextState() {
        currentStateIndex = (currentStateIndex + 1) % entityStates.size();
        return currentStateIndex;
    }

    public void renderShadow(Stage stage) {
        EntityState entityState = entityStates.get(currentStateIndex);
        entityState.setPosition(getPosition());
        entityState.setRotation(getRotation());
        entityState.renderShadow(stage);
    }

    public void render(Stage stage) {
        EntityState entityState = entityStates.get(currentStateIndex);
        entityState.render(stage);
    }

    @Override
    public void release() {

    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
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

    public void updateId(int id) {
        this.id = id;
    }

    public boolean isIntersects(Vector3f cameraDirection, Vector3f cameraPosition, Value<Float> distance) {
        EntityState currentState = getCurrentState();
        BoxBody boxBody = currentState.getBody();
        return IntersectionExtensions.isIntersects(
                cameraPosition,
                cameraDirection,
                boxBody.getBoxDefaultNormals(),
                boxBody.getBoxDefaultNormalPoints(),
                boxBody.getCenter(),
                boxBody.getPosition(),
                boxBody.getRotation(),
                distance);
    }
}
