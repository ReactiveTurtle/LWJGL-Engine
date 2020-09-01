package ru.reactiveturtle.game.player;

import org.joml.Quaternionf;
import org.joml.Vector3f;
import ru.reactiveturtle.engine.base.Transform3D;
import ru.reactiveturtle.engine.shadow.Shadow;
import ru.reactiveturtle.game.types.Builder;
import ru.reactiveturtle.game.types.Collectable;
import ru.reactiveturtle.game.types.Destroyer;
import ru.reactiveturtle.physics.RigidBody;

public class Player extends Transform3D implements Shadow {
    private boolean isLockYMove = false;
    private boolean isLockTopBottomRotation = false;

    private Collectable mRightHandTool;
    private Vector3f mRightHandToolPosition = new Vector3f(), mRightHandToolRotation = new Vector3f();

    private boolean mIsRightToolPitch = false;

    private boolean mIsShaking = false;
    private boolean mIsShakingStop = false;
    private float mShakingMaxAngle = (float) (Math.PI / 2);
    private float mShakingSpeedAngle = (float) (Math.PI * 2);
    private float mShakingAngle = 0f;

    private boolean mIsHit = false;
    private float mHitMaxAngle = (float) (Math.PI / 3f);
    private float mHitSpeedAngle = (float) (Math.PI * 1.6);
    private float mHitAngle = 0;

    private Movement mMovement = Movement.WALK;

    private Interface mUI;

    private Inventory mInventory;
    private Integer mObservableId;

    private Needs mNeeds;

    private ActionListener mActionListener;

    private Vector3f cameraPosition = new Vector3f();

    public Player() {
        setMovement(Movement.WALK);
        mUI = new Interface();
        mInventory = new Inventory();
        mUI.updateInventoryImage(mInventory);
        mNeeds = new Needs();
    }

    @Override
    public void addRotation(float degreesX, float degreesY, float degreesZ) {
        super.addRotation(degreesX, degreesY, degreesZ);
        if (isLockTopBottomRotation) {
            if (Math.abs(getRotationX()) > 90) {
                setRotationX(90 * Math.signum(getRotationX()));
            }
        }
        if (mRightHandTool != null) {
            Vector3f vector3f = new Vector3f();
            Quaternionf quaternionf = new Quaternionf();
            quaternionf.rotateYXZ((float) Math.toRadians(180 - getRotationY()),
                    (float) Math.toRadians(getRotationX()), 0);
            if (mIsShaking) {
                quaternionf.rotateX((mIsRightToolPitch ? mShakingMaxAngle / 2 : Math.abs(mShakingAngle / 2)) + mShakingAngle / 9);
            }
            if (mIsHit) {
                quaternionf.rotateY(mHitAngle / 2);
                quaternionf.rotateX(mHitAngle);
                quaternionf.rotateY(mHitAngle / 2);
            }
            quaternionf.getEulerAnglesXYZ(vector3f);
            mRightHandToolRotation.set(
                    (float) Math.toDegrees(vector3f.x),
                    (float) Math.toDegrees(vector3f.y),
                    (float) Math.toDegrees(vector3f.z));
        }
        if (mIsShaking) {
            Vector3f roll = new Vector3f(0, 0.1f, 0);
            roll.rotateZ(mShakingAngle);
            Vector3f pitch = new Vector3f(roll.x, 0f, 0);
            pitch.rotateY((float) Math.toRadians(-getRotationY()));
            cameraPosition.set(pitch.x, roll.y - 0.1, pitch.z);
        } else {
            cameraPosition.set(0, 0, 0);
        }
    }

    public void addRotation(Vector3f rotation) {
        addRotation(rotation.x, rotation.y, rotation.z);
    }

    public Vector3f getCameraPosition() {
        return new Vector3f(getPosition()).add(cameraPosition);
    }

    @Override
    public void setPosition(Vector3f position) {
        super.setPosition(position);
        if (mRightHandTool != null) {
            mRightHandToolPosition.set(getCameraPosition());
            float x = 0.15f * 2, y = -0.2f * 2 - cameraPosition.y / 4, z = -0.25f * 2;
            float b = z;
            b *= (float) Math.cos(Math.toRadians(rotation.x));
            mRightHandToolPosition.add((float) Math.sin(Math.toRadians(rotation.y)) * -1.0f * b,
                    (float) Math.sin(Math.toRadians(rotation.x)) * z,
                    (float) Math.cos(Math.toRadians(rotation.y)) * b);
            mRightHandToolPosition.add((float) Math.sin(Math.toRadians(rotation.y - 90)) * -1.0f * x,
                    0, (float) Math.cos(Math.toRadians(rotation.y - 90)) * x);
            b = (float) Math.cos(Math.toRadians(rotation.x + 90)) * y;
            mRightHandToolPosition.add((float) Math.sin(Math.toRadians(rotation.y)) * -1.0f * b,
                    (float) Math.sin(Math.toRadians(rotation.x + 90)) * y,
                    (float) Math.cos(Math.toRadians(rotation.y)) * b);
        }
    }

    public void setLockYMove(boolean lockYMove) {
        isLockYMove = lockYMove;
    }

    public void setLockTopBottomRotation(boolean lockTopBottomRotation) {
        isLockTopBottomRotation = lockTopBottomRotation;
    }

    public boolean isLockYMove() {
        return isLockYMove;
    }


    public void setObservableObject(Integer observableId) {
        mObservableId = observableId;
    }

    public Integer getObservableId() {
        return mObservableId;
    }

    public boolean takeInventoryItem(Collectable item) {
        int position = mInventory.getCurrentItemPosition();
        while (position < mInventory.getInventorySize() && mInventory.getItem(position) != null &&
                !((GameObject)mInventory.getItem(position)).name.equals(((GameObject) item).name)){
            position++;
        }
        if (position < mInventory.getInventorySize()) {
            mInventory.addItem(position, item);
        }
        mRightHandTool = mInventory.getCurrentItem();
        mUI.updateInventoryImage(mInventory);
        return position < mInventory.getInventorySize();
    }

    public boolean updateNeeds(float calories, float water) {
        return mNeeds.addHunger(calories);
    }

    public Collectable throwIntentoryItem() {
        Collectable item = mInventory.getCurrentItem();
        removeCurrentInventoryItem();
        return item;
    }

    public void removeCurrentInventoryItem() {
        mInventory.removeItem(mInventory.getCurrentItemPosition());
        mRightHandTool = mInventory.getCurrentItem();
        mUI.updateInventoryImage(mInventory);
    }

    public boolean isCurrentInventoryItemEmpty() {
        return mInventory.getCurrentItem() == null;
    }

    public void wheelInventory(double dy) {
        mIsHit = false;
        mInventory.wheelInventory(dy);
        mRightHandTool = mInventory.getCurrentItem();
        mUI.updateInventoryImage(mInventory);
    }

    public Collectable getRightHandTool() {
        return mRightHandTool;
    }

    public boolean isRightHandEmpty() {
        return mRightHandTool == null;
    }

    public void renderUI(double deltaTime) {
        mUI.render(deltaTime);
    }

    public Interface getUI() {
        return mUI;
    }

    @Override
    public void renderShadow() {
        if (mRightHandTool != null) {
            ((GameObject) mRightHandTool).getModel().renderShadow();
        }
    }

    public void render(double deltaTime) {
        mNeeds.update(deltaTime, getMovement());
        mUI.updateNeedsImage(mNeeds);
        if (mIsShaking) {
            calcShaking(deltaTime);
        }
        if (mIsHit) {
            calcHit(deltaTime);
        }
        if (mRightHandTool != null) {
            GameObject tool = (GameObject) mRightHandTool;
            tool.getModel().setPosition(mRightHandToolPosition);
            tool.getModel().setRotation(mRightHandToolRotation);
            tool.getModel().render();
        }
    }

    private void calcShaking(double deltaTime) {
        float last = mShakingAngle;
        mShakingAngle += mShakingSpeedAngle * deltaTime;
        if (Math.abs(mShakingAngle) >= mShakingMaxAngle) {
            if (!mIsRightToolPitch) {
                mIsRightToolPitch = true;
            }
            int mark = (int) Math.signum(mShakingSpeedAngle);
            mShakingAngle = mShakingMaxAngle * mark - mark * Math.abs(mShakingAngle) % mShakingMaxAngle;
            mShakingSpeedAngle = -mShakingSpeedAngle;
        }
        if (mIsShakingStop && Math.signum(last) != Math.signum(mShakingAngle)) {
            mIsShaking = false;
            mIsShakingStop = false;
            mIsRightToolPitch = false;
            mShakingAngle = 0;
            cameraPosition.set(0);
        }
    }

    private void calcHit(double deltaTime) {
        mHitAngle -= mHitSpeedAngle * deltaTime;
        if (mHitAngle < 0) {
            mIsHit = false;
            if (mActionListener != null) {
                mActionListener.onHitEnd();
            }
        }
    }

    public void startShaking() {
        mIsShakingStop = false;
        if (!mIsShaking) {
            mIsShaking = true;
        }
    }

    public void stopShaking() {
        mIsShakingStop = true;
    }

    public void hit() {
        if (mRightHandTool != null &&
                (mRightHandTool instanceof Destroyer || mRightHandTool instanceof Builder)) {
            if (!mIsHit) {
                mHitAngle = mHitMaxAngle;
                mIsHit = true;
            }
        }
    }

    public boolean isHit() {
        return mIsHit;
    }

    public void setActionListener(ActionListener actionListener) {
        mActionListener = actionListener;
    }

    public boolean isJump() {
        return false;
    }

    public void jump() {
        rigidBody.setUpHeight(2);
    }

    private RigidBody rigidBody;
    public void setRigidBody(RigidBody rigidBody) {
        this.rigidBody = rigidBody;
    }

    public RigidBody getRigidBody() {
        return rigidBody;
    }

    public interface ActionListener {
        void onHitEnd();

        void onJumpEnd();
    }

    public void setMovement(Movement movement) {
        this.mMovement = movement;
    }

    public Movement getMovement() {
        return mMovement;
    }

    public float getMovementSpeed() {
        return mMovement.getSpeed();
    }

    public enum Movement {
        RUN(10f), WALK(4f), CROUCH(0.5f);

        private float speed;

        Movement(float speed) {
            this.speed = speed;
        }

        public float getSpeed() {
            return speed;
        }
    }
}
