package ru.reactiveturtle.game.weapon;

import ru.reactiveturtle.game.base.Entity;
import ru.reactiveturtle.game.base.EntityState;
import ru.reactiveturtle.game.types.Collectable;

public abstract class Weapon extends Entity implements Collectable {
    private WeaponData weaponData;

    public Weapon(WeaponData data,
                  int id,
                  String name,
                  EntityState... entityStates) {
        super(id, name, entityStates);
        weaponData = data;
    }

    public abstract Bullet shot();

    public WeaponData getWeaponData() {
        return weaponData;
    }
}
