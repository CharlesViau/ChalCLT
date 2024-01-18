package Domain.Exporter.Forme.Brut;


import Domain.Enum.Direction;
import Domain.Exporter.Forme.Forme;

import static Domain.Utility.MatrixRotation.getMatrixRotation;
import static java.lang.Math.tan;
import static java.lang.Math.toRadians;


public class PignonBrutSTL extends Forme {
    public PignonBrutSTL(double x, double y, double z, double width, double thickness, Direction direction, double theta, double alpha, double beta, double gamma){
        this.x = x;
        this.y = y;
        this.z = z - thickness/2;
        this.width = width;
        this.thickness = thickness;
        this.slotDistance = slotDistance;
        this.theta = toRadians(theta);
        this.alpha = toRadians(alpha);
        this.beta = toRadians(beta);
        this.gamma = toRadians(gamma);
        this.direction = direction;
        this.slotDistance = slotDistance;
    }


    public String writer() {

        double[][] vecteur= {
                //X du petit carré(0 à 3)
                {0,0,0,0,
                        //X des petits renctangles (4 à 7)
                        0-width,0-width,0-width,0-width,
                        //X des rectangles diagonals(8 à 9)
                        -width,-width
                        //X des grands triangles(Point Déjà attribués)
                        //X des grands rectangles verticales(Déjà attribué)
                        //X des grands rectangles dessous(Déjà attribué)



                },
                //Y du petit carré(0 à 3)
                {0,0,thickness,thickness,
                        //Y des petits renctangles (4 à 7)
                        0,0,thickness,thickness,
                        //Y des rectangles diagonals(8 à 9)
                        0,thickness
                        //Y des grands triangles(Point Déjà attribués)
                        //Y des grands rectangles verticales(Déjà attribué)
                        //Y des grands rectangles dessous(Déjà attribué)
                },
                //Z du petit carré(0 à 3)
                {0,thickness/2,thickness/2,0,
                        //Z des petits rectangels (4 à 7)
                        0,thickness/2,thickness/2,0,
                        //Z des rectangles diagonals(8 à 9)
                        tan(theta)*width+thickness/2,tan(theta)*width+thickness/2
                        //Z des grands triangles(Point Déjà attribués)
                        //Z des grands rectangles verticales(Déjà attribué)
                        //Z des grands rectangles dessous(Déjà attribué)
                }
        };


        //Transformation sur la piece pignon gauche pour faire parti du chalet
        vecteur = multiplyMatrices(getMatrixRotation(alpha,beta,gamma),vecteur,3,3,vecteur[0].length);

        for (int i =0; i < vecteur[0].length; i++){
            vecteur[0][i]+=x;
            vecteur[1][i]+=y;
            vecteur[2][i]+=z;
        }

        //Transformation sur tout le chalet
        vecteur = getVecteurDirection(vecteur,direction,vecteur[0].length);

        String monSTL = "";

        //Petit carrés Devant
        //monSTL+=trianglePoint(0,1,2,vecteur);
        //monSTL+=trianglePoint(0,2,3,vecteur);
        //monSTL+=trianglePoint(4,5,6,vecteur);
        //monSTL+=trianglePoint(4,6,7,vecteur);

        //Petits rectangles
        //monSTL+=trianglePoint(0,1,5,vecteur);
        //monSTL+=trianglePoint(0,5,4,vecteur);
        //monSTL+=trianglePoint(3,2,6,vecteur);
        //monSTL+=trianglePoint(3,6,7,vecteur);

        //Rectangles diagonals
        monSTL+=trianglePoint(1,2,8,vecteur);
        monSTL+=trianglePoint(2,9,8,vecteur);

        //Grands triangles
        monSTL+=trianglePoint(1,8,5,vecteur);
        monSTL+=trianglePoint(2,9,6,vecteur);

        //Grands rectangles verticales
        monSTL+=trianglePoint(5,8,9,vecteur);
        monSTL+=trianglePoint(5,9,6,vecteur);

        //rectangle dessous
        //monSTL+=trianglePoint(0,3,7,vecteur);
        //monSTL+=trianglePoint(0,7,4,vecteur);
        monSTL+=trianglePoint(1,2,6,vecteur);
        monSTL+=trianglePoint(1,5,6,vecteur);







        return  monSTL;
    }
}
