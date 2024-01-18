package Domain.History;

import Domain.Shack.Panels.Wall;
import Domain.Utility.Vector2;

public class AccessoryMovedCommand <T> extends Command{
    private  boolean premiereFois=true;
    private Wall activeWall;
    public AccessoryMovedCommand(T value, T oldValue, Wall activeWall) {
        super(value, oldValue);
        this.activeWall = activeWall;
    }

    @Override
    public void Execute() {
        if(!premiereFois){
            activeWall.moveAccessory((Vector2) m_Value,(Vector2) m_OldValue,true);
        }

        premiereFois=false;
    }

    @Override
    public void undo() {
        activeWall.moveAccessory((Vector2) m_OldValue,(Vector2) m_Value,true);
    }
}
