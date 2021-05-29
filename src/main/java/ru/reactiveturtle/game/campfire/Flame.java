package ru.reactiveturtle.game.campfire;

import org.joml.Vector3f;
import ru.reactiveturtle.engine.base3d.Stage3D;
import ru.reactiveturtle.engine.texture.Texture;
import ru.reactiveturtle.engine.particle.FlowParticle;
import ru.reactiveturtle.engine.particle.Particle;
import ru.reactiveturtle.engine.particle.ParticleInfo;
import ru.reactiveturtle.engine.particle.ParticleShader;

public class Flame extends FlowParticle {
    public float mBurningTime = 60f;
    public static final float limit = 60f * 3;

    public Flame(ParticleShader particleShader) {
        super(new Particle(
                1f, 1f,
                new Texture("texture/fire.png"),
                1f, 1f,
                new Vector3f(0, 2.5f, 0), 1000), 200, 50);
        float range = 0.1f;
        float scale = 0.4f;
        baseParticle.setShader(particleShader);
        defaultParticleInfo.setScale(scale);
        setActive(true);

        setPositionRndRange(range);
        setSpeedRndRange(range);
    }

    public float getLastY() {
        if (particleInfos.size() > 0) {
            return particleInfos.get(0).getY();
        }
        return 0;
    }

    public float getBurningTime() {
        return mBurningTime;
    }

    public void render(Stage3D stage, float cameraRotationY) {
        for (ParticleInfo particleInfo : getPositions()) {
            particleInfo.setRotationY(cameraRotationY);
            baseParticle.setParticleInfo(particleInfo);
            baseParticle.getParticleInfo().setScale(particleInfo.getAlpha() / 2f);
            baseParticle.draw(stage, particleInfo.getAlpha());
        }
    }

    public void setPosition(Vector3f position) {
        defaultParticleInfo.setPosition(position);
    }
}
