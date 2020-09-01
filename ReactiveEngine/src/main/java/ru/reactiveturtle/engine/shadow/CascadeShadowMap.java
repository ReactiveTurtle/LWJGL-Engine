package ru.reactiveturtle.engine.shadow;

public class CascadeShadowMap {
    public CascadeShadowMap(int mapsCount, int width, int depth) {
        if (mapsCount < 1) {
            throw new IllegalArgumentException("Maps collectCount must be > 0");
        }
    }
}
