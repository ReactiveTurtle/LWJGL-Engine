package ru.reactiveturtle.engine.shadow;

import ru.reactiveturtle.engine.texture.DepthTexture;

import static org.lwjgl.opengl.GL11.GL_NONE;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glDrawBuffer;
import static org.lwjgl.opengl.GL11.glReadBuffer;
import static org.lwjgl.opengl.GL30.*;

public class ShadowMap {
    private static final int WIDTH = 2048, HEIGHT = 2048;
    private final DepthTexture depthTexture;
    private final int frameBufferId;

    public ShadowMap() {
        depthTexture = new DepthTexture(WIDTH, HEIGHT);
        frameBufferId = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, frameBufferId);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, depthTexture.getTextureId(), 0);
        // Set only depth
        glDrawBuffer(GL_NONE);
        glReadBuffer(GL_NONE);

        if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
            throw new IllegalArgumentException("Could not create FrameBuffer");
        }
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    public int getFrameBufferId() {
        return frameBufferId;
    }

    public DepthTexture getDepthTexture() {
        return depthTexture;
    }
}
