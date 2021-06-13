package ru.reactiveturtle.game.base;

import org.joml.Vector3f;
import ru.reactiveturtle.engine.base.Disposeable;
import ru.reactiveturtle.engine.base3d.Stage3D;
import ru.reactiveturtle.engine.geometry.Frustum;
import ru.reactiveturtle.engine.model.Model;
import ru.reactiveturtle.engine.base.Renderable;
import ru.reactiveturtle.engine.shadow.ShadowRenderable;
import ru.reactiveturtle.game.Helper;
import ru.reactiveturtle.game.world.BoxBodyModel;
import ru.reactiveturtle.physics.BoxBody;

public class EntityPhase implements ShadowRenderable, Renderable<Stage3D>, Disposeable {
    private Entity entity;

    private Model model;
    private BoxBody body;
    private boolean isInFrustum = false;
    private boolean isInFrustumCalled = false;

    public EntityPhase(Entity entity, Model model, BoxBody body) {
        this.entity = entity;
        this.model = model;
        this.body = body;
    }

    @Override
    public void render(Stage3D stage) {
        if (entity.isEntityPositionUpdated) {
            Vector3f position = entity.getPosition();
            body.setPosition(position);
            entity.isEntityPositionUpdated = false;
        }
        if (entity.isEntityRotationUpdated) {
            Vector3f rotation = entity.getRotation();
            body.setRotation(rotation);
            entity.isEntityRotationUpdated = false;
        }

        Frustum frustum = stage.getCamera().getFrustum();
        boolean isInFrustum = frustum.isFigureInFrustum(body.getBoxPoints());
        if (isInFrustum) {
            model.setPosition(body.getPosition());
            model.setRotation(body.getRotation());
            model.render(stage);

            // For debugging
            debug(stage);
        }
    }

    private void debug(Stage3D stage) {
        BoxBodyModel boxBodyModel = Helper.bodyToModel(body);
        boxBodyModel.setShader(model.getShader());
        boxBodyModel.render(stage);
    }

    @Override
    public void renderShadow(Stage3D stage) {
        model.renderShadow(stage);
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
