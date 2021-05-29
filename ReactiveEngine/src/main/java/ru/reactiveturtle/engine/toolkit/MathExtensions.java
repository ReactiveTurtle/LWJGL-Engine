package ru.reactiveturtle.engine.toolkit;

public final class MathExtensions {
    private MathExtensions() {
    }

    public static float round(float number, int marksCount) {
        if (marksCount < 0) {
            throw new IllegalArgumentException("Marks count must be greater or equal 0");
        }

        int integer = (int) number;
        float mod = number - integer;
        double factor = Math.pow(10, marksCount);
        double fractional = mod * factor;
        fractional = Math.round(fractional) / factor;
        return (float) (integer + fractional);
    }
}
