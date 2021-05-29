package ru.reactiveturtle.game.player.inventory;

import org.joml.Vector3f;
import ru.reactiveturtle.engine.base.Renderable;
import ru.reactiveturtle.engine.base3d.Stage3D;
import ru.reactiveturtle.engine.shadow.ShadowRenderable;
import ru.reactiveturtle.engine.toolkit.Pair;
import ru.reactiveturtle.game.base.Entity;
import ru.reactiveturtle.game.types.Collectable;

import java.util.Objects;

public class InventoryItem implements Renderable<Stage3D>, ShadowRenderable {
    private Entity entity;
    private int count;

    public InventoryItem(Entity entity, int count) {
        if (!(entity instanceof Collectable)) {
            throw new IllegalStateException("Entity should be implements with \"Collectable\"");
        }
        Objects.requireNonNull(entity);
        this.entity = entity;
        this.count = count;
    }

    @Override
    public void render(Stage3D stage) {
        entity.getCurrentState().getModel().render(stage);
    }

    @Override
    public void renderShadow(Stage3D stage) {
        entity.getCurrentState().getModel().renderShadow(stage);
    }

    public void updatePositionAndRotationRelativelyPlayer(Vector3f position, Vector3f rotation) {
        Collectable collectable = (Collectable) entity;
        Pair<Vector3f> pair = collectable.getPositionAndRotationRelativelyPlayer(position, rotation);
        entity.getCurrentState().getModel().setPosition(pair.first);
        entity.getCurrentState().getModel().setRotation(pair.second);
    }

    public void countUp(int count) {
        if (count <= 0) {
            throw new IllegalArgumentException("Count must be greater than 0");
        }
        this.count += count;
    }

    public boolean countDown(int count) {
        if (this.count - count < 0) {
            return false;
        }
        this.count -= count;
        return true;
    }

    public int getCount() {
        return count;
    }

    public String getEntityTag() {
        return entity.getTag();
    }

    public Entity takeEntity() {
        Entity entity = this.entity;
        this.entity = null;
        return entity;
    }
}
