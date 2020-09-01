package ru.reactiveturtle.game.game.player;

public class Inventory {
    private Collectable[] items = new Collectable[8];
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

    public void setItem(int position, Collectable item) {
        items[position] = item;
    }

    public void setItem(Collectable item) {
        items[currentItemPosition] = item;
    }
}
