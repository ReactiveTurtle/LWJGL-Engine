package ru.reactiveturtle.engine.geometry;

import org.joml.Vector3f;
import org.joml.Vector4f;
import ru.reactiveturtle.engine.base.Shader;
import ru.reactiveturtle.engine.base.Stage;
import ru.reactiveturtle.engine.base.Value;
import ru.reactiveturtle.engine.material.Material;
import ru.reactiveturtle.engine.model.base.Sphere;

public class Plane {
    private float xFactor, yFactor, zFactor, dFactor;
    private Vector3f position;

    public Plane(Vector3f position, Vector3f normal) {
        this.position = new Vector3f(position);
        xFactor = normal.x;
        yFactor = normal.y;
        zFactor = normal.z;
        dFactor = -position.x * xFactor - position.y * yFactor - position.z * zFactor;
    }

    public Plane(Vector3f first, Vector3f second, Vector3f third) {
        xFactor = (second.y - first.y) * (third.z - first.z) - (second.z - first.z) * (third.y - first.y);
        yFactor = -((second.x - first.x) * (third.z - first.z) - (second.z - first.z) * (third.x - first.x));
        zFactor = (second.x - first.x) * (third.y - first.y) - (second.y - first.y) * (third.x - first.x);
        dFactor = -first.x * xFactor - first.y * yFactor - first.z * zFactor;
    }

    public boolean isPointAtFront(Vector3f point) {
        return xFactor * point.x + yFactor * point.y + zFactor * point.z + dFactor > 1;
    }

    public boolean isPointAtFrontOrIn(Vector3f point) {
        return xFactor * point.x + yFactor * point.y + zFactor * point.z + dFactor >= 1;
    }

    public Vector3f intersects(Line line, Value<Boolean> isIntersects) {
        return line.intersects(this, isIntersects);
    }

    public Vector4f getFactors() {
        return new Vector4f(xFactor, yFactor, zFactor, dFactor);
    }

    public void render(Stage stage, Shader textureShader) {
        Sphere sphere = new Sphere(0.1f, 8, false);
        Sphere sphere2 = new Sphere(0.1f, 8, false);
        Material material = new Material();
        Vector3f vector3f = new Vector3f((float) Math.random(), (float) Math.random(), (float) Math.random());
        material.setDiffuse(vector3f);
        material.setAmbient(new Vector3f(vector3f).div(3));

        sphere.setMaterial(material);
        sphere.setPosition(position);
        sphere.addY(1f);
        sphere.addZ(1f);
        sphere.setShader(textureShader);

        sphere2.setMaterial(material);
        sphere2.setPosition(position);
        sphere2.addY(1f);
        sphere2.addZ(1f);
        sphere2.addPosition(new Vector3f(xFactor, yFactor, zFactor).normalize(0.4f));
        sphere2.setShader(textureShader);

        sphere.render(stage);
        sphere2.render(stage);
    }
}
