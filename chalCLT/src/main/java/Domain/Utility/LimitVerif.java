package Domain.Utility;

import Domain.Interface.IElementsShack;

public class LimitVerif {
    public boolean isInBounds(Vector2 MouseCoordinates, IElementsShack e) {
        if(!e.isVisible())
            return false;

        if(MouseCoordinates.getX() >= e.getX()){
            if(MouseCoordinates.getX() <= e.getWidth() + e.getX()){
                if(MouseCoordinates.getY() >= e.getY()){
                    if(MouseCoordinates.getY() <= e.getHeight() + e.getY()){
                        return true;
                    }
                }
            }
        }

        return false;
    }
}
