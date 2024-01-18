package Domain.History;

import Domain.General.Components.Component;
import Domain.General.Entity;

import java.util.UUID;

public class Destroyable {
    public Entity entity;
    public UUID parent;
    public Class<? extends Component> parentComponent;

    public Class<? extends Component> objectType;
    public Destroyable(Entity entity,UUID parent,Class<? extends Component> parentComponent,Class<? extends Component> objectType){
        this.entity = entity;
        this.parent = parent;
        this.parentComponent = parentComponent ;
        this.objectType = objectType;
    }

}
