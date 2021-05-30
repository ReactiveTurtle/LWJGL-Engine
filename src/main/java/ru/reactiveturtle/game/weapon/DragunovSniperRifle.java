package ru.reactiveturtle.game.weapon;

import org.joml.Vector3f;
import ru.reactiveturtle.engine.base.Shader;
import ru.reactiveturtle.game.MainGame;
import ru.reactiveturtle.game.base.EntityPhase;
import ru.reactiveturtle.game.base.ModelLoader;
import ru.reactiveturtle.game.types.Collectable;
import ru.reactiveturtle.physics.BoxBody;
import ru.reactiveturtle.physics.RigidBody;

public class DragunovSniperRifle extends Weapon {
    public DragunovSniperRifle(MainGame gameContext) {
        super(gameContext, "Dragunov Sniper Rifle", getDefaultWeaponData());
        setRotationX((float) (Math.PI / 2));
    }

    protected EntityPhase[] getDefaultEntityPhases(ModelLoader modelLoader) {
        BoxBody boxBody = new BoxBody(new Vector3f(2.25f, 0.8f, 0.15f));
        boxBody.setCenter(new Vector3f(-0.6f, 0f, 0));
        boxBody.setType(RigidBody.Type.DYNAMIC);
        boxBody.setY(6f);
        return new EntityPhase[]{
                new EntityPhase(this, modelLoader.getDragunovSniperRiffle(), boxBody)
        };
    }

    @Override
    protected Shader initShader() {
        return gameContext.getShaderLoader().getTextureShader();
    }

    private static WeaponData getDefaultWeaponData() {
        return new WeaponData(
                830f,
                10,
                10);
    }

    @Override
    public Bullet shot() {
        return null;
    }

    @Override
    protected int getNextState() {
        return 0;
    }

    @Override
    public Collectable take() {
        return this;
    }

    private int count;

    @Override
    public void setCount(int count) {
        if (count < 1) throw new IllegalArgumentException();
        this.count = count;
    }

    @Override
    public int getCount() {
        return 0;
    }
}
