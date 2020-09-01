package ru.reactiveturtle.game.player;

import ru.reactiveturtle.game.player.Player;
import ru.reactiveturtle.game.world.DayNight;

public class Needs {
    private float maxHealth = 100;
    private float health = 100;

    public float getMaxHealth() {
        return maxHealth;
    }

    public float getHealth() {
        return health;
    }

    private float maxHunger = 2400;
    private float hunger = 1500;
    private float hungerSpeed = maxHunger / (DayNight.dayLongTime * 2);
    private float healthHungerSpeed = maxHealth / (DayNight.dayLongTime * 6f);

    public float getMaxHunger() {
        return maxHunger;
    }

    public float getHunger() {
        return hunger;
    }

    public boolean addHunger(float calories) {
        if (hunger + calories <= maxHunger) {
            hunger += calories;
        }
        return hunger + calories <= maxHunger;
    }

    public void update(double deltaTime, Player.Movement movement) {
        float hungerFactor = movement.getSpeed() * 4 / Player.Movement.WALK.getSpeed();
        hunger -= hungerSpeed * deltaTime * hungerFactor;
        if (hunger <= 0) {
            hunger = 0;
            health -= healthHungerSpeed * deltaTime * hungerFactor;
        } else if (health <= maxHealth) {
            health += healthHungerSpeed * deltaTime;
        } else {
            health = 100f;
        }
    }
}
