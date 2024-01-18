package Domain.Shack.Panels;

import Domain.Enum.Direction;
import Domain.General.Entity;
import Domain.Utility.Vector3;

import static Domain.Shack.ShackSettings.*;
import static Domain.Shack.ShackSettings.DEFAULT_GROOVE;

public class Extension extends PanelCLT {
    protected Extension(Entity _entity) {
        super(_entity);
    }

    //À changer si nécessaire
    public void init(Vector3 pos, Direction dir) {
        init(DEFAULT_GABLE_HEIGHT, DEFAULT_GABLE_WIDTH, DEFAULT_GABLE_THICKNESS, 0, DEFAULT_GROOVE, pos, dir);
        transform.showInInspector = false;
        showInInspector = false;
    }

    @Override
    public Class<?> getElementType() {
        return Extension.class;
    }

}
