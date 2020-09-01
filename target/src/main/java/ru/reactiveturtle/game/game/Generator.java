package ru.reactiveturtle.game.game;

import org.joml.Vector4f;

public class Generator {
    public static Vector4f[] genPalmForest(int count, float width, float depth) {
        Vector4f[] positions = new Vector4f[count];
        for (int i = 0; i < count; i++) {
            Vector4f vector4f = new Vector4f();
            vector4f.w = (float) (Math.random() * 90);
            vector4f.y = (int) Math.round(Math.random() * 2);
            boolean exists = true;
            while (exists) {
                exists = false;
                vector4f.x = (float) (Math.random() * width - width / 2f);
                vector4f.z = (float) (Math.random() * depth - depth / 2f);
                for (int j = 0; j < positions.length; j++) {
                    if (positions[j] == null) {
                        continue;
                    }
                    if (Math.sqrt(Math.pow(vector4f.x - positions[j].x, 2) + Math.pow(vector4f.z - positions[j].z, 2)) <= 5) {
                        exists = true;
                        break;
                    }
                }
            }
            positions[i] = vector4f;
        }
        return positions;
    }
}
