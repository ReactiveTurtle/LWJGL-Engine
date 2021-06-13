package ru.reactiveturtle.physics;

import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.Arrays;

public class BoxBody extends RigidBody {
    /*
        Cube center in X: 0, Y: 0, Z: 0
     */
    public BoxBody(float width, float height, float depth) {
        initBox(width, height, depth, 0, 0, 0);
    }

    public BoxBody(float width, float height, float depth,
                   float x, float y, float z) {
        initBox(width, height, depth, x, y, z);
    }

    public BoxBody(Vector3f size) {
        initBox(size.x, size.y, size.z, 0, 0, 0);
    }

    public BoxBody(Vector3f size, Vector3f position) {
        initBox(size.x, size.y, size.z, position.x, position.y, position.z);
    }

    protected final float[] boxDefaultPoints = new float[24];

    protected final float[] boxDefaultNormals = new float[]{
            0, 0, 1,
            0, 0, -1,
            1, 0, 0,
            -1, 0, 0,
            0, 1, 0,
            0, -1, 0
    };

    public float[] getBoxDefaultNormals() {
        return boxDefaultNormals;
    }

    protected final float[] boxDefaultNormalPoints = new float[18];

    public float[] getBoxDefaultNormalPoints() {
        return boxDefaultNormalPoints;
    }

    protected float[] boxDefaultNormalRotatedPoints = new float[18];

    private void initBox(float width, float height, float depth,
                         float x, float y, float z) {
        position.set(x, y, z);

        float halfWidth = width / 2;
        float halfHeight = height / 2;
        float halfDepth = depth / 2;
        initDefaultPoints(halfWidth, halfHeight, halfDepth);

        boxDefaultNormalPoints[2] = halfDepth;
        boxDefaultNormalPoints[5] = -halfDepth;
        boxDefaultNormalPoints[6] = halfWidth;
        boxDefaultNormalPoints[9] = -halfWidth;
        boxDefaultNormalPoints[13] = halfHeight;
        boxDefaultNormalPoints[16] = -halfHeight;
        System.arraycopy(boxDefaultNormalPoints, 0, boxDefaultNormalRotatedPoints, 0, 18);

        createBox();
    }

    private void initDefaultPoints(float halfWidth, float halfHeight, float halfDepth) {
        boxDefaultPoints[0] = -halfWidth;
        boxDefaultPoints[1] = -halfHeight;
        boxDefaultPoints[2] = halfDepth;

        boxDefaultPoints[3] = -halfWidth;
        boxDefaultPoints[4] = halfHeight;
        boxDefaultPoints[5] = halfDepth;

        boxDefaultPoints[6] = halfWidth;
        boxDefaultPoints[7] = halfHeight;
        boxDefaultPoints[8] = halfDepth;

        boxDefaultPoints[9] = halfWidth;
        boxDefaultPoints[10] = -halfHeight;
        boxDefaultPoints[11] = halfDepth;

        boxDefaultPoints[12] = -halfWidth;
        boxDefaultPoints[13] = -halfHeight;
        boxDefaultPoints[14] = -halfDepth;

        boxDefaultPoints[15] = -halfWidth;
        boxDefaultPoints[16] = halfHeight;
        boxDefaultPoints[17] = -halfDepth;

        boxDefaultPoints[18] = halfWidth;
        boxDefaultPoints[19] = halfHeight;
        boxDefaultPoints[20] = -halfDepth;

        boxDefaultPoints[21] = halfWidth;
        boxDefaultPoints[22] = -halfHeight;
        boxDefaultPoints[23] = -halfDepth;
    }

    protected float[] boxPoints = new float[24];

    protected float[] boxNormals = new float[18];
    protected float[] boxNormalPoints = new float[18];

    public float[] getBoxPoints() {
        return boxPoints;
    }

    public float[] getBoxNormals() {
        return Arrays.copyOf(boxNormals, boxNormals.length);
    }

    public float[] getBoxNormalPoints() {
        return Arrays.copyOf(boxNormalPoints, boxNormalPoints.length);
    }

    private void createBox() {
        updateBoxRotation();
        updateBoxPosition();
    }

    protected float[] boxDefaultRotatedPoints = new float[24];

    private void updateBoxPosition() {
        for (int i = 0, count = getPointsCount(); i < count; i++) {
            int index = i * 3;
            boxPoints[index] = boxDefaultRotatedPoints[index] + position.x;
            boxPoints[index + 1] = boxDefaultRotatedPoints[index + 1] + position.y;
            boxPoints[index + 2] = boxDefaultRotatedPoints[index + 2] + position.z;
        }
        for (int i = 0; i < 6; i++) {
            int index = i * 3;
            boxNormalPoints[index] = boxDefaultNormalRotatedPoints[index] + position.x;
            boxNormalPoints[index + 1] = boxDefaultNormalRotatedPoints[index + 1] + position.y;
            boxNormalPoints[index + 2] = boxDefaultNormalRotatedPoints[index + 2] + position.z;
        }
    }

    private void updateBoxRotation() {
        Quaternionf quaternionf = new Quaternionf();
        quaternionf.rotateYXZ(0, rotation.x, rotation.z);
        for (int i = 0, count = getPointsCount(); i < count; i++) {
            int index = i * 3;
            Vector3f point = new Vector3f(
                    boxDefaultPoints[index],
                    boxDefaultPoints[index + 1],
                    boxDefaultPoints[index + 2]
            ).sub(center);
            point.rotate(quaternionf);
            boxDefaultRotatedPoints[index] = point.x;
            boxDefaultRotatedPoints[index + 1] = point.y;
            boxDefaultRotatedPoints[index + 2] = point.z;
        }

        for (int i = 0; i < 6; i++) {
            int index = i * 3;
            Vector3f normalPoint = new Vector3f(
                    boxDefaultNormalPoints[index],
                    boxDefaultNormalPoints[index + 1],
                    boxDefaultNormalPoints[index + 2]
            ).sub(center);
            normalPoint.rotate(quaternionf);
            boxDefaultNormalRotatedPoints[index] = normalPoint.x;
            boxDefaultNormalRotatedPoints[index + 1] = normalPoint.y;
            boxDefaultNormalRotatedPoints[index + 2] = normalPoint.z;

            Vector3f normal = new Vector3f(
                    boxDefaultNormals[index],
                    boxDefaultNormals[index + 1],
                    boxDefaultNormals[index + 2]
            );
            normal.rotate(quaternionf);
            boxNormals[index] = normal.x;
            boxNormals[index + 1] = normal.y;
            boxNormals[index + 2] = normal.z;
        }
    }

    private Vector3f center = new Vector3f();

    public void setCenter(Vector3f center) {
        this.center = new Vector3f(center);
        createBox();
    }

    public Vector3f getCenter() {
        return center;
    }

    public int getPointsCount() {
        return 8;
    }

    @Override
    public CollisionResult isCollide(RigidBody rigidBody, int testCount) {
        // В этом методе категорически запрещается изменять положение тела
        // Допускается использование только вектора translation
        if (rigidBody instanceof PlaneBody) {
            PlaneBody planeBody = (PlaneBody) rigidBody;
            boolean isCollide = false;
            Vector3f translationAxis = new Vector3f();
            for (int i = 0; i < testCount + 1 && !isCollide; i++) {
                isCollide = isBoxIntersectsPlane(planeBody.getNormal(),
                        planeBody.getPosition(), translationAxis);
                if (isCollide) {
                    translationAxis = new Vector3f(translation).div(testCount).mul(i - 1);
                } else {
                    translationAxis = new Vector3f(translation).div(testCount).mul(i);
                }
            }
            translation.y = translationAxis.y;
            return new CollisionResult(false, isCollide, false);
        }
        if (rigidBody instanceof TerrainBody) {
            TerrainBody terrainBody = (TerrainBody) rigidBody;
            Float y = terrainBody.getY(position.x, position.z);
            if (y == null) {
                return new CollisionResult(false, false, false);
            }
            float currentY = position.y + center.y;
            boolean isCollide = false;
            float yTranslation = 0;

            for (int i = 0; i < testCount + 1 && !isCollide; i++) {
                isCollide = (currentY + translation.y * i / testCount) < y;
                if (!isCollide) {
                    yTranslation = translation.y * i / testCount;
                }
            }
            if (currentY < y) {
                yTranslation = y - currentY;
                if (startFlyY == 0) {
                    isCollide = false;
                }
            }
            if (isCollide) {
                translation.y = yTranslation;
            }
            return new CollisionResult(false, isCollide, false);
        }
        if (rigidBody instanceof BoxBody) {
            BoxBody boxBody = (BoxBody) rigidBody;
            CollisionResult collisionResult = new CollisionResult();
            Vector3f translation = new Vector3f(this.translation.x, 0, 0);
            int i;
            float translationAxis = 0;
            for (i = 0; i < testCount + 1 && !collisionResult.isXCollide(); i++) {
                boolean result = isBoxIntersectsBox(boxBody, translation, i, testCount);
                collisionResult.setXCollide(result);
                if (collisionResult.isXCollide()) {
                    translationAxis = translation.x / testCount * (i - 1);
                } else {
                    translationAxis = translation.x / testCount * i;
                }
            }
            this.translation.x = translationAxis;

            translation = new Vector3f(0, this.translation.y, 0);
            for (i = 0; i < testCount + 1 && !collisionResult.isYCollide(); i++) {
                boolean result = isBoxIntersectsBox(boxBody, translation, i, testCount);
                collisionResult.setYCollide(result);
                if (collisionResult.isYCollide()) {
                    translationAxis = translation.y / testCount * (i - 1);
                } else {
                    translationAxis = translation.y / testCount * i;
                }
            }
            this.translation.y = translationAxis;

            translation = new Vector3f(0, 0, this.translation.z);
            for (i = 0; i < testCount + 1 && !collisionResult.isZCollide(); i++) {
                boolean result = isBoxIntersectsBox(boxBody, translation, i, testCount);
                collisionResult.setZCollide(result);
                if (collisionResult.isZCollide()) {
                    translationAxis = translation.z / testCount * (i - 1);
                } else {
                    translationAxis = translation.z / testCount * i;
                }
            }
            this.translation.z = translationAxis;
            return collisionResult;
        }
        return new CollisionResult(false, false, false);
    }

    @Override
    public BoxBody copy() {
        BoxBody boxBody = (BoxBody) super.copy();
        if (boxBody != null) {
            boxBody.boxDefaultNormalRotatedPoints = Arrays.copyOf(boxDefaultNormalRotatedPoints, boxDefaultNormalRotatedPoints.length);
            boxBody.boxPoints = Arrays.copyOf(boxPoints, boxPoints.length);
            boxBody.boxNormals = Arrays.copyOf(boxNormals, boxNormals.length);
            boxBody.boxNormalPoints = Arrays.copyOf(boxNormalPoints, boxNormalPoints.length);
            boxBody.boxDefaultRotatedPoints = Arrays.copyOf(boxDefaultRotatedPoints, boxDefaultRotatedPoints.length);
        }
        return boxBody;
    }

    @Override
    public void setPosition(float x, float y, float z) {
        super.setPosition(x, y, z);
        updateBoxPosition();
    }

    @Override
    public void setRotation(float x, float y, float z) {
        super.setRotation(x, y, z);
        createBox();
    }

    protected boolean isBoxIntersectsBox(BoxBody boxBody, Vector3f translation, int iteration, int testCount) {
        Vector3f testTranslation = new Vector3f(translation).div(testCount).mul(iteration);
        boolean isNotIntersects = false;
        for (int i = 0; i < 6 && !isNotIntersects; i++) {
            int index = i * 3;
            Vector3f normal = new Vector3f(boxNormals[index],
                    boxNormals[index + 1],
                    boxNormals[index + 2]);
            Vector3f normalPoint = new Vector3f(boxNormalPoints[index],
                    boxNormalPoints[index + 1],
                    boxNormalPoints[index + 2]);
            normalPoint.add(testTranslation);
            isNotIntersects = isBoxInFrontOfPlane(boxBody, normal, normalPoint, new Vector3f());
        }
        for (int i = 0; i < 6 && !isNotIntersects; i++) {
            int index = i * 3;
            Vector3f normal = new Vector3f(boxBody.boxNormals[index],
                    boxBody.boxNormals[index + 1],
                    boxBody.boxNormals[index + 2]);
            Vector3f normalPoint = new Vector3f(boxBody.boxNormalPoints[index],
                    boxBody.boxNormalPoints[index + 1],
                    boxBody.boxNormalPoints[index + 2]);
            isNotIntersects = isBoxInFrontOfPlane(this, normal, normalPoint, testTranslation);
        }
        return !isNotIntersects;
    }

    private static boolean isBoxInFrontOfPlane(BoxBody boxBody, Vector3f direction, Vector3f position, Vector3f translation) {
        boolean isInFront = true;
        for (int i = 0; i < 8 && isInFront; i++) {
            int index = i * 3;
            Vector3f point = new Vector3f(boxBody.boxPoints[index],
                    boxBody.boxPoints[index + 1],
                    boxBody.boxPoints[index + 2]);
            point.add(translation);
            isInFront = isPointInPlaneFront(direction, position, point);
        }
        return isInFront;
    }

    private boolean isBoxIntersectsPlane(Vector3f direction, Vector3f position, Vector3f translation) {
        float d = -direction.x * position.x - direction.y * position.y - direction.z * position.z;
        boolean isIntersects = false;
        Boolean isPrevFront = null;
        for (int i = 0; i < getPointsCount() && !isIntersects; i++) {
            int index = i * 3;
            boolean isFront = (direction.x * (boxPoints[index] + translation.x) +
                    direction.y * (boxPoints[index + 1] + translation.y) +
                    direction.z * (boxPoints[index + 2] + translation.z) + d) > 0;
            if (isPrevFront != null) {
                isIntersects = isFront != isPrevFront;
            }
            isPrevFront = isFront;
        }
        return isIntersects;
    }

    private static boolean isPointInPlaneFront(Vector3f direction, Vector3f position, Vector3f point) {
        float d = -direction.x * position.x - direction.y * position.y - direction.z * position.z;
        return (direction.x * point.x +
                direction.y * point.y +
                direction.z * point.z + d) > 0;
    }
}
