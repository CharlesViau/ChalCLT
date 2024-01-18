package Domain.Exporter.Forme.Rainure;

import Domain.Enum.Direction;
import Domain.Exporter.Forme.Forme;

import static Domain.Utility.MatrixRotation.getMatrixRotation;
import static java.lang.Math.tan;
import static java.lang.Math.toRadians;

public class RetraitRallongeAvant extends Forme {
    public RetraitRallongeAvant(double x, double y, double z, double width, double height, double thickness, Direction direction, double theta, double alpha, double beta, double gamma){
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
        double petiteHeight = height - tan(theta) * thickness / 2;
        double moyenneHeight = height - tan(theta) * thickness;
        double grandeHeight = height - 3 * tan(theta) * thickness / 2;
        double[][] vecteur = {
                //X de la facade (0 à 7)
                {0, 0, -width, -width, -thickness / 2, -thickness / 2, -width + thickness / 2, -width + thickness / 2,
                        //X interieur de la facade(8 à 13)
                        -width + thickness / 2, -width + thickness / 2,  -width, -thickness / 2, -thickness / 2, 0,
                        //X Derrière façade(14 à 19)
                        0, 0, -width, -width, -width + thickness / 2, -thickness / 2,
                        //X Dessus façade(20 à 21)
                        -width+thickness / 2,-thickness / 2,
                        //X coté du diagonale prisme rectangulaire finale (22 et 23)
                        0,-width
                },
                //Y de la facade(0 à 7)
                {0, 0, 0, 0, 0, 0, 0, 0,
                        //Y interieur de la facade(8 à 13)
                        -thickness / 2, -thickness / 2, -thickness, -thickness / 2, -thickness / 2, -thickness,
                        //Y Derrière façade(14 à 19)
                        - thickness / 2, -thickness / 2, -thickness / 2, -thickness / 2, -thickness / 2, -thickness / 2,
                        //Y Dessus façade(20 à 21)
                        -thickness / 2,-thickness / 2,
                        //Y coté du diagonale prisme rectangulaire finale (22 et 23)
                        -thickness / 2,-thickness / 2
                },
                //Z de la facade(0 à 7)
                {0, height, height, 0, 0, grandeHeight, grandeHeight, 0,
                        //Z interieur de la facade(8 à 13)
                        moyenneHeight, petiteHeight, height, moyenneHeight, petiteHeight, height,
                        //Z Derrière façade(14 à 19)
                        0, height, height, 0, 0, 0,
                        //Z Dessus façade(20 à 21)
                        height,height,
                        //Z coté du diagonale prisme rectangulaire finale (22 et 23)
                        petiteHeight,petiteHeight
                }
        };


        //Transformation sur la piece Rallonge verticale pour faire parti du chalet
        vecteur = multiplyMatrices(getMatrixRotation(alpha, beta, gamma), vecteur, 3, 3, vecteur[0].length);

        for (int i = 0; i < vecteur[0].length; i++) {
            vecteur[0][i] += x;
            vecteur[1][i] += y;
            vecteur[2][i] += z;
        }

        //Transformation sur tout le chalet
        vecteur = getVecteurDirection(vecteur, direction, vecteur[0].length);

        String monSTL = "";

        //Facade
        monSTL += trianglePoint(0, 1, 4, vecteur);
        monSTL += trianglePoint(4, 1, 5, vecteur);
        monSTL += trianglePoint(3, 7, 6, vecteur);
        monSTL += trianglePoint(3, 6, 2, vecteur);
        monSTL += trianglePoint(1,  6,5, vecteur);
        monSTL += trianglePoint(1,  2,6, vecteur);

        //Interieur façade
        monSTL += trianglePoint(4, 5, 11, vecteur);
        monSTL += trianglePoint(4,  11,19, vecteur);
        monSTL += trianglePoint(18, 8, 6, vecteur);
        monSTL += trianglePoint(18,  6,7, vecteur);

        //Dessus façade
        monSTL+=trianglePoint(5,11,8,vecteur);
        monSTL+=trianglePoint(5,8,6,vecteur);

        //Rectangle verticale
        monSTL+=trianglePoint(11,12,9,vecteur);
        monSTL+=trianglePoint(11,9,8,vecteur);

        //Rectangle Diagonale
        monSTL+=trianglePoint(22,10,23,vecteur);
        monSTL+=trianglePoint(22,13,10,vecteur);

        //Cote gauche façade
        monSTL+=trianglePoint(0,1,14,vecteur);
        monSTL+=trianglePoint(15,14,1,vecteur);

        //Cote Droit Façade
        monSTL+=trianglePoint(3,17,2,vecteur);
        monSTL+=trianglePoint(17,16,2,vecteur);

        //Dessus exterrieur facade
        monSTL+=trianglePoint(1,2,16,vecteur);
        monSTL+=trianglePoint(1,16,15,vecteur);

        //derriere façade
        monSTL+=trianglePoint(16,17,18,vecteur);
        monSTL+=trianglePoint(18,20,16,vecteur);
        monSTL+=trianglePoint(14,19,21,vecteur);
        monSTL+=trianglePoint(14,21,15,vecteur);

        //Dessus derrière
        monSTL+=trianglePoint(15,10,13,vecteur);
        monSTL+=trianglePoint(15,16,10,vecteur);

        //Triangle gauche
        monSTL+=trianglePoint(13,22,15,vecteur);

        //Triangle Droit
        monSTL+=trianglePoint(23,10,16,vecteur);
        return monSTL;


    }
}
