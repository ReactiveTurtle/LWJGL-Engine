package ru.reactiveturtle.game.world;

import ru.reactiveturtle.engine.base3d.Stage3D;
import ru.reactiveturtle.engine.base.Renderable;
import ru.reactiveturtle.engine.shadow.ShadowRenderable;
import ru.reactiveturtle.engine.toolkit.ReactiveList;
import ru.reactiveturtle.game.MainGame;
import ru.reactiveturtle.game.base.Entity;
import ru.reactiveturtle.game.types.Intersectable;
import ru.reactiveturtle.game.weapon.DragunovSniperRifle;

public class LootMap implements Renderable<Stage3D>, ShadowRenderable {
    private final ReactiveList<Entity> lootMap = new ReactiveList<>();

    public LootMap(MainGame gameContext, Physic physic) {
        generateWeapons(gameContext, physic);
    }

    private void generateWeapons(MainGame gameContext, Physic physic) {
        DragunovSniperRifle dsr = new DragunovSniperRifle(gameContext);
        physic.putBody(dsr.getCurrentState().getBody());
        lootMap.add(dsr);
    }

    @Override
    public void render(Stage3D stage) {
        Entity intersected = null;
        Float intersectedDistance = null;
        for (Entity entity : lootMap) {
            entity.render(stage);
            if (entity instanceof Intersectable) {
                Intersectable intersectable = (Intersectable) entity;
                Float distance = intersectable.intersect(stage.getCamera());
                if (distance != null) {
                    if (intersectedDistance == null || distance < intersectedDistance) {
                        intersected = entity;
                        intersectedDistance = distance;
                    }
                }
            }
        }
        if (intersectionListener != null) {
            if (intersected != null) {
                intersectionListener.onIntersect(intersected, intersectedDistance);
            } else {
                intersectionListener.onNotIntersect();
            }
        }
    }

    @Override
    public void renderShadow(Stage3D stage) {
        for (Entity entity : lootMap) {
            entity.renderShadow(stage);
        }
    }

    public void add(Entity entity) {
        lootMap.add(entity);
    }

    public void remove(int id) {
        int index = lootMap.indexOf(x -> x.getId() == id);
        if (index > -1) {
            lootMap.remove(index);
        }
    }

    private IntersectionListener intersectionListener;

    public void setIntersectionListener(IntersectionListener intersectionListener) {
        this.intersectionListener = intersectionListener;
    }

    public interface IntersectionListener {
        void onIntersect(Entity entity, Float distance);

        void onNotIntersect();
    }
}
