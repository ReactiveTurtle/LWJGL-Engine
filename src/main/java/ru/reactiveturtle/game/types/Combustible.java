package ru.reactiveturtle.game.types;

public interface Combustible extends Collectable {
    /*
    * param burningTime like real seconds
    */
    void setBurningTime(float burningTime);

    float getBurningTime();
}
