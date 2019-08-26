package engine.environment;

import org.joml.Vector3f;

import java.util.Arrays;
import java.util.List;

public abstract class Environment {
    public static final int MAX_LIGHTS_COUNT = 16;

    public static PointLight[] getNearestPointLights(List<PointLight> pointLightsList, Vector3f modelPosition) {
        int[] ints = new int[pointLightsList.size()];
        double[] lengths = new double[pointLightsList.size()];
        for (int i = 0; i < lengths.length; i++) {
            PointLight pointLight = pointLightsList.get(i);
            ints[i] = i;
            lengths[i] = Math.sqrt(Math.pow(pointLight.getX() - modelPosition.x, 2) +
                    Math.pow(pointLight.getY() - modelPosition.y, 2) +
                    Math.pow(pointLight.getZ() - modelPosition.z, 2));
        }

        for (int i = 1; i < lengths.length; i++) {
            if (lengths[i] < lengths[i - 1]) {
                double buffer = lengths[i];
                int bufferi = ints[i];

                lengths[i] = lengths[i - 1];
                ints[i] = ints[i - 1];

                int j = i - 1;
                while (j > 0 && buffer < lengths[j - 1]) {
                    lengths[j] = lengths[j - 1];
                    ints[j] = ints[j - 1];
                    j--;
                }

                lengths[j] = buffer;
                ints[j] = bufferi;
            }
        }

        PointLight[] pointLights = new PointLight[pointLightsList.size() > MAX_LIGHTS_COUNT ? MAX_LIGHTS_COUNT : pointLightsList.size()];
        for (int i = 0; i < pointLights.length; i++) {
            pointLights[i] = pointLightsList.get(ints[i]);
        }
        return pointLights;
    }

    public static SpotLight[] getNearestSpotLights(List<SpotLight> spotLightsList, Vector3f modelPosition) {
        int[] ints = new int[spotLightsList.size()];
        double[] lengths = new double[spotLightsList.size()];
        for (int i = 0; i < lengths.length; i++) {
            SpotLight pointLight = spotLightsList.get(i);
            ints[i] = i;
            lengths[i] = Math.sqrt(Math.pow(pointLight.getX() - modelPosition.x, 2) +
                    Math.pow(pointLight.getY() - modelPosition.y, 2) +
                    Math.pow(pointLight.getZ() - modelPosition.z, 2));
        }

        for (int i = 1; i < lengths.length; i++) {
            if (lengths[i] < lengths[i - 1]) {
                double buffer = lengths[i];
                int bufferi = ints[i];

                lengths[i] = lengths[i - 1];
                ints[i] = ints[i - 1];

                int j = i - 1;
                while (j > 0 && buffer < lengths[j - 1]) {
                    lengths[j] = lengths[j - 1];
                    ints[j] = ints[j - 1];
                    j--;
                }

                lengths[j] = buffer;
                ints[j] = bufferi;
            }
        }

        SpotLight[] spotLights = new SpotLight[spotLightsList.size() > MAX_LIGHTS_COUNT ? MAX_LIGHTS_COUNT : spotLightsList.size()];
        for (int i = 0; i < spotLights.length; i++) {
            spotLights[i] = spotLightsList.get(ints[i]);
        }
        return spotLights;
    }

    public class Attenuation {
        public float constant = 0;
        public float linear = 1;
        public float exponent = 0;
    }
}
