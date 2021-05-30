package ru.reactiveturtle.game.base;

import ru.reactiveturtle.engine.base.Disposeable;
import ru.reactiveturtle.engine.model.Model;
import ru.reactiveturtle.game.MainGame;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

public class EntityArchive implements Disposeable {
    private String tag;
    private Model model;
    private Class<? extends Entity> entityClass;

    public EntityArchive(Entity entity) {
        Objects.requireNonNull(entity);
        this.tag = entity.getTag();
        this.model = entity.getCurrentState().getModel();
        this.entityClass = entity.getClass();
    }

    public String getTag() {
        return tag;
    }

    public Model getModel() {
        return model;
    }

    public Entity build(MainGame gameContext) {
        try {
            Constructor<? extends Entity> constructor = entityClass.getConstructor(MainGame.class, String.class);
            return constructor.newInstance(gameContext, tag);
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            try {
                Constructor<? extends Entity> constructor = entityClass.getConstructor(MainGame.class);
                return constructor.newInstance(gameContext);
            } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e1) {
                e.printStackTrace();
                e1.printStackTrace();
            }
        }
        throw new UnsupportedOperationException("Entity can't create from class instance " + entityClass.getName());
    }

    @Override
    public void dispose() {
        tag = null;
        model = null;
        entityClass = null;
    }
}
