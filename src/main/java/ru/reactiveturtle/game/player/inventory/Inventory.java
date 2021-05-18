package ru.reactiveturtle.game.player.inventory;


import ru.reactiveturtle.game.types.Collectable;

public class Inventory {
    private Collectable[] items = new Collectable[8];
    private int[] itemsCount = new int[8];
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

    public Collectable getCurrentItem() {
        return items[currentItemPosition];
    }

    public void addItem(int position, Collectable item) {
        if (items[position] == null) {
            items[position] = item;
        }
        itemsCount[position]++;
    }

    public void removeItem(int position) {
        itemsCount[position]--;
        if (itemsCount[position] == 0) {
            items[position] = null;
        }
    }

    public Collectable getItem(int position) {
        return items[position];
    }

    public int getInventorySize() {
        return items.length;
    }

    public Collectable[] getItems() {
        return items;
    }

    public int[] getItemsCounter() {
        return itemsCount;
    }
}
