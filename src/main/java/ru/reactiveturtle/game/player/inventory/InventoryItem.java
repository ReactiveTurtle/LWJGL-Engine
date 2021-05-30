package ru.reactiveturtle.game.player.inventory;

import ru.reactiveturtle.engine.base.Renderable;
import ru.reactiveturtle.engine.base3d.Stage3D;
import ru.reactiveturtle.engine.shadow.ShadowRenderable;
import ru.reactiveturtle.game.MainGame;
import ru.reactiveturtle.game.base.Entity;
import ru.reactiveturtle.game.base.EntityArchive;
import ru.reactiveturtle.game.types.Collectable;

import java.util.Objects;

public class InventoryItem implements Renderable<Stage3D>, ShadowRenderable {
    private EntityArchive entityArchive;
    private int count;

    public InventoryItem(Entity entity, int count) {
        if (!(entity instanceof Collectable)) {
            throw new IllegalStateException("Entity should be implements with \"Collectable\"");
        }
        Objects.requireNonNull(entity);
        this.entityArchive = new EntityArchive(entity);
        this.count = count;
    }

    @Override
    public void render(Stage3D stage) {
        entityArchive.getModel().render(stage);
    }

    @Override
    public void renderShadow(Stage3D stage) {
        entityArchive.getModel().renderShadow(stage);
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
        return entityArchive.getTag();
    }

    public Entity takeEntity(MainGame gameContext) {
        return entityArchive.build(gameContext);
    }
}
