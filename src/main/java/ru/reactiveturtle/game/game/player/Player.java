package ru.reactiveturtle.game.game.player;

import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector3f;
import ru.reactiveturtle.game.engine.base.Transform3D;
import ru.reactiveturtle.game.engine.model.Model;
import ru.reactiveturtle.game.engine.model.loader.ObjLoader;
import ru.reactiveturtle.game.engine.shader.TextureShader;

import java.io.IOException;

public class Player extends Transform3D {
    private boolean isLockYMove = false;
    private boolean isLockTopBottomRotation = false;

    private Model mRightHandTool;

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

    private boolean mIsJump = false;
    private float mJumpMaxHeight = 1.5f;
    private float mG = 10f;
    private float mJumpTime = 0;
    private float mFallTime = (float) Math.sqrt(2 * mJumpMaxHeight / mG);
    private float mJumpStartY;
    private boolean mIsFalling = false;

    private Movement mMovement = Movement.WALK;

    private Interface mUI;
    private Inventory mInventory;

    private ActionListener mActionListener;

    private Vector3f cameraPosition = new Vector3f();

    public Player(TextureShader shader, String rightHandTool) {
        try {
            mRightHandTool = ObjLoader.load(rightHandTool);
            mRightHandTool.setShader(shader);
            mRightHandTool.setScale(0.2f);
            setMovement(Player.Movement.WALK);
            mUI = new Interface();
            mInventory = new Inventory();
            mUI.setSelectedInventoryItem(mInventory.getSelectedInventory());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addRotation(float degreesX, float degreesY, float degreesZ) {
        super.addRotation(degreesX, degreesY, degreesZ);
        if (isLockTopBottomRotation) {
            if (Math.abs(getRotationX()) > 90) {
                setRotationX(90 * Math.signum(getRotationX()));
            }
        }
        if (mInventory.getSelectedInventory() == 0) {
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
            mRightHandTool.setRotationX((float) Math.toDegrees(vector3f.x));
            mRightHandTool.setRotationY((float) Math.toDegrees(vector3f.y));
            mRightHandTool.setRotationZ((float) Math.toDegrees(vector3f.z));
        }
        if (mIsShaking) {
            Vector3f roll = new Vector3f(0, 0.1f, 0);
            roll.rotateZ(mShakingAngle);
            Vector3f pitch = new Vector3f(roll.x, 0f, 0);
            pitch.rotateY((float) Math.toRadians(-getRotationY()));
            cameraPosition.set(pitch.x, roll.y - 0.1, pitch.z);
        } else {
            cameraPosition.set(0);
        }
    }

    public void addRotation(Vector3f rotation) {
        addRotation(rotation.x, rotation.y, rotation.z);
    }

    public Vector3f getCameraPosition() {
        return new Vector3f(getPosition()).add(cameraPosition);
    }

    public void addPosition(Vector3f translation, double deltaTime) {
        if (translation.z != 0) {
            float b2 = translation.z;
            if (!isLockYMove) {
                this.position.y += (float) Math.sin(Math.toRadians(rotation.x)) * translation.z;
                b2 *= (float) Math.cos(Math.toRadians(rotation.x));
            }
            this.position.x += (float) Math.sin(Math.toRadians(getRotationY())) * -1.0f * b2;
            this.position.z += (float) Math.cos(Math.toRadians(getRotationY())) * b2;
        }
        if (translation.x != 0) {
            this.position.x += (float) Math.sin(Math.toRadians(getRotationY() - 90)) * -1.0f * translation.x;
            this.position.z += (float) Math.cos(Math.toRadians(getRotationY() - 90)) * translation.x;
        }

        if (mInventory.getSelectedInventory() == 0) {
            mRightHandTool.setPosition(getCameraPosition());
            float x = 0.15f * 2, y = -0.2f * 2 - cameraPosition.y / 4, z = -0.25f * 2;
            float b = z;
            b *= (float) Math.cos(Math.toRadians(rotation.x));
            mRightHandTool.addPosition((float) Math.sin(Math.toRadians(rotation.y)) * -1.0f * b,
                    (float) Math.sin(Math.toRadians(rotation.x)) * z,
                    (float) Math.cos(Math.toRadians(rotation.y)) * b);
            mRightHandTool.addPosition((float) Math.sin(Math.toRadians(rotation.y - 90)) * -1.0f * x,
                    0, (float) Math.cos(Math.toRadians(rotation.y - 90)) * x);
            b = (float) Math.cos(Math.toRadians(rotation.x + 90)) * y;
            mRightHandTool.addPosition((float) Math.sin(Math.toRadians(rotation.y)) * -1.0f * b,
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

    public void takeInventoryItem(Model model) {
        
    }

    public void wheelInventory(double dy) {
        mInventory.wheelInventory(dy);
        mUI.setSelectedInventoryItem(mInventory.getSelectedInventory());
    }

    public void renderUI(double deltaTime) {
        mUI.render(deltaTime);
    }

    public Interface getUI() {
        return mUI;
    }

    public void renderShadow() {
        mRightHandTool.renderShadow();
    }

    public void render(double deltaTime) {
        if (mIsShaking) {
            calcShaking(deltaTime);
        }
        if (mIsHit) {
            calcHit(deltaTime);
        }
        if (mIsJump) {
            calcJump(deltaTime);
        }
        mRightHandTool.render();
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

    private void calcJump(double deltaTime) {
        if (!mIsFalling) {
            mJumpTime -= deltaTime;
        } else {
            mJumpTime += deltaTime;
        }
        if (mJumpTime >= mFallTime) {
            mJumpTime = mFallTime - mJumpTime % mFallTime;
            mIsFalling = true;
        }
        float jumpY = (float) (mJumpMaxHeight - mG * Math.pow(mJumpTime, 2) / 2);
        position.y = mJumpStartY + jumpY;
        if (position.y <= mJumpStartY) {
            mIsJump = false;
            mJumpTime = 0;
            position.y = mJumpStartY;
            mIsFalling = false;
            if (mActionListener != null) {
                mActionListener.onJumpEnd();
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
        if (!mIsHit) {
            mHitAngle = mHitMaxAngle;
            mIsHit = true;
        }
    }

    public void jump() {
        if (!mIsJump) {
            stopShaking();
            mJumpStartY = position.y;
            mJumpTime = mFallTime;
            mIsJump = true;
        }
    }

    public void setActionListener(ActionListener actionListener) {
        mActionListener = actionListener;
    }

    public Vector3f getDirection() {
        Quaternionf quaternionf = new Quaternionf();
        quaternionf.rotateYXZ(
                (float) Math.toRadians(-getRotationY()),
                (float) Math.toRadians(-getRotationX()),
                (float) Math.toRadians(getRotationZ()));
        return new Vector3f(0, 0, -1).rotate(quaternionf);
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
