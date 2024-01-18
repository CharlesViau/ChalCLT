package Domain.Drawing;

import Domain.MainController;
import Domain.Utility.Vector2;
import Domain.Utility.ViewHolder;
import Domain.Utility.ZoomConverter;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;

public class GridDrawer extends Drawer {

    Grid grid;
    public GridDrawer(MainController controller, ViewHolder viewHolder, Grid grid, ZoomConverter zoom, AffineTransform affineTransform) {
        super(controller, viewHolder,zoom);
        this.grid = grid;
    }

    public void draw(Graphics g) {
        Graphics2D g2 = (Graphics2D)g;
        g2.setStroke(new BasicStroke(2));
        g2.setColor(Color.GRAY);

        double panelWidth = controller.getPanelSize().getX();
        double panelHeight = controller.getPanelSize().getY();
        double distance = inchToPixel(grid.getDistance());
        double MARGIN = 25;

        float zoom = zoomConverter.getZoomFactor();
        double xOffset = grid.getXOffset() * distance;
        double yOffset = grid.getYOffset() * distance;

        AffineTransform oldTransform = g2.getTransform();
        AffineTransform newTransform = new AffineTransform();
        Vector2 center = controller.getCenter();
        newTransform.translate(center.getX(), center.getY());
        newTransform.scale(zoom, zoom);
        newTransform.translate(-center.getX(), -center.getY());
        g2.transform(newTransform);

        for(double i = panelHeight/2 - distance/2; i >= -MARGIN*panelHeight; i -= distance) {
            Line2D.Double line = new Line2D.Double(-panelWidth*MARGIN, i + yOffset, 2*MARGIN*panelWidth, i + yOffset);
            g2.draw(line);
        }

        for(double i = panelHeight/2 + distance/2; i <= MARGIN*panelHeight; i += distance) {
            Line2D.Double line = new Line2D.Double(-MARGIN*panelWidth, i + yOffset, 2*MARGIN*panelWidth, i + yOffset);
            g2.draw(line);
        }

        for(double i = panelWidth/2 - distance/2; i >= -MARGIN*panelWidth; i -= distance) {
            Line2D.Double line = new Line2D.Double(i + xOffset, -panelHeight*MARGIN, i + xOffset, 2*panelHeight*MARGIN);
            g2.draw(line);
        }

        for(double i = panelWidth/2 + distance/2; i <= MARGIN*panelWidth; i += distance) {
            Line2D.Double line = new Line2D.Double(i + xOffset, -panelHeight*MARGIN, i + xOffset, 2*panelHeight*MARGIN);
            g2.draw(line);
        }

        g2.setTransform(oldTransform);

    }

}
