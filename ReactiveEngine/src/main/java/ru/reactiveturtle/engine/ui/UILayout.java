package ru.reactiveturtle.engine.ui;

import ru.reactiveturtle.engine.base.Stage;
import ru.reactiveturtle.engine.base2d.SquareShader;
import ru.reactiveturtle.engine.base3d.Stage3D;
import ru.reactiveturtle.engine.model.Disposeable;
import ru.reactiveturtle.engine.model.Renderable;
import ru.reactiveturtle.engine.toolkit.ReactiveList;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;

public class UILayout<T extends UIElement> extends UIElement implements Renderable<Stage>, Disposeable {
    private UIContext uiContext;
    private SquareShader squareShader;
    protected final ReactiveList<T> elements = new ReactiveList<>();

    public UILayout(UIContext uiContext) {
        super(uiContext, 1, 1);
        this.uiContext = uiContext;
        squareShader = new SquareShader();
    }

    @Override
    public void render(Stage stage) {
        squareShader.bind();
        if (stage instanceof Stage3D) {
            boolean isDepthTestEnabled = glIsEnabled(GL_DEPTH_TEST);
            if (isDepthTestEnabled) {
                glDisable(GL_DEPTH_TEST);
            }
            elements.forEach((e) -> {
                if (!e.isHidden())
                    e.draw((Stage3D) stage);
            });
            if (isDepthTestEnabled) {
                glEnable(GL_DEPTH_TEST);
            }
        }
        squareShader.unbind();
    }

    public int add(T element) {
        int id = uiContext.generateId();
        uiContext.addIdFor(element, id);
        this.elements.add(element);
        element.setShader(squareShader);
        return id;
    }

    public void show(int id) {
        T hidden = this.elements.first(e -> e.getId() == id);
        if (hidden != null) {
            hidden.show();
        }
    }

    public void hide(int id) {
        T hideable = this.elements.first(e -> e.getId() == id);
        if (hideable != null) {
            hideable.hide();
        }
    }

    public void remove(int id) {
        T disposed;
        int index = this.elements.indexOf(e -> e.getId() == id);
        if (index != -1) {
            disposed = this.elements.remove(index);
            if (!disposed.disposed) {
                disposed.dispose();
            }
            uiContext.removeIdFor(id);
        }
    }

    @Override
    public void dispose() {
        uiContext = null;
        squareShader.dispose();
        squareShader = null;
        for (UIElement uiElement : elements) {
            uiElement.dispose();
        }
    }

    public UIContext getUIContext() {
        return uiContext;
    }
}
