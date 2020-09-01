package ru.reactiveturtle.engine.particle;

import org.joml.Vector3f;
import ru.reactiveturtle.engine.base.Transform3D;

public class ParticleInfo extends Transform3D {
    private Vector3f speed = new Vector3f();
    private Vector3f scale = new Vector3f(1);

    /**
     * Time to live for particle in milliseconds.
     */
    private long ttl;
    private long maxTtl;

    public ParticleInfo(Vector3f speed, long ttl) {
        this.speed.set(speed);
        this.ttl = ttl;
        this.maxTtl = ttl;
    }

    public void setSpeed(Vector3f speed) {
        this.speed = speed;
    }

    public Vector3f getSpeed() {
        return speed;
    }

    public void setTtl(long ttl) {
        this.ttl = ttl;
    }

    public long updateTtl(long elapsedTime) {
        this.ttl -= elapsedTime;
        return this.ttl;
    }

    public long geTtl() {
        return ttl;
    }

    public void setScale(float scale) {
        this.scale.set(scale);
    }

    public void setScale(Vector3f scale) {
        this.scale.set(scale);
    }

    public Vector3f getScale() {
        return scale;
    }

    public float getAlpha() {
        return (float) ttl / maxTtl;
    }

    public ParticleInfo copy() {
        ParticleInfo particleInfo = new ParticleInfo(speed, ttl);
        particleInfo.setScale(scale);
        particleInfo.setPosition(position);
        particleInfo.setRotation(rotation);
        particleInfo.maxTtl = maxTtl;
        return particleInfo;
    }

    public long getMaxTtl() {
        return maxTtl;
    }

    public void setMaxTtl(long ttl) {
        maxTtl = ttl;
    }
}
