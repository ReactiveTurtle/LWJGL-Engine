package ru.reactiveturtle.game.world;

import ru.reactiveturtle.engine.model.Model;
import ru.reactiveturtle.engine.model.mesh.Mesh;
import ru.reactiveturtle.physics.BoxBody;

public class BoxBodyModel extends Model {
    public BoxBodyModel(BoxBody boxBody) {
        float[] vertices = getVertices(boxBody);
        int[] indices = new int[vertices.length / 3];
        for (int i = 0; i < indices.length; i++) {
            indices[i] = i;
        }
        Mesh mesh = new Mesh("body", vertices, indices);
        meshes.put("body", mesh);
    }

    private static float[] getVertices(BoxBody boxBody) {
        float[] points = boxBody.getBoxPoints();
        return new float[]{
                points[3], points[4], points[5],
                points[0], points[1], points[2],
                points[6], points[7], points[8],
                points[6], points[7], points[8],
                points[0], points[1], points[2],
                points[9], points[10], points[11],

                points[18], points[19], points[20],
                points[21], points[22], points[23],
                points[15], points[16], points[17],
                points[15], points[16], points[17],
                points[21], points[22], points[23],
                points[12], points[13], points[14],
        };
    }
}
