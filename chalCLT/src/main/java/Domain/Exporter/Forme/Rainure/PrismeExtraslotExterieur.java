package Domain.Exporter.Forme.Rainure;


import Domain.Enum.Direction;
import Domain.Exporter.Forme.Forme;

public class PrismeExtraslotExterieur extends Forme {
    private double widthGauche;
    private double widthDevant;

    private int reflexion;
    public PrismeExtraslotExterieur(double widthG, double widthF, double height, double thickness, double slotDistance, Direction direction, int reflexion){
        this.widthGauche = widthG;
        this.widthDevant = widthF;
        this.height = height;
        this.thickness = thickness;
        this.slotDistance = slotDistance;
        this.direction = direction;
        this.reflexion = reflexion;

    }



    public String writer(){

        double[][] vecteur = {
                //X
                {
                    //X du rectangle bas (0 à 3)
                        widthGauche/2-slotDistance/2,widthGauche/2+slotDistance/2,widthGauche/2-slotDistance/2,widthGauche/2+slotDistance/2,
                    //X du rectangle Haut (4 à 7)
                        widthGauche/2-slotDistance/2,widthGauche/2+slotDistance/2,widthGauche/2-slotDistance/2,widthGauche/2+slotDistance/2

                },
                //Y
                {
                    //Y du rectangle bas (0 à 3)
                        widthDevant/2,widthDevant/2,widthDevant/2-thickness/2,widthDevant/2-thickness/2,
                    //Y du rectangle haut (4 à 7)
                        widthDevant/2,widthDevant/2,widthDevant/2-thickness/2,widthDevant/2-thickness/2
                },
                //Z
                {
                    //Z du rectangle bas (0 à 3)
                        0,0,0,0,
                    //Z du rectangle haut (4 à 7)
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
        monSTL+=trianglePoint(4,5,7,vecteur);
        monSTL+=trianglePoint(4,6,7,vecteur);


        //Petit carré bas
        monSTL+=trianglePoint(0,1,3,vecteur);
        monSTL+=trianglePoint(0,3,2,vecteur);

        //Rectangle droite
        monSTL+=trianglePoint(0,1,5,vecteur);
        monSTL+=trianglePoint(0,5,4,vecteur);

        //Rectangle devant
        monSTL+=trianglePoint(1,3,7,vecteur);
        monSTL+=trianglePoint(1,7,5,vecteur);

        //Rectangle gauche
        monSTL+=trianglePoint(3,2,6,vecteur);
        monSTL+=trianglePoint(3,6,7,vecteur);

        //Rectangle Derriere
        monSTL+=trianglePoint(2,6,4,vecteur);
        monSTL+=trianglePoint(0,4,2,vecteur);

        return monSTL;
    }
}
