package engine.model.figures;

import engine.model.figures.Parallelepiped;

public class SkyBox extends Parallelepiped {
    public SkyBox(float width, float height, float depth) {
        super(width, height, depth);
        super.setTextureCoordinates(new float[]{
                0.667f, 0.5f, 0.667f, 1f, 0.333f, 1f, 0.333f, 0.5f,
                0f, 0.5f, 0f, 1f, 0.333f, 1f, 0.333f, 0.5f,
                1f, 0.5f, 1f, 1f, 0.667f, 1f, 0.667f, 0.5f,
                0.667f, 0f, 0.667f, 0.5f, 1f, 0.5f, 1f, 0f,
                0.667f, 0.5f, 0.667f, 0f, 0.333f, 0f, 0.333f, 0.5f,
                0.333f, 0f, 0.333f, 0.5f, 0f, 0.5f, 0f, 0f
        });
    }

}
