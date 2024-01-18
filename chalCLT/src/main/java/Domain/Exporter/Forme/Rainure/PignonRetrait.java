package Domain.Exporter.Forme.Rainure;

import Domain.Enum.Direction;
import Domain.Exporter.Forme.Forme;

import java.io.IOException;

import static Domain.Utility.MatrixRotation.getMatrixRotation;
import static java.lang.Math.*;

public class PignonRetrait extends Forme {
    public PignonRetrait(double x, double y, double z, double width, double thickness, Direction direction, double theta, double alpha, double beta, double gamma){
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
        this.slotDistance = slotDistance;
    }


    public String writer() {

        double[][] vecteur = new double[3][12];

        for (int i = 0; i <= 11; i++){
            switch (i%6){
                case 0:
                    vecteur[0][i] = 0;
                    vecteur[2][i] = 0;
                    break;
                case 1:
                    vecteur[0][i] = -thickness/2;
                    vecteur[2][i] = 0;
                    break;
                case 2:
                    vecteur[0][i] = -width + thickness/2;
                    vecteur[2][i] = tan(theta)*(width-thickness);
                    break;
                case 3:
                    vecteur[0][i] = -width;
                    vecteur[2][i] = tan(theta) * width;
                    break;
                case 4:
                    vecteur[0][i] = -width + thickness/2;
                    vecteur[2][i] = 0;
                    break;
                case 5:
                    vecteur[0][i] = -width;
                    vecteur[2][i] = 0;
                    break;
            }

            if ((i/6)<1){
                vecteur[1][i] = thickness/2;
            }else{
                vecteur[1][i] = thickness;
            }


        }


        //Transformation sur la piece pignon gauche pour faire parti du chalet
        vecteur = multiplyMatrices(getMatrixRotation(alpha, beta, gamma), vecteur, 3, 3, vecteur[0].length);

        for (int i = 0; i < vecteur[0].length; i++) {
            vecteur[0][i] += x;
            vecteur[1][i] += y;
            vecteur[2][i] += z;
        }

        //Transformation sur tout le chalet
        vecteur = getVecteurDirection(vecteur, direction, vecteur[0].length);

        String monSTL = "";

        //Interieur Diagonal Gauche
        monSTL+=trianglePoint(0,1,3,vecteur);
        monSTL+=trianglePoint(1,2,3,vecteur);

        //Interieur Vertical Gauche
        monSTL+=trianglePoint(4,5,2,vecteur);
        monSTL+=trianglePoint(5,3,2,vecteur);

        //Interieur Diagonal Droite
        monSTL+=trianglePoint(6,7,9,vecteur);
        monSTL+=trianglePoint(7,8,9,vecteur);

        //Interieur Verticale Droite
        monSTL+=trianglePoint(10,8,9,vecteur);
        monSTL+=trianglePoint(10,9,11,vecteur);

        //Dessous pente
        monSTL+=trianglePoint(1,2,8,vecteur);
        monSTL+=trianglePoint(1,7,8,vecteur);

        //Dessous base petit debut
        monSTL+=trianglePoint(0,1,7,vecteur);
        monSTL+=trianglePoint(0,7,6,vecteur);

        //Dessus pente
        monSTL+=trianglePoint(0,6,3,vecteur);
        monSTL+=trianglePoint(6,9,3,vecteur);

        //Derriere Vertical
        monSTL+=trianglePoint(5,11,9,vecteur);
        monSTL+=trianglePoint(5,9,3,vecteur);

        //Devant Vertical
        monSTL+=trianglePoint(4,10,8,vecteur);
        monSTL+=trianglePoint(4,8,2,vecteur);

        //Petit carré dessous
        monSTL+=trianglePoint(4,5,11,vecteur);
        monSTL+=trianglePoint(4,11,10,vecteur);


        return monSTL;
    }
}
