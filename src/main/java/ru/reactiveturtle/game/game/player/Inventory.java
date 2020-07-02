package ru.reactiveturtle.game.game.player;

import ru.reactiveturtle.game.engine.base.GameContext;
import ru.reactiveturtle.game.engine.base2d.Square;
import ru.reactiveturtle.game.engine.base2d.SquareShader;
import ru.reactiveturtle.game.engine.material.Texture;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Inventory {
    private int selectedInventoryItem;

    public void wheelInventory(double dy) {
        selectedInventoryItem += dy;
        if (selectedInventoryItem < 0) {
            selectedInventoryItem = 7;
        } else if (selectedInventoryItem > 7) {
            selectedInventoryItem = 0;
        }
    }

    public int getSelectedInventory() {
        return selectedInventoryItem;
    }
}
