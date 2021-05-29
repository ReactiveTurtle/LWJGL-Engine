package ru.reactiveturtle.engine.ui;

import org.joml.Vector2i;
import ru.reactiveturtle.engine.base3d.Stage3D;

import java.awt.*;
import java.awt.font.LineMetrics;
import java.awt.image.BufferedImage;
import java.util.Objects;

public class Label extends UIElement {
    public Label(UIContext uiContext, float relativeWidth, float relativeHeight) {
        super(uiContext, relativeWidth, relativeHeight);
        setSizing(Sizing.FIXED);
    }

    public Label(UIContext uiContext) {
        super(uiContext, 0, 0);
        setSizing(Sizing.ADAPT);
    }

    private Sizing sizing = Sizing.ADAPT;

    public void setSizing(Sizing sizing) {
        Objects.requireNonNull(sizing);
        this.sizing = sizing;
        if (sizing == Sizing.FIXED) {
            isNeedToRedraw = true;
        }
    }

    private Vector2i fixedSize = new Vector2i(1);

    /**
     * Размер применяется только в том случае,
     * когда поле sizing находится в состоянии Sizing.FIXED
     *
     * @param size Размер изображения
     */
    public void setImageSize(Vector2i size) {
        this.fixedSize = size;
        isNeedToRedraw = sizing == Sizing.FIXED;
    }

    private int fontSize = 40;

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
        isNeedToRedraw = true;
    }

    private String[] textLines = new String[0];

    public void setText(String text) {
        if (text != null) {
            this.textLines = text.split("\n");
        } else {
            this.textLines = new String[0];
        }
        isNeedToRedraw = true;
    }

    private Color textColor = new Color(255, 255, 255, 255);

    public void setTextColor(int r, int g, int b, int a) {
        textColor = new Color(r, g, b, a);
        isNeedToRedraw = true;
    }

    @Override
    public void draw(Stage3D stage) {
        if (isNeedToRedraw) {
            redraw();
        }
        super.draw(stage);
    }

    protected void redraw() {
        if (sizing == Sizing.ADAPT) {
            Graphics2D labelTextDrawer = bufferedImage.createGraphics();
            labelTextDrawer.setFont(new Font("monospace", Font.PLAIN, fontSize));
            FontMetrics fontMetrics = labelTextDrawer.getFontMetrics();
            fontMetrics.getLineMetrics("String", labelTextDrawer);

            String maxLine = "";
            int maxLineWidth = 0;
            for (String line : textLines) {
                int lineWidth = fontMetrics.stringWidth(line);
                if (lineWidth > maxLineWidth) {
                    maxLine = line;
                    maxLineWidth = lineWidth;
                }
            }
            LineMetrics lineMetrics = fontMetrics.getLineMetrics(maxLine, labelTextDrawer);
            int height = (int) ((textLines.length) * lineMetrics.getHeight());
            setSize(new Vector2i(maxLineWidth + 1, height + 1));
        } else if (sizing == Sizing.FIXED) {
            setSize(fixedSize);
        } else {
            throw new EnumConstantNotPresentException(Sizing.class, "sizing");
        }
        Vector2i size = getSize();
        bufferedImage = new BufferedImage(size.x, size.y, BufferedImage.TYPE_INT_ARGB);
        Graphics2D labelTextDrawer = bufferedImage.createGraphics();
        labelTextDrawer.setFont(new Font("monospace", Font.PLAIN, fontSize));
        FontMetrics fontMetrics = labelTextDrawer.getFontMetrics();
        LineMetrics lineMetrics = fontMetrics.getLineMetrics("String", labelTextDrawer);

        super.redraw(labelTextDrawer);

        labelTextDrawer.setColor(textColor);
        for (int i = 0; i < textLines.length; i++) {
            String line = textLines[i];
            labelTextDrawer.drawString(line, 0, (i + 1) * lineMetrics.getAscent() + i * lineMetrics.getDescent());
        }

        float width = getWidth();
        if (sizing == Sizing.ADAPT) {
            width = (float) size.x / getUIContext().getScreenWidth();
        }
        resize(width, (float) size.y / size.x * width, 1f, 1f);
        getTexture().set(bufferedImage);

        isNeedToRedraw = false;
    }
}
