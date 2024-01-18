package View.ViewUtility;

import Domain.Utility.Vector2;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Vector;

public class AdapterInchPixel {
    private final float _PixelPerInch = 4;

    public AdapterInchPixel() {

    }

    public Vector2 pixelToInch(Vector2 pixel, float zoomFactor, Vector2 CenterGap) {
//        pixel = EnleverDecalages(pixel, zoomFactor, DiffMilieu, CenterGap);
     //   Vector2 v = new Vector2(((pixel.getX()) / zoomFactor) / _PixelPerInch, ((pixel.getY()) / zoomFactor) / _PixelPerInch);
    //    v.setX((v.getX() - DiffMilieu.getX()) + CenterGap.getX());
     //   v.setY((v.getY() - DiffMilieu.getY()) + CenterGap.getY());

        //vérifier si zoomFactor est pertinent ici
        //v.setX(v.getX()-((CenterGap.getX()/zoomFactor)/_PixelPerInch));
        //v.setY(v.getY()-((CenterGap.getY()/zoomFactor)/_PixelPerInch));
        //    System.out.println("clic x apres ad: " + v.getX());
        // System.out.println("clic y  apres ad: " + v.getY());
        //    return v;
        //return new Vector2(pixel.getX()/_PixelPerInch,pixel.getY()/_PixelPerInch);
        return new Vector2(pixel.getX()/_PixelPerInch, pixel.getY()/_PixelPerInch);
    }
}
/*
    public Vector2 EnleverDecalages( Vector2 clic, float zoomFactor, Vector2 DiffMilieu, Vector2 CenterGap){
        BufferedImage image = new BufferedImage((int) clic.getX(), (int) clic.getY(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics2D = image.createGraphics();
        Point2D point = new Point2D.Float(clic.getX(), clic.getY());
        Point2D pointResultant = new Point2D.Float();

        AffineTransform newTransform = new AffineTransform();
        newTransform.translate(CenterGap.getX(), CenterGap.getY());
        newTransform.scale(zoomFactor, zoomFactor);
        newTransform.translate(-CenterGap.getX(), -CenterGap.getY());

        newTransform.transform(point, pointResultant);

        return new Vector2((float) pointResultant.getX(), (float) pointResultant.getY());
    }
    public float inchToPixel(float inch) {
        return (float) (inch * _PixelPerInch);
    }

}
*/