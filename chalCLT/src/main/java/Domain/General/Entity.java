package Domain.General;

import Domain.CustumAnnotations.HideInInspector;
import Domain.CustumAnnotations.SerializeField;
import Domain.CustumAnnotations.SetPropertyEvent;
import Domain.General.Components.Component;
import Domain.General.Components.Transform;
import Domain.Interface.IHoverable;
import Domain.Interface.IInspectable;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class Entity implements Serializable, IInspectable, IHoverable {

    public String name;

    @HideInInspector
    public final Transform transform;
    private final UUID uuid;
    @SerializeField
    private final Map<Class<? extends Component>, Component> components = new HashMap<>();

    private final transient EntityManager manager;
    private boolean isVisible;


    private Boolean showInHierarchy = true;

    //CONSTRUCTORS
    private Entity(EntityManager manager, String name, Entity parent, Class<? extends Component>[] componentTypeToAdd) {
        uuid = UUID.randomUUID();
        this.manager = manager;

        //Add Mandatory transform Component to object.
        transform = addComponent(Transform.class);

        //Set Object Name
        if (name == null) {
            this.name = "Object : " + uuid;
        } else {
            this.name = name;
        }

        //SetParent
        if (transform != null && parent != null)
            transform.setParent(parent.transform);

        //Add Components
        if (componentTypeToAdd != null) {
            for (Class<? extends Component> comp : componentTypeToAdd) {
                this.addComponent(comp);
            }
        }
    }

    private Entity(EntityManager manager) {
        this(manager, null);
    }

    private Entity(EntityManager manager, String name) {
        this(manager, name, null);
    }

    private Entity(EntityManager manager, String name, Entity parent) {
        this(manager, name, parent, null);
    }


    //CREATION FROM COMPONENT
    private Entity instantiate(Object... constructionArgs) {
        Method instantiateMethod;

        try {
            // Get the instantiate method of the EntityManager class
            instantiateMethod = EntityManager.class.getDeclaredMethod("instantiate", Object[].class);

            instantiateMethod.setAccessible(true);
            // Invoke the instantiate method with the appropriate parameters
            Entity newEntity = (Entity) instantiateMethod.invoke(manager, (Object) constructionArgs);

            instantiateMethod.setAccessible(false);

            return newEntity;

        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("Couldn't instantiate object.");
        }
    }

    //COMPONENTS RELATED
    public <T extends Component> T addComponent(Class<T> componentType) {
        if (components.containsKey(componentType))
            return null; // Component of the same type already exists

        try {
            Constructor<T> constructor = componentType.getDeclaredConstructor(Entity.class);
            constructor.setAccessible(true);
            T component = constructor.newInstance(this);
            constructor.setAccessible(false);
            components.put(componentType, component);
            return component; // Successfully added the component


        } catch (Exception e) {
            return null; // Return null in case of an exception
        }
    }

    public <T extends Component> T getComponent(Class<T> componentType) {
        Component component = components.get(componentType);
        if (component != null)
            return componentType.cast(component);
        throw new RuntimeException("couldn't get component " + componentType.getName() + " on " + this.name);
    }

    public <T extends Component> void removeComponent(Class<T> componentType) {
        if (componentType == Transform.class)
            return;
        components.remove(componentType);
    }

    //GETTER
    public UUID getUuid() {
        return uuid;
    }

    @Override
    public String getName() {
        return name;
    }

    public IInspectable getParent() {
        return (IInspectable) transform.getParent();
    }

    @Override
    public Entity getEntity() {
        return this;
    }

    public ArrayList<IInspectable> getIChildren() {
        ArrayList<IInspectable> children = new ArrayList<>();
        for (Transform child : transform.getChildren()) {
            children.add((IInspectable) child);
        }
        return children;
    }

    //DESTRUCTION
    public void Destroy(Entity entity) {
        manager.destroy(entity);
    }

    private void onDestroy() {
        for (Component comp : components.values()) {
            invokeOnDestroy(comp);
        }


        //In case if some other component use the transform in their onDestroy it should be done last.
        if (transform.getParent() != null) {
            transform.getParent().getChildren().remove(this.transform);
        }

    }

    //PRIVATE (You don't want to know)
    private void invokeOnDestroy(Component comp) {
        invokeMethod(comp, "onDestroy");
    }

    public static void invokeMethod(Object target, String methodName) {
        try {
            Class<?> clazz = target.getClass();

            // Iterate through the class hierarchy
            while (clazz != null) {
                try {
                    Method method = clazz.getDeclaredMethod(methodName);
                    method.setAccessible(true);
                    method.invoke(target);
                    method.setAccessible(false);
                    return;
                } catch (NoSuchMethodException e) {
                    // Method not found in the current class, check the superclass
                    clazz = clazz.getSuperclass();
                }
            }

            // If we reach here, the method was not found in the entire class hierarchy
            throw new NoSuchMethodException("Method not found: " + methodName);

        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException("Couldn't invoke method " + methodName, e);
        }
    }

    public void setVisible(boolean b) {
        isVisible = b;
    }

    public boolean isVisible() {
        return isVisible;
    }

    @Override
    public String onHover() {
        String hover = name + ": \n";
        for (Component component : components.values()) {
            if (component instanceof IHoverable) {
                hover += ((IHoverable) component).onHover();
            }
        }
        return hover;
    }
}
