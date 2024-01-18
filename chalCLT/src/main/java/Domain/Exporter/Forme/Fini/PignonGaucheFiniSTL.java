package Domain.Exporter.Forme.Fini;


import Domain.Enum.Direction;
import Domain.Exporter.Forme.Forme;

import static Domain.Utility.MatrixRotation.getMatrixRotation;
import static java.lang.Math.tan;
import static java.lang.Math.toRadians;


public class PignonGaucheFiniSTL extends Forme {
    public PignonGaucheFiniSTL(double x, double y, double z, double width, double thickness,Direction direction, double theta, double alpha, double beta, double gamma){
        this.x = x;
        this.y = y;
        this.z = z;
        this.width = width;
        this.thickness = thickness;
        this.theta = toRadians(theta);
        this.alpha = toRadians(alpha);
        this.beta = toRadians(beta);
        this.gamma = toRadians(gamma);
        this.direction = direction;
    }


    public String writer() {
        this.z-=thickness/2;
        double grandeHeight = tan(theta)*width+thickness/2-tan(theta)*thickness;
        double petiteHeight = tan(theta)*(width-thickness)+thickness/2-tan(theta)*thickness;
        double[][] vecteur= {
                //X des petits carrés(0 à 7)
                {0,0,0,0,-thickness/2,-thickness/2,-thickness/2,-thickness/2,
                        //X des petits renctangles (8 à 15)
                        0-width,0-width,0-width,0-width,thickness/2-width,thickness/2-width,-width+thickness/2,-width+thickness/2,
                        //X des rectangles diagonals(16 à 19)
                        -width,-width,-width+thickness/2,-width+thickness/2
                        //X des grands triangles(Point Déjà attribués)
                        //X des grands rectangles verticales(Déjà attribué)
                        //X des grands rectangles dessous(Déjà attribué)



                },
                //Y des petits carrés(0 à 7)
                {0,0,thickness/2,thickness/2,thickness/2,thickness/2,thickness,thickness,
                        //Y des petits renctangles (8 à 15)
                        0,0,thickness/2,thickness/2,thickness/2,thickness/2,thickness,thickness,
                        //Y des rectangles diagonals(16 à 19)
                        0,thickness/2,thickness/2,thickness
                        //Y des grands triangles(Point Déjà attribués)
                        //Y des grands rectangles verticales(Déjà attribué)
                        //Y des grands rectangles dessous(Déjà attribué)
                },
                //Z des petits carrés(0 à 7)
                {0,thickness/2,thickness/2,0,0,thickness/2,thickness/2,0,
                        //Z des petits rectangels (8 à 15)
                        0,thickness/2,thickness/2,0,0,thickness/2,thickness/2,0,
                        //Z des rectangles diagonals(16 à 19)
                        grandeHeight,grandeHeight,petiteHeight,petiteHeight
                        //Z des grands triangles(Point Déjà attribués)
                        //Z des grands rectangles verticales(Déjà attribué)
                        //Z des grands rectangles dessous(Déjà attribué)
                }
        };


        //Transformation sur la piece pignon gauche pour faire parti du chalet
        vecteur = multiplyMatrices(getMatrixRotation(alpha,beta,gamma),vecteur,3,3,20);

        for (int i =0; i < vecteur[0].length; i++){
            vecteur[0][i]+=x;
            vecteur[1][i]+=y;
            vecteur[2][i]+=z;
        }

        //Transformation sur tout le chalet
        //vecteur = getVecteurDirection(vecteur,direction,20);

        String monSTL = "";

        //Petit carrés
        /*monSTL+=trianglePoint(0,1,2,vecteur);
        monSTL+=trianglePoint(0,2,3,vecteur);
        monSTL+=trianglePoint(4,5,6,vecteur);
        monSTL+=trianglePoint(4,6,7,vecteur);

        //Petits rectangles
        monSTL+=trianglePoint(0,9,1,vecteur);
        monSTL+=trianglePoint(0,8,9,vecteur);
        monSTL+=trianglePoint(3,10,2,vecteur);
        monSTL+=trianglePoint(3,11,10,vecteur);
        monSTL+=trianglePoint(7,14,6,vecteur);
        monSTL+=trianglePoint(7,15,14,vecteur);*/

        //Rectangles diagonals
        monSTL+=trianglePoint(1,2,16,vecteur);
        monSTL+=trianglePoint(2,17,16,vecteur);
        monSTL+=trianglePoint(5,18,6,vecteur);
        monSTL+=trianglePoint(6,18,19,vecteur);

        //Grands triangles
        monSTL+=trianglePoint(1,9,16,vecteur);
        monSTL+=trianglePoint(2,10,17,vecteur);
        monSTL+=trianglePoint(6,14,19,vecteur);

        //Grands rectangles verticales
        /*monSTL+=trianglePoint(8,11,16,vecteur);
        monSTL+=trianglePoint(11,17,16,vecteur);
        monSTL+=trianglePoint(12,15,18,vecteur);
        monSTL+=trianglePoint(15,19,18,vecteur);*/
        monSTL+=trianglePoint(9,10,16,vecteur);
        monSTL+=trianglePoint(10,17,16,vecteur);
        monSTL+=trianglePoint(13,14,18,vecteur);
        monSTL+=trianglePoint(14,19,18,vecteur);

        //rectangle dessous
        /*monSTL+=trianglePoint(0,3,8,vecteur);
        monSTL+=trianglePoint(3,8,11,vecteur);
        monSTL+=trianglePoint(4,7,12,vecteur);
        monSTL+=trianglePoint(7,12,15,vecteur);*/
        monSTL+=trianglePoint(1,2,9,vecteur);
        monSTL+=trianglePoint(2,10,9,vecteur);
        monSTL+=trianglePoint(5,6,13,vecteur);
        monSTL+=trianglePoint(6,13,14,vecteur);





        return  monSTL;
    }
}
