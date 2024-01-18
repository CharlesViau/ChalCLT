package Domain.Shack;

import Domain.CustumAnnotations.HideInInspector;
import Domain.Enum.Direction;

import static java.lang.Math.tan;

public class ShackSettings {
    public static final float DEFAULT_ROOF_ANGLE = 15;
    public static final float DEFAULT_GABLE_WIDTH = 120;
    public static final float DEFAULT_GABLE_HEIGHT = (float) (tan(DEFAULT_ROOF_ANGLE) * DEFAULT_GABLE_WIDTH);
    public static final float DEFAULT_GABLE_THICKNESS = 15;
    public static final float DEFAULT_WALL_HEIGHT = 96;
    public static final float DEFAULT_WALL_WIDTH = 120;
    public static final float DEFAULT_SLOT_DISTANCE = 0;
    public static final float DEFAULT_WALL_THICKNESS = 10;
    public static final float DEFAULT_GROOVE = DEFAULT_WALL_THICKNESS / 2;
    public static final float DEFAULT_DIST_ACCESSORY = 3;
    public static final Direction DEFAULT_ROOF_DIRECTION = Direction.FRONT;

}
