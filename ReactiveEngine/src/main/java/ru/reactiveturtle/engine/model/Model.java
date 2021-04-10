package ru.reactiveturtle.engine.model;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import ru.reactiveturtle.engine.base.Stage;
import ru.reactiveturtle.engine.base.Transform3D;
import ru.reactiveturtle.engine.model.mesh.Mesh;
import ru.reactiveturtle.engine.base.Shader;
import ru.reactiveturtle.engine.material.Material;
import ru.reactiveturtle.engine.shadow.Shadow;

import java.util.HashMap;

public class Model extends Transform3D implements Shadow, Renderable, Releasable {
    private final Vector3f scale = new Vector3f(1f, 1f, 1f);

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
                .rotateXYZ(getRotationX(),
                        getRotationY(),
                        getRotationZ())
                .scale(getScale());
    }

    @Override
    public void renderShadow(Stage stage) {
        if (stage.getGameContext().getShadowManager().isShadowEnabled()) {
            for (Mesh mesh : meshes.values()) {
                mesh.renderShadow(stage, getModelMatrix());
            }
        }
    }

    @Override
    public void render(Stage stage) {
        shader.bind();
        for (Mesh mesh : meshes.values()) {
            mesh.render(stage, shader, getModelMatrix());
        }
        shader.unbind();
    }

    public void setScale(float scale) {
        this.scale.x = scale;
        this.scale.y = scale;
        this.scale.z = scale;
    }

    public void setScale(Vector3f scale) {
        scale.set(scale.x, scale.y, scale.z);
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

    @Override
    public void release() {
        for (Mesh mesh : meshes.values()) {
            mesh.destroy();
        }
    }
}
