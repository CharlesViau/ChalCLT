package Domain.Exporter.Forme.Rainure;


import Domain.Enum.Direction;
import Domain.Exporter.Forme.Forme;

public class PrismeExtraslotMilieu extends Forme {
    private double widthGauche;
    private double widthDevant;
    private int reflexion;

    public PrismeExtraslotMilieu(double widthG, double widthF, double height, double thickness, double slotDistance, Direction direction, int reflexion){
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
                        0,0,0,0,0,0,0,0,
                        //X du rectangle bas (8 à 11)
                        widthGauche/2+slotDistance/2,widthGauche/2+slotDistance/2,widthGauche/2-thickness/2,widthGauche/2-thickness/2,
                        //X du rectangle haut (12 à 15)
                        widthGauche/2+slotDistance/2,widthGauche/2+slotDistance/2,widthGauche/2-thickness/2,widthGauche/2-thickness/2
                },
                //Y
                {
                        0,0,0,0,0,0,0,0,
                        //Y du rectangle bas (8 à 11)
                        widthDevant/2-thickness/2-slotDistance/2,widthDevant/2-thickness/2+slotDistance/2,widthDevant/2-thickness/2+slotDistance/2,widthDevant/2-thickness/2-slotDistance/2,
                        //Y du rectangle haut (12 à 15)
                        widthDevant/2-thickness/2-slotDistance/2,widthDevant/2-thickness/2+slotDistance/2,widthDevant/2-thickness/2+slotDistance/2,widthDevant/2-thickness/2-slotDistance/2
                },
                //Z
                {
                        0,0,0,0,0,0,0,0,
                        //Z du rectangle bas (8 à 11)
                        0,0,0,0,
                        //Z du rectangle haut (12 à 15)
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
        monSTL+=trianglePoint(13,14,15,vecteur);
        monSTL+=trianglePoint(13,12,15,vecteur);


        //Petit carré bas
        monSTL+=trianglePoint(8,9,10,vecteur);
        monSTL+=trianglePoint(8,10,11,vecteur);

        //Rectangle droite
        monSTL+=trianglePoint(9,10,14,vecteur);
        monSTL+=trianglePoint(9,13,14,vecteur);

        //Rectangle devant
        monSTL+=trianglePoint(8,9,13,vecteur);
        monSTL+=trianglePoint(8,12,13,vecteur);

        //Rectangle gauche
        monSTL+=trianglePoint(8,11,15,vecteur);
        monSTL+=trianglePoint(8,12,15,vecteur);

        //Rectangle Derriere
        monSTL+=trianglePoint(10,11,15,vecteur);
        monSTL+=trianglePoint(10,14,15,vecteur);

        return monSTL;
    }
}
