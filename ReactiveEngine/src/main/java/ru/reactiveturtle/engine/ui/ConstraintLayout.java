package ru.reactiveturtle.engine.ui;

import ru.reactiveturtle.engine.toolkit.ReactiveList;

public class ConstraintLayout extends UILayout<UIElement> {
    public ReactiveList<ConstraintConnection> connections = new ReactiveList<>();

    public ConstraintLayout(UIContext uiContext) {
        super(uiContext);
    }

    public void connect(int fromId, Side fromSide, int toId, Side toSide) {
        ConstraintConnection constraintConnection = new ConstraintConnection(fromId, fromSide, toId, toSide);
        if (fromSide == Side.BOTTOM || fromSide == Side.TOP) {
            if (toSide != Side.BOTTOM && toSide != Side.TOP) {
                throw new IllegalArgumentException("Invalid sides");
            }
        } else if (fromSide == Side.LEFT || fromSide == Side.RIGHT) {
            if (toSide != Side.LEFT && toSide != Side.RIGHT) {
                throw new IllegalArgumentException("Invalid sides");
            }
        }
        if (!connections.contains(e -> e.hashCode() == constraintConnection.hashCode())) {
            connections.add(constraintConnection);
            recalculatePositions();
        }
    }

    private void recalculatePositions() {
        
    }
}
