package Domain.Exporter.Forme.Fini;


import Domain.Enum.Direction;
import Domain.Exporter.Forme.Forme;

import static Domain.Utility.MatrixRotation.getMatrixRotation;
import static java.lang.Math.*;

public class ToitFiniSTL extends Forme {
    private final double widthParra;
    private final double widthPerpen;

    public ToitFiniSTL(double x, double y, double z, double widthParra, double widthPerpen, double thickness, Direction direction, double theta, double alpha, double beta, double gamma) {
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
                {0, -thickness / 2, 0, -widthParra - thickness, -widthParra - thickness
                        //Les X du bord à droite(5 à 9)
                        , -thickness / 2, 0, 0, -widthParra - thickness, -widthParra - thickness,
                        // Les point X du dessus du toit sont déjà tracés
                        // Les point X des rectangles grand et petite dessous, devant, derriere
                        // Les X du bord droite de la rainure(10 à 13)
                        -thickness / 2, -thickness, -widthParra + thickness / 2 - thickness, -widthParra + thickness / 2 - thickness,
                        // Les X du bord gauche de la rainure(14 à 17)
                        -thickness / 2, -thickness, -widthParra + thickness / 2 - thickness, -widthParra + thickness / 2 - thickness
                        //Les X pour faire le rectangle du bout de la rainure et le grand rectangle de la rainure sont déjà là
                        //Le X du rectangle horizontal du bas de la rainure est déjà là
                },
                //Les Y du bord à gauche (0 à 4)
                {0, 0, 0, 0, 0,
                        //Les Y du bord à Droite (5 à 9)
                        widthPerpen, widthPerpen, widthPerpen, widthPerpen, widthPerpen,
                        // Les point Y du dessus du toit sont déjà tracés
                        // Les point Y des rectangles grand et petite dessous, devant, derriere
                        // Les Y du bord droite de la rainure(10 à 13)
                        widthPerpen - thickness / 2, widthPerpen - thickness / 2, widthPerpen - thickness / 2, widthPerpen - thickness / 2,
                        // Les Y du bord gauche de la rainure(14 à 17)
                        thickness / 2, thickness / 2, thickness / 2, thickness / 2
                        //Les Y pour faire le rectangle du bout de la rainure et le grand rectangle de la rainure sont déjà là
                        //Le Y du rectangle horizontal du bas de la rainure est déjà là
                },
                //Les Z du bord à gauche (0 à 4)
                {0, 0, thickness / 2, widthParra * tan(theta) - thickness / 2 * tan(theta), widthParra * tan(theta) + thickness / 2
                        //Les Z du bord à Droite (5 à 9)
                        , 0, 0, thickness / 2, widthParra * tan(theta) + thickness / 2, widthParra * tan(theta) - thickness / 2 * tan(theta),
                        // Les point Z du dessus du toit sont déjà tracés
                        // Les point Z des rectangles grand et petite dessous, devant, derriere
                        // Les Z du bord droite de la rainure(10 à 13)
                        0, 0, widthParra * tan(theta) - thickness / 2 * tan(theta) - thickness / 2 * sin(theta), widthParra * tan(theta) - thickness * tan(theta) - thickness / 2 * sin(theta),
                        // Les Z du bord gauche de la rainure(14 à 17)
                        0, 0, widthParra * tan(theta) - thickness / 2 * tan(theta) - thickness / 2 * sin(theta), widthParra * tan(theta) - thickness * tan(theta) - thickness / 2 * sin(theta)
                        //Les Z pour faire le rectangle du bout de la rainure et le grand rectangle de la rainure sont déjà là
                        //Le Z du rectangle horizontal du bas de la rainure est déjà là
                },
        };

        //Transformation sur la piece pignon droit pour faire parti du chalet
        vecteur = multiplyMatrices(getMatrixRotation(alpha, beta, gamma), vecteur, 3, 3, vecteur[0].length);

        for (int i = 0; i < vecteur[0].length; i++) {
            vecteur[0][i] += x;
            vecteur[1][i] += y;
            vecteur[2][i] += z;
        }

        //Transformation sur tout le chalet
        //vecteur = getVecteurDirection(vecteur,direction,18);

        String monStl = "";

        //Coté gauche principale
        monStl += trianglePoint(1, 0, 2, vecteur);
        monStl += trianglePoint(1, 2, 3, vecteur);
        monStl += trianglePoint(2, 4, 3, vecteur);

        //Coté droit principale
        monStl += trianglePoint(5, 6, 7, vecteur);
        monStl += trianglePoint(5, 7, 8, vecteur);
        monStl += trianglePoint(5, 8, 9, vecteur);

        //Dessus du toit
        monStl += trianglePoint(2, 4, 8, vecteur);
        monStl += trianglePoint(2, 8, 7, vecteur);

        //Rectangle devant
        monStl += trianglePoint(0, 2, 7, vecteur);
        monStl += trianglePoint(0, 7, 6, vecteur);

        //Petit Rectangle Dessous
        monStl += trianglePoint(0, 1, 6, vecteur);
        monStl += trianglePoint(1, 5, 6, vecteur);

        //Rectangle derrière
        monStl += trianglePoint(3, 4, 8, vecteur);
        monStl += trianglePoint(3, 8, 9, vecteur);

        //Grand Rectangle Dessous
        monStl += trianglePoint(1, 3, 9, vecteur);
        monStl += trianglePoint(1, 9, 5, vecteur);

        //Rectangle côté droit
        monStl += trianglePoint(10, 11, 12, vecteur);
        monStl += trianglePoint(11, 13, 12, vecteur);

        //Rectangle côté gauche
        monStl += trianglePoint(14, 15, 16, vecteur);
        monStl += trianglePoint(15, 17, 16, vecteur);

        //Le rectangle du bout de la rainure
        monStl += trianglePoint(12, 13, 16, vecteur);
        monStl += trianglePoint(13, 17, 16, vecteur);

        //Le grand rectangle de la rainure
        monStl += trianglePoint(11, 17, 13, vecteur);
        monStl += trianglePoint(11, 15, 17, vecteur);

        //Le rectangle horizontal du bas de la rainure
        monStl += trianglePoint(10, 11, 14, vecteur);
        monStl += trianglePoint(11, 15, 14, vecteur);
        return monStl;
    }
}
