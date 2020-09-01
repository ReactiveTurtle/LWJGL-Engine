package ru.reactiveturtle.game.campfire;

import org.joml.Vector3f;
import ru.reactiveturtle.engine.base.GameContext;
import ru.reactiveturtle.engine.light.PointLight;
import ru.reactiveturtle.engine.model.Model;
import ru.reactiveturtle.engine.model.loader.ObjLoader;
import ru.reactiveturtle.engine.particle.ParticleShader;
import ru.reactiveturtle.engine.shader.TextureShader;
import ru.reactiveturtle.game.player.GameObject;
import ru.reactiveturtle.game.types.Collectable;
import ru.reactiveturtle.game.types.Combustible;
import ru.reactiveturtle.game.types.Firebox;
import ru.reactiveturtle.game.world.DayNight;

import java.io.IOException;

public class Campfire extends GameObject implements Firebox {
    private Model mStone, mFirewood;
    private Flame mFlame;
    private PointLight mPointLight;
    private Vector3f mDiffuse;

    public Campfire(int id, String name,
                    PointLight pointLight, TextureShader textureShader, ParticleShader particleShader) {
        super(id, name, new Model[0], new Vector3f(), new Vector3f());
        try {
            setSelectBox(1.2f, 0.2f, 1.2f);
            setSelectBoxY(0.1f);
            mStone = ObjLoader.load("object/campfire/campfire_stone");
            mStone.setShader(textureShader);
            mFirewood = ObjLoader.load("object/campfire/firewood");
            mFirewood.setShader(textureShader);
            mFirewood.setScale(1.25f);

            mFlame = new Flame(particleShader);

            mPointLight = pointLight;
            mDiffuse = new Vector3f(0.6f, 0.6f, 0.4f);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void renderShadow() {
        mStone.renderShadow();
        mFirewood.renderShadow();
    }

    @Override
    public void setDefaultPosition(Vector3f defaultPosition) {
        super.setDefaultPosition(defaultPosition);
        if (mStone == null)
            return;
        mStone.setPosition(defaultPosition);
        mFirewood.setPosition(defaultPosition);
        mFlame.getBaseParticle().getParticleInfo().setPosition(defaultPosition);
    }

    @Override
    public void setDefaultRotation(Vector3f defaultRotation) {
        super.setDefaultRotation(defaultRotation);
        if (mStone == null)
            return;
        mStone.setRotation(defaultRotation);
        mFirewood.setRotation(defaultRotation);
        mFlame.getBaseParticle().getParticleInfo().setRotation(defaultRotation);
    }

    @Override
    public void render(Vector3f direction, Vector3f position) {
        render();
    }

    private void render() {
        mStone.render();
        mFirewood.render();
        if (mFlame.isActive()) {
            if (mFlame.mBurningTime <= 0) {
                mFlame.setActive(false);
                mFlame.mBurningTime = 0;
            } else if (mFlame.mBurningTime > 0) {
                double deltaTime = GameContext.getDeltaTime();
                mFlame.mBurningTime -= deltaTime * DayNight.secondsPerRealSecond;
                float realSeconds = mFlame.mBurningTime / DayNight.secondsPerRealSecond;
                float k = (float) Math.sqrt((realSeconds > Flame.limit ?
                        Flame.limit : realSeconds) / Flame.limit);
                long ttl = (long) (k * 1000);
                mFlame.defaultParticleInfo.setTtl(ttl);
                mFlame.defaultParticleInfo.setMaxTtl(ttl);
                mFlame.update((long) (deltaTime * 1000));
                mPointLight.setDiffuse(new Vector3f(mDiffuse).mul(k));
                mPointLight.setY(mFlame.getLastY());
            }
        }
    }

    public Flame getFlame() {
        return mFlame;
    }

    public PointLight getPointLight() {
        return mPointLight;
    }

    @Override
    public boolean put(Collectable combustible) {
        if (combustible instanceof Combustible) {
            mFlame.mBurningTime += ((Combustible) combustible).getBurningTime();
            if (!mFlame.isActive()) {
                mFlame.setActive(true);
            }
            return true;
        }
        return false;
    }

    @Override
    public float getBurningTime() {
        return mFlame.getBurningTime();
    }
}
