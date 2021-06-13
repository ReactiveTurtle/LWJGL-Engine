package ru.reactiveturtle.game.player;

import org.joml.Quaternionf;
import org.joml.Vector3f;
import ru.reactiveturtle.engine.base.Stage;
import ru.reactiveturtle.engine.base3d.Stage3D;
import ru.reactiveturtle.engine.base.Transform3D;
import ru.reactiveturtle.engine.module.moving.Movable;
import ru.reactiveturtle.engine.shadow.ShadowRenderable;
import ru.reactiveturtle.game.Helper;
import ru.reactiveturtle.game.MainGame;
import ru.reactiveturtle.game.base.Entity;
import ru.reactiveturtle.game.player.inventory.Inventory;
import ru.reactiveturtle.game.player.inventory.InventoryItem;
import ru.reactiveturtle.game.types.Builder;
import ru.reactiveturtle.game.types.Destroyer;
import ru.reactiveturtle.game.world.BoxBodyModel;
import ru.reactiveturtle.physics.BoxBody;
import ru.reactiveturtle.physics.RigidBody;

import java.util.Objects;

public class Player extends Transform3D implements Movable, ShadowRenderable {
    private boolean isLockYMove = false;
    private boolean isLockTopBottomRotation = false;

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

    private InventoryItem mRightHandItem;
    private Inventory mInventory;
    private Entity mObservable;

    private Needs mNeeds;

    private ActionListener mActionListener;

    private Vector3f cameraPosition = new Vector3f();

    private MainGame gameContext;

    public Player(MainGame gameContext) {
        Objects.requireNonNull(gameContext);
        this.gameContext = gameContext;

        setMovement(Movement.WALK);
        mUI = new Interface(this.gameContext.getStage().getUIContext());
        mInventory = new Inventory();
        mUI.updateInventoryImage(mInventory);
        mNeeds = new Needs();
        initRigidBody();

        this.gameContext.setCursorCallback(bias -> {
            float biasX = bias.y;
            float biasY = bias.x;
            addRotation((float) Math.toRadians(biasX), (float) Math.toRadians(biasY), 0);
        });

        this.gameContext.getStage().setMouseCallback(new Stage.MouseCallback() {
            @Override
            public void onScroll(int direction) {
                super.onScroll(direction);
                wheelInventory(-direction);
            }
        });
    }

    @Override
    public void addRotation(float radianX, float radianY, float radianZ) {
        super.addRotation(radianX, radianY, radianZ);
        if (isLockTopBottomRotation) {
            if (Math.abs(getRotationX()) > Math.PI / 2) {
                setRotationX((float) (Math.PI / 2 * Math.signum(getRotationX())));
            }
        }
        if (mRightHandItem != null) {
            Vector3f vector3f = new Vector3f();
            Quaternionf quaternionf = new Quaternionf();
            quaternionf.rotateYXZ((float) (Math.PI - getRotationY()),
                    getRotationX(), 0);
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
                    vector3f.x,
                    vector3f.y,
                    vector3f.z);
        }
        if (mIsShaking) {
            Vector3f roll = new Vector3f(0, 0.1f, 0);
            roll.rotateZ(mShakingAngle);
            Vector3f pitch = new Vector3f(roll.x, 0f, 0);
            pitch.rotateY(-getRotationY());
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
        if (mRightHandItem != null) {
            mRightHandToolPosition.set(getCameraPosition());
            float x = 0.15f * 2, y = -0.2f * 2 - cameraPosition.y / 4, z = -0.25f * 2;
            float b = z;
            b *= (float) Math.cos(rotation.x);
            mRightHandToolPosition.add((float) Math.sin(rotation.y) * -1.0f * b,
                    (float) Math.sin(rotation.x) * z,
                    (float) Math.cos(rotation.y) * b);
            mRightHandToolPosition.add((float) Math.sin(rotation.y - Math.PI / 2) * -1.0f * x,
                    0, (float) Math.cos(rotation.y - Math.PI / 2) * x);
            b = (float) Math.cos(rotation.x + Math.PI / 2) * y;
            mRightHandToolPosition.add((float) Math.sin(rotation.y) * -1.0f * b,
                    (float) Math.sin(rotation.x + Math.PI / 2) * y,
                    (float) Math.cos(rotation.y) * b);
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


    public void setObservableObject(Entity observable) {
        mObservable = observable;
    }

    public Entity getObservableEntity() {
        return mObservable;
    }

    public boolean takeObservable() {
        Entity item = mObservable;
        if (item == null) {
            return false;
        }
        setObservableObject(null);

        int emptyPosition = -1;
        int equalEntityPosition = -1;
        for (int i = 0; i < mInventory.getInventorySize() && (emptyPosition == -1 || equalEntityPosition == -1); i++) {
            if (emptyPosition == -1) {
                if (mInventory.isCellEmpty(i)) {
                    emptyPosition = i;
                }
            }
            if (equalEntityPosition == -1) {
                if (!mInventory.isCellEmpty(i) &&
                        mInventory.getItem(i).getEntityTag().equals(item.getTag())) {
                    equalEntityPosition = i;
                }
            }
        }

        int position = equalEntityPosition == -1 ? emptyPosition : equalEntityPosition;
        if (position == -1) {
            return false;
        }

        if (position < mInventory.getInventorySize()) {
            mInventory.addItem(position, item);
        }
        mRightHandItem = mInventory.getCurrentItem();
        mUI.updateInventoryImage(mInventory);
        return position < mInventory.getInventorySize();
    }

    public boolean updateNeeds(float calories, float water) {
        return mNeeds.addHunger(calories);
    }

    public Entity throwCurrentInventoryItem() {
        InventoryItem item = mInventory.getCurrentItem();
        if (item == null) {
            return null;
        }

        mInventory.removeItem(mInventory.getCurrentItemPosition());
        mRightHandItem = mInventory.getCurrentItem();
        mUI.updateInventoryImage(mInventory);

        Quaternionf quaternionf = new Quaternionf();
        quaternionf.rotateYXZ(
                getRotationY(),
                getRotationX(),
                getRotationZ());

        Vector3f vector3f = new Vector3f(0, 0, 2);
        vector3f.rotate(quaternionf);

        Entity entity = item.takeEntity(gameContext);
        entity.setPosition(getPosition().add(vector3f));
        return entity;
    }

    public void wheelInventory(double dy) {
        mIsHit = false;
        mInventory.wheelInventory(dy);
        mRightHandItem = mInventory.getCurrentItem();
        mUI.updateInventoryImage(mInventory);
    }

    public InventoryItem getRightHandItem() {
        return mRightHandItem;
    }

    public boolean isRightHandEmpty() {
        return mRightHandItem == null;
    }

    public Interface getUI() {
        return mUI;
    }

    @Override
    public void renderShadow(Stage3D stage) {
        if (mRightHandItem != null) {
            mRightHandItem.renderShadow(stage);
        }
    }

    public void render(Stage3D stage, double deltaTime) {
        mNeeds.update(deltaTime, getMovement());
        mUI.updateNeedsImage(mNeeds);
        if (mIsShaking) {
            calcShaking(deltaTime);
        }
        if (mIsHit) {
            calcHit(deltaTime);
        }
        if (mRightHandItem != null) {
            InventoryItem tool = mRightHandItem;
            tool.render(stage);
        }

        /*BoxBodyModel boxBodyModel = Helper.bodyToModel(rigidBody);
        boxBodyModel.setShader(gameContext.getShaderLoader().getModelShader());
        boxBodyModel.render(stage);*/
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
        if (mRightHandItem != null &&
                (mRightHandItem instanceof Destroyer || mRightHandItem instanceof Builder)) {
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
        rigidBody.setUpHeight(1f);
    }

    private BoxBody rigidBody;

    public RigidBody getRigidBody() {
        return rigidBody;
    }

    private void initRigidBody() {
        rigidBody = new BoxBody(0.5f, 1.85f, 0.5f);
        rigidBody.setCenter(new Vector3f(0, -0.925f, 0));
        rigidBody.setY(10f);
        rigidBody.setZ(1);
        rigidBody.tag = "player";
        rigidBody.setType(RigidBody.Type.DYNAMIC);
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
