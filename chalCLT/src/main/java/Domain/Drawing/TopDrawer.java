package Domain.Drawing;

import Domain.Enum.Direction;
import Domain.MainController;
import Domain.Utility.Tuple;
import Domain.Utility.Vector2;
import Domain.Utility.ViewHolder;
import Domain.Utility.ZoomConverter;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;

public class TopDrawer extends Drawer {
    private AffineTransform _affineTransform;
    public TopDrawer (MainController p_controller, ViewHolder view, ZoomConverter zoom, AffineTransform affineTransform){
        super(p_controller, view,zoom);
        _affineTransform = affineTransform;
    }

    public void draw(Graphics g){

        Graphics2D graph2D = (Graphics2D) g;
        HashMap<Direction, RectangleView[]> myRect = new HashMap<Direction, RectangleView[]>();
        Tuple tup;
        double frontBackWidth =  inchToPixel(controller.getFrontAndBackWallsWidth());
        double rightLeftWidth =  inchToPixel(controller.getSideWallsWidth());
        //Changer pour inch to pixel ?
        double thickness = inchToPixel(controller.getExteriorPanelThickness());
        graph2D.setStroke(new BasicStroke(3));
        Color brunPale = new Color(236, 158, 80);
        Color brunFonce = new Color(200, 125, 50);

        //Translation
        double panelWidth = controller.getPanelSize().getX();
        double panelHeight = controller.getPanelSize().getY();
        double translationX;
        double extraslot = inchToPixel(controller.getExtraSlotDistance());
        double translationY;

        float zoom = zoomConverter.getZoomFactor();
        Vector2 gap = zoomConverter.getCenterGap();

        AffineTransform oldTransform = graph2D.getTransform();
        AffineTransform newTransform = new AffineTransform();
        Vector2 center = controller.getCenter();
        newTransform.translate(center.getX(), center.getY());
        newTransform.scale(zoom, zoom);
        newTransform.translate(-center.getX(), -center.getY());
        graph2D.transform(newTransform);

        translationX = panelWidth/2 - (frontBackWidth + 2 * extraslot)/2;
        translationY = panelHeight/2 - (rightLeftWidth + thickness + 2 * extraslot)/2;

        double x1, x2, y1, y2, width1, width2, height1, height2;

        if (controller.getRoofFacingDirection() == Direction.BACK || controller.getRoofFacingDirection() == Direction.FRONT){

            x1 = translationX;
            y1 = translationY;
            width1 = frontBackWidth;
            height1 = thickness /2 - extraslot/2;

            //En haut
            graph2D.setColor(brunFonce);
            Rectangle2D.Double haut = new Rectangle2D.Double(x1,y1,width1,height1);
            graph2D.fill(haut);

            x2 = thickness /2 + extraslot/2 + translationX;
            y2 = thickness /2 - extraslot/2+ translationY;
            width2 = frontBackWidth - thickness - extraslot;
            height2 = thickness /2 + extraslot/2;

            Rectangle2D.Double haut2 = new Rectangle2D.Double(x2,y2,width2,height2);
            graph2D.fill(haut2);

            myRect.put(Direction.BACK,
                    new RectangleView[]{
                            new RectangleView(x1/4, y1/4,
                                    width1/4, height1/4),
                            new RectangleView(x2/4, y2/4, width2/4, height2/4)});


            //À gauche

            x1 = translationX;
            y1 = thickness /2 + extraslot/2 + translationY;
            width1 = thickness /2 - extraslot/2;
            height1 = rightLeftWidth - extraslot;

            graph2D.setColor(brunPale);
            Rectangle2D.Double gauche = new Rectangle2D.Double(x1, y1,
                    width1, height1);
            graph2D.fill(gauche);

            x2 = thickness/2 - extraslot/2 + translationX;
            y2 = thickness + extraslot + translationY;
            width2 = thickness /2 + extraslot/2;
            height2 = rightLeftWidth  - thickness - 2 * extraslot;

            Rectangle2D.Double gauche2 = new Rectangle2D.Double(x2,y2,
                    width2 , height2);
            graph2D.fill(gauche2);
            myRect.put(Direction.LEFT,
                    new RectangleView[]{
                            new RectangleView(x1/4,y1/4,
                                    width1/4,height1/4),
                            new RectangleView(x2/4, y2/4,
                                    width2/4, height2/4)});



        //En bas
            x1 = thickness /2 + extraslot/2 + translationX;
            y1 = rightLeftWidth + translationY;
            width1 = frontBackWidth - thickness - extraslot;
            height1 = thickness/2 + extraslot/2;

            graph2D.setColor(brunFonce);
            Rectangle2D.Double bas = new Rectangle2D.Double( x1, y1
                    , width1 , height1);
            graph2D.fill(bas);

            x2 = translationX;
            y2 = rightLeftWidth + thickness /2 + extraslot/2 + translationY;
            width2 = frontBackWidth ;
            height2 = thickness /2- extraslot/2;

            Rectangle2D.Double bas2 = new Rectangle2D.Double(x2 , y2, width2
                    , height2);
            graph2D.fill(bas2);
            myRect.put(Direction.FRONT,
                    new RectangleView[]{
                            new RectangleView(x1/4, y1/4,
                                    width1/4,  height1/4),
                            new RectangleView(x2/4,y2/4,
                                    width2/4,  height2/4)});

            //À droite
            graph2D.setColor(brunPale);
            x1 =  frontBackWidth - thickness + translationX;
            y1 = thickness + extraslot + translationY;
            width1 = thickness/2 + extraslot/2;
            height1 = rightLeftWidth - thickness - 2*extraslot;

            Rectangle2D.Double droite = new Rectangle2D.Double( x1, y1, width1, height1);
            graph2D.fill(droite);

            x2 = frontBackWidth - thickness/2 + extraslot/2 + translationX;
            y2 = thickness/2 + extraslot/2 + translationY;
            width2 = thickness/2 - extraslot/2;
            height2 = rightLeftWidth - extraslot;
            Rectangle2D.Double droite2 = new Rectangle2D.Double(x2, y2, width2, height2);
            graph2D.fill(droite2);

            myRect.put(Direction.RIGHT, new RectangleView[]{
                    new RectangleView(x1/4, y1/4,  width1/4,
                            height1/4),
                    new RectangleView(x2/4,
                            y2/4, width2/4, height2/4)});

        }else{

            //En haut

            x1 = translationX+thickness/2+extraslot/2;
            y1 = translationY;
            width1 = frontBackWidth-extraslot;
            height1 = thickness /2-extraslot/2;

            graph2D.setColor(brunFonce);
            Rectangle2D.Double haut = new Rectangle2D.Double(x1,y1,width1
                    , height1);
            graph2D.fill(haut);

            x2 = thickness + extraslot + translationX;
            y2 = thickness/2-extraslot/2 + translationY;
            width2 = frontBackWidth - thickness -2 * extraslot;
            height2 = thickness /2 + extraslot/2;
            Rectangle2D.Double haut2 = new Rectangle2D.Double( x2,y2
                     , width2 ,  height2);
            graph2D.fill(haut2);


            myRect.put(Direction.BACK,
                    new RectangleView[]{
                            new RectangleView(x1/4, y1/4,
                                    width1/4, height1/4),
                            new RectangleView(x2/4, y2/4,
                                    width2/4, height2/4)});


            //À gauche
            graph2D.setColor(brunPale);
            x1 = translationX;
            y1 = translationY;
            width1 = thickness /2-extraslot/2;
            height1 = rightLeftWidth;

            Rectangle2D.Double gauche = new Rectangle2D.Double(x1, y1, width1, height1);
            graph2D.fill(gauche);

            x2 = thickness /2 - extraslot/2 + translationX;
            y2 = thickness/2 +extraslot/2+ translationY;
            width2 = thickness/2 + extraslot/2;
            height2 = rightLeftWidth  - thickness- extraslot;
            Rectangle2D.Double gauche2 = new Rectangle2D.Double( x2, y2, width2
                    , height2);
            graph2D.fill(gauche2);


            myRect.put(Direction.LEFT,
                    new RectangleView[]{
                            new RectangleView(x1/4,y1/4,
                                    width1/4,height1/4),
                            new RectangleView(x2/4, y2/4,
                                    width2/4, height2/4)});



            //En bas

            x1 = thickness /2 + extraslot/2+ translationX;
            y1 = rightLeftWidth + extraslot/2 -thickness/2 + translationY;
            width1 = frontBackWidth -  extraslot;
            height1 = thickness /2-extraslot/2;

            graph2D.setColor(brunFonce);
            Rectangle2D.Double bas = new Rectangle2D.Double( x1, y1
                    , width1, height1);
            graph2D.fill(bas);

            x2 = translationX+thickness+extraslot;
            y2 = rightLeftWidth-thickness+ translationY;
            width2 = frontBackWidth-thickness-2*extraslot;
            height2 = thickness/2+extraslot/2;
            Rectangle2D.Double bas2 = new Rectangle2D.Double(x2, y2, width2
                    , height2);
            graph2D.fill(bas2);
            myRect.put(Direction.FRONT,
                    new RectangleView[]{
                            new RectangleView(x1/4, y1/4,
                                    width1/4,  height1/4),
                            new RectangleView(x2/4,y2/4,
                                    width2/4,  height2/4)});

            //À droite

            x1 = frontBackWidth+translationX;
            y1 = thickness/2 + extraslot/2 + translationY;
            width1 = thickness /2+extraslot/2;
            height1 = rightLeftWidth  - thickness - extraslot;
            graph2D.setColor(brunPale);

            Rectangle2D.Double droite = new Rectangle2D.Double( x1, y1, width1
                    , height1);
            graph2D.fill(droite);

            x2 = thickness/2+frontBackWidth+extraslot/2+translationX;
            y2 = translationY;
            width2 = thickness/2-extraslot/2;
            height2 = rightLeftWidth;

            Rectangle2D.Double droite2 = new Rectangle2D.Double( x2, y2
                    , width2, height2);
            graph2D.fill(droite2);

            myRect.put(Direction.RIGHT, new RectangleView[]{
                    new RectangleView(x1/4, y1/4,  width1/4,
                            height1/4),
                    new RectangleView(x2/4,
                            y2/4, width2/4, height2/4)});


        }
        controller.setMapRectangleTopView(myRect);

        graph2D.setTransform(oldTransform);
    }
}
