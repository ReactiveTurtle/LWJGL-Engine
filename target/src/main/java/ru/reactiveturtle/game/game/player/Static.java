package ru.reactiveturtle.game.game.player;

import org.joml.Quaternionf;
import org.joml.Vector3f;
import ru.reactiveturtle.game.engine.model.Model;
import ru.reactiveturtle.game.engine.model.Selectable;

public class Static implements Selectable {
    public String name;
    public Model model;
    public Vector3f defaultPosition;
    public Vector3f defaultRotation;

    public Static(String name, Model model, Vector3f defaultPosition, Vector3f defaultRotation) {
        this.name = name;
        this.model = model;
        this.defaultPosition = defaultPosition;
        this.defaultRotation = defaultRotation;
    }

    public void renderShadow() {
        model.setPosition(defaultPosition);
        model.setRotation(defaultRotation);
        model.renderShadow();
    }

    public void render(Vector3f direction, Vector3f position) {
        if (isSelectBoxInFrustum(direction, position)) {
            model.setPosition(defaultPosition);
            model.setRotation(defaultRotation);
            model.render();
        }
    }

    protected Vector3f mBoxParams;
    protected Vector3f mIntersectionPoint;
    protected float mSelectBoxX = 0;
    protected float mSelectBoxY = 0;

    @Override
    public void setSelectBox(float width, float height, float depth) {
        mBoxParams = new Vector3f(width / 2, height / 2, depth / 2);
    }

    @Override
    public void removeSelectBox() {
        mBoxParams = null;
    }

    @Override
    public void setSelectBoxX(float x) {
        mSelectBoxX = x;
    }

    @Override
    public void setSelectBoxY(float y) {
        mSelectBoxY = y;
    }

    @Override
    public Vector3f calcIntersectionPoint(Vector3f cameraPosition, Vector3f direction, Model model) {
        if (mBoxParams != null) {
            Quaternionf quaternionf = new Quaternionf();
            quaternionf.rotateXYZ(
                    (float) Math.toRadians(defaultRotation.x),
                    (float) Math.toRadians(defaultRotation.y),
                    (float) Math.toRadians(defaultRotation.z));
            int[] signs = new int[]{
                    -1, 1, 1, //front
                    -1, -1, 1,
                    1, 1, 1,

                    1, 1, -1, //right
                    1, -1, -1,
                    1, 1, 1,

                    -1, 1, -1, //left
                    -1, -1, -1,
                    -1, 1, 1,

                    1, 1, -1, //back
                    1, -1, -1,
                    -1, 1, -1,

                    -1, 1, -1, //top
                    -1, 1, 1,
                    1, 1, -1,

                    1, -1, -1, //bottom
                    1, -1, 1,
                    -1, -1, -1,
            };
            Vector3f min = new Vector3f();
            Vector3f max = new Vector3f();
            Vector3f[] boxPoints = new Vector3f[]{new Vector3f(), new Vector3f(), new Vector3f()};
            Vector3f resultIntersection = null;
            for (int i = 0; i < 6; i++) {
                int iIndex = i * 9;
                for (int j = 0; j < boxPoints.length; j++) {
                    int jIndex = iIndex + j * 3;
                    boxPoints[j].set(signs[jIndex] * mBoxParams.x,
                            signs[jIndex + 1] * mBoxParams.y,
                            signs[jIndex + 2] * mBoxParams.z)
                            .add(mSelectBoxX, 0, 0)
                            .add(0, mSelectBoxY, 0)
                            .rotate(quaternionf)
                            .add(defaultPosition);
                }
                Vector3f intersectPoint = intersectPoint(new Vector3f(direction), new Vector3f(cameraPosition),
                        getNormal(boxPoints[0], boxPoints[1], boxPoints[2]), new Vector3f(boxPoints[0].add(boxPoints[2]).div(2)));

                float minX = Math.min(Math.min(boxPoints[0].x, boxPoints[1].x), boxPoints[2].x);
                float maxX = Math.max(Math.max(boxPoints[0].x, boxPoints[1].x), boxPoints[2].x);

                float minY = Math.min(Math.min(boxPoints[0].y, boxPoints[1].y), boxPoints[2].y);
                float maxY = Math.max(Math.max(boxPoints[0].y, boxPoints[1].y), boxPoints[2].y);

                float minZ = Math.min(Math.min(boxPoints[0].z, boxPoints[1].z), boxPoints[2].z);
                float maxZ = Math.max(Math.max(boxPoints[0].z, boxPoints[1].z), boxPoints[2].z);
                if (intersectPoint.x >= minX && intersectPoint.x <= maxX &&
                        intersectPoint.y >= minY && intersectPoint.y <= maxY &&
                        intersectPoint.z >= minZ && intersectPoint.z <= maxZ) {
                    if (resultIntersection == null ||
                            new Vector3f(cameraPosition).sub(intersectPoint).length() < new Vector3f(cameraPosition).sub(resultIntersection).length()) {
                        min.set(minX, minY, minZ);
                        max.set(maxX, maxY, maxZ);
                        resultIntersection = intersectPoint;
                    }
                }
            }
            if (resultIntersection != null && isPointInFrustum(direction, cameraPosition, resultIntersection)) {
                mIntersectionPoint = resultIntersection;
                if (model != null) {
                    model.setPosition(defaultPosition);
                    model.addX(mSelectBoxX);
                    model.addY(mSelectBoxY);
                    model.setRotation(defaultRotation);
                    model.setScale(mBoxParams.x, mBoxParams.y, mBoxParams.z);
                    model.getScale().mul(2);
                    model.render();
                }
                return resultIntersection;
            }
            return null;
        }
        return null;
    }

    @Override
    public Vector3f getIntersectionPoint() {
        return mIntersectionPoint;
    }

    private Vector3f getNormal(Vector3f first, Vector3f second, Vector3f third) {
        float xFactor = (second.y - first.y) * (third.z - first.z) - (second.z - first.z) * (third.y - first.y);
        float yFactor = (second.x - first.x) * (third.z - first.z) - (second.z - first.z) * (third.x - first.x);
        float zFactor = (second.x - first.x) * (third.y - first.y) - (second.y - first.y) * (third.x - first.x);
        return new Vector3f(xFactor, yFactor, zFactor);
    }

    private Vector3f intersectPoint(Vector3f lineDirection, Vector3f linePoint, Vector3f planeNormal, Vector3f planePoint) {
        double t = (planeNormal.dot(planePoint) - planeNormal.dot(linePoint)) / planeNormal.dot(lineDirection.normalize());
        Vector3f vector3f = linePoint.add(lineDirection.normalize().mul((float) t));
        vector3f.set(Math.round(vector3f.x * 10000) / 10000f,
                Math.round(vector3f.y * 10000) / 10000f,
                Math.round(vector3f.z * 10000) / 10000f);
        return vector3f;
    }

    private int[] selectBoxFrustumSigns = new int[]{
            -1, -1, 1,
            1, -1, 1,
            -1, 1, 1,
            1, 1, 1,
            -1, -1, -1,
            1, -1, -1,
            -1, 1, -1,
            1, 1, -1,
    };

    private boolean isSelectBoxInFrustum(Vector3f direction, Vector3f position) {
        if (mBoxParams != null) {
            return true;
        }
        float d = -direction.x * position.x - direction.y * position.y - direction.z * position.z;
        Quaternionf quaternionf = new Quaternionf();
        quaternionf.rotateXYZ(
                (float) Math.toRadians(defaultRotation.x),
                (float) Math.toRadians(defaultRotation.y),
                (float) Math.toRadians(defaultRotation.z));
        boolean inFrustum = false;
        Vector3f vector3f = new Vector3f();
        for (int i = 0; i < 6 && !inFrustum; i++) {
            int iIndex = i * 3;
            vector3f.set(selectBoxFrustumSigns[iIndex] * mBoxParams.x,
                    selectBoxFrustumSigns[iIndex + 1] * mBoxParams.y,
                    selectBoxFrustumSigns[iIndex + 2] * mBoxParams.z)
                    .add(mSelectBoxX, 0, 0)
                    .add(0, mSelectBoxY, 0)
                    .rotate(quaternionf)
                    .add(defaultPosition);
            inFrustum = (direction.x * vector3f.x +
                    direction.y * vector3f.y +
                    direction.z * vector3f.z + d) > 0;
        }
        return inFrustum;
    }

    private boolean isPointInFrustum(Vector3f direction, Vector3f position, Vector3f point) {
        float d = -direction.x * position.x - direction.y * position.y - direction.z * position.z;
        return (direction.x * point.x +
                direction.y * point.y +
                direction.z * point.z + d) > 0;
    }
}
