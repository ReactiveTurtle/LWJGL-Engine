package ru.reactiveturtle.engine.ui;

import org.joml.Vector2i;
import ru.reactiveturtle.engine.base2d.Square;
import ru.reactiveturtle.engine.texture.Texture;

import java.awt.*;
import java.awt.image.BufferedImage;

public class UIElement extends Square {
    private UIContext uiContext;
    protected BufferedImage bufferedImage;
    protected Integer id;

    public UIElement(UIContext uiContext, float relativeWidth, float relativeHeight) {
        super(relativeWidth, relativeHeight, new Texture(1, 1, Texture.PixelFormat.RGBA), 1, 1);
        this.uiContext = uiContext;
        bufferedImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
    }

    private boolean isHidden = false;
    public void show() {
        isHidden = false;
    }

    public void hide() {
        isHidden = true;
    }

    public boolean isHidden() {
        return isHidden;
    }

    private final Vector2i size = new Vector2i(1);

    public void setSize(Vector2i size) {
        this.size.set(size);
        isNeedToRedraw = true;
    }

    public Vector2i getSize() {
        return new Vector2i(size);
    }

    private Color backgroundColor = new Color(0, 0, 0, 0);

    public void setBackground(int r, int g, int b, int alpha) {
        backgroundColor = new Color(r, g, b, alpha);
        isNeedToRedraw = true;
    }

    protected boolean isNeedToRedraw = true;

    public Color getBackgroundColor() {
        return new Color(
                backgroundColor.getRed(),
                backgroundColor.getGreen(),
                backgroundColor.getBlue(),
                backgroundColor.getAlpha());
    }

    protected void redraw(Graphics2D graphics2D) {
        graphics2D.setColor(getBackgroundColor());
        graphics2D.fillRect(0, 0, size.x, size.y);
    }

    boolean disposed = false;
    @Override
    public void dispose() {
        disposed = true;
        uiContext.removeElement(id);
        id = null;
        uiContext = null;
        super.dispose();
    }

    public void setId(int id) {
        if (this.id != null || disposed) {
            throw new UnsupportedOperationException("You can not change id when it already set");
        }
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public UIContext getUIContext() {
        return uiContext;
    }
}
