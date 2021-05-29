package ru.reactiveturtle.engine.base;

import ru.reactiveturtle.engine.toolkit.ReactiveList;
import ru.reactiveturtle.engine.ui.UIContext;

/**
 * Базовая сцена. Содержит в себе контекст игры и функции
 * обратной связи для периферийных устройств (клавиатура, мышь)
 */
public abstract class Stage {
    protected ReactiveList<Renderable<Stage>> renderables = new ReactiveList<>();
    public Stage(GameContext gameContext) {
        this.gameContext = gameContext;
        uiContext = new UIContext(gameContext);
        renderables.add(uiContext);
    }

    public abstract void start();

    public abstract void render();

    protected final GameContext gameContext;

    /**
     * Returns game context
     * @return GameContext
     */
    public GameContext getGameContext() {
        return gameContext;
    }

    protected KeyCallback keyCallback;

    public void setKeyCallback(KeyCallback keyCallback) {
        this.keyCallback = keyCallback;
        gameContext.updateKeyCallback();
    }

    public interface KeyCallback {
        void onChange(int key, int action);
    }

    protected MouseCallback mouseCallback;

    public void setMouseCallback(MouseCallback mouseCallback) {
        this.mouseCallback = mouseCallback;
        gameContext.updateMouseCallback();
    }

    public abstract static class MouseCallback {
        public void onLeftButtonDown() {

        }

        public void onLeftButtonUp() {

        }

        public void onRightButtonDown() {

        }

        public void onRightButtonUp() {

        }

        public void onScroll(int direction) {

        }
    }

    protected UIContext uiContext;

    public void enableUI() {
        uiContext.getUILayout().show();
    }

    public void disableUI() {
        uiContext.getUILayout().hide();
    }

    public UIContext getUIContext() {
        return uiContext;
    }
}
