package ru.reactiveturtle.game.world;

import org.joml.Vector3f;
import org.joml.Vector4f;

public class Generator {
    public static Vector4f[] genPalmForest(int count, float width, float depth) {
        Vector4f[] positions = new Vector4f[count];
        for (int i = 0; i < count; i++) {
            Vector4f vector4f = new Vector4f();
            vector4f.w = (float) (Math.random() * 90);
            vector4f.y = (int) Math.round(Math.random() * 3);
            boolean exists = true;
            while (exists) {
                vector4f.x = (float) (Math.random() * width - width / 2f);
                vector4f.z = (float) (Math.random() * depth - depth / 2f);
                exists = new Vector3f(vector4f.x, 0, vector4f.z).length() <= 4;
                if (!exists) {
                    for (int j = 0; j < positions.length; j++) {
                        if (positions[j] == null) {
                            continue;
                        }
                        if (new Vector3f(vector4f.x - positions[j].x, 0, vector4f.z - positions[j].z).length() <= 5) {
                            exists = true;
                            break;
                        }
                    }
                }
            }
            positions[i] = vector4f;
        }
        return positions;
    }
}
