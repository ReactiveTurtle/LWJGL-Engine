package ru.reactiveturtle.game.base;

import org.joml.Vector3f;
import ru.reactiveturtle.engine.base.Stage;
import ru.reactiveturtle.engine.geometry.Frustum;
import ru.reactiveturtle.engine.model.Model;
import ru.reactiveturtle.engine.model.Renderable;
import ru.reactiveturtle.engine.shadow.Shadow;
import ru.reactiveturtle.game.Helper;
import ru.reactiveturtle.game.world.BoxBodyModel;
import ru.reactiveturtle.physics.BoxBody;
import ru.reactiveturtle.physics.Transform3D;

public class EntityState extends Transform3D implements Shadow, Renderable {
    private Model model;
    private BoxBody body;
    private boolean isInFrustum = false;
    private boolean isInFrustumCalled = false;

    public EntityState(Model model, BoxBody body) {
        this.model = model;
        this.body = body;
    }

    @Override
    public void render(Stage stage) {
        Frustum frustum = stage.getCamera().getFrustum();
        boolean isInFrustum = frustum.isFigureInFrustum(body.getBoxPoints());
        if (isInFrustum) {
            System.out.println(model.getMeshes().keySet().toArray()[0]);
            model.setPosition(getBody().getPosition());
            model.setRotation(getBody().getRotation());
            model.render(stage);

            // For debugging
            BoxBodyModel boxBodyModel = Helper.bodyToModel(body);
            boxBodyModel.setShader(model.getShader());
            boxBodyModel.render(stage);
        }
    }

    @Override
    public void renderShadow(Stage stage) {
        Frustum frustum = stage.getCamera().getFrustum();
        if (isInFrustumCalled) {
            if (isInFrustum) {
                model.renderShadow(stage);
            }
            isInFrustumCalled = false;
            isInFrustum = false;
        } else if ((isInFrustum = frustum.isFigureInFrustum(body.getBoxPoints()))) {
            model.renderShadow(stage);
            isInFrustumCalled = true;
        }
    }

    @Override
    public void setPosition(float x, float y, float z) {
        model.setPosition(x, y, z);
    }

    @Override
    public void setRotation(float x, float y, float z) {
        model.setRotation(x, y, z);
    }

    @Override
    public Vector3f getPosition() {
        return model.getPosition();
    }

    @Override
    public Vector3f getRotation() {
        return model.getRotation();
    }

    public BoxBody getBody() {
        return body;
    }

    public Model getModel() {
        return model;
    }
}
