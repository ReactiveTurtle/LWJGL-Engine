package ru.reactiveturtle.engine.model.mesh;

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import ru.reactiveturtle.engine.base.Shader;
import ru.reactiveturtle.engine.base3d.Stage3D;
import ru.reactiveturtle.engine.material.Material;
import ru.reactiveturtle.engine.model.Disposeable;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL13.*;

public class Mesh implements Disposeable {
    private int vertexArrayId, vertexBufferId, vertexCount;
    private String key;
    private Integer indicesBufferId, textureCoordsBufferId, normalsBufferId;

    private Material material;

    public Mesh(String key, float[] vertices, int[] indices) {
        create(key, vertices, indices);
    }

    private void create(String key, float[] vertices, int[] indices) {
        this.key = key;

        vertexArrayId = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vertexArrayId);
        vertexBufferId = storeData(vertices, 0, 3);
        bindIndices(indices);

        setTextureCoordinates(new float[]{0, 0});

        GL30.glBindVertexArray(0);
    }

    public void resize(String key, float[] vertices, int[] indices) {
        dispose();
        create(key, vertices, indices);
    }

    public void renderShadow(Stage3D stage, Matrix4f modelMatrix) {
        GL30.glBindVertexArray(vertexArrayId);
        GL20.glEnableVertexAttribArray(0);

        stage.getGameContext().getShadowManager().getShadowShader().load(stage, modelMatrix, this);
        GL20.glDrawElements(GL11.GL_TRIANGLES, vertexCount, GL11.GL_UNSIGNED_INT, 0);

        GL20.glDisableVertexAttribArray(0);
        GL30.glBindVertexArray(0);
    }

    public void render(Stage3D stage, Shader shader, Matrix4f modelMatrix) {
        if (!shader.isBind()) {
            throw new IllegalStateException("Shader must be bind for correct rendering");
        }
        GL30.glBindVertexArray(vertexArrayId);
        GL20.glEnableVertexAttribArray(0);

        if (material == null) {
            GL20.glEnableVertexAttribArray(1);
        } else if (textureCoordsBufferId != null) {
            GL20.glEnableVertexAttribArray(2);
            glActiveTexture(GL_TEXTURE0);
            GL11.glBindTexture(GL_TEXTURE_2D, material.getTexture().getTextureId());
        }
        if (material != null) {
            if (material.getNormalMap() != null) {
                glActiveTexture(GL_TEXTURE1);
                GL11.glBindTexture(GL_TEXTURE_2D, material.getNormalMap().getTextureId());
            } else if (normalsBufferId != null) {
                GL20.glEnableVertexAttribArray(3);
            }
        }
        glActiveTexture(GL_TEXTURE2);
        if (stage.getLights().get(0).getShadowMap() != null) {
            GL11.glBindTexture(GL_TEXTURE_2D, stage.getLights().get(0).getShadowMap().getShadowTexture().getTextureId());
        }

        shader.load(stage, modelMatrix, this);

        GL20.glDrawElements(GL11.GL_TRIANGLES, vertexCount, GL11.GL_UNSIGNED_INT, 0);

        GL20.glDisableVertexAttribArray(0);
        if (material == null)
            GL20.glDisableVertexAttribArray(1);
        else if (textureCoordsBufferId != null) {
            GL20.glDisableVertexAttribArray(2);
            GL11.glBindTexture(GL_TEXTURE_2D, 0);
        }
        if (material != null && normalsBufferId != null)
            GL20.glDisableVertexAttribArray(3);

        GL30.glBindVertexArray(0);
    }

    @Override
    public void dispose() {
        if (normalsBufferId != null) GL15.glDeleteBuffers(normalsBufferId);
        if (textureCoordsBufferId != null) GL15.glDeleteBuffers(textureCoordsBufferId);
        GL15.glDeleteBuffers(indicesBufferId);
        GL15.glDeleteBuffers(vertexBufferId);
        GL30.glDeleteVertexArrays(vertexArrayId);
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public Material getMaterial() {
        return material;
    }

    public void setTextureCoordinates(float[] textureCoordinates) {
        if (textureCoordsBufferId != null) GL15.glDeleteBuffers(textureCoordsBufferId);

        GL30.glBindVertexArray(vertexArrayId);
        textureCoordsBufferId = storeData(textureCoordinates, 2, 2);
        GL30.glBindVertexArray(0);
    }

    public void setNormals(float[] normals) {
        if (normalsBufferId != null) GL15.glDeleteBuffers(normalsBufferId);

        GL30.glBindVertexArray(vertexArrayId);
        normalsBufferId = storeData(normals, 3, 3);
        GL30.glBindVertexArray(0);
    }

    private void bindIndices(int[] indices) {
        IntBuffer indicesBuffer = BufferUtils.createIntBuffer(indices.length);
        indicesBuffer.put(indices);
        indicesBuffer.flip();

        indicesBufferId = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, indicesBufferId);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL15.GL_STATIC_DRAW);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

        vertexCount = indices.length;
    }

    public static int storeData(float[] data, int index, int size) {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
        buffer.put(data).flip();

        int bufferId = GL15.glGenBuffers();

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, bufferId);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(index, size, GL11.GL_FLOAT, false, 0, 0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        return bufferId;
    }

    public String getKey() {
        return key;
    }

    public int getVertexCount() {
        return vertexCount;
    }
}
