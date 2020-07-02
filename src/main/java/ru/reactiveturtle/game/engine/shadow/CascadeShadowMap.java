package ru.reactiveturtle.game.engine.shadow;

public class CascadeShadowMap {
    public CascadeShadowMap(int mapsCount, int width, int depth) {
        if (mapsCount < 1) {
            throw new IllegalArgumentException("Maps count must be > 0");
        }
    }
}
