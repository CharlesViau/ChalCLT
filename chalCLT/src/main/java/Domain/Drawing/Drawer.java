package Domain.Drawing;
import Domain.MainController;
import Domain.Utility.Vector2;
import Domain.Utility.ViewHolder;
import Domain.Utility.ZoomConverter;
import View.CurrentView;
import java.awt.*;
import java.awt.geom.AffineTransform;

public abstract class Drawer {
    protected MainController controller;
    private ViewHolder currentView;
    protected ZoomConverter zoomConverter;

    protected Drawer(MainController controller, ViewHolder currentView,ZoomConverter zoomConverter) {
        this.controller = controller;
        this.currentView = currentView;
        this.zoomConverter = zoomConverter;
    }

    public CurrentView getCurrentView() {
        return this.currentView.getCurrentView();
    }
    public void setCurrentView(CurrentView view) {
        this.currentView.setCurrentView(view);
    }
    public float inchToPixel(float inches) {
        return inches * 4;
    }
    protected void draw(Graphics g) {
    }

    protected void applyZoomAndTranslation(Graphics2D g2) {
        float zoom = zoomConverter.getZoomFactor();
        Vector2 center = controller.getCenter();
        double panelWidth = controller.getPanelSize().getX();
        double panelHeight = controller.getPanelSize().getY();

        AffineTransform transform = new AffineTransform();
        transform.translate(center.getX(), center.getY());
        transform.scale(zoom, zoom);
        transform.translate(-center.getX(), -center.getY());
        g2.setTransform(transform);
    }
}