package engine.model;

import engine.shader.*;
import engine.util.Texture;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.*;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class Model {
    private int vertexArrayId, vertexBufferId, vertexCount;
    private Integer indicesBufferId, colorPaletteBufferId, textureCoordsBufferId, normalsBufferId;

    private Vector3f position = new Vector3f();
    private Vector3f rotation = new Vector3f();
    private Vector3f scale = new Vector3f(1f, 1f, 1f);

    private Texture texture;
    private Material material;

    public Model(float[] vertices, int[] indices) {
        vertexArrayId = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vertexArrayId);

        bindIndices(indices);
        vertexBufferId = storeData(vertices, 0, 3);

        float[] palette = new float[vertexCount * 3];
        for (int i = 0; i < palette.length; i++) {
            palette[i] = 0;
        }
        setColorPalette(palette);
        setTextureCoordinates(new float[]{0, 0});

        GL30.glBindVertexArray(0);
    }

    public void render(Shader shader) {
        if (shader != null) {
            shader.load(getModelMatrix(), material);
        }

        GL30.glBindVertexArray(vertexArrayId);
        GL20.glEnableVertexAttribArray(0);

        if (shader != null) {
            if (texture == null && colorPaletteBufferId != null)
                GL20.glEnableVertexAttribArray(1);
            else if (textureCoordsBufferId != null) {
                GL20.glEnableVertexAttribArray(2);
                GL13.glActiveTexture(GL13.GL_TEXTURE0);
                GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.getTextureId());
            }
            if (material != null && normalsBufferId != null) {
                GL20.glEnableVertexAttribArray(3);
            }
        }

        GL11.glDrawElements(GL11.GL_TRIANGLES, vertexCount, GL11.GL_UNSIGNED_INT, 0);

        GL20.glDisableVertexAttribArray(0);
        if (shader != null) {
            if (material == null)
                GL20.glDisableVertexAttribArray(1);
            else if (textureCoordsBufferId != null)
                GL20.glDisableVertexAttribArray(2);
            if (material != null && normalsBufferId != null)
                GL20.glDisableVertexAttribArray(3);
        }

        GL30.glBindVertexArray(0);
    }

    public void destroy() {
        if (texture != null) texture.destroy();
        if (normalsBufferId != null) GL15.glDeleteBuffers(normalsBufferId);
        if (textureCoordsBufferId != null) GL15.glDeleteBuffers(textureCoordsBufferId);
        if (colorPaletteBufferId != null) GL15.glDeleteBuffers(colorPaletteBufferId);
        GL15.glDeleteBuffers(indicesBufferId);
        GL15.glDeleteBuffers(vertexBufferId);
        GL30.glDeleteVertexArrays(vertexArrayId);
    }

    public Integer getVertexCount() {
        return vertexCount;
    }


    public void setColorPalette(float[] colorPalette) {
        if (colorPaletteBufferId != null)
            GL15.glDeleteBuffers(colorPaletteBufferId);

        GL30.glBindVertexArray(vertexArrayId);
        colorPaletteBufferId = storeData(colorPalette, 1, 3);
        GL30.glBindVertexArray(0);
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

    public void setPosition(Vector3f position) {
        this.position.set(position);
    }

    public void setPosition(float x, float y, float z) {
        position.set(x, y, z);
    }

    public void setX(float x) {
        position.x = x;
    }

    public void setY(float y) {
        position.y = y;
    }

    public void setZ(float z) {
        position.z = z;
    }

    public void addPosition(Vector3f vector) {
        position.add(vector);
    }

    public void addPosition(float x, float y, float z) {
        position.add(x, y, z);
    }

    public void addX(float x) {
        position.x += x;
    }

    public void addY(float y) {
        position.y += y;
    }

    public void addZ(float z) {
        position.z += z;
    }

    public float getX() {
        return position.x;
    }

    public float getY() {
        return position.y;
    }

    public float getZ() {
        return position.z;
    }

    public Vector3f getPosition() {
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
        this.rotation.add(rotation);
    }

    public void addRotationX(float degreesX) {
        rotation.x += degreesX;
    }

    public void addRotationY(float degreesY) {
        rotation.y += degreesY;
    }

    public void addRotationZ(float degreesZ) {
        rotation.z += degreesZ;
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

    public void setColor(float r, float g, float b) {
        //Надо сделать через uniform в файле шейдера
        float[] palette = new float[vertexCount * 6];
        for (int i = 0; i < palette.length; i += 3) {
            palette[i] = r;
            palette[i + 1] = g;
            palette[i + 2] = b;
        }
        setColorPalette(palette);
    }

    public void setTexture(Texture texture) {
        if (colorPaletteBufferId != null) {
            GL15.glDeleteBuffers(colorPaletteBufferId);
            colorPaletteBufferId = null;
        }

        this.texture = texture;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public Matrix4f getModelMatrix() {
        return new Matrix4f().identity()
                .translate(getPosition())
                .rotateXYZ((float) Math.toRadians(getRotationX()),
                        (float) Math.toRadians(getRotationY()),
                        (float) Math.toRadians(getRotationZ()))
                .scale(getScale());
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
}
