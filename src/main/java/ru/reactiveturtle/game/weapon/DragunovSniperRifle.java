package ru.reactiveturtle.game.weapon;

import org.joml.Vector3f;
import ru.reactiveturtle.engine.material.Texture;
import ru.reactiveturtle.engine.model.Model;
import ru.reactiveturtle.engine.model.loader.ObjLoader;
import ru.reactiveturtle.game.base.EntityState;
import ru.reactiveturtle.game.types.Collectable;
import ru.reactiveturtle.physics.BoxBody;
import ru.reactiveturtle.physics.RigidBody;

import java.io.IOException;

public class DragunovSniperRifle extends Weapon {
    public DragunovSniperRifle(int id) {
        super(getDefaultWeaponData(), id, "Dragunov Sniper Rifle", getDefaultEntityStates());
    }

    private static EntityState[] getDefaultEntityStates() {
        try {
            Model model = ObjLoader.load("object/weapon/DSR", 1, 1);
            model.getMeshes().values().iterator().next().getMaterial()
                    .setTexture(new Texture("object/weapon/DSR.bmp"));
            model.setScale(0.625f);
            //BoxBody boxBody = new BoxBody(new Vector3f(2.25f, 0.8f, 0.15f));
            BoxBody boxBody = new BoxBody(new Vector3f(5f, 5f, 5f));
            boxBody.setCenter(new Vector3f(-0.6f, 0f, 0));
            boxBody.setRotationX((float) (Math.PI / 4));
            boxBody.setType(RigidBody.Type.DYNAMIC);
            boxBody.setY(6f);
            return new EntityState[]{
                    new EntityState(model, boxBody)
            };
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
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
    public int getNextState() {
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
