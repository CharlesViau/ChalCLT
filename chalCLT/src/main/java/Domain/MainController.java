package Domain;

import Domain.Drawing.*;
import Domain.Enum.Direction;
import Domain.Exporter.BrutExporterSTL;
import Domain.Exporter.FiniExporterSTL;
import Domain.Exporter.RetraitExporterSTL;
import Domain.General.Components.Component;
import Domain.General.Entity;
import Domain.General.EntityManager;
import Domain.History.*;
import Domain.Interface.*;
import Domain.MenuActions.ProjectManager;
import Domain.Shack.Panels.Wall;
import Domain.Shack.Shack;
import Domain.Shack.ShackFactory;
import Domain.Utility.Vector2;
import Domain.Utility.ViewHolder;
import Domain.Utility.ZoomConverter;
import Domain.Utility.ZoomUtility;
import View.HeaderEnum;
import View.CurrentView;
import ca.ulaval.glo2004.gui.MainWindow;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.function.Consumer;

public class MainController implements IPropertyChangeManager, IObjectSelectorManager {
    private final ViewHolder viewHolder;
    //private CurrentView currentView = CurrentView.LEFT;
    private Shack shack;
    private final ShackFactory shackFactory;

    private final Grid grid;
    private static final float DEFAULT_GRID_DISTANCE = 15;

    private final WallDrawer wallDrawer;

    private final TopDrawer topDrawer;

    private final GridDrawer gridDrawer;

    private final ShackDrawer shackDrawer;

    private Vector2 panelSize;

    private final ArrayList<IObjectSelectorListener> selectionListeners;

    private static MainController instance;

    private HashMap<Direction, RectangleView[]> mapRectangleTopView;

    private final EntityManager entityManager;
    private final ProjectManager projectManager;

    private final ZoomUtility zoomUtility = new ZoomUtility();
    private final History m_history = new History();

    private BrutExporterSTL exporterBrutSTL;
    private FiniExporterSTL exporterFiniSTL;
    private RetraitExporterSTL exporterRetraitSTL;
    private Vector2 center = new Vector2(0, 0);

    public MainController(String appName) {

        if (instance != null)
            throw new RuntimeException("MainController already exists!");
        instance = this;
        //Create EntityManager
        entityManager = new EntityManager();

        //Create project manager
        projectManager = new ProjectManager();

        //Create Shack
        shackFactory = new ShackFactory();

        shack = shackFactory.createShack(entityManager, this);

        grid = new Grid(DEFAULT_GRID_DISTANCE, false);
        ZoomConverter zoom = new ZoomConverter(zoomUtility);
        AffineTransform affineTransform = new AffineTransform();

        //Drawing Stuff
        viewHolder = new ViewHolder(CurrentView.TOP);
        wallDrawer = new WallDrawer(this, viewHolder, zoom,affineTransform, shack);
        topDrawer = new TopDrawer(this, viewHolder, zoom,affineTransform);
        gridDrawer = new GridDrawer(this, viewHolder, grid, zoom,affineTransform);
        shackDrawer = new ShackDrawer();

        //Listeners
        selectionListeners = new ArrayList<>();
        //MainWindow
        MainWindow.Init(this, appName,affineTransform);
        Vector2 panelSize = this.getPanelSize();
        //shack.setPanelSize(panelSize);

        //Exporter
        exporterBrutSTL = new BrutExporterSTL();
        exporterFiniSTL = new FiniExporterSTL();
        exporterRetraitSTL = new RetraitExporterSTL();

        projectManager.subISaveable(entityManager);

        entityManager.OnPropertyChange.addEventHandler((ctx) -> {
            projectManager.setDirty();
        });

        notifySelection(shack.entity);
    }

    public void drawingPanelMouseClicked(Vector2 coordinates) {
        if (shack.foundElement(coordinates)) {

            Entity selected = shack.findSelectedElement(coordinates);
            Command c = new PanelCommand(selected, shack.getActiveElement(), shack, new ListenersAccessor(this));
            m_history.Push(c);
        }
    }


    public HashMap<Direction, RectangleView[]> getMapRectangleTopView() {
        return mapRectangleTopView;
    }

    public void setMapRectangleTopView(HashMap<Direction, RectangleView[]> mapRectangleTopView) {
        this.mapRectangleTopView = mapRectangleTopView;
    }

    public CurrentView getCurrentView() {
        return viewHolder.getCurrentView();
    }

    public void setCurrentView(CurrentView view) {
        viewHolder.setCurrentView(view);
    }

    public float getExteriorPanelThickness() {
        return shack.getPanelsThickness();
    }

    public float getExtraSlotDistance() {
        return shack.getExtraSlotDistance();
    }

    public float getRoofInclinaisonAngle() {
        return shack.getRoof().getRoofAngle();
    }

    public Direction getRoofFacingDirection() {
        return shack.getRoof().getDirection();
    }

    public void setFrontAndBackWallsWidth(float width) {
        shack.getExteriorWall(Direction.FRONT).setWidth(width);
        shack.getExteriorWall(Direction.BACK).setWidth(width);
    }

    public float getFrontAndBackWallsWidth() {
        return shack.getExteriorWall(Direction.FRONT).getWidth();
    }

    public void setSideWallsWidth(float width) {
        shack.getExteriorWall(Direction.LEFT).setWidth(width);
        shack.getExteriorWall(Direction.RIGHT).setWidth(width);
    }

    public float getSideWallsWidth() {
        return shack.getExteriorWall(Direction.LEFT).getWidth();
    }

    public void setWallsHeight(float height) {
        shack.setWallsHeight(height);
    }

    public float getWallsHeight() {
        return shack.getWallsHeight();
    }

    public void fileAction(HeaderEnum header, String file) {

    }

    public void selectedViewChanged(CurrentView view, Graphics g) {
        if (shackDrawer != null && shack != null && grid != null && wallDrawer != null && topDrawer != null && gridDrawer != null && g != null) {
            CurrentView oldV = this.getCurrentView();
            //éviter les doublons
            if (view != oldV) {
                ViewCommand viewCommand = new ViewCommand(view, oldV, shackDrawer, shack, grid, wallDrawer, topDrawer, gridDrawer, g, viewHolder);
                m_history.Push(viewCommand);
            }
            //empêche le code de ne PAS afficher le chalet à l'ouverture
            else {
                shackDrawer.changeSelectedView(view, shack, grid, wallDrawer, topDrawer, gridDrawer, g);

            }

        }


    }

    public void setPanelSize(Vector2 panelSize) {
        this.panelSize = panelSize;
        shack.setPanelSize(panelSize);
    }

    public Vector2 getPanelSize() {
        return panelSize;
    }


    @Override
    public void editProperty(UUID objectUuid, Class<? extends Component> componentType, String fieldName, Object newValue) {
        m_history.Push(new PropertyCommand(newValue,null,entityManager,objectUuid, componentType, fieldName));
    }

    @Override
    public void subscribeToPropertyChangeEvents(Consumer<Object[]> eventHandler) {
        entityManager.OnPropertyChange.addEventHandler(eventHandler);
    }

    @Override
    public boolean validatePropertyChange(UUID objectUuid, Class<? extends Component> component, String fieldName, String newValue) {
        return entityManager.validatePropertyChange(objectUuid, component, fieldName, newValue);
    }

    @Override
    public void addObject(UUID parentObject, Class<? extends Component> selectedComp, Class<? extends Component> typeToAdd, Object[] args) {
        AjouterHistorique(new AccessoryCommand(parentObject,null, entityManager,selectedComp, typeToAdd, args));
    }

    @Override
    public boolean validateObjectCreation(UUID parentUuid,Class<? extends Component> selectedComponent, Class<? extends Component> typeToAdd, Object[] args) {
        return entityManager.validateObjectCreation(parentUuid, selectedComponent, typeToAdd, args);
    }

    @Override
    public void removeObject(UUID objectUuid, UUID parentObject, Class<? extends Component> selectedComponent, Class<? extends Component> typeToAdd) {

        AjouterHistorique(new AccessoryDeletionCommand(objectUuid,null,entityManager,selectedComponent,typeToAdd,parentObject));
    }

    @Override
    public void subscribeToSelectionEvents(IObjectSelectorListener listener) {
        selectionListeners.add(listener);
    }

    @Override
    public void notifySelection(IInspectable obj) {

        for (IObjectSelectorListener listener : selectionListeners) {
            listener.onObjectSelection(obj.getEntity());
        }
    }

    @Override
    public void notifyUnselection() {
        for (IObjectSelectorListener listener : selectionListeners) {
            listener.onObjectUnselect();
        }
    }

    public float getZoom() {
        return zoomUtility.getZoomFactor();
    }

    public void setZoom(float zoom) {
        zoomUtility.setZoomFactor(zoom);
    }

    public Vector2 getCenterGap() {
        return zoomUtility.getCenterGap();
    }

    public void setCenterGap(Vector2 centerGap) {
        zoomUtility.setCenterGap(centerGap.getX(), centerGap.getY());
    }
    public Vector2 getCenterDiff() {
        return zoomUtility.getMousePosition();
    }
    public void setCenterDiff(Vector2 p){
        zoomUtility.setMousePosition(p);
    }

    public boolean saveProject() {
        return projectManager.save();
    }

    public void saveProjectAs(File file) {
        projectManager.saveAs(file);
    }

    public void openProject(File file) {
        m_history.clearHistory();
        projectManager.open(file);
        shack = entityManager.getShack();
        assert shack != null;
        shack.setController(this);
        wallDrawer.setShack(shack);

        entityManager.OnPropertyChange.invoke();
    }

    public void newProject() {
        m_history.clearHistory();
        projectManager.newProject();
        shack = shackFactory.createShack(entityManager, this);
        wallDrawer.setShack(shack);
        notifySelection(shack.entity);
        entityManager.OnPropertyChange.invoke();
    }

    public boolean isSaved() {
        return projectManager.isSaved();
    }

    public File getSaveLocation() {
        return projectManager.getSaveLocation();
    }

    public String getCurrentProjectName() {
        return projectManager.getCurrentProjectName();
    }

    public void setCurrentProjectName() {
        String name = projectManager.getCurrentProjectName();
        exporterFiniSTL.setProjectName(name);
        exporterBrutSTL.setProjectName(name);
    }

    public void exportBrut(File file) throws IOException {
        exporterBrutSTL.convertirMursSTL(this.shack, file);
    }

    public void exportFini(File file) throws IOException {
        exporterFiniSTL.convertirMursSTL(this.shack, file);
    }

    public void exportRetrait(File file) throws IOException {
        exporterRetraitSTL.convertRetraitSTL(this.shack, file);
    }

    public void Undo() {
        m_history.Pop();
    }

    public void Redo() {
        m_history.Redo();
    }


    public ArrayList<IInspectable> getIInspectable(){
        return entityManager.getEntities();
    }

    public float getGridDistance() {
        return grid.getDistance();
    }

    public void setGridDistance(float distance) {
        Command c = new CommandMesureGrille(distance,grid.getDistance(),grid);
        m_history.Push(c);
        entityManager.OnPropertyChange.invoke();
    }

    public boolean getGridVisibility() {
        return grid.isVisible();
    }

    public void setGridVisibility(boolean visible) {
        Command c = new CommandGrille(visible,!visible,grid);
        m_history.Push(c);
        entityManager.OnPropertyChange.invoke();
    }

    public float getXOffset() {
        return grid.getXOffset();
    }

    public void setXOffset(float xOffset) {

        grid.setXOffset(xOffset);
        entityManager.OnPropertyChange.invoke();
    }

    public float getYOffset() {
        return grid.getYOffset();
    }

    public void setYOffset(float yOffset) {

        grid.setYOffset(yOffset);
        entityManager.OnPropertyChange.invoke();
    }

    public void AjouterHistorique(Command c){
        m_history.Push(c);
    }

    public void moveAccessory(Vector2 pos, Vector2 initial_pos,boolean finalDeplacement) {
        Direction dir = null;
        switch(getCurrentView()) {
            case LEFT:
                dir = Direction.LEFT;
                break;
            case RIGHT:
                dir = Direction.RIGHT;
                break;
            case FRONT:
                dir = Direction.FRONT;
                break;
            case BACK:
                dir = Direction.BACK;
                break;
        }
        Wall currentWall = this.shack.getExteriorWall(dir);
        if(finalDeplacement){

            AjouterHistorique(new AccessoryMovedCommand(pos,initial_pos,currentWall));
        }
        else{
            currentWall.moveAccessory(pos, initial_pos,false);
        }

    }


    public boolean foundElementOnMouse(Vector2 coord) {
        return shack.foundElement(coord);
    }

    public Entity findSelectedElement(Vector2 coord) {
        return shack.findSelectedElement(coord);
    }

    public Vector2 getCenter() {
        return this.center;
    }

    public void setCenter(Vector2 c) {
        this.center = c;
    }
}



