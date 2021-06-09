package ru.reactiveturtle.engine.geometry;

import org.joml.Quaternionf;
import org.joml.Vector3f;
import ru.reactiveturtle.engine.base.Shader;
import ru.reactiveturtle.engine.base3d.Stage3D;
import ru.reactiveturtle.engine.camera.Camera;

public class Frustum {
    private final Plane[] planes = new Plane[6];

    public Frustum(Camera camera) {
        Vector3f cameraDirection = camera.getDirection();
        Quaternionf quaternionf = new Quaternionf();
        quaternionf.rotateYXZ(
                -camera.getRotationY(),
                (float) (Math.PI / 2 - camera.getRotationX() - camera.getFieldOfView()),
                0);

        planes[0] = new Plane(camera.getPosition(), cameraDirection);
        planes[5] = new Plane(camera.getPosition().add(
                new Vector3f(cameraDirection).normalize(camera.getFar())),
                new Vector3f(cameraDirection).negate());

        planes[1] = new Plane(camera.getPosition(), new Vector3f(0, 0, -1).rotate(quaternionf));
        quaternionf.rotateX((float) (-Math.PI + camera.getFieldOfView() * 2));
        planes[2] = new Plane(camera.getPosition(), new Vector3f(0, 0, -1).rotate(quaternionf));

        quaternionf.rotateX((float) (Math.PI / 2 - camera.getFieldOfView()));
        quaternionf.rotateY((float) (Math.PI / 2 - camera.getFieldOfView()));
        planes[3] = new Plane(camera.getPosition(), new Vector3f(0, 0, -1).rotate(quaternionf));
        quaternionf.rotateY((float) (-Math.PI + camera.getFieldOfView() * 2));
        planes[4] = new Plane(camera.getPosition(), new Vector3f(0, 0, -1).rotate(quaternionf));
    }

    public boolean isFigureInFrustum(float[] points) {
        boolean isFigureInFrustum = false;
        for (int i = 0; i < points.length && !isFigureInFrustum; i += 3) {
            Vector3f point = new Vector3f(points[i], points[i + 1], points[i + 2]);
            isFigureInFrustum = isPointInFrustum(point);
        }
        return isFigureInFrustum;
    }

    public boolean isPointInFrustum(Vector3f point) {
        boolean isInFrustum = true;
        for (int i = 0; i < planes.length && isInFrustum; i++) {
            isInFrustum = planes[i].isPointAtFront(point);
        }
        return isInFrustum;
    }

    /**
     * For debugging
     **/
    public void render(Stage3D stage, Shader textureShader) {
        for (Plane plane : planes) {
            if (plane == null) continue;
            plane.render(stage, textureShader);
        }
    }
}
