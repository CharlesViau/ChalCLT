package Domain.General.Components;

import Domain.CustumAnnotations.HideInInspector;
import Domain.General.Entity;
import Domain.Interface.IHoverable;
import Domain.Interface.IInspectable;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.UUID;

public abstract class Component implements Serializable, IInspectable, IHoverable {
    
    @HideInInspector
    public final Entity entity;
    @HideInInspector
    public final Transform transform;
    @HideInInspector
    public boolean showInInspector = true;

    protected Component(Entity _entity) {

        this.entity = _entity;
        this.transform = entity.transform;

    }

    // Use this method to get other components on the same Entity
    protected final <T extends Component> T getComponent(Class<T> componentType) {
        return entity.getComponent(componentType);
    }

    // Protected method for adding a component type to the parent Entity
    protected final void addComponent(Class<? extends Component> componentType) {
        entity.addComponent(componentType);

    }

    // Protected method for removing a component type from the parent Entity
    protected final void removeComponent(Class<? extends Component> componentType) {
        entity.removeComponent(componentType);
    }

    protected final Entity instantiate() {
        return instantiatePrivate(new Object[]{});
    }

    protected final Entity instantiate(String name) {

        return instantiatePrivate(new Object[]{name});
    }

    protected final Entity instantiate(String name, Entity parent) {

        return instantiatePrivate(new Object[]{name, parent});
    }

    protected final Entity instantiate(String name, Entity parent, Class<? extends Component>[] componentTypeToAdd) {
        return instantiatePrivate(new Object[]{name, parent, componentTypeToAdd});
    }

    protected void onDestroy() {

    }

    private Entity instantiatePrivate(Object... constructionArgs) {
        Method instantiateMethod;

        try {
            // Get the instantiate method of the Entity class
            instantiateMethod = Entity.class.getDeclaredMethod("instantiate", Object[].class);

            instantiateMethod.setAccessible(true);
            // Invoke the instantiate method with the appropriate parameters
            Entity newEntity = (Entity) instantiateMethod.invoke(entity, new Object[]{constructionArgs});

            instantiateMethod.setAccessible(false);

            return newEntity;

        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("Couldn't instantiate object.");
        }
    }

    @Override
    public UUID getUuid() {
        return entity.getUuid();
    }

    @Override
    public String getName() {
        return entity.getName();
    }

    @Override
    public IInspectable getParent() {
        return entity.getParent();
    }

    @Override
    public ArrayList<IInspectable> getIChildren() {
        return entity.getIChildren();
    }

    @Override
    public Entity getEntity() {
        return entity;
    }

    @Override
    public String onHover() {
        return "";
    }
}
