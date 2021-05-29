package ru.reactiveturtle.engine.toolkit;

import org.joml.Quaternionf;
import org.joml.Vector3f;
import ru.reactiveturtle.engine.geometry.Line;
import ru.reactiveturtle.engine.geometry.Plane;

public class GeometryExtensions {
    /**
     * @param position                  Положение направляющей прямой в пространстве,
     *                                  которая нужна для описания прямой
     *                                  по которой ищется пересечение (обычно это координаты камеры)
     * @param direction                 Направляющая прямой, которая нужна для описания прямой
     *                                  по которой ищется пересечение (обычно это направление камеры)
     * @param targetDefaultNormals      6 неизменённых (не повёрнутых) нормалей коробки
     *                                  (нормали должны смотреть наружу коробки относительно
     *                                  их точек). Нормали должны быть перпендикулярны или параллельны
     *                                  осям координат
     * @param targetDefaultNormalPoints 6 точек положения плоскостей коробки, которыми она задана
     *                                  (точки должны находится в начале координат,
     *                                  то есть представляют собой локальную систему координат)
     * @param targetCenter              Смещение точек плоскости относительно центра координат
     * @param targetPosition            Глобальные координаты коробки (цели)
     * @param targetRotation            Поворот коробки
     * @return Возвращает расстояние до коробки или null, если пересечение не найдено
     */
    public static Float intersectBox(Vector3f position,
                                     Vector3f direction,
                                     float[] targetDefaultNormals,
                                     float[] targetDefaultNormalPoints,
                                     Vector3f targetCenter,
                                     Vector3f targetPosition,
                                     Vector3f targetRotation) {
        Float distance = null;
        // Перевод в локальную систему координат относительно коробки,
        // чтобы сравнивать параллельно осям координат
        Quaternionf reverseQuaternion = new Quaternionf()
                .rotateYXZ(-targetRotation.y, -targetRotation.x, -targetRotation.z);
        Quaternionf quaternion = new Quaternionf()
                .rotateYXZ(targetRotation.y, targetRotation.x, targetRotation.z);
        Vector3f localCameraDirection = new Vector3f(direction).rotate(reverseQuaternion);
        Vector3f localCameraPosition = new Vector3f(position.sub(targetPosition)
                .add(targetCenter)).rotate(reverseQuaternion);

        Line line = new Line(localCameraDirection, localCameraPosition);
        Plane[] box = new Plane[6];
        for (int i = 0; i < 6; i++) {
            int index = i * 3;
            Plane plane = new Plane(
                    new Vector3f(
                            targetDefaultNormalPoints[index],
                            targetDefaultNormalPoints[index + 1],
                            targetDefaultNormalPoints[index + 2]),
                    new Vector3f(
                            targetDefaultNormals[index],
                            targetDefaultNormals[index + 1],
                            targetDefaultNormals[index + 2]));
            box[i] = plane;
        }
        Plane cameraPlane = new Plane(position, direction);
        for (int i = 0; i < 6; i++) {
            Vector3f point = line.intersects(box[i]);
            if (point != null) {
                Vector3f worldPoint = new Vector3f(point).rotate(quaternion)
                        .add(targetPosition).sub(targetCenter);
                if (cameraPlane.isPointAtFrontOrIn(worldPoint)) {
                    boolean isInBox =
                            targetDefaultNormalPoints[5] <= point.z && point.z <= targetDefaultNormalPoints[2] &&
                                    targetDefaultNormalPoints[9] <= point.x && point.x <= targetDefaultNormalPoints[6] &&
                                    targetDefaultNormalPoints[16] <= point.y && point.y <= targetDefaultNormalPoints[13];
                    if (isInBox) {
                        float newDistance = new Vector3f(localCameraPosition).sub(point).length();
                        if (distance == null || newDistance < distance) {
                            distance = newDistance;
                        }
                    }
                }
            }
        }
        return distance;
    }
}
