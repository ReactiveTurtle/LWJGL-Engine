package ru.reactiveturtle.engine.model;

import org.joml.Matrix4f;
import ru.reactiveturtle.engine.base3d.Stage3D;
import ru.reactiveturtle.engine.base.Transform3D;
import ru.reactiveturtle.engine.model.mesh.Mesh;
import ru.reactiveturtle.engine.base.Shader;
import ru.reactiveturtle.engine.material.Material;
import ru.reactiveturtle.engine.shadow.Shadow;

import java.util.HashMap;

public class Model extends Transform3D implements Shadow, Renderable<Stage3D>, Disposeable {

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
    public void renderShadow(Stage3D stage) {
        if (stage.getGameContext().getShadowManager().isShadowEnabled()) {
            for (Mesh mesh : meshes.values()) {
                mesh.renderShadow(stage, getModelMatrix());
            }
        }
    }

    @Override
    public void render(Stage3D stage) {
        shader.bind();
        for (Mesh mesh : meshes.values()) {
            mesh.render(stage, shader, getModelMatrix());
        }
        shader.unbind();
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
    public void dispose() {
        for (Mesh mesh : meshes.values()) {
            mesh.dispose();
        }
    }
}
