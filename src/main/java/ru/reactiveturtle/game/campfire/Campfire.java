package ru.reactiveturtle.game.campfire;

import org.joml.Vector3f;
import ru.reactiveturtle.engine.base3d.Stage3D;
import ru.reactiveturtle.engine.light.PointLight;
import ru.reactiveturtle.engine.model.Model;
import ru.reactiveturtle.engine.model.loader.ObjLoader;
import ru.reactiveturtle.engine.particle.ParticleShader;
import ru.reactiveturtle.engine.shader.TextureShader;
import ru.reactiveturtle.game.base.Entity;
import ru.reactiveturtle.game.base.EntityState;
import ru.reactiveturtle.game.types.Collectable;
import ru.reactiveturtle.game.types.Combustible;
import ru.reactiveturtle.game.types.Firebox;
import ru.reactiveturtle.game.world.DayNight;

import java.io.IOException;

public class Campfire extends Entity implements Firebox {
    private Model mStone, mFirewood;
    private Flame mFlame;
    private PointLight mPointLight;
    private Vector3f mDiffuse;

    public Campfire(int id, String name, TextureShader textureShader, ParticleShader particleShader) {
        super(id, name);
        try {
            mStone = ObjLoader.load("object/campfire/campfire_stone");
            mStone.setShader(textureShader);
            mFirewood = ObjLoader.load("object/campfire/firewood");
            mFirewood.setShader(textureShader);
            mFirewood.setScale(1.25f);

            mFlame = new Flame(particleShader);

            mPointLight = new PointLight();
            mDiffuse = new Vector3f(0.6f, 0.6f, 0.4f);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void renderShadow(Stage3D stage) {
        mStone.renderShadow(stage);
        mFirewood.renderShadow(stage);
    }

    @Override
    public void render(Stage3D stage) {
        mStone.render(stage);
        mFirewood.render(stage);
        if (mFlame.isActive()) {
            if (mFlame.mBurningTime <= 0) {
                mFlame.setActive(false);
                mFlame.mBurningTime = 0;
            } else if (mFlame.mBurningTime > 0) {
                double deltaTime = stage.getGameContext().getDeltaTime();
                mFlame.mBurningTime -= deltaTime * DayNight.secondsPerRealSecond;
                float realSeconds = mFlame.mBurningTime / DayNight.secondsPerRealSecond;
                float k = (float) Math.sqrt((Math.min(realSeconds, Flame.limit)) / Flame.limit);
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

    @Override
    protected EntityState[] getDefaultEntityStates() {
        return new EntityState[0];
    }

    @Override
    public int getNextState() {
        return 0;
    }
}
