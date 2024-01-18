package Domain.Interface;

import Domain.Enum.Direction;
import Domain.General.Entity;
import Domain.Utility.Vector2;
import Domain.Utility.Vector3;

import java.util.UUID;

public interface IElementsShack {
    Direction getDirection();

    Class<?> getElementType();

    boolean isVisible();

    void setVisible(boolean b);

    Vector3 getPosition();
    
    float getX();

    float getY();

    float getWidth();

    float getHeight();

    Entity getEntity();
}
