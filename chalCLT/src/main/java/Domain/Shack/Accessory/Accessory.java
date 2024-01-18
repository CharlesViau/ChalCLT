package Domain.Shack.Accessory;

import Domain.CustumAnnotations.Imperial;
import Domain.CustumAnnotations.SerializeField;
import Domain.CustumAnnotations.SetPropertyEvent;
import Domain.Enum.Direction;
import Domain.General.Components.Component;
import Domain.General.Entity;
import Domain.Interface.IElementsShack;
import Domain.Shack.Panels.Wall;
import Domain.Shack.Shack;
import Domain.Utility.Vector3;

public abstract class Accessory extends Component implements IElementsShack {

    @SerializeField
    @Imperial
    @SetPropertyEvent(eventTriggerMethodName = "setHeight")
    protected float height;
    @SerializeField
    @Imperial
    @SetPropertyEvent(eventTriggerMethodName = "setWidth")
    protected float width;
    private Direction direction;

    protected Accessory(Entity _entity) {
        super(_entity);
    }

    protected void init(float height, float width, Vector3 localPosition, Direction dir) {
        this.height = height;
        this.width = width;
        this.direction = dir;


        transform.setX((float)localPosition.x);
        transform.setY((float)localPosition.y);

    }

    public void setHeight(float height) {
        this.height = height;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public void setX(float x) {
        transform.setX(x);
    }

    public void setY(float y) {
        transform.setY(y);
    }


    public float getHeight() {
        return height;
    }


    public float getWidth() {
        return width;
    }

    public boolean isVisible() {
        return entity.isVisible();
    }

    public void setVisible(boolean visible) {
        entity.setVisible(visible);
    }

    @Override
    public Vector3 getPosition() {
        return transform.position;
    }

    @Override
    public float getX() {
        return (float) transform.position.x;
    }

    @Override
    public float getY() {
        return (float) transform.position.y;
    }

    public Direction getDirection() {
        return this.direction;
    }

    @Override
    public Class<?> getElementType() {
        return this.getClass();
    }

    @Override
    public void onDestroy() {

        transform.getParent().entity.getComponent(Wall.class).removeAccessory(getUuid());
        transform.getParent().getParent().entity.getComponent(Shack.class).removeElements(this);
    }

    public  boolean isValid(){
        Wall monWall = (transform.getParent()).entity.getComponent(Wall.class);
        return monWall.isInsideWall(this.getClass(),transform.getX(),transform.getY()) && monWall.hasNoCollision(this);
    }
}
