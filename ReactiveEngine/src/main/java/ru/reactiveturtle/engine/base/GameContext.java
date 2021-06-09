package ru.reactiveturtle.engine.base;

import org.joml.Vector2f;
import org.joml.Vector2i;
import org.lwjgl.BufferUtils;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFWWindowFocusCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryStack;
import ru.reactiveturtle.engine.base3d.Stage3D;
import ru.reactiveturtle.engine.base.control.CursorCallback;
import ru.reactiveturtle.engine.shadow.ShadowManager;

import java.nio.DoubleBuffer;
import java.nio.IntBuffer;
import java.util.*;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * Игровой контекст. Здесь стартует игра.
 * Контекст содержит всю основную информацию об окне игры.
 * Унаследуйте какой-либо класс от этого класса.
 * Чтобы запустить игру вызовите метод start().
 */
public abstract class GameContext implements Disposeable {
    public static final String ENGINE_RESOURCE_PATH = "ReactiveEngine/src/main/resources/";
    public static final String RESOURCE_PATH = "src/main/resources/";

    protected abstract void run();

    public void dispose() {
        mShadowManager.setShadowEnabled(false);
        mShadowManager.release();
    }

    private static final String NAME = "Shooter";

    private boolean mIsFullscreen;
    public int width = 1200, height = 675;

    private long windowId;

    private ShadowManager mShadowManager;

    private boolean isWindowFocused = true;

    private double lastTime;
    private double deltaTime;

    /**
     * Метод отвечает за запуск окна игры и последующий рендеринг
     * @param isWindowResizable - Означает можно ли менять размер окна
     * @param isCursorCenter - Означает центрировать ли мышку постоянно при рендеринге
     */
    public void start(boolean isWindowResizable, boolean isCursorCenter) {
        this.isCursorCenter = isCursorCenter;
        System.out.println("LWJGL version - " + Version.getVersion());

        init(isWindowResizable);

        startRender();

        glfwFreeCallbacks(windowId);
        glfwDestroyWindow(windowId);

        glfwTerminate();

        Objects.requireNonNull(glfwSetErrorCallback(null)).free();
    }

    private void init(boolean isWindowResizable) {
        glfwInit();
        GLFWErrorCallback.createPrint(System.err).set();
        if (!glfwInit())
            throw new IllegalStateException("Unable to initialize GLFW");

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, isWindowResizable ? GLFW_TRUE : GLFW_FALSE);
        glfwWindowHint(GLFW_FOCUSED, GLFW_TRUE);

        GLFWVidMode vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        if (vidMode == null)
            return;
        //width = vidMode.width();height = vidMode.height();

        windowId = glfwCreateWindow(width, height, NAME, mIsFullscreen ? glfwGetPrimaryMonitor() : 0, NULL);

        if (windowId == NULL)
            throw new RuntimeException("Failed to create the GLFW window");

        glfwSetWindowFocusCallback(windowId, new GLFWWindowFocusCallback() {
            @Override
            public void invoke(long window, boolean focused) {
                isWindowFocused = focused;
                if (isWindowFocused) {
                    glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_HIDDEN);
                } else {
                    glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
                }
            }
        });

        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1);
            IntBuffer pHeight = stack.mallocInt(1);

            glfwGetWindowSize(windowId, pWidth, pHeight);

            int dWidth = pWidth.get(), dHeight = pHeight.get();

            System.out.println("vidMode: width - " + vidMode.width() + ", height - " + vidMode.height());
            System.out.println("programWindow: width - " + dWidth + ", height - " + dHeight);
            glfwSetWindowPos(windowId,
                    (int) (vidMode.width() / 2f - dWidth / 2f),
                    (int) (vidMode.height() / 2f - dHeight / 2f));
        }

        glfwMakeContextCurrent(windowId);
        GL.createCapabilities();
        glEnable(GL11.GL_DEPTH_TEST);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glfwSwapInterval(1);

        glfwShowWindow(windowId);

        mShadowManager = new ShadowManager(this);
        run();

        if (cursorCallback != null) {
            lastCursorPosition = getCursorPosition();
        }

        moveCursorToCenter();

        glfwFocusWindow(windowId);

        lastTime = glfwGetTime();
    }

    private CursorCallback cursorCallback;
    private boolean isCursorCenter;
    private Vector2f lastCursorPosition;
    private float cursorSensitivity = 0.1f;

    private void startRender() {
        double time = glfwGetTime();
        Vector2i windowCenter = new Vector2i(width / 2, height / 2);
        System.out.println("Загрузка завершена. Время загрузки: " + Math.round(time * 1000_000) / 1000f + " миллисекунд");

        while (!glfwWindowShouldClose(windowId)) {
            if (isWindowFocused) {
                glfwPollEvents();
                deltaTime = glfwGetTime() - lastTime;
                lastTime = glfwGetTime();
                if (stage != null) {
                    stage.render();
                    stage.renderables.forEach(e -> e.render(stage));
                }
                glfwSwapBuffers(windowId);

                if (cursorCallback != null) {
                    if (isCursorCenter) {
                        Vector2f vector2f = getCursorPosition();
                        cursorCallback.onMouseMove(vector2f.sub(new Vector2f(windowCenter)).mul(cursorSensitivity));
                        moveCursorToCenter();
                    } else {
                        Vector2f vector2f = getCursorPosition();
                        cursorCallback.onMouseMove(new Vector2f(vector2f).sub(lastCursorPosition).mul(cursorSensitivity));
                        lastCursorPosition = vector2f;
                    }
                }
                if (glfwGetTime() - time >= 0.2f) {
                    glfwSetWindowTitle(windowId, NAME + " " + (int) (1d / deltaTime));
                    time = glfwGetTime();
                }
            } else {
                glfwWaitEvents();
            }
        }
        dispose();
    }

    public void setFullscreen(boolean isFullscreen) {
        mIsFullscreen = isFullscreen;
    }

    public boolean isFullscreen() {
        return mIsFullscreen;
    }

    public double getDeltaTime() {
        return deltaTime;
    }

    public ShadowManager getShadowManager() {
        return mShadowManager;
    }

    public float getAspectRatio() {
        return (float) width / height;
    }

    public void setCursorCallback(CursorCallback cursorCallback) {
        this.cursorCallback = cursorCallback;
    }

    public Vector2f getCursorPosition() {
        DoubleBuffer xBuffer = BufferUtils.createDoubleBuffer(1);
        DoubleBuffer yBuffer = BufferUtils.createDoubleBuffer(1);
        glfwGetCursorPos(windowId, xBuffer, yBuffer);
        return new Vector2f((float) xBuffer.get(0), (float) yBuffer.get(0));
    }

    public void moveCursorToCenter() {
        glfwSetCursorPos(windowId, width / 2f, height / 2f);
    }

    public long getWindowId() {
        return windowId;
    }

    private Stage3D stage = null;

    public void setStage(Stage3D stage) {
        this.stage = stage;
        updateKeyCallback();
    }

    public Stage3D getStage() {
        return stage;
    }

    public void updateKeyCallback() {
        if (stage != null) {
            glfwSetKeyCallback(windowId, stage.keyCallback == null ? null : (window, key, scancode, action, mods) -> stage.keyCallback.onChange(key, action));
        }
    }

    public void updateMouseCallback() {
        if (stage != null) {
            glfwSetMouseButtonCallback(windowId, stage.mouseCallback == null ? null : (window, button, action, mods) -> {
                switch (action) {
                    case GLFW_PRESS:
                        switch (button) {
                            case GLFW_MOUSE_BUTTON_LEFT:
                                stage.mouseCallback.onLeftButtonDown();
                                break;
                            case GLFW_MOUSE_BUTTON_RIGHT:
                                stage.mouseCallback.onRightButtonDown();
                                break;
                        }
                        break;
                    case GLFW_RELEASE:
                        switch (button) {
                            case GLFW_MOUSE_BUTTON_LEFT:
                                stage.mouseCallback.onLeftButtonUp();
                                break;
                            case GLFW_MOUSE_BUTTON_RIGHT:
                                stage.mouseCallback.onRightButtonUp();
                                break;
                        }
                        break;
                }
            });

            glfwSetScrollCallback(windowId, stage.mouseCallback == null ? null : (l, dx, dy) ->
                    stage.mouseCallback.onScroll((int) Math.signum(dy)));
        }
    }

    public boolean isKeyPressed(int key) {
        return glfwGetKey(windowId, key) == GLFW_PRESS;
    }

    public int getKey(int key) {
        return glfwGetKey(windowId, key);
    }

    public void closeWindow() {
        glfwSetWindowShouldClose(windowId, true);
        System.exit(0);
    }
}
