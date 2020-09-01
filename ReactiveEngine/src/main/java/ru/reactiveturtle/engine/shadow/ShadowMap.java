package ru.reactiveturtle.engine.shadow;

import ru.reactiveturtle.engine.material.Texture;

import static org.lwjgl.opengl.GL11.GL_NONE;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glDrawBuffer;
import static org.lwjgl.opengl.GL11.glReadBuffer;
import static org.lwjgl.opengl.GL30.*;

public class ShadowMap {
    private static final int WIDTH = 2048, HEIGHT = 2048;
    private Texture shadowTexture;
    private int frameBufferId;
    public ShadowMap() {
        frameBufferId = glGenFramebuffers();
        shadowTexture = new Texture(WIDTH, HEIGHT);
        glBindFramebuffer(GL_FRAMEBUFFER, frameBufferId);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, shadowTexture.getTextureId(), 0);
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

    public Texture getShadowTexture() {
        return shadowTexture;
    }
}
