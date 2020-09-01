package ru.reactiveturtle.game.engine.shadow;

public class ShadowManager {
    private boolean mIsShadowEnabled = false;
    private ShadowShader mShadowShader;

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

    public void startShadowRender() {
        mShadowShader.bind();
    }

    public void endShadowRender() {
        mShadowShader.unbind();
    }
}
