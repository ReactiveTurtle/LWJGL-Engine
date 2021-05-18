package ru.reactiveturtle.game.world;

import ru.reactiveturtle.engine.base3d.Stage3D;
import ru.reactiveturtle.engine.model.Renderable;
import ru.reactiveturtle.engine.shader.TextureShader;
import ru.reactiveturtle.game.MainGame;
import ru.reactiveturtle.game.base.Entity;
import ru.reactiveturtle.game.types.Intersectable;
import ru.reactiveturtle.game.weapon.DragunovSniperRifle;

import java.util.ArrayList;
import java.util.List;

public class LootMap implements Renderable<Stage3D> {
    private final List<Entity> lootMap = new ArrayList<>();

    public LootMap(Physic physic, TextureShader textureShader) {
        generateWeapons(physic, textureShader);
    }

    private void generateWeapons(Physic physic, TextureShader textureShader) {
        DragunovSniperRifle dsr = new DragunovSniperRifle(MainGame.generateId());
        dsr.getCurrentState().getModel().setShader(textureShader);
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
                System.out.println(intersected.getName() + " intersects. Distance: " + intersectedDistance + ".");
            } else {
                intersectionListener.onNotIntersect();
            }
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
