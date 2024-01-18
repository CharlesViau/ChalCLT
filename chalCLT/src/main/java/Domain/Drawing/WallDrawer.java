package Domain.Drawing;

import Domain.Enum.Direction;
import Domain.Interface.IElementsShack;
import Domain.MainController;
import Domain.Shack.Accessory.Accessory;
import Domain.Shack.Accessory.Door;
import Domain.Shack.Panels.Wall;
import Domain.Shack.Shack;
import Domain.Utility.Vector2;
import Domain.Utility.ViewHolder;
import Domain.Utility.ZoomConverter;
import View.CurrentView;

import java.awt.*;
import java.awt.geom.*;

public class WallDrawer extends Drawer {
    private AffineTransform _affineTransfom;
    private Shack shack;
    public WallDrawer(MainController controller, ViewHolder viewHolder, ZoomConverter zoom, AffineTransform affineTransform, Shack shack) {
        super(controller, viewHolder,zoom);
        _affineTransfom = affineTransform;
        this.shack = shack;
    }

    @Override
    public void draw(Graphics g) {

        CurrentView currentView = this.getCurrentView();

        MainController controller = this.controller;

        float zoom = zoomConverter.getZoomFactor();
        Vector2 gap = zoomConverter.getCenterGap();
        double panelWidth = controller.getPanelSize().getX();
        double panelHeight = controller.getPanelSize().getY();

        float wallHeight = inchToPixel(controller.getWallsHeight());

        float wallWidth;
        Wall currentWall = null;
        if(currentView == CurrentView.LEFT || currentView == CurrentView.RIGHT) {
            wallWidth = inchToPixel(controller.getSideWallsWidth());
            if(currentView == CurrentView.LEFT) {
                currentWall = shack.getExteriorWall(Direction.LEFT);
            } else {
                currentWall = shack.getExteriorWall(Direction.RIGHT);
            }
        } else {
            wallWidth = inchToPixel(controller.getFrontAndBackWallsWidth());
            if(currentView == CurrentView.BACK) {
                currentWall = shack.getExteriorWall(Direction.BACK);
            } else {
                currentWall = shack.getExteriorWall(Direction.FRONT);
            }
        }

        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(5));

        Color brunPale = new Color(236, 158, 80);
        Color brunFonce = new Color(200, 125, 50);
        Color beauBrun = new Color(153, 75, 0);
        if(currentView == CurrentView.LEFT || currentView == CurrentView.RIGHT) {
            g2.setColor(brunPale);
        } else {
            g2.setColor(brunFonce);
        }


        AffineTransform oldTransform = g2.getTransform();
        AffineTransform newTransform = new AffineTransform();
        Vector2 center = controller.getCenter();
        newTransform.translate(center.getX(), center.getY());
        newTransform.scale(zoom, zoom);
        newTransform.translate(-center.getX(), -center.getY());
        g2.transform(newTransform);

        Direction roofDirection = controller.getRoofFacingDirection();
        boolean isLeftOrRightView = currentView == CurrentView.LEFT || currentView == CurrentView.RIGHT;
        boolean roofDirectionIsLeftOrRight = roofDirection == Direction.LEFT || roofDirection == Direction.RIGHT;

        float slot_d = inchToPixel(controller.getExtraSlotDistance()/2);

        Rectangle2D.Double wall;
        wall = new Rectangle2D.Double(((panelWidth - wallWidth) / 2) + slot_d, (panelHeight - wallHeight) / 2, wallWidth - 2*slot_d, wallHeight);

        //System.out.println(wall.getX());
        //System.out.println(wall.getY());

        //System.out.println(wall.getWidth());;
        //System.out.println(wall.getHeight());;
        ;


        g2.fill(wall);

        g2.setColor(Color.WHITE);
        if(!currentWall.getAccessories().isEmpty()) {
            for(Accessory accessory : currentWall.getAccessories()) {

                float accHeight = inchToPixel(accessory.getHeight());
                float accWidth = inchToPixel(accessory.getWidth());

                float accX = inchToPixel((float)accessory.transform.position.x);
                float accY = inchToPixel((float)accessory.transform.position.y);

                float floor = inchToPixel(currentWall.getHeight()) + currentWall.getY() - inchToPixel(currentWall.getDistanceAccessoryPadding());
                float door_y = currentWall.getY() + accY;
                boolean onFloor = door_y + accHeight - floor == 0;

                Color gold = new Color(255, 224, 14);
                Color bleu = new Color(84, 201, 243);

                if(accessory.getElementType() == Door.class) {

                    if(accessory.getElementType() == Door.class && !onFloor || !currentWall.hasNoCollision(accessory)) {
                        g2.setColor(Color.RED);
                        Rectangle2D.Double door = new Rectangle2D.Double((panelWidth - wallWidth + 2*accX) / 2, (panelHeight - wallHeight + 2*accY) / 2, accWidth, accHeight);
                        g2.fill(door);
                    } else {
                        g2.setColor(beauBrun);
                        Rectangle2D.Double door = new Rectangle2D.Double((panelWidth - wallWidth + 2*accX) / 2, (panelHeight - wallHeight + 2*accY) / 2, accWidth, accHeight);
                        g2.fill(door);

                        g2.setColor(gold);
                        Ellipse2D.Double handle = new Ellipse2D.Double(((panelWidth - wallWidth + 2*accX) / 2) + (8*accWidth)/10, ((panelHeight - wallHeight + 2*accY) / 2) + accHeight/2, accWidth/10,     accWidth/10);
                        g2.fill(handle);
                    }

                } else {

                    if(!currentWall.hasNoCollision(accessory)) {
                        g2.setColor(Color.RED);
                        Rectangle2D.Double window = new Rectangle2D.Double((panelWidth - wallWidth + 2*accX) / 2, (panelHeight - wallHeight + 2*accY) / 2, accWidth, accHeight);
                        g2.fill(window);
                    } else {
                        g2.setColor(bleu);
                        Rectangle2D.Double window = new Rectangle2D.Double((panelWidth - wallWidth + 2*accX) / 2, (panelHeight - wallHeight + 2*accY) / 2, accWidth, accHeight);
                        g2.fill(window);

                        g2.setColor(beauBrun);
                        float win_t = Math.min(accWidth, accHeight)/10;
                        g2.setColor(beauBrun);
                        Rectangle2D.Double top = new Rectangle2D.Double((panelWidth - wallWidth + 2*accX) / 2, (panelHeight - wallHeight + 2*accY) / 2, accWidth, win_t);
                        Rectangle2D.Double bottom = new Rectangle2D.Double((panelWidth - wallWidth + 2*accX) / 2, ((panelHeight - wallHeight + 2*accY) / 2) + accHeight - win_t, accWidth, win_t);
                        Rectangle2D.Double right = new Rectangle2D.Double((panelWidth - wallWidth + 2*accX) / 2, (panelHeight - wallHeight + 2*accY) / 2, win_t, accHeight);
                        Rectangle2D.Double left = new Rectangle2D.Double(((panelWidth - wallWidth + 2*accX) / 2) + accWidth - win_t, (panelHeight - wallHeight + 2*accY) / 2, win_t, accHeight);
                        Rectangle2D.Double h = new Rectangle2D.Double((panelWidth - wallWidth + 2*accX) / 2, ((panelHeight - wallHeight + 2*accY) / 2) + accHeight/2 - win_t/2, accWidth, win_t);
                        Rectangle2D.Double v = new Rectangle2D.Double(((panelWidth - wallWidth + 2*accX) / 2) + accWidth/2 - win_t/2, (panelHeight - wallHeight + 2*accY) / 2, win_t, accHeight);
                        g2.fill(top);
                        g2.fill(bottom);
                        g2.fill(right);
                        g2.fill(left);
                        g2.fill(h);
                        g2.fill(v);
                    }
                }

            }
        }

        if(currentWall.getIsolated()) {
            g2.setTransform(oldTransform);
            return;
        }

        float wallThickness = controller.getExteriorPanelThickness();
        switch(currentView) {
            case LEFT:
            case RIGHT:
                if(roofDirection == Direction.FRONT || roofDirection == Direction.BACK) {
                    float sideWallHeight = wallHeight;
                    float sideWallWidth = inchToPixel((wallThickness/2));

                    g2.setColor(brunFonce);
                    Rectangle2D.Double sideWall1 = new Rectangle2D.Double((panelWidth - wallWidth - 2*sideWallWidth) / 2, (panelHeight - wallHeight) / 2, sideWallWidth - slot_d, sideWallHeight);
                    Rectangle2D.Double sideWall2 = new Rectangle2D.Double(((panelWidth + wallWidth) / 2) + slot_d, (panelHeight - wallHeight) / 2, sideWallWidth - slot_d, sideWallHeight);
                    g2.fill(sideWall1);
                    g2.fill(sideWall2);
                }
                break;
            case FRONT:
            case BACK:
                if(roofDirection == Direction.LEFT || roofDirection == Direction.RIGHT) {
                    float sideWallHeight = wallHeight;
                    float sideWallWidth = inchToPixel((wallThickness/2));

                    g2.setColor(brunPale);
                    Rectangle2D.Double sideWall1 = new Rectangle2D.Double((panelWidth - wallWidth - 2*sideWallWidth) / 2, (panelHeight - wallHeight) / 2, sideWallWidth - slot_d, sideWallHeight);
                    Rectangle2D.Double sideWall2 = new Rectangle2D.Double(((panelWidth + wallWidth) / 2) + slot_d, (panelHeight - wallHeight) / 2, sideWallWidth - slot_d, sideWallHeight);
                    g2.fill(sideWall1);
                    g2.fill(sideWall2);
                }
                break;
        }

        Color beauBrunPlusPale = new Color(225, 140, 50);
        g2.setColor(beauBrunPlusPale);
        float roofAngle = this.controller.getRoofInclinaisonAngle();
        float sideWallsWidthInPixel = inchToPixel(this.controller.getSideWallsWidth());
        float frontAndBackWallsWidthInPixel = inchToPixel(this.controller.getFrontAndBackWallsWidth());
        double[] coords_x;
        double[] coords_y;

        double o_x = (panelWidth - wallWidth) / 2;
        double o_y = (panelHeight - wallHeight) / 2;

        if(isLeftOrRightView && !roofDirectionIsLeftOrRight) {

            double side_triangle_height = sideWallsWidthInPixel * Math.tan(Math.toRadians(roofAngle));
            //side_triangle_height -= 2*wallThickness;

            if((currentView == CurrentView.LEFT && roofDirection == Direction.FRONT) || (currentView == CurrentView.RIGHT && roofDirection == Direction.BACK)) {
                coords_x = new double[]{o_x + sideWallsWidthInPixel - 2*slot_d, o_x + slot_d, o_x + slot_d};
                coords_y = new double[]{o_y, o_y, o_y - side_triangle_height + 2*slot_d};
            } else {
                coords_x = new double[]{o_x + 2*slot_d, o_x + sideWallsWidthInPixel - slot_d, o_x + sideWallsWidthInPixel - slot_d};
                coords_y = new double[]{o_y, o_y, o_y - side_triangle_height + 2*slot_d};
            }

            drawTriangle(coords_x, coords_y, g2);

        } else if(!isLeftOrRightView && roofDirectionIsLeftOrRight) {
            double frontBackTriangleHeight = frontAndBackWallsWidthInPixel * Math.tan(Math.toRadians(roofAngle));

            if((currentView == CurrentView.FRONT && roofDirection == Direction.RIGHT) || (currentView == CurrentView.BACK && roofDirection == Direction.LEFT)) {
                coords_x = new double[]{o_x + frontAndBackWallsWidthInPixel - 2*slot_d, o_x + slot_d, o_x + slot_d};
                coords_y = new double[]{o_y, o_y, o_y - frontBackTriangleHeight + 2*slot_d};
            } else {
                coords_x = new double[]{o_x + 2*slot_d, o_x + frontAndBackWallsWidthInPixel - slot_d, o_x + frontAndBackWallsWidthInPixel - slot_d};
                coords_y = new double[]{o_y, o_y, o_y - frontBackTriangleHeight + 2*slot_d};
            }

            drawTriangle(coords_x, coords_y, g2);
        }

        g2.setColor(Color.GRAY);
        double extension_height;
        if(roofDirectionIsLeftOrRight) {
            extension_height = frontAndBackWallsWidthInPixel * Math.tan(Math.toRadians(roofAngle));
            //extension_height -= 2*wallThickness;
        } else {
            extension_height = sideWallsWidthInPixel * Math.tan(Math.toRadians(roofAngle));
            //extension_height -= 2*wallThickness;
        }

        Color beauBrunPale = new Color(202, 113, 25);
        g2.setColor(beauBrunPale);
        switch(currentView) {
            case LEFT:
                drawExtension(o_x, o_y, extension_height, CurrentView.LEFT, g2, slot_d);
                break;
            case RIGHT:
                drawExtension(o_x, o_y, extension_height, CurrentView.RIGHT, g2, slot_d);
                break;
            case FRONT:
                drawExtension(o_x, o_y, extension_height, CurrentView.FRONT, g2, slot_d);
                break;
            case BACK:
                drawExtension(o_x, o_y, extension_height, CurrentView.BACK, g2, slot_d);
                break;
        }

        g2.setColor(beauBrun);
        switch(currentView) {
            case LEFT:
                drawRoof(o_x, o_y, extension_height, CurrentView.LEFT, g2, slot_d);
                break;
            case RIGHT:
                drawRoof(o_x, o_y, extension_height, CurrentView.RIGHT, g2, slot_d);
                break;
            case FRONT:
                drawRoof(o_x, o_y, extension_height, CurrentView.FRONT, g2, slot_d);
                break;
            case BACK:
                drawRoof(o_x, o_y, extension_height, CurrentView.BACK, g2, slot_d);
                break;
        }

        g2.setTransform(oldTransform);
    }

    public void drawTriangle(double[] c_x, double[] c_y, Graphics2D g2) {
        GeneralPath gable_triangle = new GeneralPath();
        gable_triangle.moveTo(c_x[0], c_y[0]);
        for (int i = 1; i < c_x.length; i++) {
            gable_triangle.lineTo(c_x[i], c_y[i]);
        }
        gable_triangle.closePath();
        g2.fill(gable_triangle);
    }

    public void drawExtension(double o_x, double o_y, double extension_height, CurrentView view, Graphics2D g2, double slot_d) {
        double wallThickness = this.controller.getExteriorPanelThickness();
        double roofAngle = this.controller.getRoofInclinaisonAngle();
        Direction roofDirection = this.controller.getRoofFacingDirection();
        double frontAndBackWallsWidthInPixel = inchToPixel(this.controller.getFrontAndBackWallsWidth());
        double sideWallsWidthInPixel = inchToPixel(this.controller.getSideWallsWidth());
        double w;

        Direction dir1;
        Direction dir2;
        Direction dir3;

        if(view == CurrentView.LEFT) {
            w = sideWallsWidthInPixel;
            dir1 = Direction.RIGHT;
            dir2 = Direction.FRONT;
            dir3 = Direction.BACK;
        } else if(view == CurrentView.RIGHT) {
            w = sideWallsWidthInPixel;
            dir1 = Direction.LEFT;
            dir2 = Direction.BACK;
            dir3 = Direction.FRONT;
        } else if(view == CurrentView.FRONT) {
            w = frontAndBackWallsWidthInPixel;
            dir1 = Direction.BACK;
            dir2 = Direction.RIGHT;
            dir3 = Direction.LEFT;
        } else {
            w = frontAndBackWallsWidthInPixel;
            dir1 = Direction.FRONT;
            dir2 = Direction.LEFT;
            dir3 = Direction.RIGHT;
        }

        double extension_triangle_height = 2*wallThickness * Math.tan(Math.toRadians(roofAngle));

        if(roofDirection == dir1) {
            Rectangle2D.Double extension = new Rectangle2D.Double(o_x, o_y - extension_height, w, extension_height + extension_triangle_height);
            g2.fill(extension);
        } else if (roofDirection == dir2) {
            Rectangle2D.Double side_extension = new Rectangle2D.Double(o_x - 2*wallThickness, o_y - extension_height, 2*wallThickness - slot_d, extension_height);
            g2.fill(side_extension);
            double[] c_x = {o_x - slot_d, o_x - 2*wallThickness, o_x - 2*wallThickness};
            double[] c_y = {o_y - extension_height, o_y - extension_height, o_y - extension_height - extension_triangle_height};
            drawTriangle(c_x, c_y, g2);
        } else if (roofDirection == dir3) {
            Rectangle2D.Double side_extension = new Rectangle2D.Double(o_x + w, o_y - extension_height, 2*wallThickness, extension_height);
            g2.fill(side_extension);

            double[] c_x = {o_x + w, o_x + w + 2*wallThickness, o_x + w + 2*wallThickness};
            double[] c_y = {o_y - extension_height, o_y - extension_height, o_y - extension_height - extension_triangle_height};
            //drawTriangle(c_x, c_y, g2);
        }
    }

    public void drawRoof(double o_x, double o_y, double extension_height, CurrentView view, Graphics2D g2, double slot_d) {
        Direction roofDirection = controller.getRoofFacingDirection();
        float wallThickness = controller.getExteriorPanelThickness();
        float roofAngle = controller.getRoofInclinaisonAngle();

        float wallWidth;
        if(view == CurrentView.LEFT || view == CurrentView.RIGHT) {
            wallWidth = inchToPixel(controller.getSideWallsWidth());
        } else {
            wallWidth = inchToPixel(controller.getFrontAndBackWallsWidth());
        }

        Direction dir1;
        Direction dir2;
        Direction dir3;
        Direction dir4;
        if(view == CurrentView.LEFT) {
            dir1 = Direction.FRONT;
            dir2 = Direction.RIGHT;
            dir3 = Direction.LEFT;
            dir4 = Direction.BACK;
        } else if(view == CurrentView.RIGHT) {
            dir1 = Direction.BACK;
            dir2 = Direction.LEFT;
            dir3 = Direction.RIGHT;
            dir4 = Direction.FRONT;
        } else if(view == CurrentView.FRONT) {
            dir1 = Direction.RIGHT;
            dir2 = Direction.BACK;
            dir3 = Direction.FRONT;
            dir4 = Direction.LEFT;
        } else {
            dir1 = Direction.LEFT;
            dir2 = Direction.FRONT;
            dir3 = Direction.BACK;
            dir4 = Direction.RIGHT;
        }

        double extension_triangle_height = 2*wallThickness * Math.tan(Math.toRadians(roofAngle));
        if(roofDirection == dir1) {
            double[] xPoints = {o_x - 2*wallThickness, o_x - 2*wallThickness, o_x + wallWidth, o_x + wallWidth};
            double[] yPoints = {o_y - extension_height - 2*wallThickness, o_y - extension_height, o_y, o_y - 2*wallThickness};
            Path2D.Double roof = drawPolygon(xPoints, yPoints);

            double y = 2*wallThickness - extension_triangle_height;
            double[] xPoints2 = {o_x + wallWidth, o_x + wallWidth, o_x + wallWidth + 2*wallThickness, o_x + wallWidth + 2*wallThickness};
            double[] yPoints2 = {o_y, o_y - 2*wallThickness, o_y - y, o_y};
            Path2D.Double roof_ext = drawPolygon(xPoints2, yPoints2);

            g2.fill(roof);
            g2.fill(roof_ext);
        } else if (roofDirection == dir2) {
            Rectangle2D.Double roof = new Rectangle2D.Double(o_x, o_y - extension_height - 2*wallThickness, wallWidth, 2*wallThickness);
            g2.fill(roof);
        } else if (roofDirection == dir3) {
            Rectangle2D.Double roof = new Rectangle2D.Double(o_x, o_y - extension_height - 2*wallThickness, wallWidth, extension_height + 2*wallThickness);
            g2.fill(roof);
        } else if (roofDirection == dir4) {
            double[] xPoints = {o_x + wallWidth + 2*wallThickness, o_x + wallWidth + 2*wallThickness, o_x, o_x};
            double[] yPoints = {o_y - extension_height - 2*wallThickness, o_y - extension_height, o_y, o_y - 2*wallThickness};
            Path2D.Double roof = drawPolygon(xPoints, yPoints);

            double y = 2*wallThickness - extension_triangle_height;
            double[] xPoints2 = {o_x, o_x, o_x - 2*wallThickness, o_x - 2*wallThickness};
            double[] yPoints2 = {o_y, o_y - 2*wallThickness, o_y - y, o_y};
            Path2D.Double roof_ext = drawPolygon(xPoints2, yPoints2);

            g2.fill(roof);
            g2.fill(roof_ext);
        }
    }

    public Path2D.Double drawPolygon(double[] xPoints, double[] yPoints) {
        Path2D.Double path = new Path2D.Double();
        path.moveTo(xPoints[0], yPoints[0]);
        path.lineTo(xPoints[1], yPoints[1]);
        path.lineTo(xPoints[2], yPoints[2]);
        path.lineTo(xPoints[3], yPoints[3]);
        path.closePath();

        return path;
    }

    public void setShack(Shack shack) {
        this.shack = shack;
    }
}
