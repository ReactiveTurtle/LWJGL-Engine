package engine;

import engine.environment.DirectionalLight;
import engine.environment.PointLight;
import engine.environment.SpotLight;
import org.joml.Vector2d;
import org.lwjgl.BufferUtils;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFWWindowFocusCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryStack;

import java.nio.DoubleBuffer;
import java.nio.IntBuffer;
import java.util.*;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public abstract class Base {

    protected abstract void run();

    protected abstract void render();

    protected abstract void destroy();

    private static final String NAME = "Shooter";

    public static int width = 800, height = 600;

    protected long defaultWindow;

    public static Camera camera;

    public static DirectionalLight directionalLight;

    public static List<PointLight> pointLightsList = new ArrayList<>();
    public static List<SpotLight> spotLightsList = new ArrayList<>();

    private boolean isWindowFocused = true;

    public void start() {
        System.out.println("LWJGL version - " + Version.getVersion());

        init();

        startRender();

        glfwFreeCallbacks(defaultWindow);
        glfwDestroyWindow(defaultWindow);

        glfwTerminate();

        Objects.requireNonNull(glfwSetErrorCallback(null)).free();
    }

    private void init() {
        glfwInit();
        GLFWErrorCallback.createPrint(System.err).set();
        if (!glfwInit())
            throw new IllegalStateException("Unable to initialize GLFW");

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);
        glfwWindowHint(GLFW_FOCUSED, GLFW_TRUE);

        GLFWVidMode vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        if (vidMode == null)
            return;
        width = vidMode.width();
        height = vidMode.height();

        defaultWindow = glfwCreateWindow(width, height, NAME, NULL, NULL);

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
            glfwSetWindowPos(defaultWindow, 0, 72);
        }

        glfwMakeContextCurrent(defaultWindow);
        GL.createCapabilities();
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        glfwSwapInterval(1);

        glfwShowWindow(defaultWindow);

        run();

        glfwFocusWindow(defaultWindow);
    }

    private void startRender() {
        int fps = 0;
        boolean isFirst = true;
        double time = glfwGetTime();
        System.out.println("Загрузка завершена. Время загрузки: " + Double.toString(time * 1000).substring(0, 7) + " миллисекунд");

        while (!glfwWindowShouldClose(defaultWindow)) {
            if (isWindowFocused) {
                if (isFirst) {
                    isFirst = false;
                    moveCursorToCenter();
                }
                render();
                if (glfwGetTime() - time >= 1) {
                    glfwSetWindowTitle(defaultWindow, NAME + "(" + fps + ")");
                    time = glfwGetTime();
                    fps = 0;
                } else {
                    fps++;
                }
            } else {
                isFirst = true;
                glfwWaitEvents();
            }
        }
        destroy();
    }

    protected void setCamera(Camera camera) {
        Base.camera = camera;
    }

    protected void setDirectionalLight(DirectionalLight directionalLight) {
        Base.directionalLight = directionalLight;
    }

    protected DirectionalLight getDirectionalLight() {
        return directionalLight;
    }

    protected void addPointLight(PointLight pointLight) {
        pointLightsList.add(pointLight);
    }

    protected void removePointLight(int index) {
        pointLightsList.remove(index);
    }

    protected void addSpotLight(SpotLight spotLight) {
        spotLightsList.add(spotLight);
    }

    protected void removeSpotLight(int index) {
        spotLightsList.remove(index);
    }

    protected Vector2d getCursorPosition() {
        DoubleBuffer xBuffer = BufferUtils.createDoubleBuffer(1);
        DoubleBuffer yBuffer = BufferUtils.createDoubleBuffer(1);
        glfwGetCursorPos(defaultWindow, xBuffer, yBuffer);
        return new Vector2d(xBuffer.get(0), yBuffer.get(0));
    }

    protected void moveCursorToCenter() {
        glfwSetCursorPos(defaultWindow, Base.width / 2, Base.height / 2);
    }
}
