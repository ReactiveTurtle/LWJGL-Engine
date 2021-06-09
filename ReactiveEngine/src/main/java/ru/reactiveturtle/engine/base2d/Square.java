package ru.reactiveturtle.engine.base2d;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import ru.reactiveturtle.engine.base3d.Stage3D;
import ru.reactiveturtle.engine.base.Transform3D;
import ru.reactiveturtle.engine.base.Disposeable;
import ru.reactiveturtle.engine.model.mesh.Mesh;
import ru.reactiveturtle.engine.material.Material;
import ru.reactiveturtle.engine.texture.Texture;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_BLEND;

public class Square extends Transform3D implements Disposeable {
    private float width, height;
    public static final String MESH_NAME = "square";

    private Vector3f scale = new Vector3f(1f, 1f, 1f);

    protected Mesh mesh;
    private SquareShader shader;

    public Square(float width,
                  float height,
                  Texture texture,
                  float textureX,
                  float textureY) {
        mesh = getMesh(width, height, texture, textureX, textureY);
        this.width = width;
        this.height = height;
    }

    public void resize(float width, float height, float textureX, float textureY) {
        this.width = width;
        this.height = height;
        mesh.setVertices(getVertices(width, height), getIndices());
        mesh.setTextureCoordinates(new float[]{
                0, 0,
                0, textureX,
                textureY, textureX,
                textureY, 0,
        });
    }

    public void setShader(SquareShader shader) {
        this.shader = shader;
    }

    public SquareShader getShader() {
        return shader;
    }

    public Matrix4f getModelMatrix() {
        return new Matrix4f().identity()
                .translate(position)
                .rotateXYZ((float) Math.toRadians(getRotationX()),
                        (float) Math.toRadians(getRotationY()),
                        (float) Math.toRadians(getRotationZ()))
                .scale(scale);
    }

    public void draw(Stage3D stage) {
        glEnable(GL_BLEND);
        mesh.render(stage, shader, getModelMatrix());
        glDisable(GL_BLEND);
    }

    @Override
    public void dispose() {
        mesh.dispose();
    }

    public Mesh getMesh() {
        return mesh;
    }

    public void setTexture(Texture texture) {
        mesh.getMaterial().setTexture(texture);
    }

    public Texture getTexture() {
        return mesh.getMaterial().getTexture();
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    private static float[] getVertices(float width, float depth) {
        return new float[]{
                -width, depth, 0,
                -width, -depth, 0,
                width, -depth, 0,
                width, depth, 0,
        };
    }

    private static int[] getIndices() {
        return new int[]{
                0, 1, 3, 3, 1, 2,
        };
    }

    public static Mesh getMesh(float width, float height, Texture texture, float textureX, float textureY) {
        Mesh mesh = new Mesh(MESH_NAME, getVertices(width, height), getIndices());
        mesh.setTextureCoordinates(new float[]{
                0, 0,
                0, textureX,
                textureY, textureX,
                textureY, 0,
        });
        mesh.setMaterial(new Material(texture));
        return mesh;
    }
}
