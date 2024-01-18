package Domain.Shack.Accessory;

import Domain.Enum.Direction;
import Domain.General.Entity;
import Domain.Utility.Vector3;

public class Window extends Accessory {

    private static final float DEFAULT_WINDOW_HEIGHT = 20;
    private static final float DEFAULT_WINDOW_WIDTH = 20;

    protected Window(Entity _entity) {
        super(_entity);
    }

    public void init(Vector3 localPosition, Direction dir) {
        init(DEFAULT_WINDOW_HEIGHT, DEFAULT_WINDOW_WIDTH, localPosition, dir);
        entity.name = "Window";
    }

}
