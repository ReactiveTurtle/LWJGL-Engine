package ru.reactiveturtle.engine.particle;

import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FlowParticle {
    private int maxParticles;

    private boolean active;

    protected final List<ParticleInfo> particleInfos;

    public final ParticleInfo defaultParticleInfo;
    protected final Particle baseParticle;

    private long creationPeriodMillis;

    private long lastCreationTime;

    private float speedRndRange;

    private float positionRndRange;

    private float scaleRndRange;

    public FlowParticle(Particle baseParticle, int maxParticles, long creationPeriodMillis) {
        particleInfos = new ArrayList<>();
        defaultParticleInfo = baseParticle.getParticleInfo().copy();
        this.baseParticle = baseParticle;
        this.maxParticles = maxParticles;
        this.active = false;
        this.lastCreationTime = 0;
        this.creationPeriodMillis = creationPeriodMillis;
    }

    public Particle getBaseParticle() {
        return baseParticle;
    }

    public long getCreationPeriodMillis() {
        return creationPeriodMillis;
    }

    public int getMaxParticles() {
        return maxParticles;
    }

    public List<ParticleInfo> getPositions() {
        return particleInfos;
    }

    public float getPositionRndRange() {
        return positionRndRange;
    }

    public float getScaleRndRange() {
        return scaleRndRange;
    }

    public float getSpeedRndRange() {
        return speedRndRange;
    }

    public void setCreationPeriodMillis(long creationPeriodMillis) {
        this.creationPeriodMillis = creationPeriodMillis;
    }

    public void setMaxParticles(int maxParticles) {
        this.maxParticles = maxParticles;
    }

    public void setPositionRndRange(float positionRndRange) {
        this.positionRndRange = positionRndRange;
    }

    public void setScaleRndRange(float scaleRndRange) {
        this.scaleRndRange = scaleRndRange;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setSpeedRndRange(float speedRndRange) {
        this.speedRndRange = speedRndRange;
    }

    public void update(long ellapsedTime) {
        long now = System.currentTimeMillis();
        if (lastCreationTime == 0) {
            lastCreationTime = now;
        }
        Iterator<? extends ParticleInfo> it = particleInfos.iterator();
        while (it.hasNext()) {
            ParticleInfo particleInfo = it.next();
            if (particleInfo.updateTtl(ellapsedTime) < 0) {
                it.remove();
            } else {
                updatePosition(particleInfo, ellapsedTime);
            }
        }

        int length = this.getPositions().size();
        if (now - lastCreationTime >= this.creationPeriodMillis && length < maxParticles) {
            createParticle();
            this.lastCreationTime = now;
        }
    }

    private void createParticle() {
        ParticleInfo particleInfo = defaultParticleInfo.copy();
        particleInfo.setRotationX(45);
        float sign = Math.random() > 0.5d ? -1.0f : 1.0f;
        float speedInc = sign * (float) Math.random() * this.speedRndRange;
        float posInc = sign * (float) Math.random() * this.positionRndRange;
        float scaleInc = sign * (float) Math.random() * this.scaleRndRange;
        particleInfo.getPosition().add(posInc, posInc, posInc);
        particleInfo.getSpeed().add(speedInc, speedInc, speedInc);
        particleInfo.setScale(particleInfo.getScale().add(scaleInc, scaleInc, scaleInc));
        particleInfos.add(particleInfo);
    }

    /**
     * Updates a particle position
     *
     * @param particleInfo The particle to update
     * @param elapsedTime  Elapsed time in milliseconds
     */
    public void updatePosition(ParticleInfo particleInfo, long elapsedTime) {
        Vector3f speed = particleInfo.getSpeed();
        float delta = elapsedTime / 1000.0f;
        float dx = speed.x * delta;
        float dy = speed.y * delta;
        float dz = speed.z * delta;
        Vector3f pos = particleInfo.getPosition();
        particleInfo.setPosition(pos.x + dx, pos.y + dy, pos.z + dz);
    }
}
