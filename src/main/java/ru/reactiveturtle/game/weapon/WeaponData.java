package ru.reactiveturtle.game.weapon;

public class WeaponData {
    private float startVelocity;

    private int bulletsCount;
    private int magazineCapacity;

    public WeaponData(float startVelocity,
                      int bulletsCount,
                      int magazineCapacity) {
        this.startVelocity = startVelocity;
        this.bulletsCount = bulletsCount;
        this.magazineCapacity = magazineCapacity;
    }
}
