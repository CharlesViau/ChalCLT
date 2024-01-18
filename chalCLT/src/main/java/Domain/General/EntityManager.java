package Domain.General;

import Domain.CustumAnnotations.AddObjectEvent;
import Domain.CustumAnnotations.SetPropertyEvent;
import Domain.General.Components.Component;

import Domain.Interface.IInspectable;
import Domain.Interface.IManager;
import Domain.Shack.Panels.Wall;
import Domain.Shack.Roof;
import Domain.Shack.Shack;
import Domain.Utility.Action;


import java.io.EOFException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.*;
import java.util.*;

public final class EntityManager implements IManager, Serializable {

    private final Map<UUID, Entity> entities;
    private static EntityManager _instance;
    public final transient Action OnPropertyChange = new Action();

    public EntityManager() {
        if (_instance != null)
            throw new RuntimeException("Entity Manager already exists!");

        entities = new HashMap<>();
        _instance = this;
    }

    @Override
    public void save(ObjectOutputStream objectOutputStream) {
        for (Entity entity : entities.values()) {
            try {
                objectOutputStream.writeObject(entity);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void load(ObjectInputStream objectInputStream) {
        entities.clear();
        try {
            while (true) {
                Entity entity = (Entity) objectInputStream.readObject();
                entities.put(entity.getUuid(), entity);
                System.out.println("Loaded object " + entity.name + " with UUID " + entity.getUuid());
            }
        } catch (EOFException e) {
            System.out.println("Finished loading objects");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void newProject() {
        entities.clear();

    }

    public ArrayList<IInspectable> getEntities() {
        ArrayList<IInspectable> inspectables = new ArrayList<>();
        for (Entity entity : entities.values()) {
            if (entity.transform.getParent() == null) {
                inspectables.add(entity);
            }
        }
        return inspectables;
    }

    public Shack getShack() {
        for (Entity entity : entities.values()) {
            try {
                Component component = entity.getComponent(Shack.class);
                if (component != null) {
                    return (Shack) component;
                }
            }
            catch (Exception e) {
                continue;
            }
        }
        return null;
    }


    //FROM INSPECTOR
    public Object editProperty(UUID objectUuid, Class<? extends Component> componentType, String fieldName, Object newValue) {

        Entity entity = getEntity(objectUuid);

        Object comp = getComponent(entity, componentType);

        if (comp == null) {
            comp = entity;
        }

        Field field = getField(comp, fieldName);

        assert field != null;
        Object oldValue;

        try {
            field.setAccessible(true);
            oldValue = field.get(comp);
            field.setAccessible(false);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }


        Method setMethod = field.getAnnotation(SetPropertyEvent.class) == null ?
                null : GetSetMethod(field, comp);

        Object convertedValue;

        if (newValue instanceof String) {
            convertedValue = convertStringToType((String) newValue, field.getType());
        } else {
            convertedValue = newValue;
        }

        if (setMethod != null) {
            // Invoke the setEvent if it exists
            InvokeSetMethod(setMethod, comp, convertedValue);
        } else {
            // Set the field directly
            setField(comp, field, convertedValue);
        }

        OnPropertyChange.invoke();

        return oldValue;
    }

    public Entity instantiate(UUID invokerUuid, Class<? extends Component> selectedComp, Class<? extends Component> componentTypeToAdd, Object[] args) {

        //get all needed object
        Entity entity = getEntity(invokerUuid);
        Component comp = getComponent(entity, selectedComp);
        Field field = getCollectionField(entity, comp, componentTypeToAdd);

        //Create Entity
        Entity newEntity = instantiate(componentTypeToAdd.getName(), entity, new Class[]{componentTypeToAdd});

        //Get new component Instance
        Component newComponent = newEntity.getComponent(componentTypeToAdd);

        //Prepare args for invocation
        Object[] convertedArgs = getAddEventInvocationArgs(newComponent, args, field);

        //get Method
        Method eventMethod = field.getAnnotation(AddObjectEvent.class) == null ?
                null : GetAddMethod(field, comp);

        if (eventMethod == null)
            return null;


        try {
            eventMethod.invoke(comp, convertedArgs);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }

        OnPropertyChange.invoke();

        return newEntity;
    }

    public boolean validatePropertyChange(UUID objectUuid, Class<? extends Component> componentType, String fieldName, String newValue) {
        //get all needed object
        Entity entity = getEntity(objectUuid);
        Component comp = getComponent(entity, componentType);
        Field field = comp == null ? getField(entity, fieldName) : getField(comp, fieldName);

        assert field != null;
        Method validationMethod = field.getAnnotation(SetPropertyEvent.class) == null ?
                null : GetPropertyChangeValidationMethod(field, comp);

        Object convertedValue = convertStringToType(newValue, field.getType());

        if (validationMethod != null) {
            return InvokePropertyValidationMethod(validationMethod, comp, convertedValue);
        } else
            return true;
    }

    public boolean validateObjectCreation(UUID invokerUuid, Class<? extends Component> selectedComp, Class<?> typeToAdd, Object[] args) {
        //get all needed object
        Entity entity = getEntity(invokerUuid);
        Component comp = getComponent(entity, selectedComp);
        Field field = getCollectionField(entity, comp, typeToAdd);

        Method validationMethod = field.getAnnotation(AddObjectEvent.class) == null ?
                null : GetCreationValidationMethod(field, comp);

        Object[] newArgs = prependElement(args, typeToAdd);
        Object[] convertedValue = convertStringsToTypes(newArgs, field.getAnnotation(AddObjectEvent.class).validationMethodArgs());

        if (validationMethod != null) {
            return InvokePropertyValidationMethod(validationMethod, comp, convertedValue);
        } else
            return true;

    }


    public void destroy(UUID uuid) {
        Entity toDestroy = entities.get(uuid);
        entities.remove(uuid);
        //UUID parent = toDestroy.getParent().getUuid();
        InvokeDestroy(toDestroy);
        OnPropertyChange.invoke();

    }

    //FROM COMPONENTS
    private Entity instantiate(Object... constructorParams) {

        Constructor<? extends Entity> constructor;

        try {
            //Add Entity Manager to construction Args
            Object[] newConstructorParams = prependElement(constructorParams, this);

            constructor = findCompatibleConstructor(newConstructorParams);

            assert constructor != null;
            constructor.setAccessible(true);

            // Create a new instance using the constructor
            Entity entity = constructor.newInstance(newConstructorParams);

            constructor.setAccessible(false);

            // Add the entity to the entities map
            entities.put(entity.getUuid(), entity);

            return entity;

        } catch (InstantiationException | IllegalAccessException | InvocationTargetException ignored) {
            throw new RuntimeException("couldn't find an appropriate constructor for the entity");
        }
    }

    public void destroy(Entity entity) {
        entities.remove(entity.getUuid());
        InvokeDestroy(entity);
        OnPropertyChange.invoke();
    }

    //
    public Entity instantiate() {
        return instantiate(new Object[]{});
    }

    //OTHER PRIVATE
    private Method GetCreationValidationMethod(Field field, Object target) {
        AddObjectEvent addEventAnnotation = field.getAnnotation(AddObjectEvent.class);
        String methodName = addEventAnnotation.validationMethodName();

        try {
            return target.getClass().getMethod(methodName, addEventAnnotation.validationMethodArgs());
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    public Entity getEntity(UUID uuid) {
        Entity entity = entities.get(uuid);

        if (entity == null) {
            throw new RuntimeException("Couldn't get object with UUID " + uuid);
        }

        return entity;
    }

    private Component getComponent(Entity entity, Class<? extends Component> componentType) {
        if (componentType == null) {
            return null;
        }

        Component comp = entity.getComponent(componentType);

        if (comp == null) {
            throw new RuntimeException("Couldn't get component " + componentType.getName() + " in object " + entity.getUuid());
        }

        return comp;
    }

    private static Object[] getAddEventInvocationArgs(Component component, Object[] args, Field field) {
        //add typeToAdd to args
        Object[] allArgs = prependElement(args, component);

        //get args concrete type for conversion
        AddObjectEvent annotation = field.getAnnotation(AddObjectEvent.class);
        Class<?>[] argsType = annotation.methodArgs();

        //convert all args from string input to concrete types.
        return convertStringsToTypes(allArgs, argsType);
    }

    private static void InvokeDestroy(Entity toDestroy) {

        try {
            Method destroyMethod = toDestroy.getClass().getDeclaredMethod("onDestroy");

            destroyMethod.setAccessible(true);

            destroyMethod.invoke(toDestroy);

            destroyMethod.setAccessible(false);

        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    //Not used with refactor, but kept for now.
    private Field getCollectionField(Entity entity, Component componentType, Class<?> typeToAdd) {
        Field collectionField = getFieldOfCollectionOfType(componentType, typeToAdd);

        if (collectionField == null)
            throw new RuntimeException("Couldn't find collection assignable from type "
                    + typeToAdd + " in " + entity.getClass().getName());

        return collectionField;
    }

    private static Field getField(Object object, String fieldName) {
        Class<?> clazz = object.getClass();
        while (clazz != null) {
            try {
                return clazz.getDeclaredField(fieldName);

            } catch (NoSuchFieldException e) {
                // Field not found in this class, check the superclass
                clazz = clazz.getSuperclass();
            }
        }
        return null;
    }

    private static void setField(Object obj, Field field, Object convertedValue) {

        if (convertedValue != null) {
            try {
                field.setAccessible(true);
                field.set(obj, convertedValue);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } finally {
                field.setAccessible(false);
            }
        }
    }

    private static Object convertStringToType(String value, Class<?> fieldType) {
        if (fieldType == int.class) {
            return Integer.parseInt(value);
        } else if (fieldType == double.class) {
            return Double.parseDouble(value);
        } else if (fieldType == boolean.class) {
            return Boolean.parseBoolean(value);
        } else if (fieldType == String.class) {
            return value;
        } else if (fieldType == byte.class) {
            return Byte.parseByte(value);
        } else if (fieldType == short.class) {
            return Short.parseShort(value);
        } else if (fieldType == long.class) {
            return Long.parseLong(value);
        } else if (fieldType == float.class) {
            return Float.parseFloat(value);
        } else if (fieldType == char.class && value.length() == 1) {
            return value.charAt(0);
        }
        return null;
    }

    private Method GetSetMethod(Field field, Object target) {
        SetPropertyEvent propertySetAnnotation = field.getAnnotation(SetPropertyEvent.class);
        String methodName = propertySetAnnotation.eventTriggerMethodName();

        try {
            return target.getClass().getMethod(methodName, field.getType());
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    private Method GetPropertyChangeValidationMethod(Field field, Object target) {
        SetPropertyEvent propertyEventAnnotation = field.getAnnotation(SetPropertyEvent.class);
        String methodName = propertyEventAnnotation.validationMethodName();

        try {
            return target.getClass().getMethod(methodName, field.getType());
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    private Method GetAddMethod(Field field, Object target) {
        AddObjectEvent annotation = field.getAnnotation(AddObjectEvent.class);
        String methodName = annotation.eventTriggerMethodName();
        Class<?>[] argsType = annotation.methodArgs();

        try {
            return target.getClass().getMethod(methodName, argsType);
        } catch (NoSuchMethodException e) {
            return null;
        }

    }

    private static Object[] prependElement(Object[] array, Object element) {
        Object[] newArray = new Object[array.length + 1];
        newArray[0] = element;
        System.arraycopy(array, 0, newArray, 1, array.length);
        return newArray;
    }

    private static Object[] convertStringsToTypes(Object[] values, Class<?>[] fieldTypes) {
        if (values.length != fieldTypes.length) {
            throw new IllegalArgumentException("Values and field types arrays must have the same length");
        }

        Object[] convertedValues = new Object[values.length];

        for (int i = 0; i < values.length; i++) {
            if (!fieldTypes[i].isPrimitive() || fieldTypes[i] == String.class) {
                // If the field type is Class, skip conversion and set the value as is.
                convertedValues[i] = values[i];
            } else {
                convertedValues[i] = convertStringToType((String) values[i], fieldTypes[i]);
            }
        }

        return convertedValues;
    }

    private void InvokeSetMethod(Method method, Object target, Object arg) {
        try {
            method.invoke(target, arg);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean InvokePropertyValidationMethod(Method method, Object target, Object arg) {
        try {
            return (boolean) method.invoke(target, arg);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean InvokePropertyValidationMethod(Method method, Object target, Object[] args) {
        try {
            return (boolean) method.invoke(target, args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private static Field getFieldOfCollectionOfType(Object object, Class<?> typeToFind) {
        Field[] fields = object.getClass().getDeclaredFields();

        for (Field field : fields) {
            if (!(Collection.class.isAssignableFrom(field.getType()) || Map.class.isAssignableFrom(field.getType()))) {
                continue;
            }

            if (!(field.getGenericType() instanceof ParameterizedType)) {
                continue;
            }

            ParameterizedType genericType = (ParameterizedType) field.getGenericType();
            Class<?> fieldTypeArg = (Class<?>) genericType.getActualTypeArguments()[0];
            Class<?> fieldTypeArg2 = (genericType.getActualTypeArguments().length > 1) ? (Class<?>) genericType.getActualTypeArguments()[1] : null;

            if (typeToFind.isAssignableFrom(fieldTypeArg) || fieldTypeArg.isAssignableFrom(typeToFind) || typeToFind.isAssignableFrom(Objects.requireNonNull(fieldTypeArg2)) || fieldTypeArg2.isAssignableFrom(typeToFind)) {
                return field;
            }
        }

        return null;
    }

    private Constructor<? extends Entity> findCompatibleConstructor(Object... constructorParams) {
        for (Constructor<?> constructor : Entity.class.getDeclaredConstructors()) {
            if (isCompatibleConstructor(constructor, constructorParams))
                //noinspection unchecked
                return (Constructor<? extends Entity>) constructor;
        }
        return null;
    }

    private boolean isCompatibleConstructor(Constructor<?> constructor, Object[] constructorParams) {
        Class<?>[] paramTypes = constructor.getParameterTypes();

        if (paramTypes.length != constructorParams.length) {
            return false;
        }

        for (int i = 0; i < paramTypes.length; i++) {
            if (!paramTypes[i].isAssignableFrom(constructorParams[i].getClass())) {
                return false;
            }
        }

        return true;
    }



}

