package ru.reactiveturtle.game.base;

import org.joml.Vector3f;
import ru.reactiveturtle.engine.base.Disposeable;
import ru.reactiveturtle.engine.base3d.Stage3D;
import ru.reactiveturtle.engine.geometry.Frustum;
import ru.reactiveturtle.engine.model.Model;
import ru.reactiveturtle.engine.base.Renderable;
import ru.reactiveturtle.engine.shadow.ShadowRenderable;
import ru.reactiveturtle.game.Helper;
import ru.reactiveturtle.game.Log;
import ru.reactiveturtle.game.world.BoxBodyModel;
import ru.reactiveturtle.physics.BoxBody;

public class EntityState implements ShadowRenderable, Renderable<Stage3D>, Disposeable {
    private Entity entity;

    private Model model;
    private BoxBody body;
    private boolean isInFrustum = false;
    private boolean isInFrustumCalled = false;

    public EntityState(Entity entity, Model model, BoxBody body) {
        this.entity = entity;
        this.model = model;
        this.body = body;
    }

    @Override
    public void render(Stage3D stage) {
        Vector3f position = entity.getPosition();
        Vector3f rotation = entity.getRotation();

        body.setPosition(position);
        body.setRotation(rotation);

        Frustum frustum = stage.getCamera().getFrustum();
        boolean isInFrustum = frustum.isFigureInFrustum(body.getBoxPoints());
        if (isInFrustum) {
            Log.addInFrustumEntityKey((String) model.getMeshes().keySet().toArray()[0]);

            model.setPosition(position);
            model.setRotation(rotation);
            model.render(stage);

            // For debugging
            /*BoxBodyModel boxBodyModel = Helper.bodyToModel(body);
            boxBodyModel.setShader(model.getShader());
            boxBodyModel.render(stage);*/
        }
    }

    @Override
    public void renderShadow(Stage3D stage) {
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

    public BoxBody getBody() {
        return body;
    }

    public Model getModel() {
        return model;
    }

    @Override
    public void dispose() {

    }
}
