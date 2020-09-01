package ru.reactiveturtle.engine.light;

import org.joml.Vector3f;
import ru.reactiveturtle.engine.shadow.ShadowMap;

import java.util.ArrayList;
import java.util.List;

public abstract class Light {
    public ShadowMap shadowMap;
    public static final int MAX_LIGHTS_COUNT = 16;

    public static PointLight[] getNearestPointLights(List<Light> lightList, Vector3f modelPosition) {
        ArrayList<Integer> ints = new ArrayList<>();
        ArrayList<Double> lengths = new ArrayList<>();
        for (int i = 0; i < lightList.size(); i++) {
            if (lightList.get(i) instanceof PointLight) {
                PointLight pointLight = (PointLight) lightList.get(i);
                ints.add(i);
                lengths.add(Math.sqrt(Math.pow(pointLight.getX() - modelPosition.x, 2) +
                        Math.pow(pointLight.getY() - modelPosition.y, 2) +
                        Math.pow(pointLight.getZ() - modelPosition.z, 2)));
            }
        }

        for (int i = 1; i < lengths.size(); i++) {
            if (lengths.get(i) < lengths.get(i - 1)) {
                double buffer = lengths.get(i);
                int bufferi = ints.get(i);

                lengths.set(i, lengths.get(i - 1));
                ints.set(i, ints.get(i - 1));

                int j = i - 1;
                while (j > 0 && buffer < lengths.get(j - 1)) {
                    lengths.set(j, lengths.get(j - 1));
                    ints.set(j, ints.get(j - 1));
                    j--;
                }

                lengths.set(j, buffer);
                ints.set(j, bufferi);
            }
        }

        PointLight[] pointLights = new PointLight[lightList.size() > MAX_LIGHTS_COUNT ? MAX_LIGHTS_COUNT : ints.size()];
        for (int i = 0; i < pointLights.length; i++) {
            pointLights[i] = (PointLight) lightList.get(ints.get(i));
        }
        return pointLights;
    }

    public static SpotLight[] getNearestSpotLights(List<Light> lightList, Vector3f modelPosition) {
        ArrayList<Integer> ints = new ArrayList<>();
        ArrayList<Double> lengths = new ArrayList<>();
        for (int i = 0; i < lightList.size(); i++) {
            if (lightList.get(i) instanceof SpotLight) {
                SpotLight spotLights = (SpotLight) lightList.get(i);
                ints.add(i);
                lengths.add(Math.sqrt(Math.pow(spotLights.getX() - modelPosition.x, 2) +
                        Math.pow(spotLights.getY() - modelPosition.y, 2) +
                        Math.pow(spotLights.getZ() - modelPosition.z, 2)));
            }
        }

        for (int i = 1; i < lengths.size(); i++) {
            if (lengths.get(i) < lengths.get(i - 1)) {
                double buffer = lengths.get(i);
                int bufferi = ints.get(i);

                lengths.set(i, lengths.get(i - 1));
                ints.set(i, ints.get(i - 1));

                int j = i - 1;
                while (j > 0 && buffer < lengths.get(j - 1)) {
                    lengths.set(j, lengths.get(j - 1));
                    ints.set(j, ints.get(j - 1));
                    j--;
                }

                lengths.set(j, buffer);
                ints.set(j, bufferi);
            }
        }

        SpotLight[] spotLights = new SpotLight[lightList.size() > MAX_LIGHTS_COUNT ? MAX_LIGHTS_COUNT : ints.size()];
        for (int i = 0; i < spotLights.length; i++) {
            spotLights[i] = (SpotLight) lightList.get(ints.get(i));
        }
        return spotLights;
    }

    public static DirectionalLight[] getDirectionalLights(List<Light> lights) {
        List<DirectionalLight> directionalLights = new ArrayList<>();
        for (Light light : lights) {
            if (light instanceof DirectionalLight) {
                directionalLights.add((DirectionalLight) light);
            }
        }
        DirectionalLight[] result = new DirectionalLight[directionalLights.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = directionalLights.get(i);
        }
        return result;
    }

    public void setShadowMap(ShadowMap shadowMap) {
        this.shadowMap = shadowMap;
    }

    public ShadowMap getShadowMap() {
        return shadowMap;
    }

    public class Attenuation {
        public float constant = 0;
        public float linear = 1;
        public float exponent = 0;
    }
}
