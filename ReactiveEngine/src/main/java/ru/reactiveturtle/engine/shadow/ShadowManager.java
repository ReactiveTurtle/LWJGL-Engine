package ru.reactiveturtle.engine.shadow;

import ru.reactiveturtle.engine.base3d.Stage3D;
import ru.reactiveturtle.engine.light.DirectionalLight;
import ru.reactiveturtle.engine.light.Light;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;

public class ShadowManager {
    private boolean mIsShadowEnabled = false;
    private ShadowShader mShadowShader;
    public DirectionalLight renderingDirectionalLight;

    public ShadowManager() {
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

    public void renderShadow(Stage3D stage, Shadow... shadowRenderables) {
        if (mIsShadowEnabled) {
            glCullFace(GL_FRONT);
            DirectionalLight[] directionalLights = Light.getDirectionalLights(stage.getLights());
            for (DirectionalLight directionalLight : directionalLights) {
                if (directionalLight.getShadowMap() != null) {
                    mShadowShader.bind();
                    glBindFramebuffer(GL_FRAMEBUFFER, directionalLight.getShadowMap().getFrameBufferId());
                    glViewport(0, 0, directionalLight.getShadowMap().getShadowTexture().getWidth(),
                            directionalLight.getShadowMap().getShadowTexture().getHeight());
                    glClear(GL_DEPTH_BUFFER_BIT);
                    renderingDirectionalLight = directionalLight;
                    for (Shadow shadowRenderable : shadowRenderables) {
                        shadowRenderable.renderShadow(stage);
                    }
                    mShadowShader.unbind();
                }
            }
            glCullFace(GL_BACK);
            renderingDirectionalLight = null;
        }
    }

    public void release() {
        mShadowShader.unbind();
    }
}
