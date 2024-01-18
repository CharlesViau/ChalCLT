package Domain.Exporter.Forme.Fini;


import Domain.Enum.Direction;
import Domain.Exporter.Forme.Forme;


import static Domain.Utility.MatrixRotation.getMatrixRotation;
import static java.lang.Math.*;

public class RallongeVerticaleFiniSTL extends Forme {
    public RallongeVerticaleFiniSTL(double x, double y, double z, double width, double height, double thickness, Direction direction, double theta, double alpha, double beta, double gamma){
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
        double petiteHeight = height-tan(theta)*thickness/2;
        double[][] vecteur= {
                //X des trapèzes gauches(0 à 9)
                {0,0,-thickness/2,-thickness/2,0,0, 0,-thickness/2,-thickness/2,-thickness/2,
                        //X des trapèzes droites(10 à 19)
                        -width+thickness/2,-width+thickness/2,-width,-width,-width+thickness/2,-width+thickness/2,-width+thickness/2,-width,-width,-width
                        //Les X des rectangles facades sont déjà là
                        //Les X des rectangles allongés sont déjà la
                        //Les X des triangles coté sont déjà là
                        //Les X des triangles dessou sont déjà là
                },
                //Y des trapèzes gauches(0 à 9)
                {0,thickness/2,thickness/2,thickness,0,0,thickness/2,thickness/2,thickness/2,thickness,
                        //Y des trapèze droites(10 à 19)
                        thickness,thickness/2,thickness/2,0,thickness,thickness/2,thickness/2,thickness/2,0,0
                        //Les Y des rectangles facades sont déjà là
                        //Les Y des rectangles allongés sont déjà la
                        //Les Y des triangles coté sont déjà là
                        //Les Y des triangles dessou sont déjà là
                },
                //Z des trapèzes gauches(0 à 9)
                {0,0,0,0,height,petiteHeight,petiteHeight,petiteHeight-tan(theta)*thickness/2,height-3*tan(theta)*thickness/2,height-3*tan(theta)*thickness/2,
                        //Z des trapèze droites(10 à 19)
                        0,0,0,0,height-3*tan(theta)*thickness/2,height-3*tan(theta)*thickness/2,height-tan(theta)*thickness,
                        height-tan(theta)*thickness/2,height-tan(theta)*thickness/2,height
                        //Les Z des rectangles facades sont déjà là
                        //Les Z des rectangles allongés sont déjà là
                        //Les Z des triangles coté sont déjà là
                        //Les Z des triangles dessou sont déjà là
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
        //vecteur = getVecteurDirection(vecteur,direction,20);

        String monSTL = "";

        //2 rectangles gauhes
        monSTL+=trianglePoint(0,1,5,vecteur);
        monSTL+=trianglePoint(1,6,5,vecteur);
        monSTL+=trianglePoint(2,3,8,vecteur);
        monSTL+=trianglePoint(3,9,8,vecteur);
        //2 rectangle droites
        monSTL+=trianglePoint(10,11,14,vecteur);
        monSTL+=trianglePoint(11,15,14,vecteur);
        monSTL+=trianglePoint(12,13,17,vecteur);
        monSTL+=trianglePoint(13,18,17,vecteur);
        //les 3 rectangles façades
        monSTL+=trianglePoint(0,4,19,vecteur);
        monSTL+=trianglePoint(0,19,13,vecteur);
        monSTL+=trianglePoint(1,6,17,vecteur);
        monSTL+=trianglePoint(1,17,12,vecteur);
        monSTL+=trianglePoint(3,9,14,vecteur);
        monSTL+=trianglePoint(3,14,10,vecteur);

        //les 2 rectangles allongés
        monSTL+=trianglePoint(4,6,17,vecteur);
        monSTL+=trianglePoint(4,17,19,vecteur);
        monSTL+=trianglePoint(7,9,14,vecteur);
        monSTL+=trianglePoint(7,14,16,vecteur);

        //les 4 triangles cotés
        monSTL+=trianglePoint(4,5,6,vecteur);
        monSTL+=trianglePoint(7,8,9,vecteur);
        monSTL+=trianglePoint(14,15,16,vecteur);
        monSTL+=trianglePoint(17,18,19,vecteur);

        //les 4 triangles Desous
        monSTL+=trianglePoint(0,1,13,vecteur);
        monSTL+=trianglePoint(1,12,13,vecteur);
        monSTL+=trianglePoint(2,3,11,vecteur);
        monSTL+=trianglePoint(3,10,11,vecteur);
        return monSTL;
    }
}
