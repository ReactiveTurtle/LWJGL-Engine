package engine.shader;

import engine.Base;
import engine.environment.DirectionalLight;
import engine.model.Model;
import engine.util.Texture;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;

public class ShadowMap {
    protected static final int SHADOW_MAP_WIDTH = 1024;
    protected static final int SHADOW_MAP_HEIGHT = 1024;

    private final int depthMapBufferId;
    private final Texture depthMap;
    private final ShadowShader shadowShader;

    public ShadowMap() {
        shadowShader = new ShadowShader();
        depthMapBufferId = GL30.glGenFramebuffers();
        depthMap = new Texture(SHADOW_MAP_WIDTH, SHADOW_MAP_HEIGHT, GL11.GL_DEPTH_COMPONENT);
        glBindFramebuffer(GL_FRAMEBUFFER, depthMapBufferId);
        GL30.glDrawBuffer(GL11.GL_NONE);
        GL30.glReadBuffer(GL11.GL_NONE);
        GL30.glFramebufferTexture2D(GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL11.GL_TEXTURE_2D, depthMap.getTextureId(), 0);
        if (GL30.glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL30.GL_FRAMEBUFFER_COMPLETE) {
            System.out.println("Could not create FrameBuffer");
        }
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    public Texture getDepthMapTexture() {
        return depthMap;
    }

    public int getDepthMapBufferId() {
        return depthMapBufferId;
    }

    public void clear() {
        GL30.glDeleteFramebuffers(depthMapBufferId);
        depthMap.destroy();
    }

    public void render(Model model) {
        glBindFramebuffer(GL_FRAMEBUFFER, depthMapBufferId);
        glViewport(0, 0, ShadowMap.SHADOW_MAP_WIDTH, ShadowMap.SHADOW_MAP_HEIGHT);
        glClear(GL_DEPTH_BUFFER_BIT);
        shadowShader.bind();

        DirectionalLight light = Base.directionalLight;
        if (light != null) {
            Vector3f lightDirection = light.getDirection();
            float lightAngleX = (float) Math.toDegrees(Math.acos(lightDirection.y));
            float lightAngleY = (float) Math.toDegrees(Math.asin(lightDirection.x));
            float lightAngleZ = 0;

            Matrix4f lightViewMatrix = updateGenericViewMatrix(new Vector3f(lightDirection).mul(5),
                    new Vector3f(lightAngleX, lightAngleY, lightAngleZ), new Matrix4f());
            Matrix4f orthoProjectionMatrix = new Matrix4f().ortho(-10.0f, 10.0f, -10.0f, 10.0f, -1.0f, 20.0f);

            Matrix4f modelLightViewMatrix = new Matrix4f().set(lightViewMatrix).mul(model.getModelMatrix());
            shadowShader.load(orthoProjectionMatrix.mul(modelLightViewMatrix), null);
        }

        shadowShader.unbind();
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    private Matrix4f updateGenericViewMatrix(Vector3f position, Vector3f rotation, Matrix4f matrix) {
        matrix.identity();
        // First do the rotation so camera rotates over its position
        matrix.rotate((float)Math.toRadians(rotation.x), new Vector3f(1, 0, 0))
                .rotate((float)Math.toRadians(rotation.y), new Vector3f(0, 1, 0));
        // Then do the translation
        matrix.translate(-position.x, -position.y, -position.z);
        return matrix;
    }
}
