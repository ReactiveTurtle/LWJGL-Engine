package ru.reactiveturtle.game.player;

import ru.reactiveturtle.engine.base.GameContext;
import ru.reactiveturtle.engine.base3d.Stage3D;
import ru.reactiveturtle.engine.base2d.Square;
import ru.reactiveturtle.engine.base2d.SquareShader;
import ru.reactiveturtle.engine.texture.Texture;
import ru.reactiveturtle.engine.ui.UIContext;
import ru.reactiveturtle.engine.ui.UIElement;
import ru.reactiveturtle.game.base.Entity;
import ru.reactiveturtle.game.player.inventory.Inventory;
import ru.reactiveturtle.game.player.inventory.InventoryItem;
import ru.reactiveturtle.game.types.Collectable;
import ru.reactiveturtle.game.types.Container;
import ru.reactiveturtle.game.types.Destructible;
import ru.reactiveturtle.game.types.Firebox;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;

public class Interface {
    private UIElement center;

    private Square health;
    private BufferedImage healthImage;
    private Graphics2D healthDrawer;

    private UIElement inventory;
    private BufferedImage inventoryBase;
    private BufferedImage inventoryImage;
    private Graphics2D inventoryDrawer;

    private UIElement needs;
    private BufferedImage needsHungerIcon;
    private BufferedImage needsImage;
    private Graphics2D needsDrawer;

    private Square takeNotification;
    private BufferedImage takeNotificationImage;
    private Graphics2D takeNotificationDrawer;

    public Interface(UIContext uiContext) {
        healthImage = new BufferedImage(256, 16, BufferedImage.TYPE_INT_ARGB);
        healthDrawer = healthImage.createGraphics();
        healthDrawer.setColor(new Color(0, 0, 0, 128));
        healthDrawer.fillRect(0, 0, 256, 16);
        healthDrawer.setColor(Color.RED);
        healthDrawer.fillRect(0, 0, (int) (256 * 0.8f), 16);
        health = new Square(0.3f, 0.01875f,
                new Texture(healthImage),
                1f, 1f);
        health.setPosition(-1f * uiContext.getScreenAspectRatio() + health.getWidth() * 1.2f, -1f + health.getHeight() + health.getWidth() * 0.2f, 0);

        BufferedImage image = new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        graphics.setColor(Color.WHITE);
        graphics.fillOval(0, 0, 256, 256);
        center = new UIElement(uiContext, 0.0125f, 0.0125f);
        center.setTexture(new Texture(image));
        uiContext.getUILayout().add(center);

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
            inventoryDrawer.setFont(inventoryDrawer.getFont().deriveFont(16f));
            inventory = new UIElement(uiContext, 0.8f, 0.1f);
            inventory.setTexture(new Texture(inventoryImage));
            inventory.setY(-1f + inventory.getHeight());
            uiContext.getUILayout().add(inventory);

            needsImage = new BufferedImage(512, 256, BufferedImage.TYPE_INT_ARGB);
            needsDrawer = needsImage.createGraphics();
            needsHungerIcon = ImageIO.read(new File(GameContext.RESOURCE_PATH + "/texture/hunger.png"));
            needs = new UIElement(uiContext, 0.2f, 0.1f);
            needs.setTexture(new Texture(needsImage));
            needs.setPosition(-1f * uiContext.getScreenAspectRatio() + needs.getWidth() + health.getWidth() * 0.2f,
                    -1f + needs.getHeight() + health.getWidth() * 0.2f + health.getHeight() * 4f, 0);
            uiContext.getUILayout().add(needs);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static BufferedImage copyImage(BufferedImage source) {
        BufferedImage b = new BufferedImage(source.getWidth(), source.getHeight(), source.getType());
        Graphics g = b.getGraphics();
        g.drawImage(source, 0, 0, null);
        g.dispose();
        return b;
    }

    private int getXCenterLocation(Graphics2D graphics2D, int width, String string) {
        return (int) ((width - graphics2D.getFontMetrics().stringWidth(string)) / 2f);
    }

    private void clearDrawer(Graphics2D graphics2D, int width, int height) {
        graphics2D.setComposite(AlphaComposite.Clear);
        graphics2D.fillRect(0, 0, width, height);
        graphics2D.setComposite(AlphaComposite.SrcOver);
    }

    private String toNormalTime(float seconds) {
        String result = "";
        int hours = (int) (seconds / 3600f);
        int minutes = (int) ((seconds % 3600f) / 60f);
        if (hours == 0) {
            int sec = (int) ((seconds % 3600f) % 60f);
            if (minutes == 0) {
                result = sec + "s";
            } else {
                result = minutes + "m " + sec + "s";
            }
        } else {
            result = hours + "h " + minutes + "m";
        }
        return result;
    }

    public void updateInventoryImage(Inventory inventory) {
        inventoryDrawer.setComposite(AlphaComposite.Clear);
        inventoryDrawer.fillRect(0, 0, 512, 64);
        inventoryDrawer.setComposite(AlphaComposite.SrcOver);
        inventoryDrawer.drawImage(inventoryBase, 0, 0, null);
        inventoryDrawer.drawRoundRect(inventory.getCurrentItemPosition() * 64 + 2, 2, 60, 60, 16, 16);
        for (int i = 0; i < 8; i++) {
            String count = inventory.getItemCount(i) + "";
            inventoryDrawer.drawString(count, (i + 1) * 64 - inventoryDrawer.getFontMetrics().stringWidth(count) - 8,
                    64 - 8);
        }
        this.inventory.getTexture().set(inventoryImage);
    }

    public void updateNeedsImage(Needs needs) {
        clearDrawer(needsDrawer, 512, 256);
        needsDrawer.drawImage(needsHungerIcon, 64, 64, 128, 128, null);
        for (int i = 0; i < 5760 * needs.getHunger() / needs.getMaxHunger(); i++) {
            float cos = (float) Math.cos(Math.toRadians(i / 16f - 90));
            float sin = (float) Math.sin(Math.toRadians(i / 16f - 90));
            needsDrawer.drawLine((int) (128 + cos * 96), (int) (128 + sin * 96), (int) (128 + cos * 128), (int) (128 + sin * 128));
        }
        this.needs.getTexture().set(needsImage);

        clearDrawer(healthDrawer, 512, 64);
        healthDrawer.setColor(new Color(0, 0, 0, 128));
        healthDrawer.fillRect(0, 0, 256, 16);
        healthDrawer.setColor(Color.RED);
        healthDrawer.fillRect(0, 0, (int) (256f * needs.getHealth() / needs.getMaxHealth()), 16);
        this.health.getTexture().set(healthImage);
    }
}
