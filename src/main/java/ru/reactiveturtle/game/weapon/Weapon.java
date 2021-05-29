package ru.reactiveturtle.game.weapon;

import ru.reactiveturtle.engine.camera.PerspectiveCamera;
import ru.reactiveturtle.game.base.Entity;
import ru.reactiveturtle.game.base.EntityState;
import ru.reactiveturtle.game.types.Collectable;
import ru.reactiveturtle.game.types.Intersectable;

public abstract class Weapon extends Entity implements Collectable, Intersectable {
    private WeaponData weaponData;

    public Weapon(WeaponData data,
                  int id,
                  String name) {
        super(id, name);
        weaponData = data;
    }

    public abstract Bullet shot();

    @Override
    public Float intersect(PerspectiveCamera camera) {
        return intersectEntity(camera.getDirection(), camera.getPosition());
    }

    public WeaponData getWeaponData() {
        return weaponData;
    }
}
