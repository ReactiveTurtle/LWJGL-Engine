package ru.reactiveturtle.physics;

public class CollisionResult {
    private boolean isXCollide = false;
    private boolean isYCollide = false;
    private boolean isZCollide = false;

    public CollisionResult() {

    }

    public CollisionResult(boolean isCollide) {
        isXCollide = isCollide;
        isYCollide = isCollide;
        isZCollide = isCollide;
    }

    public CollisionResult(boolean isXCollide, boolean isYCollide, boolean isZCollide) {
        this.isXCollide = isXCollide;
        this.isYCollide = isYCollide;
        this.isZCollide = isZCollide;
    }

    public void setXCollide(boolean XCollide) {
        isXCollide = XCollide;
    }

    public void setYCollide(boolean YCollide) {
        isYCollide = YCollide;
    }

    public void setZCollide(boolean ZCollide) {
        isZCollide = ZCollide;
    }

    public boolean isXCollide() {
        return isXCollide;
    }

    public boolean isYCollide() {
        return isYCollide;
    }

    public boolean isZCollide() {
        return isZCollide;
    }

    public void add(CollisionResult collisionResult) {
        isXCollide |= collisionResult.isXCollide();
        isYCollide |= collisionResult.isYCollide();
        isZCollide |= collisionResult.isZCollide();
    }

    public boolean isAllCollide() {
        return isXCollide && isYCollide && isZCollide;
    }

    public boolean isCollide() {
        return isXCollide || isYCollide || isZCollide;
    }

    public void clear() {
        isXCollide = false;
        isYCollide = false;
        isZCollide = false;
    }
}
