package ru.reactiveturtle.game.generator;

public class GeneratorHelper {
    public static float round(float number, int rank) {
        if (rank < 0) {
            throw new IllegalArgumentException();
        } else {
            double floor = Math.floor(number);
            double div = Math.pow(10, rank);
            return (float) (floor + Math.round((number - floor) * div) / div);
        }
    }
}
