package Domain.Shack.Accessory;

import Domain.CustumAnnotations.Imperial;
import Domain.CustumAnnotations.SerializeField;
import Domain.CustumAnnotations.SetPropertyEvent;
import Domain.General.Entity;
import Domain.Enum.Direction;
import Domain.Shack.Panels.Wall;
import Domain.Utility.Vector3;

public class Door extends Accessory {

    private static final float DEFAULT_DOOR_HEIGHT = 88;
    private static final float DEFAULT_DOOR_WIDTH = 38;
    @Imperial
    @SerializeField
    @SetPropertyEvent(eventTriggerMethodName = "setX", validationMethodName = "validatePosition")
    private float X;

    protected Door(Entity _entity) {
        super(_entity);

    }

    public void init(Vector3 localPosition, Direction dir) {
        float wallHeight = transform.getParent().entity.getComponent(Wall.class).getHeight();
        float dist = transform.getParent().entity.getComponent(Wall.class).getDistanceAccessoryPadding();

        Vector3 realPos = new Vector3(localPosition.getX(), wallHeight - DEFAULT_DOOR_HEIGHT - dist, 0);

        init(DEFAULT_DOOR_HEIGHT, DEFAULT_DOOR_WIDTH, realPos, dir);

        X = (float) transform.position.x;

        transform.showInInspector = false;

        entity.name = "Door";
    }

    public void setHeight(float height) {
        this.height = height;

        float wallHeight = transform.getParent().entity.getComponent(Wall.class).getHeight();
        float dist = transform.getParent().entity.getComponent(Wall.class).getDistanceAccessoryPadding();

        transform.setY(wallHeight - height - dist);

    }

    @Override
    public void setX(float x) {
        X = x;
        transform.setX(x);
    }

    public boolean validatePosition(float x){
        return true;
    }


}
