package Domain.Exporter.Forme.Rainure;


import Domain.Enum.Direction;
import Domain.Exporter.Forme.Forme;

public class PrismeExtraslotInterieur extends Forme {
    private double widthGauche;
    private double widthDevant;

    private int reflexion;
    public PrismeExtraslotInterieur(double widthG, double widthF, double height, double thickness, double slotDistance, Direction direction, int reflexion){
        this.widthGauche = widthG;
        this.widthDevant = widthF;
        this.height = height;
        this.thickness = thickness;
        this.slotDistance = slotDistance;
        this.direction = direction;
        this.reflexion = reflexion;
    }


    public String writer() {
        double[][] vecteur = {
                //X
                {
                        0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
                        //X du rectangle bas (16 à 19)
                        widthGauche/2-thickness/2,widthGauche/2-thickness/2-slotDistance,widthGauche/2-thickness/2-slotDistance,widthGauche/2-thickness/2,
                        //X du rectangle haut (19 à 23)
                        widthGauche/2-thickness/2,widthGauche/2-thickness/2-slotDistance,widthGauche/2-thickness/2-slotDistance,widthGauche/2-thickness/2
                },
                //Y
                {
                        0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
                        //Y du rectangle bas (16 à 19)
                        widthDevant/2-thickness/2+slotDistance/2,widthDevant/2-thickness/2+slotDistance/2,widthDevant/2-thickness,widthDevant/2-thickness,
                        //Y du rectangle haut (19 à 23)
                        widthDevant/2-thickness/2+slotDistance/2,widthDevant/2-thickness/2+slotDistance/2,widthDevant/2-thickness,widthDevant/2-thickness
                },
                //Z
                {
                        0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
                        //Z du rectangle bas (16 à 19)
                        0,0,0,0,
                        //Z du rectangle haut (19 à 23)
                        height,height,height,height
                }
        };
        double temp;
        if (direction == Direction.FRONT || direction == Direction.BACK){
            switch (reflexion){
                case 1:
                    for (int i =0; i < vecteur[0].length; i++){
                        vecteur[0][i]*=-1;
                        temp = vecteur[0][i];
                        vecteur[0][i] = vecteur[1][i];
                        vecteur[1][i] = temp;
                    }
                    break;
                case 2:
                    for (int i =0; i < vecteur[0].length; i++){
                        vecteur[1][i]*=-1;
                        temp = vecteur[0][i];
                        vecteur[0][i] = vecteur[1][i];
                        vecteur[1][i] = temp;
                    }
                    break;
                case 3:
                    for (int i =0; i < vecteur[0].length; i++){
                        vecteur[0][i]*=-1;
                        vecteur[1][i]*=-1;
                        temp = vecteur[0][i];
                        vecteur[0][i] = vecteur[1][i];
                        vecteur[1][i] = temp;
                    }
                    break;
                default:
                    for (int i =0; i < vecteur[0].length; i++){
                        temp = vecteur[0][i];
                        vecteur[0][i] = vecteur[1][i];
                        vecteur[1][i] = temp;
                    }
                    break;
            }
        }else{
            switch (reflexion){
                case 1:
                    for (int i =0; i < vecteur[0].length; i++){
                        vecteur[0][i]*=-1;

                    }
                    break;
                case 2:
                    for (int i =0; i < vecteur[0].length; i++){
                        vecteur[1][i]*=-1;

                    }
                    break;
                case 3:
                    for (int i =0; i < vecteur[0].length; i++){
                        vecteur[0][i]*=-1;
                        vecteur[1][i]*=-1;

                    }
                    break;
            }
        }


        String monSTL = "";
        //Petit carré haut
        monSTL+=trianglePoint(20,21,22,vecteur);
        monSTL+=trianglePoint(20,23,22,vecteur);


        //Petit carré bas
        monSTL+=trianglePoint(16,17,18,vecteur);
        monSTL+=trianglePoint(16,19,18,vecteur);


        //Rectangle droite
        monSTL+=trianglePoint(16,17,21,vecteur);
        monSTL+=trianglePoint(16,20,21,vecteur);

        //Rectangle devant
        monSTL+=trianglePoint(19,16,20,vecteur);
        monSTL+=trianglePoint(19,23,20,vecteur);

        //Rectangle gauche
        monSTL+=trianglePoint(18,19,22,vecteur);
        monSTL+=trianglePoint(19,23,22,vecteur);

        //Rectangle Derriere
        monSTL+=trianglePoint(18,17,21,vecteur);
        monSTL+=trianglePoint(18,22,21,vecteur);

        return monSTL;
    }
}
