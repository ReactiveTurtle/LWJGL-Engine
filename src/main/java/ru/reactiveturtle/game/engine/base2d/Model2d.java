package ru.reactiveturtle.game.engine.base2d;

import jdk.nashorn.internal.runtime.PropertyAccess;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import ru.reactiveturtle.game.engine.material.Texture;
import ru.reactiveturtle.game.engine.model.mesh.Mesh;

import java.util.HashMap;

import static org.lwjgl.opengl.GL11.*;

class Model2d {
    private Vector2f position = new Vector2f();
    private Vector3f rotation = new Vector3f();

    HashMap<String, Mesh> meshes = new HashMap<>();

    private SquareShader shader;

    Model2d(Mesh... meshes) {
        for (Mesh mesh : meshes) {
            this.meshes.put(mesh.getKey(), mesh);
        }
    }

    public void setShader(SquareShader shader) {
        this.shader = shader;
    }

    public SquareShader getShader() {
        return shader;
    }

    public Matrix4f getModelMatrix() {
        return new Matrix4f().identity()
                .translate(position.x, position.y, 0)
                .rotateXYZ((float) Math.toRadians(getRotationX()),
                        (float) Math.toRadians(getRotationY()), 0);
    }

    public void draw() {
        glEnable(GL_BLEND);
        for (Mesh mesh : meshes.values()) {
            mesh.render(shader, getModelMatrix());
        }
        glDisable(GL_BLEND);
    }

    public void destroy() {
        for (Mesh mesh : meshes.values()) {
            mesh.destroy();
        }
    }

    public void setPosition(Vector2f position) {
        this.position.set(position);
    }

    public void setPosition(float x, float y) {
        position.set(x, y);
    }

    public void setX(float x) {
        position.x = x;
    }

    public void setY(float y) {
        position.y = y;
    }

    public void addPosition(Vector2f vector) {
        position.add(vector);
    }

    public void addPosition(float x, float y) {
        position.add(x, y);
    }

    public void addX(float x) {
        position.x += x;
    }

    public void addY(float y) {
        position.y += y;
    }

    public float getX() {
        return position.x;
    }

    public float getY() {
        return position.y;
    }

    public Vector2f getPosition() {
        return position;
    }

    public void setRotation(float degreesX, float degreesY, float degreesZ) {
        rotation.set(degreesX, degreesY, degreesZ);
    }

    public void setRotation(Vector3f rotation) {
        this.rotation.set(rotation);
    }

    public void setRotationX(float degreesX) {
        rotation.x = degreesX;
    }

    public void setRotationY(float degreesY) {
        rotation.y = degreesY;
    }

    public void setRotationZ(float degreesZ) {
        rotation.z = degreesZ;
    }

    public void addRotation(float degreesX, float degreesY, float degreesZ) {
        rotation.add(degreesX, degreesY, degreesZ);
    }

    public void addRotation(Vector3f rotation) {
        addRotation(rotation.x, rotation.y, rotation.z);
    }

    public void addRotationX(float degreesX) {
        addRotation(degreesX, 0, 0);
    }

    public void addRotationY(float degreesY) {
        addRotation(0, degreesY, 0);
    }

    public void addRotationZ(float degreesZ) {
        addRotation(0, 0, degreesZ);
    }

    public float getRotationX() {
        return rotation.x;
    }

    public float getRotationY() {
        return rotation.y;
    }

    public float getRotationZ() {
        return rotation.z;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public Texture getTexture() {
        return meshes.values().toArray(new Mesh[1])[0].getMaterial().getTexture();
    }
}
