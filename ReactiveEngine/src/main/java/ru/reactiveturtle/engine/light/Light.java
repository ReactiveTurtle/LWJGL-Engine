package ru.reactiveturtle.engine.light;

import org.joml.Vector3f;
import ru.reactiveturtle.engine.shadow.ShadowMap;

import java.util.ArrayList;
import java.util.List;

public abstract class Light {
    public static final int MAX_LIGHTS_COUNT = 16;

    public class Attenuation {
        public float constant = 0;
        public float linear = 1;
        public float exponent = 0;
    }
}
