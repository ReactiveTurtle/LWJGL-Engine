package engine.shader;

import engine.util.Texture;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

public class ShadowMap {
    protected static final int SHADOW_MAP_WIDTH = 1024;
    protected static final int SHADOW_MAP_HEIGHT = 1024;

    private final int depthMapBufferId;
    private final Texture depthMap;

    public ShadowMap() throws Exception {
        depthMapBufferId = GL30.glGenFramebuffers();
        depthMap = new Texture(SHADOW_MAP_WIDTH, SHADOW_MAP_HEIGHT, GL11.GL_DEPTH_COMPONENT);
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, depthMapBufferId);
        GL30.glDrawBuffer(GL11.GL_NONE);
        GL30.glReadBuffer(GL11.GL_NONE);
        GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL11.GL_TEXTURE_2D, depthMap.getTextureId(), 0);
        if (GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER) != GL30.GL_FRAMEBUFFER_COMPLETE) {
            throw new Exception("Could not create FrameBuffer");
        }
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
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
}
