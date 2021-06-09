package ru.reactiveturtle.game.weapon;

import ru.reactiveturtle.engine.camera.Camera;
import ru.reactiveturtle.game.MainGame;
import ru.reactiveturtle.game.base.Entity;
import ru.reactiveturtle.game.types.Collectable;
import ru.reactiveturtle.game.types.Intersectable;

public abstract class Weapon extends Entity implements Collectable, Intersectable {
    private WeaponData weaponData;

    public Weapon(MainGame gameContext,
                  String name,
                  WeaponData data) {
        super(gameContext, name);
        weaponData = data;
    }

    public abstract Bullet shot();

    @Override
    public Float intersect(Camera camera) {
        return intersectEntity(camera.getDirection(), camera.getPosition());
    }

    public WeaponData getWeaponData() {
        return weaponData;
    }
}
