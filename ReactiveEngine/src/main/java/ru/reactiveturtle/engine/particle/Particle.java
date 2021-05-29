package ru.reactiveturtle.engine.particle;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import ru.reactiveturtle.engine.base3d.Stage3D;
import ru.reactiveturtle.engine.base2d.Square;
import ru.reactiveturtle.engine.model.mesh.Mesh;
import ru.reactiveturtle.engine.texture.Texture;

import static org.lwjgl.opengl.GL11.*;

public class Particle implements Cloneable {
    private ParticleInfo particleInfo;
    private ParticleShader shader;
    private Mesh mesh;

    public Particle(float width, float height,
                    Texture texture, float textureX, float textureY,
                    Vector3f speed, long ttl) {
        mesh = Square.getMesh(width, height, texture, textureX, textureY);
        particleInfo = new ParticleInfo(new Vector3f(speed), ttl);
    }

    public void setParticleInfo(ParticleInfo particleInfo) {
        this.particleInfo = particleInfo;
    }

    public ParticleInfo getParticleInfo() {
        return particleInfo;
    }

    public void setShader(ParticleShader shader) {
        this.shader = shader;
    }

    public ParticleShader getShader() {
        return shader;
    }

    public Matrix4f getModelMatrix() {
        return new Matrix4f().identity()
                .translate(particleInfo.getPosition())
                .rotateYXZ(
                        (float) Math.toRadians(-particleInfo.getRotationY()),
                        (float) Math.toRadians(-particleInfo.getRotationX()),
                        (float) Math.toRadians(-particleInfo.getRotationZ()))
                .scale(particleInfo.getScale());
    }

    public void draw(Stage3D stage, float alpha) {
        shader.bind();
        glDepthMask(false);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE);
        shader.loadAlpha(alpha);
        mesh.render(stage, shader, getModelMatrix());
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glDisable(GL_BLEND);
        glDepthMask(true);
        shader.unbind();
    }
}
