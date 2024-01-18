package Domain.DTO;

import Domain.General.Components.Component;

import java.lang.reflect.Type;
import java.util.UUID;

public class PropertyDTO {
    public UUID uuid;
    public Class<? extends Component> componentType;
    public String name;
    public Type fieldType;

    public PropertyDTO(UUID uuid, Class<? extends Component> componentType, String name, Type fieldType) {
        this.uuid = uuid;
        this.componentType = componentType;
        this.name = name;
        this.fieldType = fieldType;
    }

    public PropertyDTO(PropertyDTO propertyDTO) {
        this.componentType = propertyDTO.componentType;
        this.fieldType = propertyDTO.fieldType;
        this.name = propertyDTO.name;
        this.uuid = propertyDTO.uuid;
    }
}
