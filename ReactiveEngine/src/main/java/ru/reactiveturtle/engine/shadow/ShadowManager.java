package ru.reactiveturtle.engine.shadow;

import ru.reactiveturtle.engine.base.GameContext;
import ru.reactiveturtle.engine.base3d.Stage3D;
import ru.reactiveturtle.engine.light.DirectionalLight;

import java.util.Objects;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;

public class ShadowManager {
    private GameContext gameContext;
    private boolean mIsShadowEnabled = false;
    private ShadowShader mShadowShader;

    public ShadowManager(GameContext gameContext) {
        Objects.requireNonNull(gameContext);
        this.gameContext = gameContext;
        mShadowShader = new ShadowShader();
    }

    public void setShadowEnabled(boolean isShadowEnabled) {
        mIsShadowEnabled = isShadowEnabled;
    }

    public boolean isShadowEnabled() {
        return mIsShadowEnabled;
    }

    public ShadowShader getShadowShader() {
        return mShadowShader;
    }

    public void renderShadow(Stage3D stage, ShadowRenderable... shadowRenderableRenderables) {
        if (mIsShadowEnabled) {
            glCullFace(GL_FRONT);
            DirectionalLight directionalLight = stage.getDirectionalLight();
            if (directionalLight != null && directionalLight.getShadowMap() != null) {
                glBindFramebuffer(GL_FRAMEBUFFER, directionalLight.getShadowMap().getFrameBufferId());
                glViewport(0, 0, directionalLight.getShadowMap().getDepthTexture().getWidth(),
                        directionalLight.getShadowMap().getDepthTexture().getHeight());
                glClear(GL_DEPTH_BUFFER_BIT);
                mShadowShader.bind();
                for (ShadowRenderable shadowRenderable : shadowRenderableRenderables) {
                    shadowRenderable.renderShadow(stage);
                }
                mShadowShader.unbind();
            }
            glCullFace(GL_BACK);
            glBindFramebuffer(GL_FRAMEBUFFER, 0);
            glViewport(0, 0, gameContext.width, gameContext.height);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        }
    }

    public void release() {
        mShadowShader.unbind();
    }
}
