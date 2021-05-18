package ru.reactiveturtle.game.player.inventory;

import ru.reactiveturtle.game.types.Collectable;

public class InventoryItem {
    public final Collectable collectable;
    public final int count;

    public InventoryItem(Collectable collectable, int count) {
        this.collectable = collectable;
        this.count = count;
    }
}
