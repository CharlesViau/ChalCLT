
package Domain.Shack;

import Domain.CustumAnnotations.HideInInspector;
import Domain.CustumAnnotations.SerializeField;
import Domain.Enum.Direction;
import Domain.General.Entity;

import Domain.Shack.Panels.Extension;
import Domain.Shack.Panels.Gable;
import Domain.Shack.Panels.PanelCLT;
import Domain.Shack.Panels.Wall;

import java.util.ArrayList;

public class Roof extends PanelCLT {

    @SerializeField
    private float angle;
    private Direction direction;
    private Extension extension;
    private ArrayList<Gable> gables;
    private float groove;

    protected Roof(Entity _entity) {
        super(_entity);
    }


    public void init() {
        angle = ShackSettings.DEFAULT_ROOF_ANGLE;
        direction = ShackSettings.DEFAULT_ROOF_DIRECTION;

        transform.showInInspector = false;
        showInInspector = false;
    }

    public boolean isValidAngle(float pAngle) {
        return pAngle >= 0 && pAngle <= 90;
    }

    public float getGroove() {
        return groove;
    }

    public void setGroove(float groove) {
        this.groove = groove;
    }

    public float getRoofAngle() {
        return angle;
    }

    public void setRoofAngle(float inclinationAngle) {
        this.angle = inclinationAngle;
    }

    public Direction getDirection() {
        return direction;
    }

    public Direction getOppositeDirection() {
        if (direction == Direction.FRONT)
            return Direction.BACK;
        else if (direction == Direction.BACK)
            return Direction.FRONT;
        else if (direction == Direction.LEFT)
            return Direction.RIGHT;
        else
            return Direction.LEFT;
    }

    @Override
    public Class<?> getElementType() {
        return Roof.class;
    }

    @Override
    public boolean isVisible() {
        return entity.isVisible();
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public Extension getExtension() {
        return extension;
    }

    public void setExtension(Extension extension) {
        this.extension = extension;
    }

    public ArrayList<Gable> getGables() {
        return gables;
    }

    public void setGables(ArrayList<Gable> gables) {
        this.gables = gables;
    }


    @Override
    public void onDestroy() {

    }

    @Override
    public String onHover() {
        //J'ai un peu triché dsl F-A... -Ramy
        float w;
        if(direction == Direction.LEFT || direction == Direction.RIGHT) {
            w = transform.getParent().entity.getComponent(Shack.class).getFrontBackWallWidth();
        } else {
            w = transform.getParent().entity.getComponent(Shack.class).getLeftRightWallsWidth();
        }

        String tooltipInfo = "";
        tooltipInfo += "- Height: " + View.ViewUtility.Imperial.floatToImperial(height)  + "\n";
        tooltipInfo += "- Width: " + View.ViewUtility.Imperial.floatToImperial(w) + " \n";
        tooltipInfo += "- Thickness: " + View.ViewUtility.Imperial.floatToImperial(thickness) + " \n";
        return tooltipInfo;
    }
}
