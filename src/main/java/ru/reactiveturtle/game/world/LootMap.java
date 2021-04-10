package ru.reactiveturtle.game.world;

import ru.reactiveturtle.engine.base.Stage;
import ru.reactiveturtle.engine.base.Value;
import ru.reactiveturtle.engine.model.Renderable;
import ru.reactiveturtle.engine.shader.TextureShader;
import ru.reactiveturtle.game.MainGame;
import ru.reactiveturtle.game.base.Entity;
import ru.reactiveturtle.game.types.Collectable;
import ru.reactiveturtle.game.weapon.DragunovSniperRifle;

import java.util.ArrayList;
import java.util.List;

public class LootMap implements Renderable {
    private List<Entity> lootMap = new ArrayList<>();

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
    public void render(Stage stage) {
        for (Entity entity : lootMap) {
            entity.render(stage);
            if (entity instanceof Collectable) {
                Value<Float> distance = new Value<>();
                if (entity.isIntersects(
                        stage.getCamera().getDirection(),
                        stage.getCamera().getPosition(),
                        distance)) {
                    System.out.println(entity.getName() + " intersects. Distance: " + distance.value + ".");
                } else {
                    System.out.println(entity.getName() + " not intersects.");
                }
            }
        }
    }
}
