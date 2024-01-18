package Domain.Shack;

import Domain.CustumAnnotations.Imperial;
import Domain.CustumAnnotations.SerializeField;
import Domain.CustumAnnotations.SetPropertyEvent;
import Domain.Drawing.RectangleView;
import Domain.Enum.Direction;
import Domain.General.Components.Component;
import Domain.General.Entity;
import Domain.Interface.IElementsShack;
import Domain.MainController;
import Domain.Shack.Accessory.Door;
import Domain.Shack.Accessory.Window;
import Domain.Shack.Panels.Extension;
import Domain.Shack.Panels.Gable;
import Domain.Shack.Panels.Wall;
import Domain.Utility.Vector2;
import Domain.Utility.Vector3;
import Domain.Utility.ZoomUtility;
import View.CurrentView;

import java.util.*;

import static Domain.Shack.ShackSettings.*;
import static java.lang.Math.tan;


public class Shack extends Component {
    //TODO: LES PANEL SIZE ON PAS DAFFAIRE LA, A REFACTOR
    private static final float PANEL_WIDTH = 284;
    private static final float PANEL_HEIGHT = 183.75f;
    private float panelHeight = PANEL_HEIGHT;
    private float panelWidth = PANEL_WIDTH;

    //Shack
    private final HashMap<Direction, Wall> exteriorWalls = new HashMap<>();
    private final HashMap<Direction, Gable> gables = new HashMap<>();
    private Roof roof;

    private Extension extension;

    @SuppressWarnings("unused")
    @SetPropertyEvent(eventTriggerMethodName = "setGableWidth")
    private float gableWidth = DEFAULT_GABLE_WIDTH;
    private float gableHeight = DEFAULT_GABLE_HEIGHT;
    private float gableThickness = DEFAULT_GABLE_HEIGHT;
    @SerializeField
    @Imperial
    @SetPropertyEvent(eventTriggerMethodName = "setWallsHeight")
    private float wallsHeight = DEFAULT_WALL_HEIGHT;

    @SerializeField
    @Imperial
    @SetPropertyEvent(eventTriggerMethodName = "setPanelsThickness")
    private float panelsThickness = DEFAULT_WALL_THICKNESS;
    @SerializeField
    @Imperial
    @SetPropertyEvent(eventTriggerMethodName = "setExtraSlotDistance")
    private float extraSlotDistance = DEFAULT_SLOT_DISTANCE;
    @SuppressWarnings("unused")
    @SerializeField
    @Imperial
    @SetPropertyEvent(eventTriggerMethodName = "setFrontBackWallWidth")
    private float frontBackWallWidth = DEFAULT_WALL_WIDTH;
    @SuppressWarnings("unused")
    @SerializeField
    @Imperial
    @SetPropertyEvent(eventTriggerMethodName = "setLeftRightWallsWidth")
    private float leftRightWallsWidth = DEFAULT_WALL_WIDTH;
    @SuppressWarnings("unused")
    @SerializeField
    @SetPropertyEvent(eventTriggerMethodName = "setRoofAngle")
    private float roofAngle = DEFAULT_ROOF_ANGLE;
    @SuppressWarnings("unused")
    @SerializeField
    @SetPropertyEvent(eventTriggerMethodName = "setRoofDirection")
    private Direction roofDirection = DEFAULT_ROOF_DIRECTION;

    private transient Entity activeElement;
    private final ArrayList<IElementsShack> elements = new ArrayList<>();
    private transient MainController _controller;


    protected Shack(Entity _entity) {
        super(_entity);
    }

    @SuppressWarnings("unchecked")
    public void init(MainController controller) {
        _controller = controller;

        Vector3 defaultPos = new Vector3((panelWidth - DEFAULT_WALL_WIDTH) / 2, (panelHeight - DEFAULT_WALL_HEIGHT) / 2, 0);

        //SHACK PARTS
        Wall frontWall = instantiate("Mur Avant", this.entity, new Class[]{Wall.class}).getComponent(Wall.class);
        Wall backWall = instantiate("Mur Arrière", this.entity, new Class[]{Wall.class}).getComponent(Wall.class);
        Wall leftWall = instantiate("Mur Gauche", this.entity, new Class[]{Wall.class}).getComponent(Wall.class);
        Wall rightWall = instantiate("Mur Droit", this.entity, new Class[]{Wall.class}).getComponent(Wall.class);
        Gable rightGable = instantiate("Pignon Droit", this.entity, new Class[]{Gable.class}).getComponent(Gable.class);
        Gable leftGable = instantiate("Pignon Gauche", this.entity, new Class[]{Gable.class}).getComponent(Gable.class);
        extension = instantiate("Rallonge Verticale", this.entity, new Class[]{Extension.class}).getComponent(Extension.class);

        //INIT WALLS
        frontWall.init(defaultPos, Direction.FRONT);
        backWall.init(defaultPos, Direction.BACK);
        leftWall.init(defaultPos, Direction.LEFT);
        rightWall.init(defaultPos, Direction.RIGHT);

        //POPULATE WALLS DICTIONARY
        exteriorWalls.put(Direction.FRONT, frontWall);
        exteriorWalls.put(Direction.BACK, backWall);
        exteriorWalls.put(Direction.LEFT, leftWall);
        exteriorWalls.put(Direction.RIGHT, rightWall);

        //INIT GABLES
        rightGable.init(new Vector3(((panelWidth - DEFAULT_WALL_WIDTH) / 2) + DEFAULT_WALL_WIDTH, (panelHeight - DEFAULT_WALL_HEIGHT) / 2, 0), Direction.RIGHT);
        leftGable.init(defaultPos, Direction.LEFT);

        //POPULATE GABLES DICTIONARY
        gables.put(Direction.RIGHT, rightGable);
        gables.put(Direction.LEFT, leftGable);

        //EXTENSION
        extension.init(defaultPos, Direction.BACK);

        //ROOF
        roof = instantiate("Toit", this.entity, new Class[]{Roof.class}).getComponent(Roof.class);
        roof.init();

        elements.add(frontWall);
        elements.add(backWall);
        elements.add(leftWall);
        elements.add(rightWall);
        elements.add(roof);
        elements.add(rightGable);
        elements.add(leftGable);
        elements.add(extension);


        updateRoof();
    }

    public void setController(MainController controller) {
        _controller = controller;
    }

    //TODO: A REFACTOR set panel size devrait envoyer un call au chalet pour
    // modfier les murs. le chalet ne devrait pas connaitre le panel size.
    public void setPanelSize(Vector2 panelSize) {
        this.panelWidth = panelSize.getX() / 4;
        this.panelHeight = panelSize.getY() / 4;

        for (Wall wall : exteriorWalls.values()) {
            wall.transform.position = new Vector3((panelWidth - wall.getWidth()) / 2,
                    (panelHeight - wall.getHeight()) / 2, 0);
        }
    }

    public Collection<IElementsShack> getElements() {

        return elements;
    }


    public boolean isInBounds(Vector2 coord, IElementsShack e) {
        return isInBounds(coord, e.getX(), e.getY(), e.getWidth(), e.getHeight());
    }

    public boolean isInBounds(Vector2 coord, Vector2 position, float width, float height) {
        return isInBounds(coord, position.getX(), position.getY(), width, height);
    }

    private boolean isInBounds(Vector2 coord, float x, float y, float width, float height) {
        return coord.getX() >= x &&
                coord.getX() <= width + x &&
                coord.getY() >= y &&
                coord.getY() <= height + y;
    }

    private boolean isInBoundsAccessory(Vector2 coord, IElementsShack e) {
        Wall wall = this.getExteriorWall(e.getDirection());
        float x = wall.getX() + e.getX();
        float y = wall.getY() + e.getY();
        return coord.getX() >= x &&
                coord.getX() <= e.getWidth() + x &&
                coord.getY() >= y &&
                coord.getY() <= e.getHeight() + y;
    }


    public Wall getExteriorWall(Direction d) {
        return exteriorWalls.get(d);
    }

    public Gable getGable(Direction d) {
        return gables.get(d);
    }

    public Roof getRoof() {
        return roof;
    }

    public float getGableWidth() {
        return gableWidth;
    }

    public void setGableWidth(float width) {
        gableWidth = width;
        for (Gable gable : gables.values()) {
            gable.setWidth(width);
        }
    }

    public Extension getExtension() {
        return extension;
    }

    public float getGableHeight() {
        return gableHeight;
    }

    public void setGableHeight() {
        gableHeight = (float) (gableWidth / tan(roofAngle));
        for (Gable gable : gables.values()) {
            gable.setHeight(gableHeight);
        }
    }

    public float getWallsHeight() {
        return wallsHeight;
    }

    public void setWallsHeight(float wallsHeight) {
        this.wallsHeight = wallsHeight;

        for (Wall wall : exteriorWalls.values()) {
            wall.setHeight(wallsHeight);
        }


    }

    public float getGableThickness() {
        return gableThickness;
    }

    public void setGableThickness(float thickness) {
        gableThickness = thickness;
        for (Gable gable : gables.values()) {
            gable.setThickness(thickness);
        }
    }

    public void setWallsWidth(Wall wall, float newValue) {

        Direction key = null;
        Wall value;
        for (Map.Entry<Direction, Wall> entry : exteriorWalls.entrySet()) {
            value = entry.getValue();
            if (wall != value)
                continue;
            key = entry.getKey();
        }

        if (key == null)
            return;

        switch (key) {
            case FRONT:
                setFrontBackWallWidth(newValue);
                break;
            case BACK:
                setFrontBackWallWidth(newValue);
                break;
            case LEFT:
                setLeftRightWallsWidth(newValue);
                break;
            case RIGHT:
                setLeftRightWallsWidth(newValue);
                break;
        }

        updateRoof();
    }

    public float getPanelsThickness() {
        return panelsThickness;
    }

    public void setFrontBackWallWidth(float wallsWidth) {
        float diff = (wallsWidth - frontBackWallWidth) / 2;
        float front_x_o = this.exteriorWalls.get(Direction.FRONT).getX();
        float back_x_o = this.exteriorWalls.get(Direction.BACK).getX();
        this.exteriorWalls.get(Direction.FRONT).transform.setX(front_x_o - diff);
        this.exteriorWalls.get(Direction.BACK).transform.setX(back_x_o - diff);

        frontBackWallWidth = wallsWidth;
        this.exteriorWalls.get(Direction.FRONT).setWidth(wallsWidth);
        this.exteriorWalls.get(Direction.BACK).setWidth(wallsWidth);

        updateRoof();
    }

    public void setLeftRightWallsWidth(float wallsWidth) {
        float diff = (wallsWidth - leftRightWallsWidth) / 2;
        float left_x_o = this.exteriorWalls.get(Direction.LEFT).getX();
        float right_x_o = this.exteriorWalls.get(Direction.RIGHT).getX();
        this.exteriorWalls.get(Direction.LEFT).transform.setX(left_x_o - diff);
        this.exteriorWalls.get(Direction.RIGHT).transform.setX(right_x_o - diff);

        leftRightWallsWidth = wallsWidth;
        this.exteriorWalls.get(Direction.LEFT).setWidth(wallsWidth);
        this.exteriorWalls.get(Direction.RIGHT).setWidth(wallsWidth);

        updateRoof();
    }

    public void setPanelsThickness(float value) {
        panelsThickness = value;

        for (Wall w : exteriorWalls.values()) {
            w.setThickness(value);
        }

        roof.setThickness(value);

        for (Gable g : gables.values()) {
            g.setThickness(value);
        }

        extension.setThickness(value);
        updateRoof();
    }

    public void setRoofAngle(float value) {
        roofAngle = value;
        getRoof().setRoofAngle(value);
        setGableHeight();   //HEIGHT DEPENDS ON ANGLE
        updateRoof();
    }

    public void setRoofDirection(Direction direction) {
        roofDirection = direction;
        getRoof().setDirection(direction);
        //TODO : Change gables and extension direction.
        updateRoof();
    }


    public float getExtraSlotDistance() {

        return extraSlotDistance;
    }

    @SuppressWarnings("unused")
    public void setExtraSlotDistance(float extraSlotDistance) {

        this.extraSlotDistance = extraSlotDistance;
    }

    public Entity getActiveElement() {

        return activeElement;
    }

    public void addElement(IElementsShack element) {
        elements.add(element);
    }

    public void removeElements(IElementsShack e) {
        elements.remove(e);
    }

    public void setActiveEntity(Entity activeElement) {
        this.activeElement = activeElement;
    }


    public boolean isValidHeight(float height) {
        return height >= 0;
    }

    public boolean isValidSlotDistance(float slotDistance) {
        return slotDistance >= 0 && slotDistance <= (this.getExteriorWall(Direction.FRONT).getThickness()) / 2;
    }

    @Override
    public void onDestroy() {
        throw new RuntimeException("You can't destroy the Shack! ヽ༼ ಠ益ಠ ༽ﾉ");
    }

    public Direction getRoofFacingDirection() {
        return roof.getDirection();
    }

    public boolean foundElTop(Vector2 coord) {

        for (Map.Entry<Direction, RectangleView[]> entry : _controller.getMapRectangleTopView().entrySet()) {

            for (RectangleView rect : entry.getValue()) {
                if (isInBounds(coord, new Vector2((float) rect.getX(), (float) rect.getY()), (float) rect.getWidth(), (float) rect.getHeight())) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean foundElLeft(Vector2 coord, IElementsShack e) {
        if (this.getRoof().getDirection() == Direction.FRONT || this.getRoof().getDirection() == Direction.BACK) {
            Wall leftWall = this.getExteriorWall(Direction.LEFT);
            Wall frontWall = this.getExteriorWall(Direction.RIGHT);
            float t = frontWall.getThickness();
            Vector2 topLeft = new Vector2((float) leftWall.transform.position.x - t, (float) leftWall.transform.position.y);
            return isInBounds(coord, topLeft, leftWall.getWidth() + 2 * t, leftWall.getHeight());
        } else return isInBounds(coord, e) && e.getDirection() == Direction.LEFT;
    }

    public boolean foundElRight(Vector2 coord, IElementsShack e) {
        if (this.getRoof().getDirection() == Direction.FRONT || this.getRoof().getDirection() == Direction.BACK) {
            Wall rightWall = this.getExteriorWall(Direction.RIGHT);
            Wall frontWall = this.getExteriorWall(Direction.RIGHT);
            float t = frontWall.getThickness();
            Vector2 topLeft = new Vector2((float) rightWall.transform.position.x - t, (float) rightWall.transform.position.y);
            return isInBounds(coord, topLeft, rightWall.getWidth() + 2 * t, rightWall.getHeight());
        } else return isInBounds(coord, e) && e.getDirection() == Direction.RIGHT;
    }

    public boolean foundElFront(Vector2 coord, IElementsShack e) {
        if (this.getRoof().getDirection() == Direction.LEFT || this.getRoof().getDirection() == Direction.RIGHT) {
            Wall frontWall = this.getExteriorWall(Direction.FRONT);
            Wall rightWall = this.getExteriorWall(Direction.RIGHT);
            float t = rightWall.getThickness();
            Vector2 topLeft = new Vector2((float) frontWall.transform.position.x - t, (float) frontWall.transform.position.y);
            return isInBounds(coord, topLeft, frontWall.getWidth() + 2 * t, frontWall.getHeight());
        } else return isInBounds(coord, e) && e.getDirection() == Direction.FRONT;
    }

    public boolean foundElBack(Vector2 coord, IElementsShack e) {
        if (this.getRoof().getDirection() == Direction.LEFT || this.getRoof().getDirection() == Direction.RIGHT) {
            Wall backWall = this.getExteriorWall(Direction.BACK);
            Wall rightWall = this.getExteriorWall(Direction.RIGHT);
            float t = rightWall.getThickness();
            Vector2 topLeft = new Vector2((float) backWall.transform.position.x - t, (float) backWall.transform.position.y);
            return isInBounds(coord, topLeft, backWall.getWidth() + 2 * t, backWall.getHeight());
        } else return isInBounds(coord, e) && e.getDirection() == Direction.BACK;
    }

    public boolean foundElement(Vector2 coord) {

        if (_controller.getCurrentView() == CurrentView.TOP) {
            return foundElTop(coord);
        } else {
            for (IElementsShack e : this.getElements()) {
                if (!e.isVisible())
                    continue;

                if (e.getElementType() == Roof.class || e.getElementType() == Extension.class || e.getElementType() == Gable.class) {
                    if (foundRoofElement(e, _controller.getCurrentView(), coord))
                        return true;
                }

                switch (_controller.getCurrentView()) {
                    case LEFT:
                        if (foundElLeft(coord, e))
                            return true;
                        break;
                    case RIGHT:
                        if (foundElRight(coord, e))
                            return true;
                        break;
                    case FRONT:
                        if (foundElFront(coord, e))
                            return true;
                        break;
                    case BACK:
                        if (foundElBack(coord, e))
                            return true;
                        break;
                }
            }
        }
        return false;
    }

    public Entity findSelectedElTop(Vector2 coord) {
        for (Map.Entry<Direction, RectangleView[]> entry : _controller.getMapRectangleTopView().entrySet()) {
            for (RectangleView rect : entry.getValue()) {
                if (isInBounds(coord, new Vector2((float) rect.getX(), (float) rect.getY()), (float) rect.getWidth(), (float) rect.getHeight())) {
                    for (IElementsShack e : this.getElements()) {
                        if (e.getDirection() == entry.getKey()) {
                            System.out.println(e.getDirection() + "");
                            return e.getEntity();
                        }
                    }
                }
            }
        }
        return null;
    }

    public Entity findSelectedElement(Vector2 coord) {

        Entity selected = null;

        if (_controller.getCurrentView() == CurrentView.TOP) {
            selected = findSelectedElTop(coord);
        } else {
            for (IElementsShack e : this.getElements()) {
                if (!e.isVisible())
                    continue;

                if (e.getElementType() == Roof.class || e.getElementType() == Extension.class || e.getElementType() == Gable.class) {
                    if (foundRoofElement(e, _controller.getCurrentView(), coord)) {
                        return e.getEntity();
                    } else {
                        continue;
                    }
                }


                switch (_controller.getCurrentView()) {

                    case LEFT:
                        if (this.getRoof().getDirection() == Direction.FRONT || this.getRoof().getDirection() == Direction.BACK) {
                            if (e.getElementType() == Wall.class) {
                                if (e.getDirection() == Direction.BACK) {
                                    Vector3 topLeft = this.getExteriorWall(Direction.LEFT).getPosition();
                                    Wall backWall = this.getExteriorWall(Direction.BACK);
                                    float t = backWall.getThickness();
                                    Vector2 topRainureLeft = new Vector2((float) topLeft.x - t, (float) topLeft.y);
                                    if (isInBounds(coord, topRainureLeft, t, backWall.getHeight()))
                                        selected = e.getEntity();
                                } else if (e.getDirection() == Direction.FRONT) {
                                    Wall leftWall = this.getExteriorWall(Direction.LEFT);
                                    Wall frontWall = this.getExteriorWall(Direction.FRONT);
                                    Vector2 topRight = new Vector2((float) leftWall.transform.position.x + leftWall.getWidth(), (float) leftWall.transform.position.y);
                                    float t = frontWall.getThickness();
                                    if (isInBounds(coord, topRight, t, frontWall.getHeight()))
                                        selected = e.getEntity();
                                } else if (e.getDirection() == Direction.LEFT) {
                                    if (isInBounds(coord, e)) {
                                        selected = e.getEntity();
                                    }
                                }
                            } else {
                                if (isInBoundsAccessory(coord, e))
                                    return e.getEntity();
                            }
                        } else if (e.getDirection() == Direction.LEFT) {
                            if (e.getElementType() == Wall.class && isInBounds(coord, e)) {
                                selected = e.getEntity();
                            } else if (e.getElementType() == Window.class || e.getElementType() == Door.class) {
                                if (isInBoundsAccessory(coord, e)) {
                                    return e.getEntity();
                                }
                            }
                        }
                        break;
                    case RIGHT:
                        if (this.getRoof().getDirection() == Direction.FRONT || this.getRoof().getDirection() == Direction.BACK) {
                            if (e.getElementType() == Wall.class) {
                                if (e.getDirection() == Direction.FRONT) {
                                    Vector3 topLeft = this.getExteriorWall(Direction.RIGHT).getPosition();
                                    Wall frontWall = this.getExteriorWall(Direction.FRONT);
                                    float t = frontWall.getThickness();
                                    Vector2 topRainureLeft = new Vector2((float) topLeft.x - t, (float) topLeft.y);
                                    if (isInBounds(coord, topRainureLeft, t, frontWall.getHeight()))
                                        selected = e.getEntity();
                                } else if (e.getDirection() == Direction.BACK) {
                                    Wall rightWall = this.getExteriorWall(Direction.RIGHT);
                                    Wall frontWall = this.getExteriorWall(Direction.BACK);
                                    Vector2 topRight = new Vector2((float) rightWall.transform.position.x + rightWall.getWidth(), (float) rightWall.transform.position.y);
                                    float t = frontWall.getThickness();
                                    if (isInBounds(coord, topRight, t, frontWall.getHeight()))
                                        selected = e.getEntity();
                                } else if (e.getDirection() == Direction.RIGHT) {
                                    if (isInBounds(coord, e)) {
                                        selected = e.getEntity();
                                    }
                                }
                            } else {
                                if (isInBoundsAccessory(coord, e))
                                    return e.getEntity();
                            }
                        } else if (e.getDirection() == Direction.RIGHT) {
                            if (e.getElementType() == Wall.class && isInBounds(coord, e)) {
                                selected = e.getEntity();
                            } else if (e.getElementType() == Window.class || e.getElementType() == Door.class) {
                                if (isInBoundsAccessory(coord, e)) {
                                    return e.getEntity();
                                }
                            }
                        }
                        break;
                    case FRONT:
                        if (this.getRoof().getDirection() == Direction.LEFT || this.getRoof().getDirection() == Direction.RIGHT) {
                            if (e.getElementType() == Wall.class) {
                                if (e.getDirection() == Direction.LEFT) {
                                    Vector3 topLeft = this.getExteriorWall(Direction.FRONT).getPosition();
                                    Wall leftWall = this.getExteriorWall(Direction.LEFT);
                                    float t = leftWall.getThickness();
                                    Vector2 topRainureLeft = new Vector2((float) topLeft.x - t, (float) topLeft.y);
                                    if (isInBounds(coord, topRainureLeft, t, leftWall.getHeight()))
                                        selected = e.getEntity();
                                } else if (e.getDirection() == Direction.RIGHT) {
                                    Wall rightWall = this.getExteriorWall(Direction.RIGHT);
                                    Wall frontWall = this.getExteriorWall(Direction.BACK);
                                    Vector2 topRight = new Vector2((float) frontWall.transform.position.x + frontWall.getWidth(), (float) frontWall.transform.position.y);
                                    float t = rightWall.getThickness();
                                    if (isInBounds(coord, topRight, t, frontWall.getHeight()))
                                        selected = e.getEntity();
                                } else if (e.getDirection() == Direction.FRONT) {
                                    if (isInBounds(coord, e)) {
                                        selected = e.getEntity();
                                    }
                                }
                            } else {
                                if (isInBoundsAccessory(coord, e))
                                    return e.getEntity();
                            }
                        } else if (e.getDirection() == Direction.FRONT) {
                            if (e.getElementType() == Wall.class && isInBounds(coord, e)) {
                                selected = e.getEntity();
                            } else if (e.getElementType() == Window.class || e.getElementType() == Door.class) {
                                if (isInBoundsAccessory(coord, e)) {
                                    return e.getEntity();
                                }
                            }
                        }
                        break;
                    case BACK:
                        if (this.getRoof().getDirection() == Direction.LEFT || this.getRoof().getDirection() == Direction.RIGHT) {
                            if (e.getElementType() == Wall.class) {
                                if (e.getDirection() == Direction.RIGHT) {
                                    Vector3 topLeft = this.getExteriorWall(Direction.BACK).getPosition();
                                    Wall rightWall = this.getExteriorWall(Direction.RIGHT);
                                    float t = rightWall.getThickness();
                                    Vector2 topRainureLeft = new Vector2((float) topLeft.x - t, (float) topLeft.y);
                                    if (isInBounds(coord, topRainureLeft, t, rightWall.getHeight()))
                                        selected = e.getEntity();
                                } else if (e.getDirection() == Direction.LEFT) {
                                    Wall backWall = this.getExteriorWall(Direction.BACK);
                                    Wall leftWall = this.getExteriorWall(Direction.LEFT);
                                    Vector2 topRight = new Vector2((float) leftWall.transform.position.x + leftWall.getWidth(), (float) leftWall.transform.position.y);
                                    float t = backWall.getThickness();
                                    if (isInBounds(coord, topRight, t, backWall.getHeight()))
                                        selected = e.getEntity();
                                } else if (e.getDirection() == Direction.BACK) {
                                    if (isInBounds(coord, e)) {
                                        selected = e.getEntity();
                                    }
                                }
                            } else {
                                if (isInBoundsAccessory(coord, e))
                                    return e.getEntity();
                            }
                        } else if (e.getDirection() == Direction.BACK) {
                            if (e.getElementType() == Wall.class && isInBounds(coord, e)) {
                                selected = e.getEntity();
                            } else if (e.getElementType() == Window.class || e.getElementType() == Door.class) {
                                if (isInBoundsAccessory(coord, e)) {
                                    return e.getEntity();
                                }
                            }
                        }
                        break;
                }
            }
        }

        return selected;
    }

    public boolean foundRoofElement(IElementsShack e, CurrentView view, Vector2 coord) {
        if (e.getElementType() == Gable.class && foundGableElement(e, view, coord))
            return true;
        if (e.getElementType() == Extension.class && foundExtensionElement(e, view, coord))
            return true;

        boolean isLeftOrRightView = view == CurrentView.LEFT || view == CurrentView.RIGHT;
        Direction roof_direction = this.getRoofFacingDirection();

        CurrentView roof_d;
        if (roof_direction == Direction.LEFT || roof_direction == Direction.RIGHT) {
            roof_d = roof_direction == Direction.LEFT ? CurrentView.LEFT : CurrentView.RIGHT;
        } else {
            roof_d = roof_direction == Direction.FRONT ? CurrentView.FRONT : CurrentView.BACK;
        }

        boolean same_dir = roof_d == view;
        boolean opposite = (roof_d == CurrentView.LEFT && view == CurrentView.RIGHT) || (roof_d == CurrentView.RIGHT && view == CurrentView.LEFT)
                || (roof_d == CurrentView.FRONT && view == CurrentView.BACK) || (roof_d == CurrentView.BACK && view == CurrentView.FRONT);
        boolean right_dir = (view == CurrentView.LEFT && roof_d == CurrentView.FRONT) || (view == CurrentView.FRONT && roof_d == CurrentView.RIGHT)
                || (view == CurrentView.RIGHT && roof_d == CurrentView.BACK) || (view == CurrentView.BACK && roof_d == CurrentView.LEFT);

        float e_w = isLeftOrRightView ? this.getExteriorWall(Direction.LEFT).getWidth() : this.getExteriorWall(Direction.FRONT).getWidth();
        float e_h = (float) (e_w * Math.tan(Math.toRadians(this.getRoof().getRoofAngle())));
        float t = this.getPanelsThickness();
        float w_w = isLeftOrRightView ? this.getExteriorWall(Direction.LEFT).getWidth() : this.getExteriorWall(Direction.FRONT).getWidth();
        float x_o = isLeftOrRightView ? this.getExteriorWall(Direction.LEFT).getX() : this.getExteriorWall(Direction.FRONT).getX();
        float y_o = isLeftOrRightView ? this.getExteriorWall(Direction.LEFT).getY() : this.getExteriorWall(Direction.FRONT).getY();

        if (same_dir) {
            e_w = isLeftOrRightView ? this.getExteriorWall(Direction.FRONT).getWidth() : this.getExteriorWall(Direction.LEFT).getWidth();
            e_h = (float) (e_w * Math.tan(Math.toRadians(this.getRoof().getRoofAngle())));
            if (isInBounds(coord, x_o, y_o - e_h - t / 2, w_w, e_h + t / 2)) {
                return true;
            }
        } else if (opposite) {
            e_w = isLeftOrRightView ? this.getExteriorWall(Direction.FRONT).getWidth() : this.getExteriorWall(Direction.LEFT).getWidth();
            e_h = (float) (e_w * Math.tan(Math.toRadians(this.getRoof().getRoofAngle())));
            if (isInBounds(coord, x_o, y_o - e_h - t / 2, w_w, t / 2)) {
                return true;
            }
        } else if (right_dir) {
            Vector2 top_l = new Vector2(x_o - t / 2, y_o - e_h - t / 2);
            Vector2 bot_l = new Vector2(x_o - t / 2, y_o - e_h);
            Vector2 top_r = new Vector2(x_o + w_w, y_o - t / 2);
            Vector2 bot_r = new Vector2(x_o + w_w, y_o);
            if (isInSkewedRectangle(top_l, bot_l, top_r, bot_r, coord)) {
                return true;
            }
        } else {
            Vector2 top_r = new Vector2(x_o + w_w + t / 2, y_o - e_h - t / 2);
            Vector2 bot_r = new Vector2(x_o + w_w + t / 2, y_o - e_h);
            Vector2 top_l = new Vector2(x_o - t / 2, y_o - t / 2);
            Vector2 bot_l = new Vector2(x_o - t / 2, y_o);
            if (isInSkewedRectangle(top_r, bot_r, top_l, bot_l, coord)) {
                return true;
            }
        }

        return false;
    }

    public boolean foundGableElement(IElementsShack e, CurrentView view, Vector2 coord) {
        boolean isLeftOrRightView = view == CurrentView.LEFT || view == CurrentView.RIGHT;
        Direction roof_direction = this.getRoofFacingDirection();

        CurrentView roof_d;
        if (roof_direction == Direction.LEFT || roof_direction == Direction.RIGHT) {
            roof_d = roof_direction == Direction.LEFT ? CurrentView.LEFT : CurrentView.RIGHT;
        } else {
            roof_d = roof_direction == Direction.FRONT ? CurrentView.FRONT : CurrentView.BACK;
        }

        boolean same_dir = roof_d == view;
        boolean opposite = (roof_d == CurrentView.LEFT && view == CurrentView.RIGHT) || (roof_d == CurrentView.RIGHT && view == CurrentView.LEFT)
                || (roof_d == CurrentView.FRONT && view == CurrentView.BACK) || (roof_d == CurrentView.BACK && view == CurrentView.FRONT);
        boolean right_dir = (view == CurrentView.LEFT && roof_d == CurrentView.FRONT) || (view == CurrentView.FRONT && roof_d == CurrentView.RIGHT)
                || (view == CurrentView.RIGHT && roof_d == CurrentView.BACK) || (view == CurrentView.BACK && roof_d == CurrentView.LEFT);

        double g_w;
        double x_o;
        double y_o;
        if (isLeftOrRightView) {
            g_w = this.getExteriorWall(Direction.LEFT).getWidth();
            x_o = this.getExteriorWall(Direction.LEFT).getX();
            y_o = this.getExteriorWall(Direction.LEFT).getY();
        } else {
            g_w = this.getExteriorWall(Direction.FRONT).getWidth();
            x_o = this.getExteriorWall(Direction.FRONT).getX();
            y_o = this.getExteriorWall(Direction.FRONT).getY();
        }
        double g_h = g_w * Math.tan(Math.toRadians(this.getRoof().getRoofAngle()));

        if (same_dir) {
            return false;
        } else if (opposite) {
            return false;
        } else if (right_dir) {
            if (isInside(x_o, y_o, x_o, y_o - g_h, x_o + g_w, y_o, coord.getX(), coord.getY()))
                return true;
        } else {
            if (isInside(x_o + g_w, y_o, x_o + g_w, y_o - g_h, x_o, y_o, coord.getX(), coord.getY()))
                return true;
        }

        return false;
    }

    public boolean foundExtensionElement(IElementsShack e, CurrentView view, Vector2 coord) {
        Direction roof_direction = this.getRoofFacingDirection();

        CurrentView roof_d;
        if (roof_direction == Direction.LEFT || roof_direction == Direction.RIGHT) {
            roof_d = roof_direction == Direction.LEFT ? CurrentView.LEFT : CurrentView.RIGHT;
        } else {
            roof_d = roof_direction == Direction.FRONT ? CurrentView.FRONT : CurrentView.BACK;
        }
        boolean opposite = (roof_d == CurrentView.LEFT && view == CurrentView.RIGHT) || (roof_d == CurrentView.RIGHT && view == CurrentView.LEFT)
                || (roof_d == CurrentView.FRONT && view == CurrentView.BACK) || (roof_d == CurrentView.BACK && view == CurrentView.FRONT);
        boolean right_dir = (view == CurrentView.LEFT && roof_d == CurrentView.FRONT) || (view == CurrentView.FRONT && roof_d == CurrentView.RIGHT)
                || (view == CurrentView.RIGHT && roof_d == CurrentView.BACK) || (view == CurrentView.BACK && roof_d == CurrentView.LEFT);
        boolean left_dir = (view == CurrentView.LEFT && roof_d == CurrentView.BACK) || (view == CurrentView.FRONT && roof_d == CurrentView.LEFT)
                || (view == CurrentView.RIGHT && roof_d == CurrentView.FRONT) || (view == CurrentView.BACK && roof_d == CurrentView.RIGHT);

        boolean isLeftOrRightView = view == CurrentView.LEFT || view == CurrentView.RIGHT;
        float e_w = isLeftOrRightView ? this.getExteriorWall(Direction.FRONT).getWidth() : this.getExteriorWall(Direction.LEFT).getWidth();
        double e_h = e_w * Math.tan(Math.toRadians(this.getRoof().getRoofAngle()));
        float w_w = isLeftOrRightView ? this.getExteriorWall(Direction.LEFT).getWidth() : this.getExteriorWall(Direction.FRONT).getWidth();
        float x_o = isLeftOrRightView ? this.getExteriorWall(Direction.LEFT).getX() : this.getExteriorWall(Direction.FRONT).getX();
        float y_o = isLeftOrRightView ? this.getExteriorWall(Direction.LEFT).getY() : this.getExteriorWall(Direction.FRONT).getY();
        float t = this.getPanelsThickness();

        if (opposite) {
            if (isInBounds(coord, x_o, y_o - (float) e_h, e_w, (float) e_h)) {
                return true;
            }
        } else if (right_dir) {
            if (isInBounds(coord, x_o - t / 2, y_o - (float) e_h, t / 2, (float) e_h)) {
                return true;
            }
        } else if (left_dir) {
            if (isInBounds(coord, x_o + w_w, y_o - (float) e_h, t / 2, (float) e_h)) {
                return true;
            }
        }

        return false;
    }

    double area(double x1, double y1, double x2, double y2, double x3, double y3) {
        return Math.abs((x1 * (y2 - y3) + x2 * (y3 - y1) + x3 * (y1 - y2)) / 2.0);
    }

    double area(Vector2 p1, Vector2 p2, Vector2 p3) {
        double x1 = p1.getX();
        double x2 = p2.getX();
        double x3 = p3.getX();
        double y1 = p1.getY();
        double y2 = p2.getY();
        double y3 = p3.getY();
        return Math.abs((x1*(y2 - y3) + x2*(y3 - y1) + x3*(y1 - y2)) / 2.0);
    }

    boolean isInside(double x1, double y1, double x2, double y2, double x3, double y3, double x, double y) {
        double A = area(x1, y1, x2, y2, x3, y3);

        double A1 = area(x, y, x2, y2, x3, y3);
        double A2 = area(x1, y1, x, y, x3, y3);
        double A3 = area(x1, y1, x2, y2, x, y);

        double epsilon = 0.000001;
        return Math.abs(A - (A1 + A2 + A3)) < epsilon;
    }

    public boolean isInSkewedRectangle(Vector2 p1, Vector2 p2, Vector2 p3, Vector2 p4, Vector2 point) {

        double totalArea = area(p1, p2, p3) + area(p2, p3, p4);

        double a_1 = area(p1, point, p4);
        double a_2 = area(p4, point, p3);
        double a_3 = area(p3, point, p2);
        double a_4 = area(point, p2, p1);

        return (a_1 + a_2 + a_3 + a_4) < totalArea;
    }

    public void updateRoof() {

        float gable_h;
        gables.get(Direction.LEFT).setThickness(panelsThickness);
        gables.get(Direction.RIGHT).setThickness(panelsThickness);
        if (roofDirection == Direction.FRONT || roofDirection == Direction.BACK) {
            gables.get(Direction.LEFT).setWidth(leftRightWallsWidth);
            gables.get(Direction.RIGHT).setWidth(leftRightWallsWidth);
            gable_h = (float) (leftRightWallsWidth * Math.tan(Math.toRadians(roofAngle)));
        } else {
            gables.get(Direction.LEFT).setWidth(frontBackWallWidth);
            gables.get(Direction.RIGHT).setWidth(frontBackWallWidth);
            gable_h = (float) (frontBackWallWidth * Math.tan(Math.toRadians(roofAngle)));
        }
        gables.get(Direction.LEFT).setHeight(gable_h);
        gables.get(Direction.RIGHT).setHeight(gable_h);

        extension.setThickness(panelsThickness);
        float extension_h;
        if (roofDirection == Direction.FRONT || roofDirection == Direction.BACK) {
            extension_h = (float) ((leftRightWallsWidth + panelsThickness / 2) * Math.tan(Math.toRadians(roofAngle)));
            extension.setWidth(frontBackWallWidth);
        } else {
            extension_h = (float) ((frontBackWallWidth + panelsThickness / 2) * Math.tan(Math.toRadians(roofAngle)));
            extension.setWidth(leftRightWallsWidth);
        }
        extension.setHeight(extension_h);

        roof.setThickness(panelsThickness / 2);

        if (roofDirection == Direction.FRONT || roofDirection == Direction.BACK) {
            roof.setHeight((float) (1 / (Math.sin(roofAngle) * 1 / (leftRightWallsWidth + panelsThickness))));
            //roof.setWidth(frontBackWallWidth);
        } else {
            roof.setHeight((float) (1 / (Math.sin(roofAngle) * 1 / (frontBackWallWidth + panelsThickness))));
            //roof.setWidth(leftRightWallsWidth);
        }

    }

    public float getLeftRightWallsWidth() {
        return leftRightWallsWidth;
    }

    public float getFrontBackWallWidth() {
        return frontBackWallWidth;
    }
}