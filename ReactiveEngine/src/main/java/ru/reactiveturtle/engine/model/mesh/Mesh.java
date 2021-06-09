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
import ru.reactiveturtle.engine.base.Disposeable;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Objects;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL13.*;

public class Mesh implements Disposeable {
    private String key;
    private int vertexArrayId;
    private Integer vertexBufferId;
    private int vertexCount;

    private Integer indicesBufferId;
    private Integer textureCoordinatesBufferId;
    private Integer normalsBufferId;

    private Material material;

    public Mesh(String key,
                float[] vertices,
                int[] indices) {
        this(key, vertices, indices, new float[]{0, 0}, null);
    }

    public Mesh(String key,
                float[] vertices,
                int[] indices,
                float[] textureCoordinates) {
        this(key, vertices, indices, textureCoordinates, null);
    }

    public Mesh(String key,
                float[] vertices,
                int[] indices,
                float[] textureCoordinates,
                float[] normals) {
        create(key, vertices, indices, textureCoordinates, normals);
    }

    private void create(String key,
                        float[] vertices,
                        int[] indices,
                        float[] textureCoordinates,
                        float[] normals) {
        this.key = key;
        vertexArrayId = GL30.glGenVertexArrays();

        setVertices(vertices, indices);
        setTextureCoordinates(textureCoordinates);
        setNormals(normals);
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

        if (material != null) {
            if (textureCoordinatesBufferId != null) {
                GL20.glEnableVertexAttribArray(2);
                glActiveTexture(GL_TEXTURE0);
                GL11.glBindTexture(GL_TEXTURE_2D, material.getTexture().getTextureId());
            }
            if (material.getNormalMapTexture() != null) {
                glActiveTexture(GL_TEXTURE1);
                GL11.glBindTexture(GL_TEXTURE_2D, material.getNormalMapTexture().getTextureId());
            } else if (normalsBufferId != null) {
                GL20.glEnableVertexAttribArray(3);
            }
        }
        if (stage.getDirectionalLight() != null && stage.getDirectionalLight().getShadowMap() != null) {
            glActiveTexture(GL_TEXTURE2);
            GL11.glBindTexture(GL_TEXTURE_2D, stage.getDirectionalLight().getShadowMap().getDepthTexture().getTextureId());
        }

        shader.load(stage, modelMatrix, this);

        GL20.glDrawElements(GL11.GL_TRIANGLES, vertexCount, GL11.GL_UNSIGNED_INT, 0);

        GL20.glDisableVertexAttribArray(0);
        if (material != null) {
            if (textureCoordinatesBufferId != null) {
                GL20.glDisableVertexAttribArray(2);
            }
            if (material.getNormalMapTexture() == null && normalsBufferId != null) {
                GL20.glDisableVertexAttribArray(3);
            }
        }
        GL11.glBindTexture(GL_TEXTURE_2D, 0);

        GL30.glBindVertexArray(0);
    }

    public void setVertices(float[] vertices, int[] indices) {
        GL30.glBindVertexArray(vertexArrayId);
        if (vertexBufferId != null) {
            GL15.glDeleteBuffers(vertexBufferId);
            vertexBufferId = null;
            if (indicesBufferId != null) {
                GL15.glDeleteBuffers(indicesBufferId);
                indicesBufferId = null;
            }
        }

        if (vertices != null) {
            vertexBufferId = storeData(vertices, 0, 3);
            indicesBufferId = storeIndices(indices);
            vertexCount = indices.length;
        }
        GL30.glBindVertexArray(0);
    }

    public void setTextureCoordinates(float[] textureCoordinates) {
        GL30.glBindVertexArray(vertexArrayId);
        if (textureCoordinatesBufferId != null) {
            GL15.glDeleteBuffers(textureCoordinatesBufferId);
            textureCoordinatesBufferId = null;
        }

        if (textureCoordinates != null) {
            textureCoordinatesBufferId = storeData(textureCoordinates, 2, 2);
        }
        GL30.glBindVertexArray(0);
    }

    public void setNormals(float[] normals) {
        GL30.glBindVertexArray(vertexArrayId);
        if (normalsBufferId != null) {
            GL15.glDeleteBuffers(normalsBufferId);
            normalsBufferId = null;
        }

        if (normals != null) {
            normalsBufferId = storeData(normals, 3, 3);
        }
        GL30.glBindVertexArray(0);
    }

    public String getKey() {
        return key;
    }

    public int getVertexCount() {
        return vertexCount;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public Material getMaterial() {
        return material;
    }

    @Override
    public void dispose() {
        setVertices(null, null);
        setTextureCoordinates(null);
        setNormals(null);
        GL30.glDeleteVertexArrays(vertexArrayId);
    }

    private static int storeIndices(int[] indices) {
        IntBuffer indicesBuffer = BufferUtils.createIntBuffer(indices.length);
        indicesBuffer.put(indices);
        indicesBuffer.flip();

        int indicesBufferId = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, indicesBufferId);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL15.GL_STATIC_DRAW);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

        return indicesBufferId;
    }

    private static int storeData(float[] data, int index, int size) {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
        buffer.put(data).flip();

        int bufferId = GL15.glGenBuffers();

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, bufferId);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(index, size, GL11.GL_FLOAT, false, 0, 0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        return bufferId;
    }
}
