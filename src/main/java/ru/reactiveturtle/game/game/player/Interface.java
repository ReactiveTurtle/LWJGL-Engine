package ru.reactiveturtle.game.game.player;

import ru.reactiveturtle.game.engine.base.GameContext;
import ru.reactiveturtle.game.engine.base2d.Square;
import ru.reactiveturtle.game.engine.base2d.SquareShader;
import ru.reactiveturtle.game.engine.material.Texture;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.io.File;
import java.io.IOException;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static ru.reactiveturtle.game.engine.base.GameContext.getAspectRatio;

public class Interface {
    private Square center;
    private Square hang;

    private Square log;
    private BufferedImage logImage;
    private Graphics2D logDrawer;

    private Square inventory;
    private BufferedImage inventoryBase;
    private BufferedImage inventoryImage;
    private Graphics2D inventoryDrawer;

    private SquareShader squareShader;

    public Interface() {
        squareShader = new SquareShader();
        BufferedImage image = new BufferedImage(256, 16, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        graphics.setColor(new Color(0, 0, 0, 128));
        graphics.fillRect(0, 0, 256, 16);
        graphics.setColor(Color.RED);
        graphics.fillRect(0, 0, (int) (256 * 0.8f), 16);
        hang = new Square(0.3f, 0.01875f,
                new Texture(image),
                1f, 1f);
        hang.setPosition(-1f * getAspectRatio() + hang.getWidth() * 1.2f, -1f + hang.getHeight() + hang.getWidth() * 0.2f);
        hang.setShader(squareShader);

        image = new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB);
        graphics = image.createGraphics();
        graphics.setColor(Color.WHITE);
        graphics.fillOval(0, 0, 256, 256);
        center = new Square(0.02f, 0.02f, new Texture(image), 1f, 1f);
        center.setShader(squareShader);

        logImage = new BufferedImage(512, 512, BufferedImage.TYPE_INT_ARGB);
        logDrawer = logImage.createGraphics();
        Font font = logDrawer.getFont();
        font = font.deriveFont(24f);
        font = font.deriveFont(Font.PLAIN);
        logDrawer.setFont(font);
        log = new Square(1f, 1f, new Texture(logImage), 1f, 1f);
        log.setPosition(-1f * getAspectRatio() + log.getWidth(), 1f - log.getHeight());
        log.setShader(squareShader);

        inventoryBase = new BufferedImage(512, 64, BufferedImage.TYPE_INT_ARGB);
        try {
            BufferedImage bufferedImage1 = ImageIO.read(new File(GameContext.RESOURCE_PATH + "/texture/inventory_item.png"));

            graphics = inventoryBase.createGraphics();
            for (int i = 0; i < 8; i++) {
                graphics.drawImage(bufferedImage1, i * 64, 0, null);
            }
            inventoryImage = copyImage(inventoryBase);
            inventoryDrawer = inventoryImage.createGraphics();
            inventoryDrawer.setColor(Color.RED);
            inventoryDrawer.setStroke(new BasicStroke(2));
            inventory = new Square(0.8f, 0.1f, new Texture(inventoryImage), 1f, 1f);
            inventory.setY(-1f + inventory.getHeight());
            inventory.setShader(squareShader);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void render(double deltaTime) {
        boolean isDepthTestEnabled = glIsEnabled(GL_DEPTH_TEST);
        if (isDepthTestEnabled) {
            glDisable(GL_DEPTH_TEST);
        }
        squareShader.bind();
        hang.draw();
        center.draw();
        log.draw();
        inventory.draw();
        squareShader.unbind();
        if (isDepthTestEnabled) {
            glEnable(GL_DEPTH_TEST);
        }
    }

    public void setIntersectionText(String string) {
        logDrawer.setComposite(AlphaComposite.Clear);
        logDrawer.fillRect(0, 0, 1024, 128);
        logDrawer.setComposite(AlphaComposite.SrcOver);
        logDrawer.drawString(string, 4, 24);
        log.getTexture().set(logImage);
    }

    public void setSelectedInventoryItem(int selectedInventoryItem) {
        inventoryDrawer.setComposite(AlphaComposite.Clear);
        inventoryDrawer.fillRect(0, 0, 512, 64);
        inventoryDrawer.setComposite(AlphaComposite.SrcOver);
        inventoryDrawer.drawImage(inventoryBase, 0, 0, null);
        inventoryDrawer.drawRoundRect(selectedInventoryItem * 64 + 2, 2, 60, 60, 16, 16);
        inventory.getTexture().set(inventoryImage);
    }

    private static BufferedImage copyImage(BufferedImage source){
        BufferedImage b = new BufferedImage(source.getWidth(), source.getHeight(), source.getType());
        Graphics g = b.getGraphics();
        g.drawImage(source, 0, 0, null);
        g.dispose();
        return b;
    }
}
