package ru.reactiveturtle.physics;

import org.joml.Vector3f;

public abstract class RigidBody extends Transform3D implements Cloneable {
    public String log;
    protected Vector3f translation = new Vector3f();

    float flyTime = 0;
    float startYVelocity = 0;
    float startFlyY = 0;
    float lastY = 0;

    private Integer id = null;
    public String tag = "body";

    protected Type type = Type.STATIC;

    public void translate(Vector3f translation) {
        this.translation.add(translation);
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public abstract CollisionResult isCollide(RigidBody rigidBody, int testCount);

    public void setId(int id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setUpHeight(float height) {
        startYVelocity = (float) Math.sqrt(2 * 9.8f * height);
        flyTime = 0;
        startFlyY = getY();
        lastY = getY();
    }

    public RigidBody copy() {
        try {
            RigidBody rigidBody = (RigidBody) clone();
            rigidBody.position = new Vector3f(rigidBody.position);
            rigidBody.rotation = new Vector3f(rigidBody.rotation);
            rigidBody.translation = new Vector3f(rigidBody.translation);
            return rigidBody;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public enum Type {
        STATIC, DYNAMIC, EMPTY
    }
}
