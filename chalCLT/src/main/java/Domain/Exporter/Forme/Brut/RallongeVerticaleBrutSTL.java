package Domain.Exporter.Forme.Brut;


import Domain.Enum.Direction;
import Domain.Exporter.Forme.Forme;

import static Domain.Utility.MatrixRotation.getMatrixRotation;
import static java.lang.Math.tan;
import static java.lang.Math.toRadians;

public class RallongeVerticaleBrutSTL extends Forme {
    public RallongeVerticaleBrutSTL(double x, double y, double z, double width, double height, double thickness, Direction direction, double theta, double alpha, double beta, double gamma){
        this.x = x;
        this.y = y;
        this.z = z;
        this.width = width;
        this.height = height;
        this.thickness = thickness;
        this.theta = toRadians(theta);
        this.alpha = toRadians(alpha);
        this.beta = toRadians(beta);
        this.gamma = toRadians(gamma);
        this.direction = direction;
    }

    @Override
    public String writer() {
        double petiteHeight = height-tan(theta)*thickness;
        double[][] vecteur= {
                //X du trapèzes gauche(0 à 4)
                {0,0,0,0, 0,
                        //X du trapèze droite(5 à 9)
                        -width,-width,-width,-width,-width
                        //Les X du rectangle facade sont déjà là
                        //Les X du rectangle allongé sont déjà là
                        //Les X du triangle coté sont déjà là
                        //Les X du triangle dessous sont déjà là
                },
                //Y du trapèzes gauches(0 à 4)
                {0,thickness,0,0,thickness,
                        //Y du trapèze droite(5 à 9)
                       0,thickness,0,0,thickness
                        //Les y du rectangle facade sont déjà là
                        //Les Y du rectangle allongé sont déjà là
                        //Les Y du triangle coté sont déjà là
                        //Les Y du triangle dessous sont déjà là
                },
                //Z du trapèzes gauche(0 à 4)
                {0,0,height,petiteHeight,height,//petiteHeight,
                        //Z du trapèze droite(5 à 9)
                        0,0,height,petiteHeight,height//petiteHeight
                        //Les Y du rectangle facade sont déjà là
                        //Les Y du rectangle allongé sont déjà là
                        //Les Y du triangle coté sont déjà là
                        //Les Y du triangle dessous sont déjà là
                }
        };



        //Transformation sur la piece Rallonge verticale pour faire parti du chalet
        vecteur = multiplyMatrices(getMatrixRotation(alpha,beta,gamma),vecteur,3,3,vecteur[0].length);

        for (int i =0; i < vecteur[0].length; i++){
            vecteur[0][i]+=x;
            vecteur[1][i]+=y;
            vecteur[2][i]+=z;
        }

        //Transformation sur tout le chalet
        vecteur = getVecteurDirection(vecteur,direction,vecteur[0].length);

        String monSTL = "";

        //rectangle gauche
        monSTL+=trianglePoint(0,3,1,vecteur);
        monSTL+=trianglePoint(1,3,4,vecteur);

        //rectangle droite
        monSTL+=trianglePoint(5,6,8,vecteur);
        monSTL+=trianglePoint(6,9,8,vecteur);

        //le rectangle façade
        monSTL+=trianglePoint(1,4,9,vecteur);
        monSTL+=trianglePoint(1,9,6,vecteur);


        //le rectangle de derrière
        monSTL+=trianglePoint(0,2,7,vecteur);
        monSTL+=trianglePoint(0,7,5,vecteur);

        //le rectangle allongé
        monSTL+=trianglePoint(4,2,7,vecteur);
        monSTL+=trianglePoint(4,7,9,vecteur);


        //les 2 triangles cotés
        monSTL+=trianglePoint(2,4,3,vecteur);
        monSTL+=trianglePoint(7,9,8,vecteur);

        //les 2 triangles Desous
        monSTL+=trianglePoint(0,1,6,vecteur);
        monSTL+=trianglePoint(0,5,6,vecteur);
    return monSTL;
    }
}
