package ru.reactiveturtle.game.engine.model;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;
import ru.reactiveturtle.game.engine.base.GameContext;
import ru.reactiveturtle.game.engine.base.Shader;
import ru.reactiveturtle.game.engine.base.Transform3D;
import ru.reactiveturtle.game.engine.material.Material;
import ru.reactiveturtle.game.engine.model.mesh.Mesh;
import ru.reactiveturtle.game.game.player.Interface;

import java.util.HashMap;

public class Model extends Transform3D implements Selectable {
    private Vector3f scale = new Vector3f(1f, 1f, 1f);

    protected HashMap<String, Mesh> meshes = new HashMap<>();

    private Shader shader;

    public Model(Mesh... meshes) {
        for (Mesh mesh : meshes) {
            this.meshes.put(mesh.getKey(), mesh);
        }
    }

    public void setShader(Shader shader) {
        this.shader = shader;
    }

    public Shader getShader() {
        return shader;
    }

    public Matrix4f getModelMatrix() {
        return new Matrix4f().identity()
                .translate(getPosition())
                .rotateXYZ((float) Math.toRadians(getRotationX()),
                        (float) Math.toRadians(getRotationY()),
                        (float) Math.toRadians(getRotationZ()))
                .scale(getScale());
    }

    public void render() {
        for (Mesh mesh : meshes.values()) {
            mesh.render(shader, getModelMatrix());
        }
    }

    public void renderShadow() {
        if (GameContext.getShadowManager().isShadowEnabled()) {
            for (Mesh mesh : meshes.values()) {
                mesh.renderShadow(getModelMatrix());
            }
        }
    }

    public void destroy() {

    }

    public void setScale(float scale) {
        this.scale.x = scale;
        this.scale.y = scale;
        this.scale.z = scale;
    }

    public void setScale(float scaleX, float scaleY, float scaleZ) {
        scale.set(scaleX, scaleY, scaleZ);
    }

    public void setScaleX(float scaleX) {
        scale.x = scaleX;
    }

    public void setScaleY(float scaleY) {
        scale.y = scaleY;
    }

    public Vector3f getScale() {
        return scale;
    }

    public float getScaleX() {
        return scale.x;
    }

    public float getScaleY() {
        return scale.y;
    }

    public void setMaterial(Material material) {
        for (Mesh mesh : meshes.values()) {
            mesh.setMaterial(material);
        }
    }

    public HashMap<String, Mesh> getMeshes() {
        return meshes;
    }

    private Vector3f mBoxParams;
    private Vector3f mIntersectionPoint;
    private float mSelectBoxY = 0;

    @Override
    public void setSelectBox(float width, float height, float depth) {
        mBoxParams = new Vector3f(width, height, depth);
    }

    @Override
    public void setSelectBoxY(float y) {
        mSelectBoxY = y;
    }

    @Override
    public Vector3f calcIntersectionPoint(Vector3f point, Vector3f direction, Model model) {
        if (mBoxParams != null) {
            Quaternionf quaternionf = new Quaternionf();
            quaternionf.rotateXYZ((float) Math.toRadians(getRotationX()), (float) Math.toRadians(getRotationY()), (float) Math.toRadians(getRotationZ()));
            float halfWidth = mBoxParams.x / 2;
            float halfHeight = mBoxParams.y / 2;
            float halfDepth = mBoxParams.z / 2;
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
                    boxPoints[j].set(signs[jIndex] * halfWidth,
                            signs[jIndex + 1] * halfHeight,
                            signs[jIndex + 2] * halfDepth).rotate(quaternionf)
                            .add(getPosition()).add(0, mSelectBoxY, 0);
                }
                Vector3f intersectPoint = intersectPoint(new Vector3f(direction), new Vector3f(point),
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
                            new Vector3f(point).sub(intersectPoint).length() < new Vector3f(point).sub(resultIntersection).length()) {
                        min.set(minX, minY, minZ);
                        max.set(maxX, maxY, maxZ);
                        resultIntersection = intersectPoint;
                    }
                }
            }
            if (resultIntersection != null) {
                mIntersectionPoint = resultIntersection;
                model.setPosition(mIntersectionPoint);
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
}
