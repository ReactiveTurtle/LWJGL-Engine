package ru.reactiveturtle.engine.ui;

import ru.reactiveturtle.engine.base.GameContext;
import ru.reactiveturtle.engine.base.Stage;
import ru.reactiveturtle.engine.base2d.SquareShader;
import ru.reactiveturtle.engine.model.Disposeable;
import ru.reactiveturtle.engine.model.Renderable;
import ru.reactiveturtle.engine.toolkit.ReactiveList;

import java.util.Random;

public class UIContext implements Renderable<Stage>, Disposeable {
    private GameContext gameContext;
    private SquareShader squareShader;
    private final ReactiveList<Integer> elementIdList = new ReactiveList<>();
    private UILayout<UIElement> uiLayout;

    public UIContext(GameContext gameContext) {
        this.gameContext = gameContext;
        squareShader = new SquareShader();
        uiLayout = new UILayout<>(this);
    }

    @Override
    public void render(Stage stage) {
        uiLayout.render(stage);
    }

    public UILayout<UIElement> getUILayout() {
        return uiLayout;
    }

    public int getScreenWidth() {
        return gameContext.width;
    }

    public int getScreenHeight() {
        return gameContext.height;
    }

    public float getScreenAspectRatio() {
        return gameContext.getAspectRatio();
    }

    @Override
    public void dispose() {
        gameContext = null;
        squareShader.dispose();
        squareShader = null;
        uiLayout.dispose();
        uiLayout = null;
    }

    public void removeElement(int id) {
        uiLayout.remove(id);
    }

    private final Random random = new Random();

    public int generateId() {
        int randInt = (int) (random.nextDouble() * Integer.MAX_VALUE);
        while (elementIdList.contains(randInt)) {
            randInt = (int) (random.nextDouble() * Integer.MAX_VALUE);
        }
        return randInt;
    }

    public <T extends UIElement> void addIdFor(T element, int id) {
        elementIdList.add(id);
        element.setId(id);
    }

    public <T extends UIElement> void removeIdFor(int id) {
        elementIdList.remove(id);
    }
}
