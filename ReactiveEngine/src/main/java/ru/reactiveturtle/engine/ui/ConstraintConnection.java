package ru.reactiveturtle.engine.ui;

import java.util.Objects;

public class ConstraintConnection {
    public final int fromId;
    public final Side fromSide;

    public final int toId;
    public final Side toSide;

    public ConstraintConnection(int fromId, Side fromSide, int toId, Side toSide) {
        this.fromId = fromId;
        this.fromSide = fromSide;
        this.toId = toId;
        this.toSide = toSide;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConstraintConnection that = (ConstraintConnection) o;
        return fromId == that.fromId &&
                toId == that.toId &&
                fromSide == that.fromSide &&
                toSide == that.toSide;
    }

    @Override
    public int hashCode() {
        return Objects.hash(fromId, fromSide, toId, toSide);
    }
}
