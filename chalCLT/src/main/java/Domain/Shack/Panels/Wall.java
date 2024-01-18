package Domain.Shack.Panels;

import Domain.CustumAnnotations.*;
import Domain.Enum.Direction;
import Domain.General.Components.Transform;
import Domain.General.Entity;
import Domain.Shack.Accessory.Accessory;

import Domain.Shack.Accessory.Door;
import Domain.Shack.Accessory.Window;
import Domain.Shack.Shack;
import Domain.Utility.Vector2;
import Domain.Utility.Vector3;
import View.WallEditor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import static Domain.Shack.ShackSettings.*;

@CustomEditor(editorType = WallEditor.class)
public class Wall extends PanelCLT {

    @SerializeField
    private boolean isIsolated;
    @SerializeField
    @Imperial
    @SetPropertyEvent(eventTriggerMethodName = "setDistanceAccessoryPadding", validationMethodName = "validateDistAccPadding")
    private float distanceAccessoryPadding;
    @SerializeField
    @AddObjectEvent(eventTriggerMethodName = "onAddAccessoryToWall", methodArgs = {Accessory.class, float.class, float.class},
            validationMethodName = "isInsideWall", validationMethodArgs = {Class.class, float.class, float.class})
    private HashMap<UUID, Accessory> accessories = new HashMap<>();

    protected Wall(Entity _entity) {
        super(_entity);
    }


    public void init(Vector3 pos, Direction dir) {
        init(DEFAULT_WALL_HEIGHT, DEFAULT_WALL_WIDTH, DEFAULT_WALL_THICKNESS, extraSlotDistance, DEFAULT_GROOVE, pos, dir);
        distanceAccessoryPadding = DEFAULT_DIST_ACCESSORY;

        getComponent(Transform.class).showInInspector = false;
    }

    public float getDistanceAccessoryPadding() {
        return distanceAccessoryPadding;
    }

    public boolean validateDistAccPadding(float distanceAccessoryPadding) {
        return distanceAccessoryPadding >= DEFAULT_DIST_ACCESSORY;
    }
    public void setDistanceAccessoryPadding(float distanceAccessoryPadding) {
        this.distanceAccessoryPadding = distanceAccessoryPadding;

        for (Accessory a: accessories.values()) {
            if(a instanceof Door){
                a.transform.position = new Vector3(a.getX(), height - a.getHeight() - distanceAccessoryPadding, 0);
                System.out.println("Accessory relative position: " + getPosition());
            }
        }
    }

    public boolean isValidPaddingDistance(float distance) {
        if (distance < 0)
            return false;

        return !(distance >= this.getWidth()) && !(distance >= this.getHeight());
    }



    public void removeAccessory(UUID accessoryUUID) {
        accessories.remove(accessoryUUID);
    }

    public void addAccessory(Accessory a) {
        accessories.put(a.getUuid(), a);
    }

    public ArrayList<Accessory> getAccessories() {
        return new ArrayList<>(accessories.values());
    }


    public boolean isInsideWall(Class<?> accessoryType, float x, float y) {
        float w = 0;
        float h = 0;
        if(accessoryType == Window.class) {
            w = 80;
            h = 80;
        } else {
            w = 4*38;
            h = 4*88;
        }

        float w_x = this.getX();
        float w_y = this.getY();
        x += w_x;
        y += w_y;
        float w_h = 4*this.getHeight();
        float w_w = 4*this.getWidth();

        return x >= w_x && x + w <= w_x + w_w && y >= w_y && y + h <= w_y + w_h;

    }

    public boolean hasNoCollision(Accessory a) {
        boolean noCollision = true;

        double accessoryX = a.getX();
        double accessoryY = a.getY();

        double accessoryXPixel = 4*accessoryX + this.getPosition().getX();
        double accessoryYPixel = 4*accessoryY + this.getPosition().getY();

        double distPixel = 4*this.distanceAccessoryPadding;

        for (Accessory existingAccessory : getAccessories()) {
            if (a.getUuid() == existingAccessory.getUuid()) {
                if(accessoryXPixel >= this.getPosition().getX() + distPixel) {
                    if(accessoryXPixel + a.getWidth()*4 <= this.getPosition().getX() + 4*this.getWidth() - distPixel) {
                        if(accessoryYPixel >= this.getPosition().getY() + distPixel) {
                            if(accessoryYPixel + a.getHeight()*4 <= this.getPosition().getY() + 4*this.getHeight() - distPixel) {
                                continue;
                            } else {
                                return false;
                            }
                        } else {
                            return false;
                        }
                    } else {
                        return false;
                    }
                }else {
                    return false;
                }
            }

            double existingAccessoryX = existingAccessory.getPosition().getX();
            double existingAccessoryY = existingAccessory.getPosition().getY();

            boolean c1;
            boolean c2;
            if (accessoryX <= existingAccessoryX) {
                c1 = (existingAccessoryX - accessoryX) >= a.getWidth() + this.distanceAccessoryPadding;
            } else {
                double existingAccessoryRightX = existingAccessoryX + existingAccessory.getWidth();
                c1 = (accessoryX - existingAccessoryRightX) >= this.distanceAccessoryPadding;
            }


            if (accessoryY <= existingAccessoryY) {
                    c2 = (existingAccessoryY - accessoryY) >= a.getHeight() + this.distanceAccessoryPadding;
            } else {
                    double existingAccessoryBelowY = existingAccessoryY + existingAccessory.getHeight();
                    c2 = (accessoryY - existingAccessoryBelowY) >= this.distanceAccessoryPadding;
            }


            if (!(c1 || c2)) {
                noCollision = false;
                break;
            }

        }
        return noCollision;
    }

    @Override
    public Class<?> getElementType() {
        return Wall.class;
    }

    @SuppressWarnings("unused") // This method is call threw reflection.
    public void onAddAccessoryToWall(Accessory accessory, float XPosition, float YPosition) {
        accessories.put(accessory.getUuid(), accessory);
        accessory.transform.setParent(this.transform);

        ((transform.getParent()).entity.getComponent(Shack.class)).addElement(accessory);

        try {
            Method method = accessory.getClass().getMethod("init", Vector3.class, Direction.class);
            method.invoke(accessory, new Vector3(XPosition, YPosition, 0), direction);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setWidthEvent(float value) {
        float relativePos = value/this.getWidth();

        super.setWidthEvent(value);
        transform.getParent().entity.getComponent(Shack.class).setWallsWidth(this, value);

        for(Accessory a : this.getAccessories()) {
            a.setX(a.getX() * relativePos);
        }

    }

    @Override
    public void setHeightEvent(float value) {
        float relativePos = value/this.getHeight();

        super.setHeightEvent(value);
        transform.getParent().entity.getComponent(Shack.class).setWallsHeight(value);

        for(Accessory a : this.getAccessories()) {
            if(a.getElementType() == Door.class) {
                a.setY(value - a.getHeight() - distanceAccessoryPadding);
            } else {
                a.setY(a.getY() * relativePos);
            }
        }
    }

    public boolean getIsolated() {
        return isIsolated;
    }

    public void setIsolated(boolean i) {
        isIsolated = i;
    }

    public void moveAccessory(Vector2 pos, Vector2 initial_pos,boolean swap) {
        Accessory selected_a = null;
        if(!swap){
            Vector2 local_pos = new Vector2(pos.getX() - this.getX(), pos.getY() - this.getY());
            System.out.println("Move accessories " + pos);
            System.out.println("Move accessories initial" + initial_pos);


            for(Accessory a : getAccessories()) {
                System.out.println("accessoire X" + a.getX()+ " -> "+ a.getWidth());
                System.out.println("accessoire Y" + a.getY()+ " -> "+ a.getHeight());

                if(local_pos.getX() >= a.getX() && local_pos.getX() <= a.getX() + a.getWidth() &&
                        local_pos.getY() >= a.getY() && local_pos.getY() <= a.getY() + a.getHeight()) {
                    selected_a = a;
                    System.out.println("========================");
                    break;
                }
            }
            System.out.println("========================");
            if(selected_a == null)
                return;

        }
        else{
            Vector2 local_pos = new Vector2(initial_pos.getX() - this.getX(), initial_pos.getY() - this.getY());
            System.out.println("Move accessories " + pos);
            System.out.println("Move accessories initial" + initial_pos);


            for(Accessory a : getAccessories()) {
                System.out.println("accessoire X" + a.getX()+ " -> "+ a.getWidth());
                System.out.println("accessoire Y" + a.getY()+ " -> "+ a.getHeight());

                if(local_pos.getX() >= a.getX() && local_pos.getX() <= a.getX() + a.getWidth() &&
                        local_pos.getY() >= a.getY() && local_pos.getY() <= a.getY() + a.getHeight()) {
                    selected_a = a;
                    System.out.println("========================");
                    break;
                }
            }
            System.out.println("========================");
            if(selected_a == null)
                return;

        }
        float diff_x = pos.getX() - initial_pos.getX();
        float diff_y = pos.getY() - initial_pos.getY();

        if(selected_a.getElementType() == Door.class) {
            selected_a.setX(selected_a.getX() + diff_x);
        } else {
            selected_a.setX(selected_a.getX() + diff_x);
            selected_a.setY(selected_a.getY() + diff_y);
        }



    }

}
