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
import ru.reactiveturtle.engine.camera.PerspectiveCamera;
import ru.reactiveturtle.engine.light.Light;
import ru.reactiveturtle.engine.base.control.CursorCallback;
import ru.reactiveturtle.engine.shadow.ShadowManager;

import java.nio.DoubleBuffer;
import java.nio.IntBuffer;
import java.util.*;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public abstract class GameContext {
    public static final String ENGINE_RESOURCE_PATH = "ReactiveEngine/src/main/resources/";
    public static final String RESOURCE_PATH = "src/main/resources/";

    protected abstract void run();

    protected abstract void render();

    protected void destroy() {
        mShadowManager.setShadowEnabled(false);
        mShadowManager.release();
    }

    private static final String NAME = "Shooter";

    private boolean mIsFullscreen;
    public static int width = 1200, height = 675;

    protected long defaultWindow;

    public static PerspectiveCamera camera;

    public static List<Light> lights = new ArrayList<>();

    private static ShadowManager mShadowManager;

    private boolean isWindowFocused = true;

    private CursorCallback cursorCallback;
    private boolean isCursorCenter;
    private Vector2f lastCursorPosition;
    private float cursorSensitivity = 0.1f;
    private double lastTime;
    private static double deltaTime;

    public void start(boolean isWindowResizable, boolean isCursorCenter) {
        this.isCursorCenter = isCursorCenter;
        System.out.println("LWJGL version - " + Version.getVersion());

        init(isWindowResizable);

        startRender();

        glfwFreeCallbacks(defaultWindow);
        glfwDestroyWindow(defaultWindow);

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

        defaultWindow = glfwCreateWindow(width, height, NAME, mIsFullscreen ? glfwGetPrimaryMonitor() : 0, NULL);

        if (defaultWindow == NULL)
            throw new RuntimeException("Failed to create the GLFW window");

        glfwSetWindowFocusCallback(defaultWindow, new GLFWWindowFocusCallback() {
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

            glfwGetWindowSize(defaultWindow, pWidth, pHeight);

            int dWidth = pWidth.get(), dHeight = pHeight.get();

            System.out.println("vidMode: width - " + vidMode.width() + ", height - " + vidMode.height());
            System.out.println("programWindow: width - " + dWidth + ", height - " + dHeight);
            glfwSetWindowPos(defaultWindow,
                    (int) (vidMode.width() / 2f - dWidth / 2f),
                    (int) (vidMode.height() / 2f - dHeight / 2f));
        }

        glfwMakeContextCurrent(defaultWindow);
        GL.createCapabilities();
        glEnable(GL11.GL_DEPTH_TEST);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glfwSwapInterval(1);

        glfwShowWindow(defaultWindow);

        mShadowManager = new ShadowManager();
        run();
        if (camera == null) {
            throw new IllegalArgumentException();
        }
        if (cursorCallback != null) {
            lastCursorPosition = getCursorPosition();
        }

        glfwFocusWindow(defaultWindow);

        lastTime = glfwGetTime();
    }

    private void startRender() {
        double time = glfwGetTime();
        Vector2i windowCenter = new Vector2i(width / 2, height / 2);
        System.out.println("Загрузка завершена. Время загрузки: " + Double.toString(time * 1000).substring(0, 7) + " миллисекунд");

        moveCursorToCenter();
        while (!glfwWindowShouldClose(defaultWindow)) {
            if (isWindowFocused) {
                glfwPollEvents();
                deltaTime = glfwGetTime() - lastTime;
                lastTime = glfwGetTime();
                render();
                glfwSwapBuffers(defaultWindow);

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
                    glfwSetWindowTitle(defaultWindow, NAME + " " + (int) (1d / deltaTime));
                    time = glfwGetTime();
                }
            } else {
                glfwWaitEvents();
            }
        }
        destroy();
        cursorCallback = null;
    }

    public void setFullscreen(boolean isFullscreen) {
        mIsFullscreen = isFullscreen;
    }

    public boolean isFullscreen() {
        return mIsFullscreen;
    }

    public static double getDeltaTime() {
        return deltaTime;
    }

    protected void setCamera(PerspectiveCamera camera) {
        GameContext.camera = camera;
    }

    protected void addLight(Light light) {
        lights.add(light);
    }

    protected void removeLight(int index) {
        lights.remove(index);
    }

    protected void setCursorCallback(CursorCallback cursorCallback) {
        this.cursorCallback = cursorCallback;
    }

    public Vector2f getCursorPosition() {
        DoubleBuffer xBuffer = BufferUtils.createDoubleBuffer(1);
        DoubleBuffer yBuffer = BufferUtils.createDoubleBuffer(1);
        glfwGetCursorPos(defaultWindow, xBuffer, yBuffer);
        return new Vector2f((float) xBuffer.get(0), (float) yBuffer.get(0));
    }

    public void moveCursorToCenter() {
        glfwSetCursorPos(defaultWindow, GameContext.width / 2, GameContext.height / 2);
    }


    public static ShadowManager getShadowManager() {
        return mShadowManager;
    }

    public static float getAspectRatio() {
        return (float) width / height;
    }
}
