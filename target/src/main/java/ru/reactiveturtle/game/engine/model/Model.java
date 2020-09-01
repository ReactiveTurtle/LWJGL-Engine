package ru.reactiveturtle.game.engine.model;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;
import ru.reactiveturtle.game.engine.base.GameContext;
import ru.reactiveturtle.game.engine.base.Shader;
import ru.reactiveturtle.game.engine.base.Transform3D;
import ru.reactiveturtle.game.engine.material.Material;
import ru.reactiveturtle.game.engine.model.mesh.Mesh;
import ru.reactiveturtle.game.game.player.Interface;

import java.util.HashMap;

public class Model extends Transform3D {
    private Vector3f scale = new Vector3f(1f, 1f, 1f);

    protected HashMap<String, Mesh> meshes = new HashMap<>();

    private Shader shader;

    public Model(Mesh... meshes) {
        for (Mesh mesh : meshes) {
            this.meshes.put(mesh.getKey(), mesh);
        }
    }

    public void setShader(Shader shader) {
        this.shader = shader;
    }

    public Shader getShader() {
        return shader;
    }

    public Matrix4f getModelMatrix() {
        return new Matrix4f().identity()
                .translate(getPosition())
                .rotateXYZ((float) Math.toRadians(getRotationX()),
                        (float) Math.toRadians(getRotationY()),
                        (float) Math.toRadians(getRotationZ()))
                .scale(getScale());
    }

    public void render() {
        for (Mesh mesh : meshes.values()) {
            mesh.render(shader, getModelMatrix());
        }
    }

    public void renderShadow() {
        if (GameContext.getShadowManager().isShadowEnabled()) {
            for (Mesh mesh : meshes.values()) {
                mesh.renderShadow(getModelMatrix());
            }
        }
    }

    public void destroy() {

    }

    public void setScale(float scale) {
        this.scale.x = scale;
        this.scale.y = scale;
        this.scale.z = scale;
    }

    public void setScale(float scaleX, float scaleY, float scaleZ) {
        scale.set(scaleX, scaleY, scaleZ);
    }

    public void setScaleX(float scaleX) {
        scale.x = scaleX;
    }

    public void setScaleY(float scaleY) {
        scale.y = scaleY;
    }

    public Vector3f getScale() {
        return scale;
    }

    public float getScaleX() {
        return scale.x;
    }

    public float getScaleY() {
        return scale.y;
    }

    public void setMaterial(Material material) {
        for (Mesh mesh : meshes.values()) {
            mesh.setMaterial(material);
        }
    }

    public HashMap<String, Mesh> getMeshes() {
        return meshes;
    }
}
