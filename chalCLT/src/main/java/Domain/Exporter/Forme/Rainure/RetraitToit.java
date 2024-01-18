package Domain.Exporter.Forme.Rainure;

import Domain.Enum.Direction;
import Domain.Exporter.Forme.Forme;

import java.io.FileOutputStream;

import static Domain.Utility.MatrixRotation.getMatrixRotation;
import static java.lang.Math.*;

public class RetraitToit extends Forme {
    private double widthParra;
    private double widthPerpen;
    public RetraitToit(double x, double y, double z, double widthParra, double widthPerpen, double height, double thickness, Direction direction, double theta, double alpha, double beta, double gamma){
        this.x = x;
        this.y = y;
        this.z = z;
        this.widthParra = widthParra;
        this.widthPerpen = widthPerpen;
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

        final double baseParaTriangleExterieurX = -thickness/2;
        final double baseParaTriangleExterieurZ = 0 ;
        final double baseParaTriangleInterieurX = -thickness;
        final double baseParaTriangleInterieurZ = 0;
        final double hautParaTriangleExterieurX = -widthParra-thickness;
        final double hautParaTriangleExterieurZ = widthParra*tan(theta) - thickness/2*tan(theta);
        final double hautParaTriangleInterieurX = -widthParra-thickness;
        final double hautParaTriangleInterieurZ = widthParra*tan(theta) - thickness/2*tan(theta) - thickness/2*sin(theta);
        final double hautParaInterieurX = -widthParra-thickness/2;
        final double hautParaExterieurX =-widthParra-thickness/2;
        final double hautParaInterieurZ = widthParra*tan(theta) - thickness*tan(theta) - thickness/2*sin(theta);
        final double hautParaExterieurZ = widthParra*tan(theta) - thickness/2*tan(theta) - thickness/2*sin(theta);
        final double triangleBasX = 0;
        final double triangleBasZ = -thickness*tan(theta);
        final double triangleMoyenX = 0;
        final double triangleMoyenZ = 0;
        final double parraTriangleGaucheY = 0;
        final  double parraGaucheY = thickness/2;
        final double parraTriangleDroiteY =widthPerpen;
        final double parraDroiteY = widthPerpen - thickness/2;

        double[][] vecteur= {
                //X du Parrallele gauche avec triangle (0 à 5)
                {triangleBasX, triangleMoyenX, baseParaTriangleExterieurX, baseParaTriangleInterieurX, hautParaTriangleInterieurX, hautParaTriangleExterieurX,
                        //X du Parrallele droit avec triangle (6 à 11)
                        triangleBasX, triangleMoyenX, baseParaTriangleExterieurX, baseParaTriangleInterieurX, hautParaTriangleInterieurX, hautParaTriangleExterieurX,
                        //X du Parrallele gauche (12 à 15)
                        baseParaTriangleExterieurX, baseParaTriangleInterieurX, hautParaInterieurX, hautParaExterieurX,
                        //X du Parrallele Droite (16 à 19)
                        baseParaTriangleExterieurX, baseParaTriangleInterieurX, hautParaInterieurX, hautParaExterieurX,
                },
                //Y du Parrallele gauche avec triangle (0 à 5)
                {parraTriangleGaucheY,parraTriangleGaucheY,parraTriangleGaucheY,parraTriangleGaucheY,parraTriangleGaucheY,parraTriangleGaucheY,
                        //Y du Parrallele droit avec triangle (6 à 11)
                        parraTriangleDroiteY,parraTriangleDroiteY,parraTriangleDroiteY,parraTriangleDroiteY,parraTriangleDroiteY,parraTriangleDroiteY,
                        //Y du Parrallele gauche (12 à 15)
                        parraGaucheY, parraGaucheY, parraGaucheY, parraGaucheY,
                        //Y du Parrallele Droite (16 à 19)
                        parraDroiteY, parraDroiteY, parraDroiteY, parraDroiteY,
                },
                //Z du Parrallele gauche avec triangle (0 à 5)
                {triangleBasZ, triangleMoyenZ, baseParaTriangleExterieurZ, baseParaTriangleInterieurZ, hautParaTriangleInterieurZ, hautParaTriangleExterieurZ,
                        //Z du Parrallele droit avec triangle (6 à 11)
                        triangleBasZ, triangleMoyenZ, baseParaTriangleExterieurZ, baseParaTriangleInterieurZ, hautParaTriangleInterieurZ, hautParaTriangleExterieurZ,
                        //Y du Parrallele gauche (12 à 15)
                        baseParaTriangleExterieurZ, baseParaTriangleInterieurZ, hautParaInterieurZ, hautParaExterieurZ,
                        //X du Parrallele Droite (16 à 19)
                        baseParaTriangleExterieurZ, baseParaTriangleInterieurZ, hautParaInterieurZ, hautParaExterieurZ,
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

        //Parrallèle gauche avec triangle
        monSTL+=trianglePoint(0,1,3,vecteur);
        monSTL+=trianglePoint(2,3,4,vecteur);
        monSTL+=trianglePoint(2,4,5,vecteur);

        //Parrallèle gauche
        monSTL+=trianglePoint(12,13,14,vecteur);
        monSTL+=trianglePoint(12,14,15,vecteur);

        //Parrallèle droite avec triangle
        monSTL+=trianglePoint(6,7,9,vecteur);
        monSTL+=trianglePoint(8,9,10,vecteur);
        monSTL+=trianglePoint(8,11,10,vecteur);

        //Parrallèle droite
        monSTL+=trianglePoint(16,17,18,vecteur);
        monSTL+=trianglePoint(16,19,18,vecteur);

        //Dessus de la base
        monSTL+=trianglePoint(1,2,8,vecteur);
        monSTL+=trianglePoint(1,8,7,vecteur);


        //Dessus de la petite partie
        monSTL+=trianglePoint(2,3,9,vecteur);
        monSTL+=trianglePoint(2,9,8,vecteur);

        //Dessous de la base
        monSTL+=trianglePoint(0,3,9,vecteur);
        monSTL+=trianglePoint(0,9,6,vecteur);

        //Derrière base
        monSTL+=trianglePoint(1,0,7,vecteur);
        monSTL+=trianglePoint(0,6,7,vecteur);

        //Facade base
        monSTL+=trianglePoint(1,0,7,vecteur);
        monSTL+=trianglePoint(0,6,7,vecteur);

        //Dessous rainure gauche
        monSTL+=trianglePoint(3,4,14,vecteur);
        monSTL+=trianglePoint(3,14,13,vecteur);

        //Dessous rainure droite
        monSTL+=trianglePoint(17,10,9,vecteur);
        monSTL+=trianglePoint(17,18,10,vecteur);

        //Dessus rainure gauche
        monSTL+=trianglePoint(12,2,5,vecteur);
        monSTL+=trianglePoint(12,5,15,vecteur);

        //Dessus rainure droite
        monSTL+=trianglePoint(8,19,11,vecteur);
        monSTL+=trianglePoint(8,16,19,vecteur);

        //Derrière branchement
        monSTL+=trianglePoint(14,15,19,vecteur);
        monSTL+=trianglePoint(14,18,19,vecteur);

        //Derrière branchement
        monSTL+=trianglePoint(4,10,14,vecteur);
        monSTL+=trianglePoint(14,10,18,vecteur);

        //Devant branchement
        monSTL+=trianglePoint(4,5,11,vecteur);
        monSTL+=trianglePoint(4,10,11,vecteur);

        //Dessus branchement
        monSTL+=trianglePoint(5,11,15,vecteur);
        monSTL+=trianglePoint(15,11,19,vecteur);
        return monSTL;
    }
}
