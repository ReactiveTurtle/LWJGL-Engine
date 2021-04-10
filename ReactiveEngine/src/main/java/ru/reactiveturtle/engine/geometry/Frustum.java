package ru.reactiveturtle.engine.geometry;

import org.joml.Quaternionf;
import org.joml.Vector3f;
import ru.reactiveturtle.engine.base.Shader;
import ru.reactiveturtle.engine.base.Stage;
import ru.reactiveturtle.engine.camera.PerspectiveCamera;

import java.util.Arrays;

public class Frustum {
    private final Plane[] planes = new Plane[6];

    public Frustum(PerspectiveCamera camera) {
        Vector3f cameraDirection = camera.getDirection();
        Quaternionf quaternionf = new Quaternionf();
        quaternionf.rotateYXZ(
                (float) Math.toRadians(-camera.getRotationY()),
                (float) Math.toRadians(90 - camera.getRotationX() - camera.getFOV()),
                0);

        planes[0] = new Plane(camera.getPosition(), cameraDirection);
        planes[5] = new Plane(camera.getPosition().add(
                new Vector3f(cameraDirection).normalize(camera.getFar())),
                new Vector3f(cameraDirection).negate());

        planes[1] = new Plane(camera.getPosition(), new Vector3f(0, 0, -1).rotate(quaternionf));
        quaternionf.rotateX((float) Math.toRadians(-180 + camera.getFOV() * 2));
        planes[2] = new Plane(camera.getPosition(), new Vector3f(0, 0, -1).rotate(quaternionf));

        quaternionf.rotateX((float) Math.toRadians(90 - camera.getFOV()));
        quaternionf.rotateY((float) Math.toRadians(90 - camera.getFOV()));
        planes[3] = new Plane(camera.getPosition(), new Vector3f(0, 0, -1).rotate(quaternionf));
        quaternionf.rotateY((float) Math.toRadians(-180 + camera.getFOV() * 2));
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
    public void render(Stage stage, Shader textureShader) {
        for (Plane plane : planes) {
            if (plane == null) continue;
            plane.render(stage, textureShader);
        }
    }
}
