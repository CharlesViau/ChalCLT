package Domain.Shack.Panels;

import Domain.CustumAnnotations.HideInInspector;
import Domain.CustumAnnotations.Imperial;
import Domain.CustumAnnotations.SerializeField;
import Domain.CustumAnnotations.SetPropertyEvent;
import Domain.Enum.Direction;
import Domain.General.Components.Component;
import Domain.General.Entity;
import Domain.Interface.IElementsShack;
import Domain.Shack.Shack;
import Domain.Utility.Vector3;


public abstract class PanelCLT extends Component implements IElementsShack {
    //Faire des set et des evenement de set separe pour pas que le chalet fasse des appels de set recursifs.
    @SerializeField
    @Imperial
    @SetPropertyEvent(eventTriggerMethodName = "setHeightEvent")
    protected float height;

    @SerializeField
    @Imperial
    @SetPropertyEvent(eventTriggerMethodName = "setThicknessEvent")
    protected float thickness;

    @SerializeField
    @Imperial
    @SetPropertyEvent(eventTriggerMethodName = "setWidthEvent")
    protected float width;
    protected float extraSlotDistance;
    @HideInInspector
    protected float groove;

    protected Direction direction;

    protected PanelCLT(Entity _entity) {
        super(_entity);
    }

    protected void init(float height, float width, float thickness, float extraSlotDistance, float groove, Vector3 position, Direction dir) {
        this.height = height;
        this.width = width;
        this.thickness = thickness;
        this.extraSlotDistance = extraSlotDistance;
        this.groove = groove;
        transform.position = position;
        System.out.println("PanelCLT init: " + position);

        this.direction = dir;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float value) {
        height = value;
    }

    public void setHeightEvent(float value) {
        height = value;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float value) {
        width = value;
    }

    public void setWidthEvent(float value) {
        width = value;
    }


    public float getThickness() {
        return thickness;
    }

    public void setThickness(float value) {
        thickness = value;
    }

    public void setThicknessEvent(float value) {
        setThickness(value);
        transform.getParent().entity.getComponent(Shack.class).setPanelsThickness(value);
    }


    public float getExtraSlotDistance() {
        return extraSlotDistance;
    }


    public float getGroove() {
        return groove;
    }


    public boolean isValidWidth(float width) {
        return width >= 0;
    }

    public Direction getDirection() {
        return direction;
    }

    @Override
    public Class<?> getElementType() {
        return this.getClass();
    }

    @Override
    public boolean isVisible() {
        return entity.isVisible();
    }

    @Override
    public void setVisible(boolean b) {
        entity.setVisible(b);
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

    @Override
    public Entity getEntity() {
        return entity;
    }

    @Override
    public String onHover() {
        String tooltipInfo = "";
        tooltipInfo += "- Height: " + View.ViewUtility.Imperial.floatToImperial(height)  + "\n";
        tooltipInfo += "- Width: " + View.ViewUtility.Imperial.floatToImperial(width) + " \n";
        tooltipInfo += "- Thickness: " + View.ViewUtility.Imperial.floatToImperial(thickness) + " \n";
        return tooltipInfo;
    }
}
