package Domain.Interface;

import Domain.General.Components.Component;

import java.util.UUID;
import java.util.function.Consumer;

public interface IPropertyChangeManager {

    void editProperty(UUID objectUuid, Class<? extends Component> componentType, String fieldName, Object newValue);

    void subscribeToPropertyChangeEvents(Consumer<Object[]> eventHandler);

    boolean validatePropertyChange(UUID objectUuid, Class<? extends Component> compType, String fieldName, String newValue);

    void addObject(UUID parentObject, Class<? extends Component> selectedComponent, Class<? extends Component> typeToAdd, Object[] args);

    boolean validateObjectCreation(UUID parentUuid,Class<? extends Component> selectedComponent, Class<? extends Component> typeToAdd, Object[]args);

    void removeObject(UUID objectUuid, UUID parentObject, Class<? extends Component> selectedComponent, Class<? extends Component> typeToAdd);

}
