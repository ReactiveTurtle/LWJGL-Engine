package ru.reactiveturtle.game.player;

import org.joml.Vector3f;
import ru.reactiveturtle.engine.model.Releasable;
import ru.reactiveturtle.engine.model.Model;
import ru.reactiveturtle.physics.RigidBody;

public abstract class GameObject extends Selectable implements Releasable, Cloneable {
    private int id;
    public String name;
    public Model[] model;
    protected int currentModel = 0;

    public GameObject(int id, String name, Model[] model, Vector3f defaultPosition, Vector3f defaultRotation) {
        super(defaultPosition, defaultRotation);
        this.id = id;
        this.name = name;
        this.model = model;
        this.setDefaultPosition(defaultPosition);
        this.setDefaultRotation(defaultRotation);
    }

    public void renderShadow() {
        model[currentModel].setPosition(getDefaultPosition());
        model[currentModel].setRotation(getDefaultRotation());
        model[currentModel].renderShadow();
    }

    public void render(Vector3f direction, Vector3f position) {
        if (isSelectBoxInFrustum(direction, position)) {
            model[currentModel].setPosition(getDefaultPosition());
            model[currentModel].setRotation(getDefaultRotation());
            model[currentModel].render();
        }
    }

    @Override
    public void release() {

    }

    public int getId() {
        return id;
    }

    public Model getModel() {
        return model[currentModel];
    }

    public GameObject copy(int id) {
        try {
            GameObject gameObject = (GameObject) clone();
            gameObject.id = id;
            gameObject.defaultPosition = new Vector3f(getDefaultPosition());
            gameObject.defaultRotation = new Vector3f(getDefaultRotation());
            if (mBoxParams != null) {
                gameObject.mBoxParams = new Vector3f(mBoxParams.x, mBoxParams.y, mBoxParams.z);
            }
            selectBoxFrustum = new float[24];
            gameObject.updateSelectBox();
            return gameObject;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void updateId(int id) {
        this.id = id;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public RigidBody rigidBody;

    public void setRigidBody(RigidBody rigidBody) {
        this.rigidBody = rigidBody;
    }

    public RigidBody getRigidBody() {
        return rigidBody;
    }
}
