package ru.reactiveturtle.game.player.inventory;


import ru.reactiveturtle.engine.toolkit.ReactiveList;
import ru.reactiveturtle.game.base.Entity;

public class Inventory {
    private ReactiveList<InventoryItem> items = new ReactiveList<>(new InventoryItem[8]);
    private int currentItemPosition;

    public void wheelInventory(double dy) {
        currentItemPosition += dy;
        if (currentItemPosition < 0) {
            currentItemPosition = 7;
        } else if (currentItemPosition > 7) {
            currentItemPosition = 0;
        }
    }

    public int getCurrentItemPosition() {
        return currentItemPosition;
    }

    public InventoryItem getCurrentItem() {
        return items.get(currentItemPosition);
    }

    public void addItem(int position, Entity entity) {
        InventoryItem inventoryItem = items.get(position);
        if (inventoryItem == null) {
            inventoryItem = new InventoryItem(entity, 0);
            items.set(position, inventoryItem);
        }
        inventoryItem.countUp(1);
    }

    public boolean removeItem(int position) {
        InventoryItem inventoryItem = getItem(position);
        if (inventoryItem == null || !inventoryItem.countDown(1)) {
            return false;
        }
        if (inventoryItem.getCount() == 0) {
            items.set(position, null);
        }
        return true;
    }

    public InventoryItem getItem(int position) {
        return items.get(position);
    }

    public int getItemCount(int position) {
        InventoryItem inventoryItem = items.get(position);
        return inventoryItem == null ? 0 : inventoryItem.getCount();
    }

    public boolean isCellEmpty(int position) {
        return getItem(position) == null;
    }

    public int getInventorySize() {
        return items.size();
    }
}
