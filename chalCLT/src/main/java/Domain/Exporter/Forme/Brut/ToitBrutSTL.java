package Domain.Exporter.Forme.Brut;


import Domain.Enum.Direction;
import Domain.Exporter.Forme.Forme;

import static Domain.Utility.MatrixRotation.getMatrixRotation;
import static java.lang.Math.tan;
import static java.lang.Math.toRadians;

public class ToitBrutSTL extends Forme {
    private final double widthParra;
    private final double widthPerpen;

    public ToitBrutSTL(double x, double y, double z, double widthParra, double widthPerpen, double thickness, Direction direction, double theta, double alpha, double beta, double gamma) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.widthParra = widthParra;
        this.widthPerpen = widthPerpen;
        this.thickness = thickness;
        this.theta = toRadians(theta);
        this.alpha = toRadians(alpha);
        this.beta = toRadians(beta);
        this.gamma = toRadians(gamma);
        this.direction = direction;
    }

    @Override
    public String writer() {

        double[][] vecteur = {
                //Les X du bord à gauche (0 à 4)
                {0,-thickness,0,-widthParra,-widthParra
                        //Les X du bord à droite(5 à 9)
                        ,0,-thickness,0,-widthParra,-widthParra,
                        // Les point X du dessus du toit sont déjà tracés
                        // Les point X des rectangles grand et petite dessous, devant, derriere
                },
                //Les Y du bord à gauche (0 à 4)
                {0,0,0,0,0,
                        //Les Y du bord à Droite (5 à 9)
                        widthPerpen,widthPerpen,widthPerpen,widthPerpen,widthPerpen,
                        // Les point Y du dessus du toit sont déjà tracés
                        // Les point Y des rectangles grand et petite dessous, devant, derriere
                },
                //Les Z du bord à gauche (0 à 4)
                {-thickness*tan(theta),0,thickness/2,widthParra*tan(theta)-thickness*tan(theta),widthParra*tan(theta) + thickness/2
                        //Les Z du bord à Droite (5 à 9)
                        ,-thickness*tan(theta),0,thickness/2,widthParra*tan(theta)-thickness*tan(theta),widthParra*tan(theta) + thickness/2
                        // Les point Z du dessus du toit sont déjà tracés
                        // Les point Z des rectangles grand et petite dessous, devant, derriere
                },
        };

        //Transformation sur la piece pignon droit pour faire parti du chalet
        vecteur = multiplyMatrices(getMatrixRotation(alpha,beta,gamma),vecteur,3,3,vecteur[0].length);

        for (int i =0; i < vecteur[0].length; i++){
            vecteur[0][i]+=x;
            vecteur[1][i]+=y;
            vecteur[2][i]+=z;
        }

        //Transformation sur tout le chalet
        vecteur = getVecteurDirection(vecteur,direction,vecteur[0].length);

        String monStl = "";

        //Coté gauche principale
        monStl+=trianglePoint(1,0,2,vecteur);
        monStl+=trianglePoint(1,2,3,vecteur);
        monStl+=trianglePoint(2,3,4,vecteur);

        //Coté droit principale
        monStl+=trianglePoint(5,6,7,vecteur);
        monStl+=trianglePoint(6,7,8,vecteur);
        monStl+=trianglePoint(7,8,9,vecteur);

        //Dessus du toit
        monStl+=trianglePoint(2,4,9,vecteur);
        monStl+=trianglePoint(2,9,7,vecteur);

        //Rectangle devant
        monStl+=trianglePoint(0,2,7,vecteur);
        monStl+=trianglePoint(0,7,5,vecteur);

        //Petit Rectangle Dessous
        monStl+=trianglePoint(0,1,6,vecteur);
        monStl+=trianglePoint(0,5,6,vecteur);

        //Rectangle derrière
        monStl+=trianglePoint(3,4,9,vecteur);
        monStl+=trianglePoint(3,9,8,vecteur);

        //Grand Rectangle Dessous
        monStl+=trianglePoint(1,6,8,vecteur);
        monStl+=trianglePoint(1,8,3,vecteur);


        return monStl;
    }
}
